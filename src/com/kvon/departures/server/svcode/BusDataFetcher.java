package com.kvon.departures.server.svcode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.rmi.UnmarshalException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;

import com.kvon.departures.client.clcode.PredictionsShortForm;
import com.kvon.departures.server.xml.PredictionsBody;
import com.kvon.departures.server.xml.RouteConfigBody;
import com.kvon.departures.server.xml.RouteListBody;


public class BusDataFetcher {
	
	
	Map<String, String> routes_list_cache;	// cache mapping route tags to their names for sf-muni
	
	Map<String, List<RouteConfigBody.Route.Stop> > stops_cache; // cache mapping the route tags to their associated list of stops
	
	List<List<Quadrant> > quadrant_list;	// 2d list holding quadrant division of map along lat and lon and their associated stops
	
	// the following is the cache mapping the stop identifiers (route_tag + "_" + stop_tag) toe their associated
	// list of predictions (that holds info such as arrival/departure times, direction title, etc.)
	Map<String, com.kvon.departures.client.clcode.Predictions> predictions_cache;
	
	private static final int NUM_QUADRANTS = 10;	// number of quadrants in each dimension to divide the map into
	
	double lat_min = Double.MAX_VALUE;
	double lon_min = Double.MAX_VALUE;
	double lat_max = -Double.MAX_VALUE;
	double lon_max = -Double.MAX_VALUE;
	
	public BusDataFetcher() throws MalformedURLException, IOException, JAXBException
	{
		routes_list_cache = new HashMap<String, String>();
		stops_cache = new HashMap<String, List<RouteConfigBody.Route.Stop> > ();
		predictions_cache = new HashMap<String, com.kvon.departures.client.clcode.Predictions>();
		loadListOfRoutes();
		loadListOfStops();
		buildQuadrants(NUM_QUADRANTS);
	}

	public List<List<Quadrant>> getQuadrantList()
	{
		return quadrant_list;
	}
	
	public void loadListOfRoutes() throws IOException, MalformedURLException, JAXBException, DataBindingException
	{
		URL route_list_URL = new URL("http://webservices.nextbus.com/service/publicXMLFeed?command=routeList&a=sf-muni");
		

		BufferedReader route_list_content = new BufferedReader(new InputStreamReader(route_list_URL.openStream()));

		RouteListBody route_list_xml = JAXB.unmarshal(route_list_content, RouteListBody.class);


		for( RouteListBody.Route next_route : route_list_xml.getRouteList() )
		{
			routes_list_cache.put(next_route.getTag(), next_route.getTitle());
		}

	 
	}
	
	public void loadListOfStops() throws MalformedURLException, IOException
	{

		for(String route_tag : routes_list_cache.keySet()  )
		{
			// loading the route config data associated to this route tag
			URL route_config_URL = new URL("http://webservices.nextbus.com/service/publicXMLFeed?command=routeConfig&a=sf-muni&r=" + route_tag);
			BufferedReader route_config_content = new BufferedReader(new InputStreamReader(route_config_URL.openStream()));
			
			RouteConfigBody route_config_xml = JAXB.unmarshal(route_config_content, RouteConfigBody.class);
		
			
			RouteConfigBody.Route route = route_config_xml.getRoute();

			stops_cache.put(route_tag,  route.getStops() );
			for( RouteConfigBody.Route.Stop stop : route.getStops() )
			{
				if ( stop.getLat() < lat_min )
					lat_min = stop.getLat();
				if ( stop.getLat() > lat_max )
					lat_max = stop.getLat();
				if (stop.getLon() < lon_min )
					lon_min = stop.getLon();
				if (stop.getLon() > lon_max )
					lon_max = stop.getLon();
				
			}
		}
		
	}
	
	// each stop is identifies by its route tag and the top tag
	public static class StopIdentifier
	{
		String routeTag;
		String stopTag;
		
		public StopIdentifier(String routeTag, String stopTag)
		{
			this.routeTag = routeTag;
			this.stopTag = stopTag;
		}
		
		public String getRouteTag()
		{
			return routeTag;
		}
		
		public String getStopTag()
		{
			return stopTag;
		}
	}
	public void loadListOfPredictions(List<StopIdentifier> stops) throws MalformedURLException, DataBindingException, IOException
	{
		if (stops.size() == 0)
			return;
		StringBuilder baseURL = new StringBuilder("http://webservices.nextbus.com/service/publicXMLFeed?command=predictionsForMultiStops&a=sf-muni&");
		baseURL.append("stops=" + stops.get(0).getRouteTag() );
		baseURL.append("%7C" + stops.get(0).getStopTag());
		
		for(int i = 1; i < stops.size(); i++ )
		{
			baseURL.append("&stops=" + stops.get(i).getRouteTag() );
			baseURL.append("%7C");
			baseURL.append(stops.get(i).getStopTag());
		}
		
		URL predictionsURL = new URL( baseURL.toString() );

			BufferedReader predictions_content = new BufferedReader(new InputStreamReader(predictionsURL.openStream()));
			PredictionsBody predictions_xml = JAXB.unmarshal(predictions_content, PredictionsBody.class);
			Date currentTimeStamp = new Date();
			
			for (com.kvon.departures.server.xml.PredictionsBody.Predictions predictions : predictions_xml.getPredictions())
			{
				com.kvon.departures.client.clcode.Predictions client_predictions = new com.kvon.departures.client.clcode.Predictions();
				client_predictions.setRouteTag(predictions.getRouteTag());
				client_predictions.setStopTag(predictions.getStopTag());
				client_predictions.setAgencyTitle(predictions.getAgencyTitle());
				client_predictions.setRouteTitle(predictions.getRouteTitle());
				client_predictions.setStopTitle(predictions.getStopTitle());
				if (predictions.getDirection() == null)
					continue;
				client_predictions.setDirection(predictions.getDirection().getTitle());
				client_predictions.setStoreTimeStamp(currentTimeStamp);
				
				for(com.kvon.departures.server.xml.PredictionsBody.Predictions.Direction.Prediction prediction : predictions.getDirection().getPredictions())
				{
					com.kvon.departures.client.clcode.Predictions.Prediction client_prediction = new com.kvon.departures.client.clcode.Predictions.Prediction();
					client_prediction.setEpochTime(prediction.getEpochTime());
					client_prediction.setIsDeparture(prediction.getIsDeparture());
					client_prediction.setMinutes(prediction.getMinutes());
					client_prediction.setSeconds(prediction.getSeconds());
					client_predictions.getPredictions().add(client_prediction);
				}
				
				String prediction_hash_key = predictions.getRouteTag() + "_" + predictions.getStopTag();
				predictions_cache.put(prediction_hash_key, client_predictions);
			}

	}
	
	// This function is called for returning the set of predictions associated to a stop
	public com.kvon.departures.client.clcode.PredictionsShortForm findPredictionsForStop(StopIdentifier si) throws MalformedURLException, IOException
	{
		com.kvon.departures.client.clcode.PredictionsShortForm predictions_shortform = new PredictionsShortForm();
		
		String stop_key = si.getRouteTag() + "_" + si.getStopTag();
		// cur_time is used to check whether the departure times have expired
		// happens when the current time is greater than the earliest departure time
		long cur_time = System.currentTimeMillis();
		
		if ( !predictions_cache.containsKey(stop_key) ||
				predictions_cache.get(stop_key).getPredictions().size() == 0 || 
				predictions_cache.get(stop_key).getPredictions().get(0).getEpochTime() < cur_time )
		{
			// System.out.println("......Reading from XML server to update cache!......");
			// need to retrieve the predictions for this stop
			List<StopIdentifier> stops = new ArrayList<StopIdentifier>();
			stops.add(si);
			loadListOfPredictions(stops);
		}

		com.kvon.departures.client.clcode.Predictions predictions = predictions_cache.get(stop_key);
		if (predictions == null )
		{
			// there is an error loading the prediction; can happen when the bus service is not 
			// working (if it's night time), and no respective entries exist for this stop in the xml
			return predictions_shortform;
		}
		predictions_shortform.setAgencyTitle(predictions.getAgencyTitle());
		predictions_shortform.setDirection(predictions.getDirection());
		predictions_shortform.setRouteTag(predictions.getRouteTag());
		predictions_shortform.setRouteTitle(predictions.getRouteTitle());
		predictions_shortform.setStopTag(predictions.getStopTag());
		predictions_shortform.setStopTitle(predictions.getStopTitle());

		List<com.kvon.departures.client.clcode.Predictions.Prediction> prediction_list = predictions.getPredictions();

		for( com.kvon.departures.client.clcode.Predictions.Prediction pred : prediction_list )
		{
			long next_departure_millis = pred.getEpochTime() - cur_time;
			int next_departure_secs = (int) (next_departure_millis / 1000);
			int next_departure_mins = (int) (next_departure_millis / (1000 * 60));

			com.kvon.departures.client.clcode.PredictionsShortForm.Prediction next_pred = new com.kvon.departures.client.clcode.PredictionsShortForm.Prediction();
			
			next_pred.setSeconds(next_departure_secs);
			next_pred.setMinutes(next_departure_mins);
			next_pred.setIsDeparture(pred.getIsDeparture());

			predictions_shortform.getPredictions().add(next_pred);
		}
		return predictions_shortform;

	}
	public void printListOfPredictions()
	{
		for(Map.Entry<String , com.kvon.departures.client.clcode.Predictions> pred_entry : predictions_cache.entrySet() )
		{
			String key = pred_entry.getKey();
			com.kvon.departures.client.clcode.Predictions predictions = pred_entry.getValue();
			System.out.println("stored timestamp: " + predictions.getStoreTimeStamp().getTime());
			System.out.println("\npredictions agencyTime=" + predictions.getAgencyTitle() + 
					" routeTitle=" + predictions.getRouteTitle() + " routeTag=" + predictions.getRouteTag() +
					" stopTitle=" + predictions.getStopTitle() + " stopTag=" + predictions.getStopTag());
			System.out.println("direction title=" + predictions.getDirection());
			
			for( com.kvon.departures.client.clcode.Predictions.Prediction each_pred : predictions.getPredictions() )
			{
				System.out.println("prediction epochTime=" + each_pred.getEpochTime() +
						" seconds" + each_pred.getSeconds() + " minutes=" + each_pred.getMinutes() +
						" isDeparture=" + each_pred.getIsDeparture());
			}
		}
	}
	
	public void printListOfShortFormPredictions(PredictionsShortForm preds)
	{
		System.out.println("Printing predications for agencyTitle=" + preds.getAgencyTitle() +
				" routeTitle=" + preds.getRouteTitle() + " routeTag=" + preds.getRouteTag() +
				" stopTitle=" + preds.getStopTitle() + " stopTag=" + preds.getStopTag());
		
		for (PredictionsShortForm.Prediction pred : preds.getPredictions() )
		{
			System.out.println("---->prediction seconds=" + pred.getSeconds() + 
					" minutes=" + pred.getMinutes() + 
					" isDeparture=" + pred.getIsDeparture() );
		}
	}
	public void buildQuadrants(int num_divisions)
	{
		quadrant_list = new ArrayList<List<Quadrant> >();
		
		double lat_incr = (lat_max - lat_min) / num_divisions;
		double lon_incr = (lon_max - lon_min) / num_divisions;
		
		for(double lat_val = lat_min; lat_val < lat_max; lat_val += lat_incr )
		{
			List<Quadrant> row = new ArrayList<Quadrant>();

			for(double lon_val = lon_min; lon_val < lon_max; lon_val += lon_incr)
			{
				Quadrant q = new Quadrant();
				q.setLatMin(lat_val);
				q.setLatMax(lat_val + lat_incr);
				q.setLonMin(lon_val);
				q.setLonMax(lon_val + lon_incr);
				
				// now determine which stops lie within this quadrant
				for(String route_tag : stops_cache.keySet()  )
				{
					for( RouteConfigBody.Route.Stop stop : stops_cache.get(route_tag) )
					{
						if ( stop.getLat() >= lat_val && stop.getLat() <= (lat_val + lat_incr) &&
								stop.getLon() >= lon_val && stop.getLon() <= (lon_val + lon_incr) )
						{
							q.getStops().add(new Quadrant.StopInfo(route_tag, stop.getTag(), 
									stop.getLat(), stop.getLon()) );
						}
					}
				}
				row.add(q);				
			}
			quadrant_list.add(row);
		}
		
	}
	
	// given a loc latitude and longitude, this function returns the set of nearest stops 
	// to the point sorts within the output set; At most, a total of max_stops # stops is returned
	public List<Quadrant.StopInfo> findNearestStops(double loc_lat, double loc_lon, int max_stops)
	{
		Set<Quadrant.StopInfo> nearest_stops = new TreeSet<Quadrant.StopInfo>(
				new Quadrant.StopInfoComparator(loc_lat, loc_lon));
		
		List<Quadrant.StopInfo> nearest_list = new ArrayList<Quadrant.StopInfo>();
		
		if ( quadrant_list.size() == 0 || quadrant_list.get(0).size() == 0 )
			return nearest_list;
		// first locate the coordinates within the existing quadrants
		// Quadrant quadrant_containing_point = null;
		int found_row = -1;
		int found_col = -1;
		for( int i = 0; i < quadrant_list.size(); i++ )
		{
			List<Quadrant> quad_row = quadrant_list.get(i);
			if ( loc_lat < quad_row.get(0).getLatMin() || loc_lat > quad_row.get(0).getLatMax() )
			{
				continue;
			}
			for( int j = 0; j < quad_row.size(); j++ )
			{
				Quadrant q = quad_row.get(j);
				if ( loc_lon >= q.getLonMin() && loc_lon <= q.getLonMax() )
				{
					// found the quadrant in which this point is located!
					found_row = i;
					found_col = j;
					break;
				}
			}
			if (found_row != -1 )
			{
				break;
			}
		}
		
		if (found_row == -1 )
		{
			// point is not found within any of the quadrants
			return nearest_list;
		}
		
		
		Set<Integer> visited_quadrants = new HashSet<Integer>();
		// else return the sorted list of stops within this quadrant and possibly the neighboring quadrant
		findNearestStopsInQuadrants(nearest_stops, found_row, found_col, 0, visited_quadrants, max_stops);
		
		nearest_list.addAll(nearest_stops);
		
		if (nearest_list.size() > max_stops)
		{
			nearest_list = nearest_list.subList(0, max_stops);
		}
		
		return nearest_list;
	}
	
	private void findNearestStopsInQuadrants(Set<Quadrant.StopInfo> nearest_stops, int row, int col, 
			int search_radius, Set<Integer> visited_quadrants, int max_stops)
	{
		for(int i = row; i <= row + search_radius && i < quadrant_list.size(); i++)
		{
			for(int j = col; j <= col + search_radius && j < quadrant_list.get(i).size(); j++)
			{
				int index = i * quadrant_list.size() + j;
				if ( visited_quadrants.contains(index) )
					continue;
				visited_quadrants.add(index);
				// add the stops to the nearest_stops set
				nearest_stops.addAll(quadrant_list.get(i).get(j).getStops());
			}
		}
		if ( nearest_stops.size() < max_stops )
		{
			// increase the search radius to look into more quadrants for their stops
			if ( row > 0)
				row--;
			if (col > 0)
				col--;
			search_radius += 2;
			findNearestStopsInQuadrants(nearest_stops, row, col, search_radius, visited_quadrants, max_stops);
		}
	}
	public void printQuadrants()
	{
		for( List<Quadrant> row : quadrant_list )
		{
			for( Quadrant q : row )
			{
				System.out.println("quad: [" + q.lat_min + ", " + q.lon_min + ", " + 
						q.lat_max + ", " + q.lon_max + "]");
				System.out.println("list of stops:");
				for (Quadrant.StopInfo stop_info : q.getStops() )
				{
					System.out.println("rt: " + stop_info.getRouteTag() + ", st: " + stop_info.getStopTag() + 
							", lat: " + stop_info.getLat() + ", lon: " + stop_info.getLon());
				}
			}
		}
	}

}
