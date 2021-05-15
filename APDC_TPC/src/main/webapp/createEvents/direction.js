// This example requires the Places library. Include the libraries=places
// parameter when you first load the API. For example:
// <script
// src="https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY&libraries=places">
let origin=null;
let destination=null;
let autoCompDirection;
const SEARCH1=0;
const SEARCH2=1;
const fields =["place_id","name","formatted_address","geometry"];
function initMap() {
  let myLat= 41.85;
  let myLong=-87.65;
  navigator.geolocation.getCurrentPosition(function(position) {
    myLat = position.coords.latitude;
    myLong = position.coords.longitude;
    if(map){
      map.setCenter({ lat:myLat, lng: myLong});
    }
  });
  const map = new google.maps.Map(document.getElementById("map"), {
    mapTypeControl: false,
    center: { lat:myLat, lng:myLong },
    zoom: 13,
  });
  autoCompDirection = new AutocompleteDirectionsHandler(map);
}
class AutocompleteDirectionsHandler {
  constructor(map) {
    this.map = map;
    this.originPlaceId = "";
    this.destinationPlaceId = "";
    this.travelMode = google.maps.TravelMode.WALKING;
    this.directionsService = new google.maps.DirectionsService();
    this.directionsRenderer = new google.maps.DirectionsRenderer();
    this.directionsRenderer.setMap(map);
    const originInput = document.getElementById("origin-input");
    const destinationInput = document.getElementById("destination-input");
    const modeSelector = document.getElementById("mode-selector");
    const originAutocomplete = new google.maps.places.Autocomplete(originInput);
    // Specify just the place data fields that you need.
    originAutocomplete.setFields(fields);
    const destinationAutocomplete = new google.maps.places.Autocomplete(destinationInput);
    // Specify just the place data fields that you need.
    destinationAutocomplete.setFields(fields);
    this.setupClickListener(
      "changemode-walking",
      google.maps.TravelMode.WALKING
    );
    this.setupClickListener(
      "changemode-transit",
      google.maps.TravelMode.TRANSIT
    );
    this.setupClickListener(
      "changemode-driving",
      google.maps.TravelMode.DRIVING
    );
    this.setupPlaceChangedListener(originAutocomplete, SEARCH1);
    this.setupPlaceChangedListener(destinationAutocomplete, SEARCH2);
    //this.map.controls[google.maps.ControlPosition.TOP_LEFT].push(originInput);
    //this.map.controls[google.maps.ControlPosition.TOP_LEFT].push(destinationInput);
    this.map.controls[google.maps.ControlPosition.TOP_LEFT].push(modeSelector);
  }
  // Sets a listener on a radio button to change the filter type on Places
  // Autocomplete.
  setupClickListener(id, mode) {
    const radioButton = document.getElementById(id);
    radioButton.addEventListener("click", () => {
      this.travelMode = mode;
      this.route();
    });
  }
  
  getPlaceName(place){
    let name;
    if(place.formatted_address){
      name=place.formatted_address;
    }else{
      name=place.place;
    }
    return name;
  }
  setupPlaceChangedListener(autocomplete, mode) {
    autocomplete.bindTo("bounds", this.map);
    autocomplete.addListener("place_changed", () => {
      const place = autocomplete.getPlace();
      if (!place.place_id) {
        window.alert("Please select an option from the dropdown list.");
        return;
      }      
      let obj={
        place_id:place.place_id,
        loc:{ lat: place.geometry.location.lat(), lng: place.geometry.location.lng() },
        name:this.getPlaceName(place)
      };
      if (mode === SEARCH1) {
        origin=obj;
        this.originPlaceId =place.place_id; // "EihBdi4gZGEgTGliZXJkYWRlLCAyOTUwIFBhbG1lbGEsIFBvcnR1Z2FsIi4qLAoUChIJT2s9gGdBGQ0RI_erjg6GhsoSFAoSCTshzcObQRkNEY6I-TQmhJhA";
      } else {
        destination=obj;
        this.destinationPlaceId = place.place_id; //"EkFBdi4gZG9zIEJvbWJlaXJvcyBWb2x1bnTDoXJpb3MgZGUgUGFsbWVsYSwgMjk1MCBQYWxtZWxhLCBQb3J0dWdhbCIuKiwKFAoSCcEuH91mQRkNESe92-wQ8bJiEhQKEgk7Ic3Dm0EZDRGOiPk0JoSYQA"; //place.place_id;
      }
      this.route();
    });
  }
  showOnTheMap(place1,place2){
    this.originPlaceId =place1.place_id;
    this.destinationPlaceId = place2.place_id;
    origin=place1;
    destination=place2;
    this.route();
  }
  route() {
    if (!this.originPlaceId || !this.destinationPlaceId) {
      return;
    }
    const me = this;
    this.directionsService.route(
      {
        origin: { placeId: this.originPlaceId },
        destination: { placeId: this.destinationPlaceId },
        travelMode: this.travelMode,
      },
      (response, status) => {
        if (status === "OK") {
          me.directionsRenderer.setDirections(response);
          this.distHaversine(origin.loc,destination.loc);
        } else {
          window.alert("Directions request failed due to " + status);
        }
      }
    );
  }
  // Functions to compute distance between two points on earth's surface
  rad(x){return x*Math.PI/180;}
  distHaversine(p1, p2) {
    if(p1==null||p2==null){
      return;
    }
    var R = 6371; // earth's mean radius in km
    var dLat  = this.rad(p2.lat - p1.lat);
    var dLong = this.rad(p2.lng - p1.lng);
  
    var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(this.rad(p1.lat)) * Math.cos(this.rad(p2.lat)) * Math.sin(dLong/2) * Math.sin(dLong/2);
    var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    var d = R * c;
    let distDiv = document.getElementById("edist").children[1];
    distDiv.textContent=d+" KM.";
    //return d;
  }
}