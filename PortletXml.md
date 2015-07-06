Example portlet descriptor

# portlet.xml #

This is an example of a typical portlet descriptor. The `portlet-class` defines the PyPortlet portlet dispatcher. The init parameter `python.home` specifies the location of your Jython installation (default is /WEB-INF/lib). The `python.portlet` is required and specifies the Python class that implements the Portlet interface.

```
    <portlet>
        <description>Jython Portlet Example</description>
        <portlet-name>portletpy</portlet-name>
        <display-name>Hello Jython Portlet</display-name>
        <portlet-class>is.hax.portlet.PyPortlet</portlet-class>
        
        <init-param>
        	<name>python.home</name>
		<value>/usr/local/jython2.2.1</value>
        </init-param>        
        <init-param>
        	<name>python.portlet</name>
        	<value>myportlet.JythonPortlet</value>
        </init-param>

        <supports>
            <mime-type>text/html</mime-type>
            <portlet-mode>VIEW</portlet-mode>
        </supports>
        <supported-locale>en</supported-locale>

        <portlet-info>
            <title>Hello Jython Portlet</title>
            <short-title>Jython Portlet</short-title>
            <keywords>jython portlet</keywords>
        </portlet-info>
    </portlet>
```