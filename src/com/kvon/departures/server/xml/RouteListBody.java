package com.kvon.departures.server.xml;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "body")
public class RouteListBody implements Serializable {
	
	@XmlElement(name = "route")
	List<RouteListBody.Route> route_list;
	

	public List<RouteListBody.Route> getRouteList()
	{
		return route_list;
	}
	
	@XmlAttribute
	String copyright;
	public String getCopyright()
	{
		return copyright;
	}

	public static class Route implements Serializable {
		
		@XmlAttribute
		String tag;
		public String getTag()
		{
			return tag;
		}
		@XmlAttribute
		String title;
		public String getTitle()
		{
			return title;
		}
	}
	
}
