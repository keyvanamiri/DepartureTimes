package com.kvon.departures.server.svcode;


import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.bind.JAXBException;

// This class produces a singleton instance of class BusDataFetcher to be shared by
// various servlets
public class BusDataFetcherInstance {

	private static BusDataFetcherInstance globalInstance = null;
	private BusDataFetcher bdf;
	
	private BusDataFetcherInstance() throws MalformedURLException, IOException, JAXBException
	{
		bdf = new BusDataFetcher();
	}
	
	public static synchronized BusDataFetcherInstance getInstance() throws MalformedURLException, IOException, JAXBException
	{
		if (globalInstance == null)
			globalInstance = new BusDataFetcherInstance();
		return globalInstance;
	}
	
	public BusDataFetcher getBusDataFetcher()
	{
		return bdf;
	}
}
