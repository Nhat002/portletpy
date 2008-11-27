from javax.portlet import GenericPortlet
from portlet import Portlet

class ExampleUse(Portlet):
    def render(self, req, res):
        res << "<p>Hello World, how are we?</p>"
        counter = req.session['counter']
        url = res.action(name='python')
        

    def action(self, req, res):
        name = req['name']


        


class HelloPortlet(GenericPortlet):
    def render(self, req, res):
       res.writer.append("<p>Hello World, how are we?</p>")
       return

class JythonJspPortlet (GenericPortlet):
	def init(self, config):
		self.index = config.portletContext.getRequestDispatcher('/index.jsp')

	def render(self, request, response):
		self.index.include(request, response)


from time import gmtime, strftime

class LogPortlet (Portlet):
    def view(self, request, response):
        logs = request.session['logs']
        if logs is None:
            logs = []
            request.session['logs'] = logs
            response << '<h2>Hello Jython!</h2>'
            response << '<p>You have no messages, add a new message below.</p>'
        response << '<ul>' 
        for log in logs:
            response << '<li>' << log << '</li>'
        response << '</ul>'

        response << '<form method="post" action="' << response.action() << '">' <<  """
                       <label>Message</label>
                       <input type="text" name="message" />
                       <input type="submit" value="Send" />
                    </form> """ 

    def action(self, request, response):
        message = request['message']
        date = strftime("%a, %d %b %Y %H:%M:%S", gmtime())
        request.session['logs'].append('[ ' + date + ' ] ' + message)



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


class JythonJspPortlet (GenericPortlet):

	def init(self, config):
		self.normalView = config.portletContext.getRequestDispatcher('/maximized.jsp')

	def render(self, request, response):
		self.normalView.include(request, response)	

