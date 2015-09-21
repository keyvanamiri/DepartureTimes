package com.kvon.departures.server.svcode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// This map is divided into quadrants according to the min and max coordinate of the stops
// This makes life a lot easier for finding the nearest bus stops to any given point on the map
public class Quadrant {

	// the first 2 is the coordinate of the lower left corner of the quadrant
	double lat_min;
	double lon_min;
	// the next two is the coordinates of the upper right corner of the quadrant
	double lat_max;
	double lon_max;
	
	List<StopInfo > stops;
	
	public Quadrant()
	{
		stops = new ArrayList<StopInfo>();
	}
	

	
	public double getLatMin() {
		return lat_min;
	}

	public void setLatMin(double lat_min) {
		this.lat_min = lat_min;
	}



	public double getLonMin() {
		return lon_min;
	}

	public void setLonMin(double lon_min) {
		this.lon_min = lon_min;
	}

	public double getLatMax() {
		return lat_max;
	}

	public void setLatMax(double lat_max) {
		this.lat_max = lat_max;
	}

	public double getLonMax() {
		return lon_max;
	}

	public void setLonMax(double lon_max) {
		this.lon_max = lon_max;
	}

	public List<StopInfo> getStops() {
		return stops;
	}


	public void setStops(List<StopInfo> stops) {
		this.stops = stops;
	}

	// class defining the stop information stored for each stop within this quadrant
	public static class StopInfo
	{
		String route_tag;
		String stop_tag;
		double lat;
		double lon;
		
		public StopInfo(String route_tag, String stop_tag, double lat, double lon)
		{
			this.route_tag = route_tag;
			this.stop_tag = stop_tag;
			this.lat = lat;
			this.lon = lon;
		}
		
		public String getRouteTag()
		{
			return route_tag;
		}
		public String getStopTag()
		{
			return stop_tag;
		}
		public double getLat()
		{
			return lat;
		}
		public double getLon()
		{
			return lon;
		}
		
		// calculates the distance between two points (given latitude/longitude of those points)
		// ref: http://www.geodatasource.com/developers/javascript 
	   //    where unit is :  'M' is statute miles (default)              
		// 					   'm' is meters
	   //:::                  'K' is kilometers                                     
	    //:::                 'N' is nautical miles   
		public double distanceFrom(double lat2, double lon2, char unit)
		{
			double radlat1 = Math.PI * lat / 180;
			double radlat2 = Math.PI * lat2 / 180;
			double theta = lon - lon2;
			double radtheta = Math.PI * theta / 180;
			double dist = Math.sin(radlat1) * Math.sin(radlat2) + 
					Math.cos(radlat1) * Math.cos(radlat2) * Math.cos(radtheta);
			dist = Math.acos(dist);
			dist = dist * 180 / Math.PI;
			dist = dist * 60 * 1.1515;
			if (unit == 'm')
			{
				dist = dist * 1.609344 * 1000;
			}
			else if (unit == 'K') { 
				dist = dist * 1.609344;
			}
			else if (unit == 'N') { 
				dist = dist * 0.8684;
			}
			return dist;
		}
		
	}
	
	public static class StopInfoComparator implements Comparator<StopInfo>
	{

		double reference_lat, reference_lon;	// the distance is minimized w.r.t this reference stop 
		public StopInfoComparator(double reference_lat, double reference_lon)
		{
			this.reference_lat = reference_lat;
			this.reference_lon = reference_lon;
		}
		@Override
		public int compare(StopInfo arg0, StopInfo arg1) {
			
			double dist1 = arg0.distanceFrom(reference_lat, reference_lon, 'M');
			double dist2 = arg1.distanceFrom(reference_lat, reference_lon, 'M');
			
			if (dist1 > dist2)
				return 1;
			if (dist1 < dist2 )
				return -1;
			return 0;
		}
	}
}
