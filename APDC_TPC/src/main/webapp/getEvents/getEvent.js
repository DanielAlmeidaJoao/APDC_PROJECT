const NO_CONTENT = 204;
const OKOK = 200;
const RMV_EVENT_TEXT = "REMOVE";
const EDIT_EVENT_TEXT="EDIT";
const HTML_EVENT_ID_SUFFIX="usev";
const GOING_TEXT="GOING";
const WANTING_TO_GO="I WILL GO";

let areaToSearch;

function showFinishedEventsBlock() {
    let show = document.getElementById("sh_fnshd_evts");
    if(show){
        show.onclick=()=>{
            if(document.getElementById("fnd_evnts_blk").childElementCount==0){
                document.getElementById("load_fnshd").click();
            }
            hideAllBlocksButOne("show_fnd_evts_blk");
            hideMap();
            selectNavBarButton(show);
        }
    }
}
/*HTML key names */
const dv = {
    DIV:"div",
    H1:"h1",
    H2:"h2",
    IMG:"img",
    BUTTON:"button",
    NAME:"name",
    SPAN:"span"
}

const cTypes={
    GSON:"application/json",
    URENC:"application/x-www-form-urlencoded"
}
let viewOnTheMap = document.getElementById("sotm");
let markers = [];
/*viewOnTheMap.onclick=()=>{
    showMap();
    autoCompDirection.showOnTheMap(origin,destination);
}*/
function resetCookies() {
    fetch("../rest/login/recks").then(response=>{
        document.getElementById("vwmrpnt").click();
    }).catch(err=>{console.log(err);});
}
function pageRefreshed(){
    if (performance.navigation.type == performance.navigation.TYPE_RELOAD||localStorage.getItem("email")!=null) {
        resetCookies()
    }else{
        document.getElementById("vwmrpnt").click();
    }
}
const handleGetEventsButton = function () {
    let viewMoreEventsPoints = document.getElementById("vwmrpnt");
    if(viewMoreEventsPoints){
        viewMoreEventsPoints.onclick=()=>{
            loadUpcomingEvents();
        }
        let loadFinisheds=document.getElementById("load_fnshd");
        loadFinisheds.onclick=()=>{
            loadFinishedEvents();
        }
    }
}
function eventDivBlock(eventObj,ownEvents) {
    let str = makeShowInfoString(eventObj,eventObj.eventAddress,ownEvents);
    return stringToHTML(str);
}
function loadEvents(path,finished,divBlock) {
    fetch(path)
    .then(response => {return response.json()})
    .then( data => {
        if(data){
            let chld;
            for(let x=0; x<data.length;x++){
                if(finished){
                    let chld = eventDivBlock(data[x],false); //singleEventBlock(data[x],false);
                    divBlock.appendChild(chld);
                }else{
                    makeMarker(data[x]);
                }
                //chld = singleEventBlock(data[x],finished);
                //divBlock.appendChild(chld);
            }
        }
    }
    )
    .catch((error) => {
        console.log('Error: '+ error);
    });
}

function loadFinishedEvents() {
    let mainBlock = document.getElementById("fnd_evnts_blk");
    let path = `/rest/events/view/finished`;
    loadEvents(path,true,mainBlock);
}

function loadUpcomingEvents() {
    let path = `/rest/events/view`;
    fetch(path,{
        method:'POST',
        headers:{
            "Content-Type":"application/json",
            "Accept-Charset":"utf-8"
        },
        body:JSON.stringify(areaToSearch)
    }).then(response=>{
        return response.json();
    }).then(data=>{
        if(data){
            let chld;
            for(let x=0; x<data.length;x++){
                try {
                    makeMarker(data[x]);
                } catch (error) {
                    console.log(error+" HELLO");
                }
            }
        }
    }).catch(err=>{
        console.log(err);
    })
}

function makeElement(elementType,valueAtt) {
    let div11 = document.createElement(elementType);
    div11.textContent = valueAtt;
    return div11;
}

/**
 * makes an html dom element
 * @param {*} ele html tag name 
 * @param {*} className html class name
 * @param {*} txt sinple text
 */
function me(ele,className,txt){
    let d = document.createElement(ele);
    d.setAttribute("class",className);
    if(txt){
        d.textContent=txt;
    }
    return d;
}
/**
 * creates the div that will keep the organizer's name and profile picture
 * @param {*} name - name of the organizer
 * @param {*} parent - parent html of the event that will be created
 */
/**
function organizerDiv(name,parent,url){
    let d=me(dv.DIV,"dlfx mgpd abt_orzr");
    let imgP = document.createElement("img");
    imgP.setAttribute("class","nav_img_prfl");
    imgP.setAttribute("src",url);
    imgP.setAttribute("alt","profile-pic");
    let dp=me(dv.DIV,"orgd");
    dp.appendChild(imgP);
    /*
    TODO : the part related to viewing the users profile picture
    */
/*
    let dnm = me(dv.DIV,"orgd",name);
    d.appendChild(dp);
    d.appendChild(dnm);
    parent.appendChild(d);
}

/**
 * creates the div that will keep the event description and title
 * @param {*} title title of the event
 * @param {*} txt text description of the event
 * @param {*} parent html parent element of the div being created
 */
/**
function eventDescDiv(title,txt,parent){
    let hh = me(dv.H2,"",title);
    let dt = me(dv.DIV,"txtDesc",txt);
    parent.appendChild(hh);
    parent.appendChild(dt);
} */
/**
 * creates a div to represent date or the place where the event is taking place
 * @param {*} className html class name for css
 * @param {*} txt text saying the location or the date
 * @returns 
 */
function littleDetails(className,txt,textDesc) {
    let dt = me(dv.DIV,className);
    let dts1 = me(dv.DIV,"mrdso",textDesc);
    let dts2 = me(dv.DIV,"mrdso",txt);
    dt.appendChild(dts1);
    dt.appendChild(dts2);
    return dt;
}
/**
 * Shows the location of an event on the map
 * @param {*} from origin place
 * @param {*} to destination place
 */
function showOnTheMapButton(from,to) {
    let btn = me(dv.BUTTON,"","Show On Map");
    btn.onclick=()=>{
        showMap();
        autoCompDirection.showOnTheMap(from,to);
    }
    return btn;
}
/**
 * 
 * @param {Number} volunteers the number of volunteers interested in this event
 * @param {String} className1 
 * @param {String} className2 
 * @param {String} className3 
 * @param {String} txt 
 * @returns html div element
 */
/*
function volunteersDivAux(volunteers,className1,className2,className3,txt) {
    let son1 = me(dv.DIV,className1);
    let span1 = me(dv.SPAN,className2,txt);
    let span2=me(dv.SPAN,className3,volunteers);

    son1.appendChild(span1);
    son1.appendChild(span2);
    return son1;
}*/
function participateIntheEvent(button,eventid) {
    let endpoint="/rest/events/participate";

    fetch(endpoint,{
        method:"POST",
        headers:{
            "Content-Type":cTypes.URENC
        },
        body:"eid="+eventid
    }).then(response=>{
        if(response.status==HttpCodes.conflict){
            alert("You are already participating!");
        }else if(response.status==HttpCodes.forbidden){
            alert("Session Expired");
        }else if(response.status==HttpCodes.success){
            return response.json();
        }
        return null;
    }).then(added=>{
        if(added==true){
            button.textContent=GOING_TEXT;
            updateNumberOfElements(null,true,button.parentElement.children[1].children[1]);
        }else if(added==false){
            button.textContent=WANTING_TO_GO;
            updateNumberOfElements(null,false,button.parentElement.children[1].children[1]);
        }
    }).catch(e=>{
        alert(e);
    })
}
function removeParticipation(eventid) {
    let endpoint="/rest/events/rparticipation/"+eventid;
    fetch(endpoint,{method:'DELETE'}).then(response=>{
        console.log(response.status);
        if(response.status==HttpCodes.forbidden){
            alert("Session Expired");
        }else if(response.status==HttpCodes.badrequest){
            alert("Oooops, ERROR UNEXPECTED!");
        }
    }).catch(e=>{
        alert(e);
    })
}
function participatingButtons(parent,isGoing,eventid) {
    let participateButton = me(dv.BUTTON,"vlts prt","I will go!");
    let goingButton = me(dv.BUTTON,"vlts prt","GOING!");
    if(isGoing){
        parent.appendChild(goingButton);
    }else{
        parent.appendChild(participateButton);
    }
    participateButton.onclick=()=>{    
        participateIntheEvent(eventid);
        parent.appendChild(goingButton);
        participateButton.remove();
    }
    goingButton.onclick=()=>{
        removeParticipation(eventid);
        goingButton.remove();
        parent.appendChild(participateButton);
        //remove from datastore
    }
}
/**
 * is going to create a div block to have the information about the volunteers in the event
 * @param {number of valunteers need to take part in the event} volunteers 
 * @param {html parent element} parent 
 */
/*
function volunteersDiv(volunteers,currentNum,parent,isOwner,eventid,participating,finished){
    let mnd = me(dv.DIV,"mgpd");
    let son1 = volunteersDivAux(volunteers,"vlt","vlts1 vlts","vlts1 vlts","Voluntarios:"); 
    let interestedDiv = volunteersDivAux(currentNum,"vlt","vlts","vlts","Interested:");            
    /*
    if(!finished && !isOwner){
        participatingButtons(interestedDiv,participating,eventid);
    }*/
    /*
    mnd.appendChild(son1);
    mnd.appendChild(interestedDiv);
    parent.appendChild(mnd);
}
/**
 * the div containing the date and location of the event
 * @param {*} where where the event is taking place
 * @param {*} when when the event is taking place
 * @param {*} grdpa parent element that will contain the new created div
 */
/*
function eventDateLocationDiv(where,when,grdpa){
    //from = JSON.parse(from);
    where =JSON.parse(where);
    //let vonMap = showOnTheMapButton(from,where);
    //from=from.name;
    where=where.name;
    let parent = me(dv.DIV,"dlfx mgpd abt_evt");
    //let dt0 =  littleDetails("mrd plc",from,"From (optional):");
    let dt =  littleDetails("mrd plc",where,"Where:");
    let dt2 = littleDetails("mrd dte",when,"When:");
    //parent.appendChild(dt0);
    parent.appendChild(dt);
    //parent.appendChild(vonMap);
    parent.appendChild(dt2);
    grdpa.appendChild(parent);
}*/

/**
 * div that will have the images associated with this event on the time of creation
 * @param {*} parent parent element that will contain the new created div
 */
/*
function eventsImagesDiv(parent,imgArr){
    let d = me(dv.DIV,"dlfx imgs_dv evnts_pcs");
    //let im= me(dv.IMG,"");
    //im.setAttribute("src","../imgs/Screen-Shot-2015-07-13-at-1.53.34-PM.png");
    //imgArr = JSON.parse(imgArr);
    let im;
    im = me(dv.IMG,"");
    im.setAttribute("src",imgArr);
    im.setAttribute("alt","eventimage1");
    d.appendChild(im);  
    parent.appendChild(d);
}*/
/**
 * async method to remove the event from the database
 * @param {*} eventId id of the event to be removed
 * @param {*} btn remove button to remove the element from the html document
 */
function deleteEvent(eventId,btn) {
    fetch('/rest/events/delete/'+eventId,{method:'DELETE'})
    .then(response =>{
        console.log(response.status+" i am status code");
        if(response.status==HttpCodes.success){
            alert("EventDeleted!");
            //btn.parentElement.parentElement.remove();
            //updateNumberOfElements("evt_counter",false);
            deleteMarker(eventId);
        }else if(response.status==HttpCodes.unauthorized||response.status==HttpCodes.forbidden){
            alert("You have no authorization!");
        }
    })
    .catch((error) => {
        console.error('Error:', error);
    });
}
function unreportEvent(eventId,btn) {
    fetch('/rest/events/unreport/'+eventId,{method:'DELETE'})
    .then(response =>{
        console.log(response.status+" i am status code");
        if(response.status==HttpCodes.success){
            btn.parentElement.parentElement.remove();
        }else if(response.status==HttpCodes.unauthorized||response.status==HttpCodes.forbidden){
            alert("You have no authorization!");
        }
    })
    .catch((error) => {
        console.error('Error:', error);
    });
}
/**
 * creates a button to revome this particular event
 * @param {*} eventId 
 * @param {*} parent 
 */

function removeEventButton(eventId,parent,eventObj,finished) {
    let d = me(dv.DIV,"rmv_evt");
    let rmv = document.createElement("button");
    parent.appendChild(d);
    rmv.textContent=RMV_EVENT_TEXT;
    rmv.onclick=()=>{
        deleteEvent(eventId,rmv);
    }
    if(!finished){
        let editButton=me(dv.BUTTON,"",EDIT_EVENT_TEXT);
        editButton.onclick=()=>{
            editEvent(eventObj);
        }
        d.appendChild(rmv);
        d.appendChild(editButton);
    }
}

/**
 * creates a div to display an event
 * @param {*} organiser organizer of the event
 * @param {*} title title
 * @param {*} txt text description
 * @param {*} where location
 * @param {*} when date
 * @param {*} eventId id of the event
 */
   /*
function singleEventBlock(eventObj,finished){
        /**
 * eventObj.name)
 * eventObj.description
 * eventObj.goals
 * ,eventObj.location
 * eventObj.meetingPlace
 * eventObj.startDate
 * eventObj.endDate
 * eventObj.eventId
 */
    //let organizerAndDescParent = me(dv.DIV,"blk_desc");
    //organizerDiv(eventObj.organizer,organizerAndDescParent,eventObj.imgUrl);

    //let descriptionBlock = me(dv.DIV,"dlfx mgpd abt_evt");
    //eventDescDiv(eventObj.name,eventObj.description,descriptionBlock);
    //eventDateLocationDiv(eventObj.location,eventObj.startDate+" Until "+eventObj.endDate,descriptionBlock)

    //volunteersDiv(eventObj.volunteers,eventObj.currentParticipants,descriptionBlock,eventObj.owner,eventObj.eventId,eventObj.participating,finished);

    //organizerAndDescParent.appendChild(descriptionBlock);

    //let organizerAndDescParentGrandPa=me(dv.DIV,"dlfx evt_disp");
    //organizerAndDescParentGrandPa.appendChild(organizerAndDescParent);
    //eventsImagesDiv(organizerAndDescParentGrandPa,eventObj.images);

    //let mainS1 = me(dv.DIV,"one_ev");

    //mainS1.setAttribute("id",HTML_EVENT_ID_SUFFIX+eventObj.eventId);
    //mainS1.appendChild(organizerAndDescParentGrandPa);
 
    /*
    if(eventObj.owner){
        removeEventButton(eventObj.eventId,mainS1,eventObj,finished);
    }
    let frag = document.createDocumentFragment();
    frag.appendChild(mainS1);
    return frag;    
}
*/
function reportEvent(btn){
    btn.parentElement.children[1].classList.toggle("hiderptdv");
}

function submitReport(btn,eventid){
    let args = JSON.stringify({
        eventId:eventid,
        reportText:btn.parentElement.children[0].value
    });
    let pathEndpoint= "/rest/events/report";
    fetch(pathEndpoint,{
        method:"POST",
        headers:{
            "Content-Type":"application/json"
        },
        body:args
    }).then(response =>{
        if(response.status==200){
            btn.parentElement.children[0].value="";
            alert("Report Submitted!");
            deleteMarker(eventid);
        }else if(response.status==401){
            alert("Unauthorized!");
        }else if(response.status==404){
            alert("Not Found!");
        }else{
            alert("Unexpected Error!");
        }
    }).catch(err=>{
        console.log(err);
    });
}
function goToProfilePage(userid,btn){
    btn = btn.children[1];
    btn.setAttribute("target","_blank");
    btn.setAttribute("href","../profile/profile.html");
    localStorage.setItem("ot",userid);
    btn.click();
}
function handleSlideShow(btn,before) {
    let selectedClassStyle = "evmgblk";
    let nextToBeSelected = null;
    let selected = btn.parentElement.querySelector("."+selectedClassStyle);
    //let childrenImgs = btn.parentElement.querySelectorAll("img");
    selected.classList.remove(selectedClassStyle);
    if(before){
        nextToBeSelected = selected.previousElementSibling
    }else{
        nextToBeSelected = selected.nextElementSibling
    }
    if(nextToBeSelected&&nextToBeSelected.tagName=='IMG'){
        selected = nextToBeSelected;
    }
    selected.classList.add(selectedClassStyle);
}
function imageHTMLstrgs(strUrls) {
    let images=JSON.parse(strUrls);
    let imagesHTMLstr="";
    let displayFirstImage = "evmgblk";
    
    if(images.length>1){
        imagesHTMLstr+=`<button onclick=handleSlideShow(this,${true})>L</button>`;
    }
    for (let index = 0; index < images.length; index++) {
        const element = images[index];
        imagesHTMLstr += `<img class='evmg ${displayFirstImage}' src=${element} alt='eventimage${index}'>`;
        displayFirstImage="";
    }
    if(images.length>1){
        imagesHTMLstr+=`<button onclick=handleSlideShow(this,${false})>R</button>`;
    }
    return imagesHTMLstr;
}
//NOTE: THIS FUNCTION MUST CALL removeEventButton(eventId,parent,eventObj,finished) TO REMOVE AND EDIT AN EVENT
function makeShowInfoString(eventObj,where,ownEvents) {
    let goingText="";
    if(eventObj.participating){
        goingText=GOING_TEXT;
    }else{
        goingText=WANTING_TO_GO;
    }
    let funcString;
    let reportBtn="";
    if(!eventObj.owner){
        funcString=`<button class='vlts prt' onclick=participateIntheEvent(this,${eventObj.eventId});>${goingText}</button>`;
        reportBtn=`<div>
                        <button class='rptbtn' onclick=reportEvent(this);>REPORT</button>
                        <div class="rptdv hiderptdv">
                            <textarea placeholder="I am reporting because"></textarea>
                            <button onclick=submitReport(this,${eventObj.eventId});>Submit</button>
                        </div>
                    </div>`;
    }else{
        funcString=`<button class='vlts prt'>${GOING_TEXT}</button>`;
    }
    let removeDiv="";
    if(ownEvents){
        let canEdit = "";
        if(!eventObj.finished){
            canEdit=`<button onclick=editEvent(this); data-obj='${JSON.stringify(eventObj)}'>EDIT</button>`;
        }
        removeDiv =
        `<div class="rmv_evt">
            <button onclick=deleteEvent(${eventObj.eventId},this)>Remove</button>
            ${canEdit}
        </div>`
    }
    let imagesHTMLstr=imageHTMLstrgs(eventObj.images);
    /*
    let images=JSON.parse(eventObj.images);
    for (let index = 0; index < images.length; index++) {
        const element = images[index];
        imagesHTMLstr += `<img class="" src=${element} alt='eventimage${index}'>`;
    }*/
    return `<div class="one_ev">
                <div class="evt_disp">
                    <div class="blk_desc">
                        <div class="dlfx mgpd abt_orzr" onclick=goToProfilePage(${eventObj.eventOwner},this)>
                            <div class="orgd"><img class="nav_img_prfl"
                                    src=${eventObj.imgUrl}
                                    alt="profile-pic"></div>
                            <a class="orgd">${eventObj.organizer}</a>
                        </div>
                        <div class="dlfx mgpd abt_evt">
                            <h2 class="">${eventObj.name}</h2>
                            <div class="txtDesc">${eventObj.description}</div>
                            <div class="dlfx mgpd abt_evt">
                                <div class="mrd plc">
                                    <div class="mrdso">Where:</div>
                                    <div class="mrdso">${where}</div>
                                </div>
                                <div class="mrd dte">
                                    <div class="mrdso">When:</div>
                                    <div class="mrdso">${eventObj.startDate+" Until "+eventObj.endDate}</div>
                                </div>
                            </div>
                            <div class="mgpd">
                                <div class="vlt"><span class="vlts1 vlts">Voluntarios:</span><span class="vlts1 vlts">${eventObj.volunteers}</span>
                                </div>
                                <div class="vlt"><span class="vlts">Interested:</span><span class="vlts">${eventObj.currentParticipants}</span></div>
                                <div class="mrdso">DIFFICULTY, 1-5: ${eventObj.difficulty}</div>
                                ${funcString}
                            </div>
                        </div>
                    </div>
                    <div class="dlfx imgs_dv evnts_pcs">${imagesHTMLstr}</div>
                </div>
                
                <div class="cmt_mndv">
                    <button class="ldcmtsbtn" id="shwcmts" onclick=handleShowCommentsButton(this,${eventObj.eventId});>COMMENTS: <span class="cnt_cmts">${eventObj.countComments}</span> </button>
                    <div class="cmtchlddv hidecmts">
                        <div class="pstcmtdv dlfx">
                            <textarea class="pstcmtxta"></textarea>
                            <button class="pstcmtbtn" onclick=publishComment(this,${eventObj.eventId});>COMMENT</button>
                        </div>
                        <div class="allcmts">
                                                        
                        </div>
                        <button>MORE COMMENTS</button>
                    </div>
                </div>
                ${removeDiv}
                ${reportBtn}
            </div>`;
}
function makeShowInfoStringForSu(eventObj,where) {
    let goingText="";
    if(eventObj.participating){
        goingText=GOING_TEXT;
    }else{
        goingText=WANTING_TO_GO;
    }
    let funcString;
    let reportBtn="";
    funcString=`<button class='vlts prt'>${goingText}</button>`;

    let reprts="";
    let reportObj = JSON.parse(eventObj.reports);
    let allReports = reportObj.reports;
    for(let x=0; x<allReports.length;x++){
        reprts +=`
        <h1 class="rpt_titl">COMPLAINTS</h1>
        <div class="rpt_txt">${allReports[x]}</div>
        `;
    }
    reportBtn=`<div>
                    <button class='rptbtn' onclick=reportEvent(this);>REPORT</button>
                    <div class="rptdv hiderptdv">
                        ${reprts}
                    </div>
                </div>`;

    let removeDiv="";
    removeDiv =
    `<div class="rmv_evt">
        <button onclick=unreportEvent(${eventObj.eventId},this)>Unreport</button>
        <button onclick=deleteEvent(${eventObj.eventId},this)>Remove</button>
    </div>`;
    let imagesHTMLstr=imageHTMLstrgs(eventObj.images);

    return `<div class="one_ev">
                <div class="evt_disp">
                    <div class="blk_desc">
                        <div class="dlfx mgpd abt_orzr" onclick=goToProfilePage(${eventObj.eventOwner},this)>
                            <div class="orgd"><img class="nav_img_prfl"
                                    src=${eventObj.imgUrl}
                                    alt="profile-pic"></div>
                            <div class="orgd">${eventObj.organizer}</div>
                        </div>
                        <div class="dlfx mgpd abt_evt">
                            <h2 class="">${eventObj.name}</h2>
                            <div class="txtDesc">${eventObj.description}</div>
                            <div class="dlfx mgpd abt_evt">
                                <div class="mrd plc">
                                    <div class="mrdso">Where:</div>
                                    <div class="mrdso">${where}</div>
                                </div>
                                <div class="mrd dte">
                                    <div class="mrdso">When:</div>
                                    <div class="mrdso">${eventObj.startDate+" Until "+eventObj.endDate}</div>
                                </div>
                            </div>
                            <div class="mgpd">
                                <div class="vlt"><span class="vlts1 vlts">Voluntarios:</span><span class="vlts1 vlts">${eventObj.volunteers}</span>
                                </div>
                                <div class="vlt"><span class="vlts">Interested:</span><span class="vlts">${eventObj.currentParticipants}</span></div>
                                <div class="mrdso">DIFFICULTY, 1-5: ${eventObj.difficulty}</div>
                                ${funcString}
                            </div>
                        </div>
                    </div>
                    ${imagesHTMLstr}
                </div>
                
                <div class="cmt_mndv">
                    <button class="ldcmtsbtn" id="shwcmts" onclick=handleShowCommentsButton(this,${eventObj.eventId});>COMMENTS: <span class="cnt_cmts">${eventObj.countComments}</span> </button>
                    <div class="cmtchlddv hidecmts">
                        <div class="pstcmtdv dlfx">
                            <textarea class="pstcmtxta"></textarea>
                            <button class="pstcmtbtn" onclick=publishComment(this,${eventObj.eventId});>COMMENT</button>
                        </div>
                        <div class="allcmts">
                                                        
                        </div>
                        <button>MORE COMMENTS</button>
                    </div>
                </div>
                ${removeDiv}
                ${reportBtn}
            </div>`;
}

/**
 * Convert a template string into HTML DOM nodes
 * @param  {String} str The template string
 * @return {Node}       The template HTML
 */
function stringToHTML(str) {
	let parser = new DOMParser();
	let doc = parser.parseFromString(str, 'text/html');
	return doc.body.firstChild;
};
function stringToDom(str){
    let doc = new DOMParser().parseFromString(str,'text/html');
    return doc.body.firstChild;
}

showFinishedEventsBlock();
handleGetEventsButton();