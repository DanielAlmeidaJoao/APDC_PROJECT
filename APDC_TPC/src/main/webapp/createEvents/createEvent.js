function showCreateEventBlock() {
    let createEvent = document.getElementById("create_events_btn");
    createEvent.onclick=()=>{
        hideAllBlocksButOne("create_events");
    }
    console.log("OLA, WORKING!");
}

function getEventCreationObject(name, description,goals,location,meetingPlace,startingDate,endingDate,duration) {
    return {
        name:name,
        description:description,
        goals: goals,
        location: location,
        meetingPlace: meetingPlace,
        startingDate: startingDate,
        endingDate:endingDate,
        duration:duration
    }
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
        console.log(getFrontEndInputs());
        return false;
    }
}
showCreateEventBlock();
handleCreateEventSubmitForm();