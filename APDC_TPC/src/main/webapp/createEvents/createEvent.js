function showCreateEventBlock() {
    let createEvent = document.getElementById("create_events_btn");
    createEvent.onclick=()=>{
        hideAllBlocksButOne("create_events");
    }
    console.log("OLA, WORKING!");
}

function getEventCreationObject(name, description,goals,location,meetingPlace,startingDate,endingDate,duration) {
    /*
    return {
        name:name,
        description:description,
        goals: goals,
        location: location,
        meetingPlace: meetingPlace,
        startingDate: startingDate,
        endingDate:endingDate,
        duration:duration,
        token:"ola"
    } */
    /*
        name, description, goals, location,
        meetingPlace, startDate, endDate, duration, token;
    */
    let ff = {
        name:"das",
        description:"das",
        goals: "da",
        location: "dsa",
        meetingPlace: "aa",
        startDate: "As",
        endDate:"dsa",
        duration:"ds",
        token:"ola"
    }
    return ff;
}
function getValue(formElement) {
    return formElement.value;
}
function getFrontEndInputs() {
    let datas = document.getElementsByName("inp_data");
    //TODO -> check if there are invalid inputs
    return getEventCreationObject(getValue(datas[0]),getValue(datas[1]),getValue(datas[2]),getValue(datas[3]),getValue(datas[4]),getValue(datas[5]),getValue(datas[6]),getValue(datas[7]));
}

function handleCreateEventSubmitForm() {
    let sbmt = document.getElementById("addEvt_frm");
    sbmt.onsubmit=()=>{
        let data = getFrontEndInputs();
        //console.log(data);
        data = JSON.stringify(data);
        uploadData(data);
        console.log(data);
        return false;
    }
}
showCreateEventBlock();
handleCreateEventSubmitForm();

function uploadData(datas) {
    fetch('/rest/events/create', {
    method: 'POST', // or 'PUT'
    headers: {
        'Content-Type': 'application/json',
    },
    body:datas
    })
    .then(response => response.json())
    .then(data => {
        console.log('Success:', data);
    })
    .catch((error) => {
        console.error('Error:', error);
    });
}