"""
Copyright 2008 Vidar Svansson

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
"""

from javax import portlet

PORTLET_SCOPE = portlet.PortletSession.PORTLET_SCOPE
APPLICATION_SCOPE = portlet.PortletSession.APPLICATION_SCOPE

class Session(portlet.PortletSession):
    def __init__(self, request):
        self.__dict__['session'] = request.getPortletSession()

    def __getitem__(self, name):
        return self.session.getAttribute(name, PORTLET_SCOPE)

    def __setitem__(self, name, obj):
        self.session.setAttribute(name, obj, PORTLET_SCOPE)

    def __contains__(self, name):
        return bool(self.session.getAttribute(name, PORTLET_SCOPE))



    def __getattr__(self, attr):
        return getattr(self.session, attr)

    def __setattr__(self, attr, value):
        return setattr(self.session, attr, value)


class Portlet(portlet.GenericPortlet):

    def dispatch(self, path, request, response):
        dispatcher = self.portletContext.getRequestDispatcher(path)
        dispatcher.include(request.request, response.response)


    def render(self, request, response):
        self.view(RenderRequest(request), RenderResponse(response))

    def processAction(self, request, response):
	self.action(ActionRequest(request), ActionResponse(response))    

    def view(self, request, response):
        pass

    def action(self, request, response):
        pass

class Request:
    def __init__(self, request):
        self.__dict__['request'] = request
	self.__dict__['session'] = Session(request)

    def __getattr__(self, name):
        if hasattr(self.request.__class__, name):
	    return getattr(self.request, name)
        return self.request.getAttribute(name)

    def __setattr__(self, name, value):
        self.request.setAttribute(name, value)

    def __getitem__(self, name):
        return self.request.getParameter(name)

    def __contains__(self, name):
        return bool(self.request.getParameter(name))

class RenderRequest(Request, portlet.RenderRequest):
    pass

class ActionRequest(Request, portlet.ActionRequest):
    pass

class ActionResponse(portlet.ActionResponse):
    def __init__(self, response):
        self.__dict__['response'] = response

    def __setattr__(self, name, value):
        self.response.setRenderParameter(name, value)



class RenderResponse(portlet.RenderResponse):
    def __init__(self, response):
        self.response = response
	

    def write(self, obj):
        self.response.writer.append(obj)

    def __lshift__(self, obj):
        print >>self.response.portletOutputStream, obj
        return self

    def addUrlParams(self, url, params):
        for (key, value) in params.iteritems():
            url.setParameter(key, value)
        return url

    def view(self, **params):
        url = self.response.createRenderURL()
	return addUrlParams(url, params)

    def action(self, **params):
        url = self.response.createActionURL()
       	return addUrlParams(url, params)

def addUrlParams(url, params):
    for (key, value) in params.iteritems():
        url.setParameter(key, value)
    return url
