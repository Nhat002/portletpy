A Java implementation of the Portlet interface that dispatches requests to a Jython portlet.

# Introduction #

The `is.hax.portlet.PyPortlet` class is a portlet dispatcher that is backed by a portlet written in Python. The dispatcher uses a Jython interpreter and therefor the portlet has access to all Java classes on the classpath.

The PyPortlet works similar as the `org.jython.util.PyServlet` does for servlets. The main difference is that PyServlet dispatches to a Python class based on the URL request while PyPortlet uses a single Python class.