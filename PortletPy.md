Documentation for the `portlet.py` module

# Introduction #

The `portlet.py` module wraps the JSR-168 API to make is super simple to program portlets using Python syntax.

# Examples #
The `portlet.Portlet` class delegates calls to `render` and `processAction` to `view` and `action`, respectively. The `request` / `response` objects are then wrapped in python objects that implement many of the `__` methods for nicer syntax.

## Hello World ##
This portlet demonstrates how to send output to the response using the `<<` operator for C++ style output.
```
from portlet import Portlet

class HelloPortlet (Portlet):
    def view(self, request, response):
        response << '<h2>Hello Jython</h2>'
```
If you don't like this, then you can also simply use `response.write`

## Accessing parameters ##
This portlet demonstrates how to check if a parameter is in the request and how to access it. It also shows how to use the `response.view` method to create portlet render URLs. To create an action URL, use `response.action`.
```
class NamePortlet (Portlet):
    def view(self, request, response):
        if 'name' in request:
            response << '<p>Hello ' << request['name'] << '</p>'
	    response << '<a href="' << response.view() << '">Bye!</a>'
```

## Complex Example ##
This portlet does a lot of stuff to demonstrate what is possible. Note that you would under normal circumstances not generate the HTML in the class but instead dispatch to a template.
```
class JythonPortlet (Portlet):

    # The equvalent of GenericPortlet.render(RenderRequest, RenderResponse)
    # the request and reponse objects here provide pythonic syntax
    # view() is executed on every page request
    def view(self, request, response):
	if 'page' in request:
            # dispatch portlet request to a JSP file 
            # with the name from the page request parameter
            self.dispatch('/' + request['page'] + '.jsp', request, response)

        elif 'name' in request:
            # use the name request parameter to greet the user
            response.write ( '<p>Hello ' + request['name'] + '</p>')
            # response.view() generates URL to the view() method of the portlet
            # Request parameters can be added if needed with keyword args (see next block)
            # you can also use print
	    print >>response, '<a href="' 
            print >>response, response.view() 
            print >>response, '">Bye!</a>'

	else:
            # The << operator sends to the OutputStream while write sends to the Writer
            response << '<h2>Hello Jython</h2>'
            # Pass request parameters to the view method generates the appropriate URL
            # action() generates URL to the action method, parameters can be added with
            # keyword args (or in the form input elements). Action is executed before
            # view() and only if the request comes from an action URL
            response << '<a href="' << response.view(page='index') << '">Index</a>' \
             '<form action="' << response.action() << '">' <<  """
               <input type="text" name="name"/>
               <input type="submit" />
              </form> """ 
	    response << '<a href="' << response.action(name='Vidar') << '">I am Vidar!</a>'

            # Sessions live between requests
            if 'previous' in request.session:
    	        response << '<p>' << request.session['previous'] << ' was here.</p>'

    # The equivalent of GenericPortlet.processAction(ActionRequest, ActionResponse).
    # The request and response objects here provide more pythonic syntax.
    # This method is only executed if the request comse from an action URL, mostly from 
    # forms. Use response.action(**params) in the view() method to generate action URLs.
    def action(self, request, response):
        # Access action parameters, e.g. from forms.
        name = request['name']
        # Make name available to the request object in view()
        response['name'] = name

        # Add the name to session
	request.session['previous'] = name
```