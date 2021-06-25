const MAX_IMAGE_sIZE=1000000;
const max_images=1;
const HttpCodes = {
    success : 200,
    notFound : 404,
    badrequest : 400,
    forbidden: 403,
    unauthorized: 401,
    conflict: 409
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
        selectNavBarButton(createEvent);
    }
    createEvent.click();
}
function cancelEventCreationEdition() {
    let cancelbtn=document.getElementById("nclbtn");
    let directionInputs=document.getElementsByClassName("controls");
    cancelbtn.onclick=()=>{
        for (let index = 0; index < directionInputs.length; index++) {
            const element = directionInputs[index];
            element.value="";
        }
        //origin=null;
        destination=null;
        resetImagesDiv();
        sbmt.removeAttribute("name");
    }
}
/**
 * 
 * @returns true if start date is less than end date, else false
 */
function validDate() {
    let startInp=document.getElementById("startDate");
    let endInp=document.getElementById("endDate");
    let startTime=document.getElementById("startTime");
    let endTime=document.getElementById("endTime");
    if(startInp.valueAsNumber<endInp.valueAsNumber){
        return true;
    }else if(startInp.valueAsNumber===endInp.valueAsNumber&&startTime.valueAsNumber<endTime.valueAsNumber){
        return true;
    }else{
        return false;
    }
}
function handleCreateEventSubmitForm() {
    sbmt.onsubmit=(e)=>{
        e.preventDefault();
        if(destination==null){
            alert("Choose an origin and a destination!");
            return null;
        }
        const okd = new FormData(e.target);
        let data = Object.fromEntries(okd.entries());
        console.log(data);
        data["location"]=JSON.stringify(destination);
        //data["meetingPlace"]=JSON.stringify(origin);
        let eventId=sbmt.getAttribute(dv.NAME);
        if(!eventId){
            eventId=0;
        }
        data["eventId"]=eventId;
        if(data==null){
            return false;
        }
        if(!validDate()){
            alert("Date is Invalid! Start Date must be before End Date!");
            return false;
        }
        data = JSON.stringify(data);
        data = makeFormData(data);
        if(data!=null){
            uploadData(data,sbmt);
        }
        return false;
    }
}
function resetImagesDiv() {
    let dvs = document.getElementById("imgs_dv");
    while(dvs.childElementCount>0){
        dvs.firstChild.remove();
    }
    eventImages=new Map();
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
            document.getElementById("nclbtn").click();
        }else if(response.status==HttpCodes.unauthorized){
            res="Session is Invalid!";
        }else if(response.status==HttpCodes.badrequest){
            res="Invalid Data!";
        }
        alert(res);
        return response.json();
    }).then(data=>{
        if(data){
            clearMarkers();
            makeSolidarityAction(data);
        }
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
let fil;
function handleImages(){
    let uploadImg = document.getElementById("gtimg");
    let caption = document.getElementById("cpt");

    eventImages=new Map();
    uploadImg.onchange=function(){
        const file = this.files[0];
        fil=file;
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
    formData.append("img_cover",fil);
    formData.append("evd",evd);
    /*
    let imgs = document.getElementById("imgs_dv").children;
    let elem;

    
    let x=0;
    console.log("GOING TO CREATE EVENT: "+eventImages.size);
    if(eventImages.size==0){
        alert("Add An Image, please!");
        return null;
    }
    eventImages.forEach((v,k) => {
        if(v.length>MAX_IMAGE_sIZE){
            alert("Image is too big!");
            return;
        }
        formData.append("img"+x,v);
        x++;
    });*/
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
cancelEventCreationEdition();