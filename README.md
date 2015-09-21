# DepartureTimes

<h1>
Providing Bus Departure Times in near Real-Time
</h1>

<p>
The purpose of this project was to design a service that gives real-time departure times for public transportation. 
I implemented this service using the freely available data on Nextbus. Here, I overview some of the major 
implementation aspects and design choices I made for the project.
The implemented service provides two types of information and interactions between the user and the server.
In the first one, the user can view the nearest bus stops to his specified location on the map.
The next service, provides the user with the realtime departure (arrival) times of a bus stop, once the user specifies any bus stop 
on the map.
The service currently works for all the bus stops in San Francisco area (identified by sf-muni in the XML files).
I deployed the service to Google App Engine, and the URL is provided below:
http://1-dot-static-anchor-107517.appspot.com/
</p>

<p>
In designing these services, one of the main goals was to make the frontend interface intuitive and simple for the user, while providing
efficiency and fast response times in the backend, in order to make it more practical than using the raw XML-based API.

For the UI visualization, I used Google Maps Javascript API which makes it easy to customize and view information on maps. Using this API, the location
of the user can also be retrieved and loaded, once the web page is launched. 

The implementation of the frontend is done in Javascript, while I used Java for the backend. I first describe the frontend, and then go into the details of the backend
implementation.
</p>


<h2>
Frontend
</h2>

<p>
In the frontend, the map is loaded using Google Maps API as was mentioned earlier. Once loaded, the user can specify their desired location by dragging the marker, and once the
marker is placed at a new location, the new bus stops near that location are loaded and displayed. Upon clicking on one of these stops, the detailed information about that stop and its
associated route as well
as departure (or arrival) times are displayed. Every click on one of the stops (it can be the same stop as well), will show the current up-to-date prediction of next departure times for
that stop. The data for this queries are provided through communication done through jQuery with the backend Servlets. 
</p>

<h2>
Backend
</h2>
<p>
The backend side consists of Java Servlets code to service requests from the web interface, as well the core implementation of the data structures and caching used to maintain
the bus stops data. There are two Servlets: BusStops for finding the nearest stops to a given location coordinate, and Departures for serving the list of departure (arrival) times
for a given bus stop.
package com.kvon.departures.server.svcode has the code responsible for fetching and maintaining a cache of stop information. Here is the details of how the caching works:
At the bootstrap, all the stop names are loaded and stored in a cache. Along with this information, the stop location coordinates are also stored in a separate data structure.
Then, according to the spatial span of all these stops (their minimum and maximum latitude and longitude coordinates in the space), the map is partitioned into a set of quadrants, and
for each quadrants the set of stops belonging to that quadrant is calculated. This partitioning enables significant reduction of computation time in finding the nearest stops to a given
location.
</p>
<p>
To find this set of nearest stops, first the quadrant where the bus stops lies is identified in constant time. Then, the set of stops within that quadrant is added to the list 
of nearest stops sorted according to their distances to the given location. If the number of these stops doesn't satisfy our maximum desired set of stops (20 stops in the implementation) 
which we are looking for,
the neighbouring quadrants are also searched for their stops. At the end of this search, we have a list of stops of a given size, where another pruning is performed to return the list
of stops that lie within some radius of the given location (500 meters in the implementation).
By doing so, the list of nearest stops is restricted to a much smaller set of nodes, as opposed to searching the entire
space of bus stops; also the large delay of loading the data from the Nextbus server every single time is prevented
by the caching mechanism.
</p>
<p>
The next aspect of caching is with regards to returning the departure time of the bus stops in near real time. This design was also as critical in avoiding unnecessary overhead
of accessing the timing information from the Nextbus API. To this end, I implemented a map of Stop keys (identified by their associated routeTag and stopTag) to the list 
of prediction times as well as other necessary information (such as the direction of the bus) obtained from the server. 
Once this data is loaded from the server, any given request for the timing of next departure times can be calculated based on the
epoch time stored for the next arrival and the current clock time. The cached data only needs to be updated either when the information about a bus stop does not exist, or
when the request time is greater than the earliest departure time of the bus, in which case the Nextbus web service is called.
This design keeps these queries at a near minimal amount, which would greatly speed up the retrieval of the departure times.
</p>



<h1> Lessons from the Project and Future Goals </h1>
I really enjoyed working on this project, as I had to incorporate different aspects both in terms of frontend simplicity and backend 
efficiency. This also made me think of some of the interesting challenges that exist at Uber, in particular as it's related to finding
the closest available drivers to a user. 
I also thought about some of the possible future enhancements to my current implementation such as:

<ul>
<li>
Scaling the model to a larger set of cities (hopefully all the U.S. in near future!), and how the algorithm for searching 
the closest set of bus stops could be optimized. Things that come to my mind, was hierarchical partitioning of the space,
rather than a flat partitioning based on quadrants.
</li>
<li>
Giving the user the ability to choose the radius of circle in which the search is done. In the current implementation, the
radius and maximum number of bus stops returned, is hard-coded into the application. But it would be nice to design
a simple interface for user specifying (or possibly dragging the circle) for larger or smaller search areas.
</li>
</ul>
I have also attempted to provide a good amount of comments in the code, and I'd be happy to answer any questions regarding the design or implementation.
