const HttpCodes = {
    success : 200,
    notFound : 404,
    badrequest : 400,
    forbidden: 403,
    unauthorized: 401,
    // etc
}
const sbmt = document.getElementById("addEvt_frm");
const hideMapClass="hdmap";
function showMap(){
    document.getElementById("map_div").classList.remove(hideMapClass);
}
function hideMap(){
    document.getElementById("map_div").classList.add(hideMapClass);
}
function showCreateEventBlock() {
    let createEvent = document.getElementById("create_events_btn");
    createEvent.onclick=()=>{
        hideAllBlocksButOne("create_events");
        showMap();
    }
}

function getEventCreationObject(name, description,goals,volunteers,
    startingDate,endingDate,startTime,endTime,eventId) {
    /*
        name, description, goals, volunteers,location,
        meetingPlace, startDate, endDate, duration;
    */
    if(origin==null||destination==null){
        alert("Choose an origin and a destination!");
        return null;
    }
    let ff = {
        name:name,
        description:description,
        goals: goals,
        volunteers:volunteers,
        location:JSON.stringify(origin),
        meetingPlace:JSON.stringify(destination),
        startDate:startingDate+" "+startTime,
        endDate:endingDate+" "+endTime,
        eventId:eventId
    }    
    return ff;
}
function getValue(formElement) {
    return formElement.value;
}
function getFrontEndInputs() {
    let datas = document.getElementsByName("inp_data");
    let eventId=sbmt.getAttribute(dv.NAME);
    //TODO -> check if there are invalid inputs
    return getEventCreationObject(getValue(datas[0]),getValue(datas[1]),
    getValue(datas[2]),getValue(datas[3]),getValue(datas[4]),getValue(datas[5])
    ,getValue(datas[6]),getValue(datas[7]),eventId);
}
function handleCreateEventSubmitForm() {
    sbmt.onsubmit=(e)=>{
        e.preventDefault();
        if(origin==null||destination==null){
            alert("Choose an origin and a destination!");
            return null;
        }
        const okd = new FormData(event.target);
        let data = Object.fromEntries(okd.entries());
        data["location"]=JSON.stringify(destination);
        data["meetingPlace"]=JSON.stringify(origin);
        let eventId=sbmt.getAttribute(dv.NAME);
        if(!eventId){
            eventId=0;
        }
        data["eventId"]=eventId;
        console.log(data);
        if(data==null){
            return false;
        }
        //console.log(data);

        data = JSON.stringify(data);
        data = makeFormData(data);
        if(data!=null){
            //uploadPictures(data,formEle);
            uploadData(data,sbmt);
        }
        //console.log(data);
        return false;
    }
}
function resetImagesDiv() {
    let dvs = document.getElementById("imgs_dv");
    while(dvs.childElementCount>0){
        dvs.firstChild.remove();
    }
}
function uploadData(datas,formEle) {
    fetch('/rest/events/create', {
    method: 'POST', // or 'PUT'
    body:datas
    })
    .then(response =>{
        let res;
        if(response.status==HttpCodes.success){
            res="Event Created With Success!";
            formEle.reset();
            resetImagesDiv();
        }else if(response.status==HttpCodes.unauthorized){
            res="Session is Invalid!";
        }else if(response.status==HttpCodes.badrequest){
            res="Invalid Data!";
        }
        alert(res);
    })
    .catch((error) => {
        console.error('Error:', error);
    });
}
let eventImages=null;
function makeImgDiv(result,caption) {
    let ppp = document.createElement("div");
 
    let imgele = document.createElement("img");
    imgele.setAttribute("src",result);
    imgele.setAttribute("alt",caption);

    let rmv = document.createElement("button");
    rmv.textContent="Remover";
    rmv.onclick=()=>{
        ppp.remove();
        eventImages.delete(caption);
    }
    ppp.appendChild(imgele);
    ppp.appendChild(rmv);
    ppp.setAttribute("class","admg");
    eventImages.set(caption,result);
    return ppp;
}
const max_images=1;
function handleImages(){
    let uploadImg = document.getElementById("gtimg");
    let caption = document.getElementById("cpt");

    eventImages=new Map();
    uploadImg.onchange=function(){
        const file = this.files[0];
        if(eventImages.size==max_images){
            alert("Only 5 images allowed!");
            return;
        }
        if(file){
            if(eventImages.has(file.name)){
                return;
            }
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

    let x=0;
    console.log("GOING TO CREATE EVENT: "+eventImages.size);
    if(eventImages.size==0){
        alert("Add An Image, please!");
        return null;
    }
    eventImages.forEach((v,k) => {
        formData.append("img"+x,v);
        x++;
    });
    /*
    for(let x=0;x<imgs.length;x++){
        elem=imgs[x].firstChild;
        if(elem.tagName=='IMG'){
            arr.push(elem.getAttribute("src"));
        }else{
            alert("INVALID DATA!");
            return null;
        }
    }
    if(arr.length>0){
        arr = JSON.stringify(arr);
        formData.append("imgs",arr);
    }*/
	return formData;
}
showCreateEventBlock();
handleCreateEventSubmitForm();
handleImages();