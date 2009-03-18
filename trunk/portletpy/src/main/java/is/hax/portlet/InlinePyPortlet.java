package is.hax.portlet;

import org.python.core.*;
import org.python.util.PythonInterpreter;

import javax.portlet.*;
import java.io.IOException;
import java.io.File;
import java.util.Properties;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: pjesi
 * Date: Mar 16, 2009
 * Time: 9:24:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class InlinePyPortlet extends GenericPortlet {

    public static final String JYTHON_INLINE_SCRIPT = "portletScript";

    public static final String JYTHON_INLINE_SCRIPT_NAME = "InlinePortlet";

    private static final String DEFAULT_SCRIPT =
            "from javax.portlet import GenericPortlet\n" +
                    "class InlinePortlet(GenericPortlet):\n" +
                    "    def render(self, req, res):\n" +
                    "        res.writer.append('<p>Hello World, how are we?</p>')\n" +
                    "        return\n";


    private PythonInterpreter interp;
    private static final String EDIT_JSP = "/WEB-INF/jsp/inline/edit.jsp";


    /**
     * Dispatch to a jsp or servlet.
     */
    protected void dispatch(RenderRequest request,
                            RenderResponse response,
                            String path)
            throws PortletException, IOException {
        PortletContext ctx = getPortletContext();
        PortletRequestDispatcher dispatcher = ctx.getRequestDispatcher(path);
        dispatcher.include(request, response);
    }


    public void render(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        PortletPreferences prefs = request.getPreferences();
        String script = prefs.getValue(JYTHON_INLINE_SCRIPT, DEFAULT_SCRIPT);

        request.setAttribute(JYTHON_INLINE_SCRIPT, script);

        PortletMode mode = request.getPortletMode();
        if (PortletMode.VIEW.equals(mode)) {
            Portlet portlet = getPortlet(script);
            response.setContentType("text/html");
            portlet.render(request, response);
        } else if (PortletMode.EDIT.equals(mode)) {
            dispatch(request, response, EDIT_JSP);
        }
    }

    private Portlet getPortlet(String script) throws PyException, PortletException {
        Portlet portlet = null;


        interp.exec(script);

        PyObject cls = interp.get(JYTHON_INLINE_SCRIPT_NAME);
        if (cls == null) {
            throw new PortletException(
                    "No callable (class or function) named " + JYTHON_INLINE_SCRIPT_NAME);
        }
        PyObject pyPortlet = cls.__call__();
        Object o = pyPortlet.__tojava__(Portlet.class);
        if (o == Py.NoConversion) {
            throw new PortletException("The value from " + JYTHON_INLINE_SCRIPT_NAME
                    + " must implement Portlet interface");
        }
        portlet = (Portlet) o;
        portlet.init(getPortletConfig());

        return portlet;
    }


    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        PortletPreferences prefs = request.getPreferences();
        String script = prefs.getValue(JYTHON_INLINE_SCRIPT, DEFAULT_SCRIPT);


        PortletMode mode = request.getPortletMode();
        if (PortletMode.VIEW.equals(mode)) {
            Portlet portlet = getPortlet(script);
            portlet.processAction(request, response);

        } else if (PortletMode.EDIT.equals(mode)) {
            script = request.getParameter(JYTHON_INLINE_SCRIPT);
            if (script != null && script.trim() != null) {
                prefs.setValue(JYTHON_INLINE_SCRIPT, script);
                prefs.store();
            }

        }


    }


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
}
