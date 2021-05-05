function showCreateEventBlock() {
    let createEvent = document.getElementById("create_events_btn");
    createEvent.onclick=()=>{
        hideAllBlocksButOne("create_events");
    }
}

function getEventCreationObject(name, description,goals,location,meetingPlace,startingDate,endingDate,duration) {
    /*
        name, description, goals, location,
        meetingPlace, startDate, endDate, duration;
    */
    let ff = {
        name:name,
        description:description,
        goals: goals,
        location:location,
        meetingPlace:meetingPlace,
        startDate:startingDate,
        endDate:endingDate,
        duration:duration,
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
        uploadData(data,sbmt);
        console.log(data);
        return false;
    }
}

function uploadData(datas,formEle) {
    fetch('/rest/events/create', {
    method: 'POST', // or 'PUT'
    headers: {
        'Content-Type': 'application/json',
    },
    body:datas
    })
    .then(response => response.json())
    .then(data => {
        formEle.reset();
        console.log('Success:', data);
    })
    .catch((error) => {
        console.error('Error:', error);
    });
}

showCreateEventBlock();
handleCreateEventSubmitForm();