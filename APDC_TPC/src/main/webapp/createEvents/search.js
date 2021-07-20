// This example adds a search box to a map, using the Google Place Autocomplete
// feature. People can enter geographical searches. The search box will return a
// pick list containing a mix of places and predicted search terms.
// This example requires the Places library. Include the libraries=places
// parameter when you first load the API. For example:
// <script src="https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY&libraries=places">
/*let   image = {
  //url: "https://developers.google.com/maps/documentation/javascript/examples/full/images/parking_lot_maps.png",
  url:"http://localhost:8080/imgs/app_images/logoAtivo%201mdpi.png",
  // This marker is 20 pixels wide by 32 pixels high.
  size: new google.maps.Size(25, 25),
  // The origin for this image is (0, 0).
  origin: new google.maps.Point(0, 0),
  // The anchor for this image is the base of the flagpole at (0, 32).
  anchor: new google.maps.Point(0, 32),
}; */
let destination=null;
let origin=null;
let markers1 = [];
const MAP_ZOOM=15;
const fields =["place_id","plus_code"];
function clearMarkers() {
  // Clear out the old markers.
  markers1.forEach((marker) => {
    marker.setMap(null);
  });
  markers1 = [];
}

let map;
function initAutocomplete() {
  map = new google.maps.Map(document.getElementById("map"), {
    center: { lat: -33.8688, lng: 151.2195 },
    MAP_ZOOM: MAP_ZOOM,
    mapTypeId: "roadmap",
  });

  navigator.geolocation.getCurrentPosition(function(position) {
    if(map){
      let lat=position.coords.latitude;
      let lng=position.coords.longitude;
        
      origin={lat:lat, lng: lng};
      map.setCenter(origin);
      geocodeLatLng(map,lat,lng);


     /* const image = {
        url: "https://storage.googleapis.com/profile_pics46335560256500/5715241090416640"
        ,
        // This marker is 20 pixels wide by 32 pixels high.
        size: new google.maps.Size(50, 50),
        // The origin for this image is (0, 0).
        origin: new google.maps.Point(0, 0),
        // The anchor for this image is the base of the flagpole at (0, 32).
        anchor: new google.maps.Point(0, 32),
      }; */
      /*
      new google.maps.Marker({
        //icon:image,
        title: "I AM HERE",
        position: new google.maps.LatLng(lat,lng),
        map: map
      });*/
    }
  });
  // Create the search box and link it to the UI element.
  const input2 = document.getElementById("pac-input2");
  const input = document.getElementById("pac-input");

  const searchBox = new google.maps.places.SearchBox(input);
  //searchBox.setFields(fields);
  const searchBox2 = new google.maps.places.SearchBox(input2);
  /*
  let mapZoom;
  let startLocation
  new google.maps.event.addListener(map, 'click', function(event) {
    mapZoom = map.getZoom();
    startLocation = event.latLng;
    //setTimeout(placeMarker, 600);
    placeMarker();
    console.log(startLocation.lat(), startLocation.lng());
    geocodeLatLng(map,startLocation.lat(),startLocation.lng()); 
  });

  function placeMarker() {
    let marker;
    if(mapZoom == map.getZoom()){
      marker =  new google.maps.Marker({position: startLocation, map: map});
      infowindow = new google.maps.InfoWindow({content:`<button>Do You Wish To Create An Event On This Location?</button>`,maxWidth:"800px"});
      infowindow.open(map, marker);
    }
  }
  **/

  map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

  // Bias the SearchBox results towards current map's viewport.
  map.addListener("bounds_changed", () => {
    searchBox.setBounds(map.getBounds());
  });

  // Listen for the event fired when the user selects a prediction and retrieve
  handleSearchBox(searchBox,searchEventParams);
  handleSearchBox(searchBox2,validPlace);
}
function handleSearchBox(searchBox,func){
  //searchBox.bindTo("bounds",map);
  searchBox.addListener("places_changed", () => {
    const places = searchBox.getPlaces();
    if (places.length == 0) {
      console.log("PLACES LENGTH "+places.length);
      return;
    }else{
      console.log("PLACES LENGTH "+places.length);
    }
    clearMarkers();
    // For each place, get the icon, name and location.
    const bounds = new google.maps.LatLngBounds();
    places.forEach((place) => {
      if (!place.geometry || !place.geometry.location) {
        console.log("Returned place contains no geometry");
        return;
      }
      console.log(place.geometry.location.lat() +","+ place.geometry.location.lng());
      const icon = {
        url: place.icon,
        size: new google.maps.Size(71, 71),
        origin: new google.maps.Point(0, 0),
        anchor: new google.maps.Point(17, 34),
        scaledSize: new google.maps.Size(25, 25),
      };
      // Create a marker for each place.
      markers1.push(
        new google.maps.Marker({
          map,
          //icon,
          title: getPlaceName(place),
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
      func(place);
    });
    map.fitBounds(bounds);
  });
}
function validPlace(place){
  let newDiv = stringToDom(`<div>${place.adr_address}</div>`);
  let postalCode=newDiv.querySelector(".postal-code");
  let locality=newDiv.querySelector(".locality");
  let countryName = newDiv.querySelector(".country-name");
  //let street_address = newDiv.querySelector(".street-address");
  console.log(newDiv);

  if(postalCode==undefined || locality==undefined || countryName==undefined){
    alert("The Address must have a street address, postal code, locality and a country name!");
    return;
  }

  let obj={
    loc:{ lat: place.geometry.location.lat(), lng: place.geometry.location.lng() },
    name:getPlaceName(place),
  };
  destination=obj;
}

function searchEventParams(place) {
  areaToSearch = {
    lat: place.geometry.location.lat(),
    lng: place.geometry.location.lng()
  }
  document.getElementById("vwmrpnt").click();
}
function getPlaceName(place){
  let name;
  if(place.formatted_address){
    name=place.formatted_address;
  }else{
    name=place.place;
  }
  return name;
}
  // Functions to compute distance between two points on earth's surface
Rad = function(x) {return x*Math.PI/180;}

DistHaversine = function(p1, p2) {
  var R = 6371; // earth's mean radius in km
  var dLat  = Rad(p2.lat - p1.lat);
  var dLong = Rad(p2.lng - p1.lng);

  var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
          Math.cos(Rad(p1.lat)) * Math.cos(Rad(p2.lat)) * Math.sin(dLong/2) * Math.sin(dLong/2);
  var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
  var d = R * c;

  return d;
}
const currentPoints = [];
function makeMarker(eventObj) {
  let loc = eventObj.loc;//JSON.parse(eventObj.loc);
  let distDiv = document.getElementById("edist");
  let marker =  new google.maps.Marker({
      title: "Event: "+eventObj.name,
      position: new google.maps.LatLng(loc.lat,loc.lng),
      map: map
  });
  marker.id=eventObj.eventId;
  currentPoints.push(marker);
  let clicked=false;
  
  let contentString = null;
  let infowindow = null;
  marker.addListener('click', function() {
    console.log("MARKER CLICKED "+clicked);
    if(clicked==false){
      console.log(" AHAHA");
      let path=`/rest/events/event/${marker.id}`;
      console.log("I AM PATHH "+path);
      fetch(path).then(response => {
        return response.json();
      }).then(event=>{
        if(event){
          clicked=true;
          contentString = makeShowInfoString(event,event.eventAddress,event.owner);
          infowindow = new google.maps.InfoWindow({content: contentString,maxWidth:"800px"});
          infowindow.open(map, marker);
          distDiv.textContent=DistHaversine(origin, { lat: loc.lat, lng: loc.lng });
        }
      }).catch(err=>{
        console.log("ERROR "+err);
      })
    }
    else{
      infowindow.open(map, marker);
      distDiv.textContent=DistHaversine(origin, { lat: loc.lat, lng: loc.lng });
    }
  });
}

function makeMarker2(eventObj) {
  let loc = eventObj.loc; //JSON.parse(eventObj.loc);
  let distDiv = document.getElementById("edist");
  
  let marker =  new google.maps.Marker({
      title: "Event: "+eventObj.name,
      position: new google.maps.LatLng(loc.lat,loc.lng),
      map: map
  });
  marker.id=eventObj.eventId;
  currentPoints.push(marker);
  let clicked=false;
  
  let contentString = makeShowInfoString(eventObj,eventObj.eventAddress,true);
  let infowindow = new google.maps.InfoWindow({content: contentString});
  marker.addListener('click', function() {
    infowindow.open(map, marker);
    distDiv.textContent=DistHaversine(origin, { lat: loc.lat, lng: loc.lng });
  });
}
let pps;
function geocodeLatLng(map,lat,lng) {
  const geocoder = new google.maps.Geocoder();
  const infowindow = new google.maps.InfoWindow();
  const latlng = {
    lat: parseFloat(lat),
    lng: parseFloat(lng),
  };
  areaToSearch=latlng;
  geocoder
    .geocode({ location: latlng })
    .then((response) => {
        pps = response.results[0].address_components;
        console.log(response.results[0]);
      if (response.results[0]) {
        map.setZoom(MAP_ZOOM);
        pageRefreshed();
        /*
        const marker = new google.maps.Marker({
          title: response.results[0].formatted_address,
          position: new google.maps.LatLng(lat,lng),
          map: map,
        });
        markers1.push(marker);
        infowindow.setContent(response.results[0].formatted_address);
        infowindow.open(map, marker);
        marker.addListener('click', function() {
          infowindow.open(map, marker);
        });*/
      } else {
        window.alert("Invalid Coordinates!");
      }
    })
    .catch((e) => window.alert("Geocoder failed due to: " + e));
}