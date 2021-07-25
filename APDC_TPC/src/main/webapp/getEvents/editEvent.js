//input fields of the form that creates elements
const elems=document.getElementById("inp_data").querySelectorAll("input");
let editingArray = null;

function createEventFormIsFree(){
    for (let index = 0; index < elems.length; index++) {
        const element = elems[index];
        if(element.value.trim()!=""){
            return false;
        }
    }
    let imgDiv=document.getElementById("imgs_dv");
    if(imgDiv.childElementCount>0){
        return false;
    }
    return true;
}
/**
 * Receives an event to be edited
 * @param {Object} eventObj 
 * @returns void
 */
function editEvent(btn){
    let eventObj =  JSON.parse(btn.getAttribute("data-obj"));
    btn.removeAttribute("data-obj");
    if(!createEventFormIsFree()){
        alert("There are unsaved events!");
        return;
    }
    editingArray=[];
    /*
    String name, description, goals, location,
	meetingPlace, startDate, endDate, organizer, startTime, endTime, images;
	long eventId, volunteers;
    */
    destination={
        loc:eventObj.loc,
        name:eventObj.eventAddress,
    };
    document.getElementById("pac-input2").value=destination.name;
    let descriptionTextarea=document.getElementById("desctxt");
    elems[1].value=eventObj.name;
    descriptionTextarea.value=eventObj.description;
    elems[2].value=eventObj.difficulty;
    elems[3].value=eventObj.volunteers;

    let start = eventObj.startDate.split(" ");
    let end = eventObj.endDate.split(" ");
    elems[4].value=start[0]; //start date
    elems[5].value=end[0]; //end date
    elems[6].value=start[1]; //start time
    elems[7].value=end[1]; //end time
    
    const imgparnt = document.getElementById("imgs_dv");
    let images=JSON.parse(eventObj.images);
    for (let index = 0; index < images.length; index++) {
        const element = images[index];
        imgparnt.appendChild(makeImgDiv(element,"eventimage"+index));
    }
    
    //
    document.getElementById("addEvt_frm").setAttribute(dv.NAME,eventObj.eventId);
    document.getElementById("create_events_btn").click();
    document.getElementById("addEvt_frm").classList.remove("hidfrm");
}
function deleteMarker(id) {
    //Find and remove the marker from the Array
    for (let i = 0; i < currentPoints.length; i++) {
        if (currentPoints[i].id == id) {
            //Remove the marker from Map                  
            currentPoints[i].setMap(null);

            //Remove the marker from array.
            currentPoints.splice(i, 1);
            return;
        }
    }
};