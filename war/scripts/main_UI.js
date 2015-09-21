// The following is used as the default initial location (some location in SF)
// if the geolocation is not supported by the browser.
var DEFAULT_LATITUDE = 37.76;
var DEFAULT_LONGITUDE = -122.50;

var DEFAULT_INIT_POS = {
		lat: DEFAULT_LATITUDE,
		lng: DEFAULT_LONGITUDE
	}

var stops_map = {};		// the list of stops currently opened by the user;
					// maps stops key (routeTag + "_" + stopTag) to their corresponding stop object

var MAX_RADIUS = 500 + 10;	// the maximum distance (in meters) of bus stops from current location that are shown on the map
							// + 10 is to account for the possible width ob bus on the screen, when it's on the border!

// the common window used for displaying stops departures information;
// only one exists for all the stops
var stop_info_window = new google.maps.InfoWindow();	
function CurrentState(map)
{
	this.map = map;
	// this.info_window.setContent('Having fun!');

	var marker_image = new google.maps.MarkerImage('./images/smallperson.png',
			new google.maps.Size(50, 60),
			new google.maps.Point(0, 0),
			new google.maps.Point(25, 30));

	this.icon = new google.maps.Marker(
			{
				position: map.getCenter(),
				map: map,
				// icon: "./images/person.png",
				icon: marker_image,
				draggable: true
			});


	// this.info_window.open(map, this.icon);
	
	var that = this;
	var pos = this.icon.getPosition();
	// var pos2 = {lat: 29.720, lng: -95.39};
	// draw a circle of a given radius around the node to indicate the area where the stops are searched
	this.circle = new google.maps.Circle (
	{
		center : pos,
		fillColor: "#ff0000",
		map: map,
		radius: MAX_RADIUS,
		strokeColor: "#ff0000",
		strokeOpacity: 0.8,
		zIndex: -10
	}		
	);
	this.updatePosition();
	// add listetener to the current location icon so the circles are updated as the user drags the pointer
	// to a new location
	google.maps.event.addListener(this.icon, "dragend", function() {
		that.updatePosition();
		}
	);
}

CurrentState.prototype.updatePosition = function()
{
	var new_pos = this.icon.getPosition();
	this.circle.setCenter(new_pos);
	this.map.panTo(new_pos);
	this.populateStops();
}

CurrentState.prototype.populateStops = function()
{
	var new_pos = this.icon.getPosition();
	var that = this;
	
	$.ajax("/departuresgoogleapp/BusStops?lat=" + new_pos.lat() + "&lon=" + new_pos.lng(), {
		
		success: function(stops_list)
		{
			// first, I'll remove the existing bus stops, which correponded to the previous
			// marker position
			for(var stop_id in stops_map )
			{
				stops_map[stop_id].icon.setMap(null);
			}
			
			// then populate the map with the list of new stops
			for(var i = 0; i < stops_list.length; i++)
			{
				var stop_key = stops_list[i].route_tag + "_" + stops_list[i].stop_tag;
				stops_map[stop_key] = new BusStop(that.map, stops_list[i]);
			}

		},
		error: function()
		{
			console.log("Error sending request to BusStopsServlet!");
		}
	}
	);
}

function BusStop(map, stop_info)
{
	this.map = map;
	this.routeTag = stop_info.route_tag;
	this.stopTag = stop_info.stop_tag;
	this.lat = stop_info.lat;
	this.lon = stop_info.lon;
	var marker_image = new google.maps.MarkerImage('./images/smallshuttle.png',
			new google.maps.Size(50, 60),
			new google.maps.Point(0, 0),
			new google.maps.Point(25, 30));
	
	var stop_pos = {
			lat: stop_info.lat,
			lng: stop_info.lon
		}

	this.icon = new google.maps.Marker(
			{
				position: stop_pos,
				map: map,
				icon: marker_image,
				draggable: false
			});

	var that = this;
	google.maps.event.addListener(this.icon, 'click', function() {
		// on clicking a stop, the departure information about that stop is displayed
		that.displayDeparturesInfo();
	}
	);
}

BusStop.prototype.displayDeparturesInfo = function()
{
	var that = this;
	$.ajax("/departuresgoogleapp/Departures?routeTag=" + this.routeTag + "&stopTag=" + this.stopTag, {
		
		success : function(departures_info){
			var depart_content = "";
			if ( departures_info.predictions.length == 0 )
			{
				depart_content += "<p>This bus doesn't operate or its information does not exist!</p>";
				stop_info_window.setContent(depart_content);
				stop_info_window.open(that.map, that.icon);

			}
			else
			{ 
				depart_content = "<p>Route: " + departures_info.routeTitle + "</p>";
				depart_content += "<p>Stop: " + departures_info.stopTitle + "</p>";
				depart_content += "<p>Direction: " + departures_info.direction + "</p>";			
				for(var i = 0; i < departures_info.predictions.length; i++)
				{
					depart_content += "<p>";
					depart_content += (departures_info.predictions[i].isDeparture) ? "departing in " :
						"arriving in ";
					depart_content += departures_info.predictions[i].minutes + " mins</p>";
				}

				stop_info_window.setContent(depart_content);
				stop_info_window.open(that.map, that.icon);
			}
		},	
		
		error: function()
		{
			console.log("(", that.routeTag, ", ", that.stopTag, "):" + 
			"Error sending request to Departures servlet!");
		}
		
	}
	);
}
var current_state;
function initialize_map()
{

	var map = new google.maps.Map(document.getElementById('map-canvas'), {
		center: {lat: -34.397, lng: 150.644},
		zoom: 16
	});
	
	// for the stop_info_window, the behavior is on clocking outside on the map, the 
	/// bus stop content should be closed.
	google.maps.event.addListener(map, "click", function() {
		stop_info_window.close();
	}
	);
	

	// var info_window = new google.maps.InfoWindow({map: map});
// in firefox, there doesn't seem to be a way to distinguish between "not share now" for current location
	// from a successful current location call (the former doesn't trigger an error)
	var pos;
	if (navigator.geolocation )
	{
		navigator.geolocation.getCurrentPosition(function(position) {
			var pos = {
				lat: position.coords.latitude,
				lng: position.coords.longitude
			}
			// if (typeof DEFAULT_INIT_POS != 'undefined')
				// pos = DEFAULT_INIT_POS;
			map.setCenter(pos);
			current_state = new CurrentState(map);
		}, function(err) {
			pos = DEFAULT_INIT_POS;
			map.setCenter(pos);
			current_state = new CurrentState(map);
		}
		);
	}


}

function handleLocationError(browserHasGeolocation, info_window, pos )
{
	info_window.setPosition(pos);
	info_window.setContent(browserHasGeolocation ? 
			'Error: The Geolocation service failed.' :
				'Error: Your browser doesn\`t suppoer geolocation.' );
}

google.maps.event.addDomListener(window, "load", initialize_map);