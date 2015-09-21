package com.kvon.departures.client.clcode;


import java.util.ArrayList;
import java.util.List;


// This class is used for returning the important information about the departure times and 
// direction from the server to the client. (It removes unnecessary fields that exist in "Predictions" class)
public class PredictionsShortForm {

	String agencyTitle;
	String routeTitle;
	String routeTag;
	String stopTag;
	String stopTitle;
	String direction;
	
	List <PredictionsShortForm.Prediction> predictions;
	
	public PredictionsShortForm()
	{
		predictions = new ArrayList<PredictionsShortForm.Prediction>();
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

	public String getStopTag() {
		return stopTag;
	}

	public void setStopTag(String stopTag) {
		this.stopTag = stopTag;
	}

	public String getStopTitle() {
		return stopTitle;
	}

	public void setStopTitle(String stopTitle) {
		this.stopTitle = stopTitle;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public List<PredictionsShortForm.Prediction> getPredictions() {
		return predictions;
	}

	public void setPredictions(List<PredictionsShortForm.Prediction> predictions) {
		this.predictions = predictions;
	}
	
	public static class Prediction
	{
		Integer seconds;

		Integer minutes;
		Boolean isDeparture;
		
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
}
