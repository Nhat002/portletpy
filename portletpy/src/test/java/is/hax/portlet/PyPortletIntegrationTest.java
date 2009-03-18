package is.hax.portlet;

import net.sourceforge.jwebunit.junit.WebTestCase;

import org.apache.pluto.core.PortletServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.python.util.PyServlet;

public class PyPortletIntegrationTest extends WebTestCase {

    // TODO yo dawg! you forgot your testing

    public void testIndexPageDisplaysMessage() throws Exception {
	    // Start at the pluto driver entry point
        beginAt("/pluto/index.jsp");
	    assertTextPresent("Hello Jython");
	}
	
	
	
	protected Server server;

	public void setUp() throws Exception {
	    System.setProperty("org.apache.pluto.embedded.portletId", "portletpy");
	    server = new Server(8083);
	    WebAppContext webapp = new WebAppContext("src/main/webapp", "/test");
	    webapp.setDefaultsDescriptor("/WEB-INF/jetty-pluto-web-default.xml");
	    ServletHolder portletServlet = new ServletHolder(new PortletServlet());
	    portletServlet.setInitParameter("portlet-name", "portletpy");
	    portletServlet.setInitOrder(1);
	    webapp.addServlet(portletServlet, "/PlutoInvoker/portletpy");
	    
	    server.addHandler(webapp);
	    server.start();
	    getTestContext().setBaseUrl("http://localhost:8083/test");
	}

	public void tearDown() throws Exception {
	    server.stop();
	}
}
