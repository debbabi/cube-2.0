package fr.liglab.adele.cube.admin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import fr.liglab.adele.cube.ICubePlatform;

public class WebAdmin extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int TOP_PAGE_COUNT = 2;

	private HttpService m_httpService;

	private ICubePlatform m_cubePlatform;

	private boolean no_cube = true;
	
	public void bindHttpService() {
		try {
			m_httpService.registerServlet("/cube", this, null, null);
			m_httpService.registerResources("/static", "/static", null);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (NamespaceException e) {
			e.printStackTrace();
		}
	}

	public void unbindHttpService() {
		m_httpService.unregister("/cube");
		m_httpService.unregister("/static");
	}

	public void bindCubePlatform() {
		no_cube = false;
	}
	
	public void unbindCubePlatform() {
		m_cubePlatform = null;
		no_cube = true;
	}
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String html = "";
		if (no_cube == false) {
			
			html += HTML.Header(m_cubePlatform);

			/* which page to show? */
			String page = req.getParameter("p");
			int p = 0;
			if (page != null) {
				p = new Integer(page).intValue();
			} else {
				// show first page by default!
				p = 1;
			}

			/* show top navbar */
			int tp = HTML.getRelatedTopPage(p);
			if (tp > TOP_PAGE_COUNT || tp < 0) {
				tp = 0;
			}
			html += HTML.TopNavbar(tp, m_cubePlatform);

			html += HTML.PageStart(m_cubePlatform);

			html += HTML.Page(p, req, m_cubePlatform);

			html += HTML.PageEnd(m_cubePlatform);

			html += HTML.Footer(m_cubePlatform);

		} else {
			html += "<h1>Not connected to Cube Platform!</h1>";
		}

		ServletOutputStream out = resp.getOutputStream();
		resp.setContentType("text/html");
		out.println(html);
		out.close();
	}
}
