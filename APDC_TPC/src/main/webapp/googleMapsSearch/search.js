// This example adds a search box to a map, using the Google Place Autocomplete
// feature. People can enter geographical searches. The search box will return a
// pick list containing a mix of places and predicted search terms.
// This example requires the Places library. Include the libraries=places
// parameter when you first load the API. For example:
// <script src="https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY&libraries=places">
let lat= 41.85;
let long=-87.65;

function initLAtLong() {
  navigator.geolocation.getCurrentPosition(function(position) {
    lat = position.coords.latitude;
    long = position.coords.longitude;
    console.log(lat);
    console.log(long);
  });
}
function initAutocomplete() {
    initLAtLong();
    const map = new google.maps.Map(document.getElementById("map"), {
      center: { lat:lat, lng: long },
      zoom: 16,
      mapTypeId: "roadmap",
    });
    // Create the search box and link it to the UI element.
    function handleSearchInput(input) {
      const searchBox = new google.maps.places.SearchBox(input);
      map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

      // Bias the SearchBox results towards current map's viewport.
      map.addListener("bounds_changed", () => {
        searchBox.setBounds(map.getBounds());
      });
      let markers = [];
      // Listen for the event fired when the user selects a prediction and retrieve
      // more details for that place.
      searchBox.addListener("places_changed", () => {
        const places = searchBox.getPlaces();
    
        if (places.length == 0) {
          return;
        }
        // Clear out the old markers.
        markers.forEach((marker) => {
          marker.setMap(null);
        });
        markers = [];
        // For each place, get the icon, name and location.
        const bounds = new google.maps.LatLngBounds();
        places.forEach((place) => {
          if (!place.geometry || !place.geometry.location) {
            console.log("Returned place contains no geometry");
            return;
          }
          const icon = {
            url: place.icon,
            size: new google.maps.Size(71, 71),
            origin: new google.maps.Point(0, 0),
            anchor: new google.maps.Point(17, 34),
            scaledSize: new google.maps.Size(25, 25),
          };
          // Create a marker for each place.
          markers.push(
            new google.maps.Marker({
              map,
              icon,
              title: place.name,
              position: place.geometry.location,
            })
          );
    
          if (place.geometry.viewport) {
            // Only geocodes have viewport.
            bounds.union(place.geometry.viewport);
          } else {
            bounds.extend(place.geometry.location);
          }
          console.log(place);
          //let loc = new google.maps.LatLng(place.geometry.location.lat(),place.geometry.location.lng());
          let loc = { lat: place.geometry.location.lat(), lng: place.geometry.location.lng() };
          coords.push(loc);
          PopulateMap(map);
        });
        map.fitBounds(bounds);
      });
    }
    const input = document.getElementById("pac-input");
    const input2 = document.getElementById("pac-input2");

    handleSearchInput(input);
    handleSearchInput(input2);
  }
  let coords=[];
  function PopulateMap(map) {    
    // Build polyline with gps coordinates
    let track = new google.maps.Polyline(
      { path: coords,    geodesic: true, strokeColor: "#FF0000", strokeOpacity: 0.6, strokeWeight: 8 }
    );
    
    // Associate track with the map
    track.setMap(map);
  }
  
  // Functions to compute distance between two points on earth's surface
Rad = function(x) {return x*Math.PI/180;}

DistHaversine = function(p1, p2) {
  var R = 6371; // earth's mean radius in km
  var dLat  = Rad(p2.lat() - p1.lat());
  var dLong = Rad(p2.lon() - p1.lon());

  var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
          Math.cos(Rad(p1.lat())) * Math.cos(Rad(p2.lat())) * Math.sin(dLong/2) * Math.sin(dLong/2);
  var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
  var d = R * c;

  return d;
}