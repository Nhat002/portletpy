/*
 * Copyright 2008 Vidar Svansson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package is.hax.portlet;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

/**
 * This portlet is used to re-serve Jython portlets.  It stores
 * bytecode for a Jython portlet and re-uses it if the underlying .py
 * file has not changed. This class is based on the org.jython.util.PyServlet class.
 *
 * Example useage
 * <pre>
 *
 * from javax.portlet import GenericPortlet
 * 
 * class HelloPortlet(GenericPortlet):
 *     def render(self, req, res):
 *         res.writer.append("&lt;p>Hello World, how are we?&lt;/p>")
 * </pre>
 *
 * The portlet module is expected to reside in /WEB-INF/jython. 
 * Example portlet.xml:
 * <pre>
 *   &lt;portlet>
 *       &lt;description>Jython Portlet Example&lt;/description>
 *       &lt;portlet-name>portletpy&lt;/portlet-name>
 *       &lt;display-name>Hello Python Portlet&lt;/display-name>
 *       &lt;portlet-class>is.hax.portlet.PyPortlet&lt;/portlet-class>
 *       
 *       &lt;init-param>
 *          &lt;name>python.home&lt;/name>
 *          &lt;value>/usr/local/jython2.2.1&lt;/value>
 *       &lt;/init-param>        
 *       &lt;init-param>
 *       	&lt;name>python.portlet&lt;/name>
 *          &lt;value>myportlet.HelloPortlet&lt;/value>
 *       &lt;/init-param>
 *
 *       &lt;supports>
 *           &lt;mime-type>text/html&lt;/mime-type>
 *           &lt;portlet-mode>VIEW&lt;/portlet-mode>
 *       &lt;/supports>
 *       &lt;supported-locale>en</supported-locale>
 *
 *       &lt;portlet-info>
 *           &lt;title>Hello Jython Portlet&lt;/title>
 *           &lt;short-title>Jython Portlet&lt;/short-title>
 *           &lt;keywords>jython portlet&lt;/keywords>
 *       &lt;/portlet-info>
 *   &lt;/portlet>
 * </pre>
 * 
 * @author Vidar Svansson
 * @see org.jython.util.PyServlet
 * 
 */
public class PyPortlet extends GenericPortlet {

	public static final String PYTHON_PORTLET_KEY = "python.portlet";
	
	private PythonInterpreter interp;
	private long timestamp;
	private Portlet portlet;
	private String path;
	private String name;
	
	@Override
	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {
				
		Portlet portlet = getPortlet();
		portlet.processAction(request, response);
	}

	@Override
	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		
		
		Portlet portlet = getPortlet();
		response.setContentType(request.getResponseContentType());
		portlet.render(request, response); 

	}		
	

	@Override
	public void init(PortletConfig config) throws PortletException {
		
		super.init(config);

		String rootPath = getPortletContext().getRealPath("/");
		if (!rootPath.endsWith(File.separator))
			rootPath += File.separator;

		Properties props = new Properties();
		Properties baseProps = PySystemState.getBaseProperties();

		// Context parameters
		PortletContext context = config.getPortletContext();
		Enumeration e = context.getInitParameterNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			props.put(name, context.getInitParameter(name));
		}

		// Config parameters
        // TODO is this the same as the one above, need to verify
		e = getInitParameterNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			props.put(name, getInitParameter(name));
		}

		if (props.getProperty("python.home") == null
				&& baseProps.getProperty("python.home") == null) {
			props.put("python.home", rootPath + "WEB-INF" + File.separator
					+ "lib");
		}

		
		String portletName = props.getProperty(PYTHON_PORTLET_KEY);
		if(portletName == null) 
			throw new PortletException(PYTHON_PORTLET_KEY +" property not set");
		
		path = rootPath + "WEB-INF" + File.separator + "jython"; 
		String[] modules = portletName.split("\\.");
		int nameIndex = modules.length - 1;
		for(int i = 0; i < nameIndex; i++) {
			path +=  File.separator + modules[i];
		}
		path += ".py";
		name = modules[nameIndex];
		
		PySystemState.initialize(baseProps, props, new String[0]);
		interp = new PythonInterpreter(null, new PySystemState());
		
		PySystemState sys = Py.getSystemState();
		sys.path.append(new PyString(rootPath));

		String modulesDir = rootPath + "WEB-INF" + File.separator + "jython";
		sys.path.append(new PyString(modulesDir));
		PySystemState.add_package("javax.portlet");

		PySystemState.add_classdir(rootPath + "WEB-INF" + File.separator
				+ "classes");

		PySystemState.add_extdir(rootPath + "WEB-INF" + File.separator + "lib",
				true);

	}


	private synchronized Portlet getPortlet()
			throws PortletException, IOException {
		if (portlet == null)
			return loadPortlet();
		File file = new File(path);
		if (file.lastModified() > timestamp)
			return loadPortlet();
		return portlet;
	}

	private Portlet loadPortlet() throws PortletException,
			IOException {
		
		File file = new File(name);

		try {
			interp.execfile(path);
			PyObject cls = interp.get(name);
			if (cls == null) {
				throw new PortletException(
						"No callable (class or function) named " + name
								+ " in " + name);
			}
			PyObject pyPortlet = cls.__call__();
			Object o = pyPortlet.__tojava__(Portlet.class);
			if (o == Py.NoConversion) {
				throw new PortletException("The value from " + name
						+ " must implement Portlet interface");
			}
			portlet = (Portlet) o;
			portlet.init(getPortletConfig());

		} catch (PyException e) {
			throw new PortletException(e);
		}
		timestamp = file.lastModified();
		
		return portlet;
	}

	
	@Override
	public void destroy() {
		if(portlet != null){
			portlet.destroy();
			portlet = null;
		} if(interp != null) {
			interp.cleanup();
			interp = null;
		}
		super.destroy();
	}	

}
