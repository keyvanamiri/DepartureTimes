package com.kvon.departures.server.xml;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement (name = "body")
public class PredictionsBody implements Serializable{

	@XmlElement
	List<PredictionsBody.Predictions> predictions;
	
	@XmlAttribute
	String copyright;
	
	public String getCopyright()
	{
		return copyright;
	}
	
		
	public List<PredictionsBody.Predictions> getPredictions() {
		return predictions;
	}



	public void setPredictions(List<PredictionsBody.Predictions> predictions) {
		this.predictions = predictions;
	}


	@XmlAccessorType(XmlAccessType.FIELD)
	 @XmlType(name = "", propOrder = {
		        "direction"
		    })
	public static class Predictions implements Serializable
	{
		@XmlElement (required = true)
		PredictionsBody.Predictions.Direction direction;
		
		@XmlAttribute
		String agencyTitle;
		
		@XmlAttribute
		String routeTitle;
		
		@XmlAttribute
		String routeTag;
		
		@XmlAttribute
		String stopTitle;
		
		@XmlAttribute
		String stopTag;
		
		public PredictionsBody.Predictions.Direction getDirection() {
			return direction;
		}

		public void setDirection(PredictionsBody.Predictions.Direction direction) {
			this.direction = direction;
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
			return stopTitle;
		}

		public void setStopTitle(String stopTitle) {
			this.stopTitle = stopTitle;
		}

		public String getStopTag() {
			return stopTag;
		}

		public void setStopTag(String stopTag) {
			this.stopTag = stopTag;
		}
		
		@XmlAccessorType(XmlAccessType.FIELD)
		public static class Direction implements Serializable
		{
			@XmlElement (name = "prediction")
			List<Prediction> predictions; 
			
			@XmlAttribute
			String title;

			public List<Prediction> getPredictions() {
				return predictions;
			}

			public void setPredictions(List<Prediction> predictions) {
				this.predictions = predictions;
			}

			public String getTitle() {
				return title;
			}

			public void setTitle(String title) {
				this.title = title;
			}
			
			@XmlAccessorType(XmlAccessType.FIELD)
			public static class Prediction implements Serializable
			{
				@XmlAttribute
				Long epochTime;
				
				@XmlAttribute
				Integer seconds;
				
				@XmlAttribute
				Integer minutes;
				
				@XmlAttribute
				Boolean isDeparture;
				
				@XmlAttribute
				String dirTag;

				public Long getEpochTime() {
					return epochTime;
				}

				public void setEpocheTime(Long epocheTime) {
					this.epochTime = epocheTime;
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

				public String getDirTag() {
					return dirTag;
				}

				public void setDirTag(String dirTag) {
					this.dirTag = dirTag;
				}
				
			}
		}
	}
	
}
