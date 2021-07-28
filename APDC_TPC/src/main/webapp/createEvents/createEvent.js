const MAX_IMAGE_sIZE=1000000;
let max_images=1;

const sbmt = document.getElementById("addEvt_frm");
const hideMapClass="hdmap";

let getMaxImagesNumber = (function() {
    var executed = false;
    return function() {
        if (!executed) {
            executed = true;
            fetch("../rest/events/consts/mgn").then(response => {
                if(response.ok){
                    return response.json();
                }
            }).then(data =>{
                if(data){
                    max_images=data;
                }
            });
        }
    };
})();

function toggleCreateEventForm() {
    //let clicked=false;
    document.getElementById("tgl_crtevt").onclick=()=>{
        getMaxImagesNumber();
        /*
        if(clicked==false){
            fetch("../rest/events/consts/mgn").then(response => {
                if(response.ok){
                    return response.json();
                }
            }).then(data =>{
                if(data){
                    max_images=data;
                    console.log(data);
                }
            });
        }
        clicked=true;*/
        document.getElementById("addEvt_frm").classList.toggle("hidfrm");

    }
}
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
        editingArray=null;
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
    let today = new Date();
    let startDate = new Date(startInp.value+" "+startTime.value);
    if(startDate>today){
        return true;
    }
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
        if(data.difficulty>5||data.difficulty<1){
            alert("Difficulty must be between 1 and 5!");
            return false;
        }
        if(data.volunteers<2){
            alert("Number of volunteers must be greater than 2!");
            return false;
        }
        if(!validDate()){
            alert("Date is Invalid! Start Date must be before End Date!");
            return false;
        }
        /*
        if(simple==false){
            let textareas = getPointsTextAreas();
            let coords = getPointsCoordinates();
            if(coords.length>6){
                alert("Only 5 Complementary points are allowed!");
                return false;
            }
            let complementaryPoints=[];
            for (let index = 0; index < textareas.length; index++) {
                const element = textareas[index];
                if(element&&element.value.trim()==""){
                    alert("Every complementary track point must have a description!");
                    return false;
                }else if(element){
                    complementaryPoints.push({
                        loc:coords[index],
                        desc: element.value
                    });
                }
            }
            if(complementaryPoints.length>0){
                data["trackPoints"]=JSON.stringify(complementaryPoints);
            }
        } */
        let eventName = data.name;
        data = JSON.stringify(data);
        data = makeFormData(data);
        if(data!=null){
            uploadData(data,eventName,eventId);
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
function updateNumberOfElements(elementid,inc,element) {
    let update=-1;
    if(inc){
        update=1;
    }
    if(element){
        element.textContent=parseInt(element.textContent)+update;
    }else{
        document.getElementById(elementid).textContent=parseInt(document.getElementById(elementid).textContent)+update;
    }
}
/**
 * creates a new event or update with the information in the json object 'datas', if the event already exist
 * @param {*} datas 
 */
function uploadData(datas,eventName,eventId) {
    let dest = destination;
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
            /*let eventBlock = document.getElementById(HTML_EVENT_ID_SUFFIX+data.eventId);
            if(eventBlock){
                deleteMarker(eventObj.eventId);
                updateNumberOfElements("evt_counter",true);
                eventBlock.parentElement.replaceChild(singleEventBlock(data,false),eventBlock);
            }*/
            //deleteMarker(data.eventId);
            clearMarkers(markers1);
            let eventObj = {
                loc:dest.loc,
                name: eventName,
                eventId: data,
              }
            makeMarker(eventObj);
        }
    })
    .catch((error) => {
        console.error('Error:', error);
    });
    
    /*
    const xhttp = new XMLHttpRequest();
    xhttp.onload = function() {
        console.log(this.responseText);
    }
    
    xhttp.open("POST", '/rest/events/create');
    //xhttp.setRequestHeader("Content-type", "multipart/form-data");
    xhttp.send(datas);
    */
}
let eventImages=null;

function makeImgDiv(result,caption,file) {
    let ppp = document.createElement("div");
 
    let imgele = document.createElement("img");
    imgele.setAttribute("src",result);
    imgele.setAttribute("alt",caption);

    let rmv = document.createElement("button");
    rmv.textContent="Remover";
    rmv.onclick=()=>{
        ppp.remove();
        eventImages.delete(caption);
        if(editingArray){
            editingArray.push(result);
        }
    }
    ppp.appendChild(imgele);
    ppp.appendChild(rmv);
    ppp.setAttribute("class","admg");
    if(file){
        eventImages.set(caption,file);
    }
    return ppp;
}
function handleImages(){
    let uploadImg = document.getElementById("gtimg");
    let caption = document.getElementById("cpt");

    eventImages=new Map();
    uploadImg.onchange=function(){
        const file = this.files[0];
        if(eventImages.size==max_images){
            alert(`Only ${max_images} images allowed!`);
            return;
        }
        if(file){
            if(eventImages.has(file.name)){
                return;
            }
            const reader = new FileReader();
            const imgparnt = document.getElementById("imgs_dv");
            reader.onload=function () {
                imgparnt.appendChild(makeImgDiv(this.result,file.name,file));
                file;
            }
            reader.readAsDataURL(file);
        }
    }
}
function makeFormData(evd){
	let formData = new FormData();	
    let x=0;    
    if(editingArray==null && eventImages.size==0){
        alert("Add An Image, please!");
        return null;
    }
    eventImages.forEach((v,k) => {
        if(x<max_images){
            formData.append("img_"+x,v);
            x++;
        }
    });
    formData.append("evd",evd);
    if(editingArray){
        formData.append("editing",JSON.stringify(editingArray));
    }

	return formData;
}
showCreateEventBlock();
handleCreateEventSubmitForm();
handleImages();
cancelEventCreationEdition();
toggleCreateEventForm();