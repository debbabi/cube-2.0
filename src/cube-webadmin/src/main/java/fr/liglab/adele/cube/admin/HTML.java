package fr.liglab.adele.cube.admin;

import javax.servlet.http.HttpServletRequest;

import fr.liglab.adele.cube.ICubePlatform;

public class HTML {
	


	public static String Header(ICubePlatform cubePlatform) {
		String tmp = "<!DOCTYPE html>"+
				   "<html lang='en'>"+
				   "<head>"+
				   "   <meta charset='utf-8' />"+
				   "   <title>Cube Platform | localhost:38000 </title>"+
				   "   <meta name='viewport' content='width=device-width, initial-scale=1.0' />"+
				   "   <meta name='description' content='' />"+
				   "   <meta name='author' content='' />"+
				   "   <!-- Le styles -->"+
				   "   <link href='static/css/bootstrap.css' rel='stylesheet' />"+
				   "   <style>"+
				   "      body {"+
				   "      padding-top: 40px; /* 40px to make the container go all the way to the bottom of the topbar */"+
				   "      padding-bottom: 60px;"+
				   "      }"+
				   "   </style>"+
				   "   <link href='static/css/bootstrap-responsive.css' rel='stylesheet' />"+
				   "   <link rel='shortcut icon' href='static/ico/favicon.ico' />"+
				   "   <link rel='apple-touch-icon-precomposed' sizes='144x144' href='static/ico/apple-touch-icon-144-precomposed.png' />"+
				   "   <link rel='apple-touch-icon-precomposed' sizes='114x114' href='static/ico/apple-touch-icon-114-precomposed.png' />"+
				   "   <link rel='apple-touch-icon-precomposed' sizes='72x72' href='static/ico/apple-touch-icon-72-precomposed.png' />"+
				   "   <link rel='apple-touch-icon-precomposed' href='static/ico/apple-touch-icon-57-precomposed.png' />"+
				   "</head>"+
				   "<body>";
		return tmp.replace('\'', '"');
	}
	
	public static String TopNavbar(int tp, ICubePlatform cubePlatform) {				
		String tmp =  "<div class='navbar navbar-inverse navbar-fixed-top'>"+
			    "     <div class='navbar-inner'>"+
			    "        <div class='container'>"+
			    "           <a class='btn btn-navbar' data-toggle='collapse' data-target='.nav-collapse'>"+
			    "           <span class='icon-bar'></span>"+
			    "           <span class='icon-bar'></span>"+
			    "           <span class='icon-bar'></span>"+
			    "           </a>"+
			    "           <a class='brand' href='#'>Cube Web Admin v2.0</a>"+
			    "           <div class='nav-collapse collapse'>"+
			    "              <ul class='nav'>";
		switch(tp) {
		case 1: {
		 tmp += "                 <li class='active'><a href='cube?p=1'>Agents Management</a></li>"+
				"                 <li><a href='cube?p=2'>Informations</a></li>";			
		 break;
		}
		case 2: {
		 tmp += "                 <li><a href='cube?p=1'>Agents Management</a></li>"+
				"                 <li class='active'><a href='cube?p=2'>Informations</a></li>";			
		 break;
		} default:
		 tmp += "                 <li><a href='cube?p=1'>Agents Management</a></li>"+
				"                 <li><a href='cube?p=2'>Informations</a></li>";						
		}
			    
		 tmp += "              </ul>"+
			    "           </div>"+
			    "        </div>"+
			    "     </div>"+
			    "  </div>";
		return tmp.replace('\'', '"');
	}
	
	public static String PageStart(ICubePlatform cubePlatform) {
		return "<div class='container'>";		
	}
	
	public static String PageContentStart(ICubePlatform cubePlatform) {
		return "<div class='row'>";		
	}
	
	public static String PageHeader(ICubePlatform cubePlatform) {
		String tmp = "<div class='page-header'>" +
					 "	<table>"+
					 "       <tr>"+
					 "          <td><img src='static/img/cube.png' /></td>"+
					 "          <td>"+
					 "             <b>Host: </b> <span class='color:#4894a3'>"+cubePlatform.getHost()+"</span><br />"+
					 "             <b>Port: </b> "+cubePlatform.getPort()+"<br />"+					
					 "          </td>"+
					 "       </tr>"+
					 "    </table>"+	
					 "</div>";
		return tmp;
	}
	
	public static int getRelatedTopPage(int p) {
		switch(p) {
		case 0: return 0;
		case 1: return 1; // agents list
		case 2: return 2; // agents list
		case 3: return 1; // agents list
		default: return 0;
		}
	}

	public static String Page(int p, HttpServletRequest req, ICubePlatform cubePlatform) {
		switch(p) {
		case 0: return "404";
		case 1: {			
			return Pages.AgentsList(cubePlatform);
		}
		case 2: {
			String tmp = "<div class='page-header'><h1>Informations</h1></div>";
			return tmp.replace('\'', '"');		
		}
		case 3: {
			return Pages.CreateAgent(cubePlatform);
		}
		default: return "Page not found";
		}
	}
	
	
	public static String PageContentEnd(ICubePlatform cubePlatform) {
		return "</div>  <!-- /row -->";		
	}
	
	public static String PageEnd(ICubePlatform cubePlatform) {
		return "</div>  <!-- /container -->";		
	}
	
	public static String Footer(ICubePlatform cubePlatform) {
		String tmp =  " <script src='static/js/jquery.js'></script>" +
			    "  		<script src='static/js/bootstrap-transition.js'></script>" +
			    "  		<script src='static/js/bootstrap-alert.js'></script>" +
			    "  		<script src='static/js/bootstrap-modal.js'></script>" +
			    "  		<script src='static/js/bootstrap-dropdown.js'></script>" +
			    "  		<script src='static/js/bootstrap-scrollspy.js'></script>" +
			    "  		<script src='static/js/bootstrap-tab.js'></script>" +
			    "  		<script src='static/js/bootstrap-tooltip.js'></script>" +
			    "  		<script src='static/js/bootstrap-popover.js'></script>" +
			    "  		<script src='static/js/bootstrap-button.js'></script>" +
			    "  		<script src='static/js/bootstrap-collapse.js'></script>" +
			    "  		<script src='static/js/bootstrap-carousel.js'></script>" +
			    "  		<script src='static/js/bootstrap-typeahead.js'></script>" +
				"   </body>" +
				"	</html>";
		return tmp.replace('\'', '"');
	}
}
