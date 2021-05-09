function showCreateEventBlock() {
    let createEvent = document.getElementById("create_events_btn");
    createEvent.onclick=()=>{
        hideAllBlocksButOne("create_events");
    }
}

function getEventCreationObject(name, description,goals,location,
    meetingPlace,startingDate,endingDate,startTime,endTime) {
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
        startDate:startingDate+" "+startTime,
        endDate:endingDate+" "+endTime,
    }    
    return ff;
}
function getValue(formElement) {
    return formElement.value;
}
function getFrontEndInputs() {
    let datas = document.getElementsByName("inp_data");
    //TODO -> check if there are invalid inputs
    return getEventCreationObject(getValue(datas[0]),getValue(datas[1]),
    getValue(datas[2]),getValue(datas[3]),getValue(datas[4]),getValue(datas[5])
    ,getValue(datas[6]),getValue(datas[7]),getValue(datas[8]));
}

function handleCreateEventSubmitForm() {
    let sbmt = document.getElementById("addEvt_frm");
    sbmt.onsubmit=(e)=>{
        e.preventDefault();
        let data = getFrontEndInputs();
        //console.log(data);
        data = JSON.stringify(data);
        data = makeFormData(data);
        if(data!=null){
            //uploadPictures(data,formEle);
            uploadData(data,sbmt);
        }
        console.log(data);
        return false;
    }
}

function uploadData(datas,formEle) {
    fetch('/rest/events/create', {
    method: 'POST', // or 'PUT'
    body:datas
    })
    .then(response => response.json())
    .then(data => {
        formEle.reset();
        if(data=="1"){
            alert("EVENT CREATED SUCCESSFULLY!");
        }else{
            alert("SOMETHING WENT WRONG, CALL GOD!");
        }
        console.log('Success:', data);
    })
    .catch((error) => {
        console.error('Error:', error);
    });
}

function makeImgDiv(result,caption) {
    let ppp = document.createElement("div");
 
    let imgele = document.createElement("img");
    imgele.setAttribute("src",result);
    imgele.setAttribute("alt",caption);

    let rmv = document.createElement("button");
    rmv.textContent="Remover";
    rmv.onclick=()=>{
        ppp.remove();
    }
    ppp.appendChild(imgele);
    ppp.appendChild(rmv);
    ppp.setAttribute("class","admg");
    return ppp;
}
function handleImages(){
    let uploadImg = document.getElementById("gtimg");
    let caption = document.getElementById("cpt");

    uploadImg.onchange=function(){
        const file = this.files[0];
        if(file){
            const reader = new FileReader();
            const imgparnt = document.getElementById("imgs_dv");
            reader.onload=function () {
                imgparnt.appendChild(makeImgDiv(this.result,file.name));
            }
            reader.readAsDataURL(file);
        }
    }
}
function makeFormData(evd){
	let formData = new FormData();	
	formData.append("evd",evd);
	let imgs = document.getElementById("imgs_dv").children;
    let elem;
    let arr = new Array();

    for(let x=0;x<imgs.length;x++){
        elem=imgs[x].firstChild;
        if(elem.tagName=='IMG'){
            arr.push(elem);
        }else{
            alert("INVALID DATA!");
            return null;
        }
    }
    if(arr.length>0){
        arr = JSON.stringify(arr);
        formData.append("imgs",arr);
    }
	return formData;
}
showCreateEventBlock();
handleCreateEventSubmitForm();
handleImages();