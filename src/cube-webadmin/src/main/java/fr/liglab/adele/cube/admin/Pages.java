package fr.liglab.adele.cube.admin;

import fr.liglab.adele.cube.ICubePlatform;

public class Pages {
	
	public static String AgentsList(ICubePlatform cubePlatform) {
		// Page 1
		String tmp = HTML.PageHeader(cubePlatform);
		tmp += HTML.PageContentStart(cubePlatform);
		
		tmp += "<div class='span3'>"+
		       "     <div class='well sidebar-nav'>"+
		       "     <div class='btn-toolbar'>"+
		       "        <div class='btn-group'>"+
		       "           <a href='cube?p=3' class='btn btn-primary'><i class='icon-plus icon-white'></i>  Create Agent</a>"+
		       "        </div>"+
		       "     </div>"+
		       "     <ul class='nav nav-list'>"+
		       "        <li class='nav-header'>Agents List</li>";

		       //"        <li class='active'><a href='#'><i class=''></i> master</a></li>"+
		       //"        <li><a href='#'><i class=''></i> agent1</a></li>"+
		       
		       //"        <li><a href='#'><i class=''></i> agent2</a></li>"+
		       
		tmp+=  "     </ul>"+
		       "  </div>"+
		       "</div>"+
		       "<div class='span7'>"+
		       "<p></p>"+
		       "</div>";
		
		tmp += HTML.PageContentEnd(cubePlatform);
		return tmp.replace('\'', '"');
	}

	public static String CreateAgent(ICubePlatform cubePlatform) {
		// Page 3
		String tmp = HTML.PageHeader(cubePlatform);
		tmp += HTML.PageContentStart(cubePlatform);
		
		tmp += "<div class='span6'>"+
				"<h1>Create Agent</h1>";		    
		tmp += "<p>Something goes here!</p>";
		tmp +=	"<div class='form-actions'>" +
				"  <button type='submit' class='btn btn-primary'>Create</button>"+
				"  <a href='cube?p=1' class='btn'>Cancel</a>"+
				"</div>"+
				"</div>";
		tmp += HTML.PageContentEnd(cubePlatform);
		return tmp.replace('\'', '"');
	}
	
}
