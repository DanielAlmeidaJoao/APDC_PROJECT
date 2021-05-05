const NO_CONTENT = 204;
const OKOK = 200;
const RMV_EVENT_TEXT = "REMOVE";
function showGetEventsBlock() {
    let createEvent = document.getElementById("get_evts_btn");
    createEvent.onclick=()=>{
        hideAllBlocksButOne("show_events_blk");
    }
}
function handleGetEventsButton() {
    let load_mr = document.getElementById("load_mr");
    load_mr.onclick=()=>{
        loadEvents();
    };
}
function loadEvents() {
    let path = `/rest/events/view`;
    console.log("GOING TO FETCH EVENTS!!");
    console.log(path);
    fetch(path)
    .then(response => response.json())
    .then( data => {
        console.log("RESPONSE:");
        console.log(data);
        let mainBlock = document.getElementById("events_blk");
        let chld;
        //let arr = JSON.parse(data);
        for(let x=0; x<data.length;x++){
            chld = makeEventBlock(data[x]);
            mainBlock.appendChild(chld);
        }
    }
    )
    .catch((error) => {
        console.log('Error: '+ error);
    });
}

function makeElement(elementType,valueAtt) {
    let div11 = document.createElement(elementType);
    div11.textContent = valueAtt;
    return div11;
}
function deleteEvent(eventId,btn) {
    fetch('/rest/events/delete/'+eventId,{method:'DELETE'})
    .then(response =>{
        console.log(response.status+" i am status code");
        if(response.status==OKOK){
            alert("EventDeleted!");
            btn.parentElement.remove();
        }else{
            alert("You have no authorization!");
        }
    })
    .catch((error) => {
        console.error('Error:', error);
    });
}
function removeEventButton(eventId) {
    let rmv = document.createElement("button");
    rmv.textContent=RMV_EVENT_TEXT;
    rmv.onclick=()=>{
        deleteEvent(eventId,rmv);
    }
    return rmv;
    
}
function makeEventBlock(eventObj) {
    /*
        String name, description, goals, location,
        meetingPlace, startDate, endDate, duration, token;
    */
    let div11 = document.createElement("div");

    let descriptionDiv = document.createElement("div");
    let goalDiv = document.createElement("div");

    div11.appendChild(makeElement("span","EVENT-ORGANIZER: "+eventObj.organizer));
    div11.appendChild(makeElement("span",eventObj.name));
    div11.appendChild(makeElement("div",eventObj.description));
    div11.appendChild(makeElement("div",eventObj.goals));
    ////////////

    let div22 = document.createElement("div");
    div22.appendChild(makeElement("span",eventObj.location));
    div22.appendChild(makeElement("span",eventObj.meetingPlace));

    ////////////
    let div33 = document.createElement("div");
    div33.appendChild(makeElement("span",eventObj.startDate));
    div33.appendChild(makeElement("span",eventObj.endDate));

    ////////////

    let parBlock = document.createElement("div");
    parBlock.appendChild(div11);
    parBlock.appendChild(div22);
    parBlock.appendChild(div33);

    parBlock.setAttribute("class","evt_disp");

    let grandPa = document.createElement("div");

    grandPa.appendChild(parBlock);
    grandPa.appendChild(removeEventButton(eventObj.eventId));

    return grandPa;
}
showGetEventsBlock();
handleGetEventsButton();