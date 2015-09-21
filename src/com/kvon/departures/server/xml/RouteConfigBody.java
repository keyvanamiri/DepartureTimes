package com.kvon.departures.server.xml;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "body")
public class RouteConfigBody implements Serializable{

	@XmlElement(name = "route")
	RouteConfigBody.Route route;
	
	public RouteConfigBody.Route getRoute()
	{
		return route;
	}
	
	@XmlAttribute 
	String copyright;
	
	public String getCopyright()
	{
		return copyright;
	}
	
	public static class Route implements Serializable
	{
		@XmlElement(name = "stop")
		List<RouteConfigBody.Route.Stop> stops;
		
		public List<RouteConfigBody.Route.Stop> getStops()
		{
			return stops;
		}
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
		
		@XmlAttribute
		String color;
		public String getColor()
		{
			return color;
		}
		
		@XmlAttribute
		String oppositeColor;
		public String getOppositeColor()
		{
			return oppositeColor;
		}
		
		@XmlAttribute
		Double latMin;
		public Double getLatMin()
		{
			return latMin;
		}
		
		@XmlAttribute
		Double latMax;
		public Double getLatMax()
		{
			return latMax;
		}
		
		@XmlAttribute
		Double lonMin;
		public Double getLonMin()
		{
			return lonMin;
		}
		
		@XmlAttribute
		Double lonMax;
		public Double getLonMax()
		{
			return lonMax;
		}
		
		public static class Stop implements Serializable
		{
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
			
			@XmlAttribute
			Double lat;
			public Double getLat()
			{
				return lat;
			}
			
			@XmlAttribute
			Double lon;
			public Double getLon()
			{
				return lon;
			}
			
			@XmlAttribute
			Integer stopId;
			public Integer getStopId()
			{
				return stopId;
			}			
			
		}
	}
}
