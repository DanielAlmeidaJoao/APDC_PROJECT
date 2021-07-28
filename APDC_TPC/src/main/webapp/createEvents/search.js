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
let iconUrl="https://maps.gstatic.com/mapfiles/place_api/icons/v1/png_71/geocode-71.png";
let currentMapLocation=null;
let destination=null;
let origin=null;
let markers1 = [];

function getPointsTextAreas() {
  return morePointsObj.txtas;
}
function getPointsCoordinates() {
  return morePointsObj.tracks;
}
const MAP_ZOOM=12;
const fields =["place_id","plus_code"];

function clearMarkers(markers) {
  // Clear out the old markers.
  markers.forEach((marker) => {
    marker.setMap(null);
  });
  markers = [];
}

let map;
function initAutocomplete() {
  areaToSearch={ lat: 38.5718676, lng: -8.8990022 };
  currentMapLocation=areaToSearch;
  map = new google.maps.Map(document.getElementById("map"), {
    zoom: MAP_ZOOM,
    center: areaToSearch,
    mapTypeId: "roadmap",
  });  
  navigator.geolocation.getCurrentPosition(function(position) {
    if(map){
      let lat=position.coords.latitude;
      let lng=position.coords.longitude;
        
      origin={lat:lat, lng: lng};
      currentMapLocation=origin;
      map.setCenter(origin);
      geocodeLatLng(map,lat,lng);
    }
  });
  navigator.permissions && navigator.permissions.query({name: 'geolocation'})
    .then(function(PermissionStatus) {
        if (PermissionStatus.state == 'granted') {
          console.log("DENIED 332");
        } else {
          pageRefreshed();
        }
    })

  // Create the search box and link it to the UI element.
  const input2 = document.getElementById("pac-input2");
  const input = document.getElementById("pac-input");

  const searchBox = new google.maps.places.SearchBox(input);
  const searchBox2 = new google.maps.places.SearchBox(input2);

  
  let mapZoom;
  let startLocation;
  new google.maps.event.addListener(map, 'click', function(event) {
    mapZoom = map.getZoom();
    startLocation = event.latLng;
    map.panTo(startLocation);

    //let marker;
    //clearMarkers(markers1);
    if(mapZoom == map.getZoom()){
      geocodeLatLng(map,startLocation.lat(),startLocation.lng(),placeMarker); 
    }
  });

  google.maps.event.addListener(map, 'idle', ()=>{
    if(!equalsCenter(currentMapLocation)){
      handleLoadEventsOnIdle();
    }
  });

  //${address},${startLocation}
  function placeMarker(address) {
    //let marker;
    let contentString;
    contentString = `
      <div class='dvifn'>
        <div class='psifn'>
          <span>Meeting Point: </span>
          <span class='darn8'>${address}</span>
        </div>
        <button class='psibtn' name='${address}' onclick=handleMakeEventOnTheMap(this,${startLocation.lat()},${startLocation.lng()})>Create Event</button>
      </div>`
    clearMarkers(markers1);
    makeMarkerAux(startLocation.lat(),startLocation.lng(),null,contentString);
  }
  map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

  // Bias the SearchBox results towards current map's viewport.
  map.addListener("bounds_changed", () => {
    searchBox.setBounds(map.getBounds());
  });

  // Listen for the event fired when the user selects a prediction and retrieve
  handleSearchBox(searchBox,searchEventParams);
  handleSearchBox(searchBox2,validPlace);
}
function handleMakeEventOnTheMap(button,lat,lng){
  button.remove();
  getMaxImagesNumber();
  let address = button.getAttribute("name");
  document.getElementById("addEvt_frm").classList.remove("hidfrm");
  document.getElementById("pac-input2").value = address;
  let loc = {lat:lat,lng:lng};
  destination={
    loc:loc,
    name:address,
  };
}
function addPointToTrackEvent(button,lat,lng){
  let address = button.getAttribute("name");
  let newDiv = `<div>
                  <textarea name='tracks' placeholder='About this point'></textarea>
                </div>`;
  let htmlEle = stringToDom(newDiv);
  button.parentElement.appendChild(htmlEle);
  let loc = {lat:lat,lng:lng};
  destination={
    loc:loc,
    name:address,
  };
  button.remove();
  makeTrack();
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
    clearMarkers(markers1);
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
      console.log(place.icon);
      // Create a marker for each place.
      markers1.push(
        new google.maps.Marker({
          map,
          icon,
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
      func(place);
    });
    map.fitBounds(bounds);
  });
}
function equalsCenter(location) {
  return location!=null&&location.lat==map.center.lat()&&location.lng==map.center.lng();
}
function handleLoadEventsOnIdle() {
  currentMapLocation = {
    lat: map.center.lat(),
    lng: map.center.lng()
  }
  areaToSearch = currentMapLocation;
  document.getElementById("vwmrpnt").click();
}
function validPlace(place){
  let newDiv = stringToDom(`<div>${place.adr_address}</div>`);
  let postalCode=newDiv.querySelector(".postal-code");
  let locality=newDiv.querySelector(".locality");
  let countryName = newDiv.querySelector(".country-name");
  //let street_address = newDiv.querySelector(".street-address");
  //console.log(newDiv);

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
  if(!equalsCenter(currentMapLocation)){
    handleLoadEventsOnIdle();
    console.log("12 " );
    console.log(currentMapLocation);
  }
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
function Rad(x) {return x*Math.PI/180;}

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
  marker.addListener('click',function() {
    if(clicked==false){
      let path=`/rest/events/event/${marker.id}`;
      fetch(path).then(response => {
        return response.json();
      }).then(event=>{
        if(event){
          clicked=true;
          contentString = makeShowInfoString(event,event.eventAddress,event.owner);
          infowindow = new google.maps.InfoWindow({content: contentString,maxWidth:"800px"});
          infowindow.open(map, marker);
          distDiv.textContent=DistHaversine(origin, { lat: loc.lat, lng: loc.lng });
          distDiv.textContent+=" KM";
        }
      });
    }
    else{
      infowindow.open(map, marker);
      distDiv.textContent=DistHaversine(origin, { lat: loc.lat, lng: loc.lng });
      distDiv.textContent+=" KM";
    }
  });

  let trackPoints=eventObj.trackPoints;
  if(trackPoints){
    trackPoints = JSON.parse(trackPoints);
    for (let index = 0; index < trackPoints.length; index++) {
      const element = trackPoints[index];
      geocodeLatLng(map,element.loc.lat,element.loc.lng,undefined,`<div>DESCRIPTION: ${element.desc}</div>`);
    }
  }
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
  //currentPoints.push(marker);
  let clicked=false;
  
  let contentString = makeShowInfoString(eventObj,eventObj.eventAddress,true);
  let infowindow = new google.maps.InfoWindow({content: contentString});
  marker.addListener('click', function() {
    infowindow.open(map, marker);
    distDiv.textContent=DistHaversine(origin, { lat: loc.lat, lng: loc.lng });
  });
}
function geocodeLatLng(map,lat,lng,func) {
  const geocoder = new google.maps.Geocoder();
  //const infowindow = new google.maps.InfoWindow();
  const latlng = {
    lat: parseFloat(lat),
    lng: parseFloat(lng),
  };
  geocoder
    .geocode({ location: latlng })
    .then((response) => {
      if (response.results[0]) {
        areaToSearch=latlng;
        //pps = response.results[0].address_components;
        if(func){
          func(response.results[0].formatted_address);
          return;
        }else{
          pageRefreshed();
        }
        map.setZoom(MAP_ZOOM);
        let title = response.results[0].formatted_address;
        makeMarkerAux(lat,lng,title,title);
      } else {
        window.alert("Invalid Coordinates!");
      }
    })
    .catch((e) => window.alert("Geocoder failed due to: " + e));
}
function makeMarkerAux(lat,lng,title,infoContent) {
  const infowindow = new google.maps.InfoWindow();
  const icon = {
    url: iconUrl,
    size: new google.maps.Size(71, 71),
    origin: new google.maps.Point(0, 0),
    anchor: new google.maps.Point(17, 34),
    scaledSize: new google.maps.Size(25, 25),
  };
  const marker = new google.maps.Marker({
    map:map,
    icon:icon,
    title:title,
    position: new google.maps.LatLng(lat,lng)
  });
  markers1.push(marker);
  infowindow.setContent(infoContent);
  infowindow.open(map, marker);
  marker.addListener('click', function() {
    infowindow.open(map, marker);
  });
}