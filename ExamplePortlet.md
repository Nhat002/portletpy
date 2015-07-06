Here is an example of a simple portlet using `portlet.py`

# Log Messages Portlet #
This portlet stores messages in session and writes them out in a list.

```
from portlet import Portlet
from time import gmtime, strftime

class LogPortlet (Portlet):
    def view(self, request, response):
        logs = request.session['logs']
        if logs is None:
            logs = []
            request.session['logs'] = logs
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
```