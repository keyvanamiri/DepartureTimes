package com.kvon.departures.client.clcode;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

// This class holds a stop identifier as well as a list of prediction times and the direction info
// This class is used as the value type in the prediction_cache
public class Predictions {

	String agencyTitle;
	String routeTitle;
	String routeTag;
	String StopTitle;
	String stopTag;
	String direction;
	
	Date storeTimeStamp;	// the time when this predictions entry is stored in the cache; this is critical in
							// keeping track of when a new cache update is necessary
	
	public Date getStoreTimeStamp() {
		return storeTimeStamp;
	}
	public void setStoreTimeStamp(Date storeTimeStamp) {
		this.storeTimeStamp = storeTimeStamp;
	}

	// Set<Prediction> predictions;
	List <Prediction> predictions;
	
	public Predictions()
	{
		// elements of predictions are sorted according to their departure time
		// predictions = new TreeSet<Prediction>(new PredictionComparator()); 
		// elements are already read and stored sorted; hence no need for TreeSet!
		predictions = new ArrayList<Predictions.Prediction>();
	}
	public String getAgencyTitle() {
		return agencyTitle;
	}

	public void setAgencyTitle(String agencyTitle) {
		this.agencyTitle = agencyTitle;
	}

	public String getRouteTitle() {
		return routeTitle;
	}

	public void setRouteTitle(String routeTitle) {
		this.routeTitle = routeTitle;
	}

	public String getRouteTag() {
		return routeTag;
	}

	public void setRouteTag(String routeTag) {
		this.routeTag = routeTag;
	}

	public String getStopTitle() {
		return StopTitle;
	}

	public void setStopTitle(String stopTitle) {
		StopTitle = stopTitle;
	}

	public String getStopTag() {
		return stopTag;
	}

	public void setStopTag(String stopTag) {
		this.stopTag = stopTag;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public List<Prediction> getPredictions() {
		return predictions;
	}

	public void setPredictions(List<Prediction> predictions) {
		this.predictions = predictions;
	}
	
	public static class Prediction
	{
		Long epochTime;
		Integer seconds;
		Integer minutes;
		Boolean isDeparture;
		
		public Long getEpochTime() {
			return epochTime;
		}
		public void setEpochTime(Long epochTime) {
			this.epochTime = epochTime;
		}
		public Integer getSeconds() {
			return seconds;
		}
		public void setSeconds(Integer seconds) {
			this.seconds = seconds;
		}
		public Integer getMinutes() {
			return minutes;
		}
		public void setMinutes(Integer minutes) {
			this.minutes = minutes;
		}
		public Boolean getIsDeparture() {
			return isDeparture;
		}
		public void setIsDeparture(Boolean isDeparture) {
			this.isDeparture = isDeparture;
		}
		
	}
	
	public static class PredictionComparator implements Comparator<Prediction>
	{

		@Override
		public int compare(Prediction arg0, Prediction arg1) {
			
			return (arg0.seconds - arg1.seconds);
		}
		
	}
	
}
