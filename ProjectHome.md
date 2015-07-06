# portlet.py #

The aim of this project is to allow developers to write portlets in Python that can be deployed on a Java Portal using the JSR-168/286 specifications. This is simply a single Java class - PyPortlet - that implements the Portlet API and dispatches requests to a Python class. The PyPortlet class is based on the org.python.util.PyServlet class that ships with the Jython distribution.

The main difference of PyPortlet compared to PyServlet mostly lies in the difference in the Servlet/Portlet APIs. Other notable difference is that PyPortlet only dispatches to a single Python class while PyServlet dispatches to a Python class based on the URL.

The project also includes a Python module, portlet.py, which is a thin wrapper around the Portlet API in order to simplify it and take advantage of python instead of being just an adapter.