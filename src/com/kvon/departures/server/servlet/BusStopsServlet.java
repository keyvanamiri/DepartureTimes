package com.kvon.departures.server.servlet;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;


import com.google.gson.Gson;
import com.kvon.departures.server.svcode.BusDataFetcher;
import com.kvon.departures.server.svcode.BusDataFetcherInstance;
import com.kvon.departures.server.svcode.Quadrant;



/**
 * Servlet implementation class BusStopsServlet
 */
public class BusStopsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final double MAX_RADIUS = 500;	// the maximum radius (in meters) of bus stations from current location
													// in order to be shown on the map
	private static final int MAX_STOPS = 20;		// the maximum @ stops returned in the neighborhood
	private BusDataFetcher bdf;
	
	private static final String LAT_PARAM_NAME = "lat";
	private static final String LON_PARAM_NAME = "lon";
	
	private Gson gson;
       
    @Override
	public void init(ServletConfig config) throws ServletException {

		super.init(config);
		System.out.println("Initializing the BusStopsServlet!");
		try {
			bdf = BusDataFetcherInstance.getInstance().getBusDataFetcher();
		} catch (IOException e) {
		} catch (JAXBException e) {

		}
	}

	/**
     * @see HttpServlet#HttpServlet()
     */
    public BusStopsServlet() {
        super();
        gson = new Gson();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		

		String latitude = request.getParameter(LAT_PARAM_NAME);
		String longitude = request.getParameter(LON_PARAM_NAME);
		
		if (!verifyArguments(latitude, longitude) )
		{
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		double loc_lat = Double.parseDouble(latitude);
		double loc_lon = Double.parseDouble(longitude);
		List<Quadrant.StopInfo> nearest_stops = bdf.findNearestStops(loc_lat, loc_lon, MAX_STOPS);
		
		/*
		for( Quadrant.StopInfo next_stop : nearest_stops )
		{
			System.out.println("----> rt:" + next_stop.getRouteTag() + ", st: " + next_stop.getStopTag() +
					", lat: " + next_stop.getLat() + ", lon: " + next_stop.getLon() );
		}
		*/

		// prune the returned nearest_stops until all distances are less than MAX_RADIUS
		for(int i = nearest_stops.size() - 1; i >= 0; i--)
		{
			double dist = nearest_stops.get(i).distanceFrom(loc_lat, loc_lon, 'm');
			if (dist > MAX_RADIUS)
				nearest_stops.remove(i);
			else
				break;
		}
		// java.lang.reflect.Type listOfStopInfo = new TypeToken<List<Quadrant.StopInfo>>(){}.getType();
		String nearest_stops_serialized = gson.toJson(nearest_stops);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter print_writer = response.getWriter();
		print_writer.write(nearest_stops_serialized);
		print_writer.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	private boolean verifyArguments(String lat, String lon)
	{
		// check if both represent valid numbers
		try{
			Double.parseDouble(lat);
			Double.parseDouble(lon);
		}
		catch (NumberFormatException e )
		{
			return false;
		}
		catch (NullPointerException e)
		{
			return false;
		}
		return true;
	}
}
