//input fields of the form that creates elements
const elems=document.getElementById("inp_data").querySelectorAll("input");
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
    /*
    String name, description, goals, location,
	meetingPlace, startDate, endDate, organizer, startTime, endTime, images;
	long eventId, volunteers;
    */
    let descriptionTextarea=document.getElementById("desctxt");
    elems[1].value=eventObj.name;
    descriptionTextarea.value=eventObj.description;
    elems[2].value=eventObj.difficulty;
    elems[3].value=eventObj.volunteers;
    //elems[3].value=eventObj.name;
    //elems[4].value=eventObj.name;
    //elems[5].value=eventObj.name;
    const imgparnt = document.getElementById("imgs_dv");
    imgparnt.appendChild(makeImgDiv(eventObj.images,"eventimage"));
    
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