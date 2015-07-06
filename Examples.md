Examples of JSR-168 portlets in Jython

# Introduction #

Here are a few examples of Python portlets. See PortletXml on how to deploy them in your webapp.
These portlets simply subclass the `GenericPortlet` Java class.
# Hello World #

```
from javax.portlet import GenericPortlet
 
class HelloPortlet(GenericPortlet):
    def render(self, request, response):
       response.writer.append("<p>Hello World!</p>")
```

# Dispatching to a JSP file #
```
class JythonJspPortlet (GenericPortlet):
    def init(self, config):
        self.index = config.portletContext.getRequestDispatcher('/index.jsp')

    def render(self, request, response):
        self.index.include(request, response)
```

# Advanced Examples #
The Jython portlets have full access to the Portlet API (currently JSR-168). The examples above are quite simple, for more advanced examples see any of the portlet documentation available.

The main advantage of using Jython is the ability to wrap the API with syntactic sugar in Python. Have a look at the `portlet.py` (PortletPy) module to have a more Python friendly API.