package com.kvon.departures.server.servlet;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXBException;

import com.google.gson.Gson;
import com.kvon.departures.client.clcode.PredictionsShortForm;
import com.kvon.departures.server.svcode.BusDataFetcher;
import com.kvon.departures.server.svcode.BusDataFetcherInstance;

;

/**
 * Servlet implementation class DeparturesServlet
 */
public class DeparturesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private BusDataFetcher bdf;
	
	private static final String ROUTE_TAG_PARAM_NAME = "routeTag";
	private static final String STOP_TAG_PARAM_NAME = "stopTag";
	
	private Gson gson;
	
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		System.out.println("Initializing the DeparturesServlet!");
		try {
			bdf = BusDataFetcherInstance.getInstance().getBusDataFetcher();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		catch (DataBindingException ex)
		{
			
		} catch (JAXBException e) {

		}
	}

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeparturesServlet() {
        super();
        gson = new Gson();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String routeTag = request.getParameter(ROUTE_TAG_PARAM_NAME);
		String stopTag = request.getParameter(STOP_TAG_PARAM_NAME);
		
		if ( !verifyArguments(routeTag, stopTag) || bdf == null)
		{
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		PredictionsShortForm preds = bdf.findPredictionsForStop(new BusDataFetcher.StopIdentifier(routeTag, stopTag));
		

		String preds_serialized = gson.toJson(preds);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter print_writer = response.getWriter();
		print_writer.write( preds_serialized );
		print_writer.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	private boolean verifyArguments(String routeTag, String stopTag)
	{
		if (routeTag == null || stopTag == null)
			return false;
		return true;
	}
	

}
