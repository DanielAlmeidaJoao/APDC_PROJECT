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
    let path = `/rest/events/view/${token}`;
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
function makeEventBlock(eventObj) {
    /*
        String name, description, goals, location,
        meetingPlace, startDate, endDate, duration, token;
    */
    let div11 = document.createElement("div");

    let descriptionDiv = document.createElement("div");
    let goalDiv = document.createElement("div");

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
    let div44 = document.createElement("div");
    div44.appendChild(makeElement("span",eventObj.duration));


    let parBlock = document.createElement("div");
    parBlock.appendChild(div11);
    parBlock.appendChild(div22);
    parBlock.appendChild(div33);
    parBlock.appendChild(div44);

    parBlock.setAttribute("class","evt_disp");

    return parBlock;
}
showGetEventsBlock();
handleGetEventsButton();