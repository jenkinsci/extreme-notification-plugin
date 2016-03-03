package org.jenkinsci.plugins.extremenotification;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import hudson.DescriptorExtensionList;
import hudson.ExtensionPoint;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.ListBoxModel;

import java.util.HashMap;
import java.util.Map;

import jenkins.model.Jenkins;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.codehaus.groovy.runtime.GStringImpl;
import org.jenkinsci.plugins.extremenotification.MyPlugin.Event;
import org.kohsuke.stapler.StaplerRequest;

import com.google.common.collect.Maps;

public abstract class NotificationEndpoint extends AbstractDescribableImpl<NotificationEndpoint> implements ExtensionPoint {
	
	public static DescriptorExtensionList<NotificationEndpoint, Descriptor<NotificationEndpoint>> all() {
        return Jenkins.getInstance().getDescriptorList(NotificationEndpoint.class);
    }
	
	public abstract void notify(Event event);
	
	public abstract void notify(Event event, EndpointEvent endpointEvent);
	
	private Map<String, EndpointEvent> events = Maps.newHashMap();
	
	public Map<String, EndpointEvent> getEvents() {
		return events;
	}
	
	protected String interpolate(String value, Event event) {
		return interpolate(value, event, new HashMap<String, Object>());
	}
	
	protected String interpolate(String value, Event event, Map<String, Object> extraArgs) {
		final Map<String, Object> args = Maps.newHashMap(event.getArgs());
		args.putAll(extraArgs);
		final GroovyShell shell = new GroovyShell(new Binding(args));
		return shell.evaluate('"' + value + '"').toString();
	}
	
	public static abstract class DescriptorImpl extends Descriptor<NotificationEndpoint> {
		
        public ListBoxModel doFillEndpointsItems() {
        	final ListBoxModel listBoxModel = new ListBoxModel();
        	for (String endpoint : MyPlugin.ENDPOINTS) {
        		listBoxModel.add(endpoint);
        	}
			return listBoxModel;
        }
        
        @Override
        public NotificationEndpoint newInstance(StaplerRequest req, JSONObject formData) throws hudson.model.Descriptor.FormException {
        	final NotificationEndpoint instance = (NotificationEndpoint) super.newInstance(req, formData);
        	
        	final JSONObject events = formData.getJSONObject("events");
        	if (!events.isNullObject()) {
	        	final JSONArray eventArray;
	        	if (events.get("event") instanceof JSONArray) {
	        		eventArray = events.getJSONArray("event");
	        	} else {
	        		eventArray = new JSONArray();
	        		eventArray.add(events.getJSONObject("event"));
	        	}
	        	for (Object event : eventArray) {
	        		final String endpoint = ((JSONObject)event).getString("endpoint");
	        		final EndpointEventCustom custom = parseCustom(((JSONObject)event));
	        		instance.getEvents().put(endpoint, new EndpointEvent(custom));
	        	}
        	}
			return instance;
        }
        
        protected EndpointEventCustom parseCustom(JSONObject event) {
        	return null;
        }
        
    }
	
	public static interface EndpointEventCustom {
		
	}
	
	public static class EndpointEvent {
		private final EndpointEventCustom custom;
		public EndpointEvent(EndpointEventCustom custom) {
			this.custom = custom;
		}
		public EndpointEventCustom getCustom() {
			return custom;
		}
	}
	
}
