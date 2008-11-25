package is.hax.portlet;

import org.apache.pluto.core.PortletServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.python.util.PyServlet;

public class JettyPyPortletLauncher {
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		 System.setProperty("org.apache.pluto.embedded.portletId", "portletpy");
	     Server server = new Server(8082);
	     WebAppContext webapp = new WebAppContext("src/main/webapp", "/test");
	     webapp.setDefaultsDescriptor("/WEB-INF/jetty-pluto-web-default.xml");
	     ServletHolder portletServlet = new ServletHolder(new PortletServlet());
	     portletServlet.setInitParameter("portlet-name", "portletpy");
	     portletServlet.setInitOrder(1);
	     webapp.addServlet(portletServlet, "/PlutoInvoker/portletpy");
	     
	     server.addHandler(webapp);
	     server.start();
	}
}
