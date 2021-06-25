const NO_CONTENT = 204;
const OKOK = 200;
const RMV_EVENT_TEXT = "REMOVE";
const EDIT_EVENT_TEXT="EDIT";
function showGetEventsBlock() {
    let show = document.getElementById("get_evts_btn");
    show.onclick=()=>{
        hideAllBlocksButOne("show_events_blk");
        hideMap();
        selectNavBarButton(show);
    }
}
function showFinishedEventsBlock() {
    let show = document.getElementById("sh_fnshd_evts");
    show.onclick=()=>{
        hideAllBlocksButOne("show_fnd_evts_blk");
        hideMap();
        selectNavBarButton(show);
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
const handleGetEventsButton = function () {
    let load_mr = document.getElementById("load_mr");
    let loadFinisheds=document.getElementById("load_fnshd");
    load_mr.onclick=()=>{
        loadUpcomingEvents();
    };
    loadFinisheds.onclick=()=>{
        loadFinishedEvents();
    }
    function loadEvents(path,finished,divBlock) {
        fetch(path)
        .then(response => response.json())
        .then( data => {
            let chld;
            for(let x=0; x<data.length;x++){
                makeSolidarityAction(data[x]);
                //chld = singleEventBlock(data[x],finished);
                //divBlock.appendChild(chld);
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
        let mainBlock = document.getElementById("events_blk");
        let path = `/rest/events/view`;
        loadEvents(path,false,mainBlock);
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
        function eventDescDiv(title,txt,parent){
            let hh = me(dv.H2,"",title);
            let dt = me(dv.DIV,"txtDesc",txt);
            parent.appendChild(hh);
            parent.appendChild(dt);
        }
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
        function volunteersDivAux(volunteers,className1,className2,className3,txt) {
            let son1 = me(dv.DIV,className1);
            let span1 = me(dv.SPAN,className2,txt);
            let span2=me(dv.SPAN,className3,volunteers);

            son1.appendChild(span1);
            son1.appendChild(span2);
            return son1;
        }
        function participateIntheEvent(eventid) {
            let endpoint="/rest/events/participate";

            fetch(endpoint,{
                method:"POST",
                headers:{
                    "Content-Type":cTypes.URENC
                },
                body:"eid="+eventid
            }).then(response=>{
                console.log(response.status);
                if(response.status==HttpCodes.conflict){
                    alert("You are already participating!");
                }else if(response.status==HttpCodes.forbidden){
                    alert("Session Expired");
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
        function volunteersDiv(volunteers,currentNum,parent,isOwner,eventid,participating,finished){
            let mnd = me(dv.DIV,"mgpd");
            let son1 = volunteersDivAux(volunteers,"vlt","vlts1 vlts","vlts1 vlts","Voluntarios:"); 
            let interestedDiv = volunteersDivAux(currentNum,"vlt","vlts","vlts","Interested:");            
            if(!finished && !isOwner){
                participatingButtons(interestedDiv,participating,eventid);
            }
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
        function eventDateLocationDiv(where,when,grdpa){
            //from = JSON.parse(from);
            where =JSON.parse(where);
            //makeMarker(where.name,where.loc.lat,where.loc.lng,map,`<div>${where.name}</div>`);
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
        }
        function stringToDom(str){
            let doc = new DOMParser().parseFromString(str,'text/html');
            return doc.body.firstChild;
        }
        /**
         * div that will have the images associated with this event on the time of creation
         * @param {*} parent parent element that will contain the new created div
         */
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
        }
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
            let organizerAndDescParent = me(dv.DIV,"blk_desc");
            organizerDiv(eventObj.organizer,organizerAndDescParent,eventObj.imgUrl);
    
            let descriptionBlock = me(dv.DIV,"dlfx mgpd abt_evt");
            eventDescDiv(eventObj.name,eventObj.description,descriptionBlock);
            eventDateLocationDiv(eventObj.location,eventObj.startDate+" Until "+eventObj.endDate,descriptionBlock)

            volunteersDiv(eventObj.volunteers,eventObj.currentParticipants,descriptionBlock,eventObj.owner,eventObj.eventId,eventObj.participating,finished);

            organizerAndDescParent.appendChild(descriptionBlock);
    
            let organizerAndDescParentGrandPa=me(dv.DIV,"dlfx evt_disp");
            organizerAndDescParentGrandPa.appendChild(organizerAndDescParent);
            eventsImagesDiv(organizerAndDescParentGrandPa,eventObj.images);
    
            let mainS1 = me(dv.DIV,"one_ev");
            mainS1.appendChild(organizerAndDescParentGrandPa);
            if(eventObj.owner){
                removeEventButton(eventObj.eventId,mainS1,eventObj,finished);
            }
            let frag = document.createDocumentFragment();
            frag.appendChild(mainS1);
            return frag;
            
        }
    /*
    let eventObj={
        organizer:"daniel joao",
        name:"salvar crianças famintas",
        description:"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla sed risus ullamcorper, ultrices ex at, ultrices nisi. Vestibulum in venenatis ante. Suspendisse et interdum nunc. Ut porttitor orci gravida rhoncus vulputate. Maecenas rutrum accumsan nisi, eu sollicitudin lorem. Sed vehicula tristique leo sed luctus. Proin id urna ex. Cras feugiat arcu a lacus fermentum, id mattis libero gravida. Morbi a lobortis ipsum, eu sollicitudin lectus. Aenean at dui ut dui porttitor volutpat non nec eros. Donec condimentum condimentum magna et ultrices. Mauris vitae interdum lectus. Donec sit amet congue neque. Etiam magna mi, gravida sit amet lectus sit amet, volutpat mollis justo. Suspendisse potenti.",
        goals:"okkoo golos",
        location:"setubal a",
        meetingPlace:"dadasdsa",
        startDate:"29 de abril 2029",
        endDate:"16 de junho 3020",
        eventId:"313321321jhb3h3j21hb3jb2"
    }
    let mainBlock = document.getElementById("events_blk");
    let chld = singleEventBlock(eventObj);
    mainBlock.appendChild(chld);*/

    

    load_mr.click();
}
function makeSolidarityAction(eventObj) {
    let where =JSON.parse(eventObj.location);
    let contentString = makeShowInfoString(eventObj.imgUrl,eventObj.organizer,eventObj.name,eventObj.description,where.name,eventObj.startDate+" Until "+eventObj.endDate,eventObj.currentParticipants,eventObj.volunteers,eventObj.images);
    makeMarker(eventObj.name,where.loc.lat,where.loc.lng,map,contentString);
}
function makeShowInfoString(profilePic,organiser,eventName,eventDescription,where,when,numinterested,capacity,eventPic) {
    return `<div class="one_ev">
    <div class="dlfx evt_disp">
        <div class="blk_desc">
            <div class="dlfx mgpd abt_orzr">
                <div class="orgd"><img class="nav_img_prfl"
                        src=${profilePic}
                        alt="profile-pic"></div>
                <div class="orgd">${organiser}</div>
            </div>
            <div class="dlfx mgpd abt_evt">
                <h2 class="">${eventName}</h2>
                <div class="txtDesc">${eventDescription}</div>
                <div class="dlfx mgpd abt_evt">
                    <div class="mrd plc">
                        <div class="mrdso">Where:</div>
                        <div class="mrdso">${where}</div>
                    </div>
                    <div class="mrd dte">
                        <div class="mrdso">When:</div>
                        <div class="mrdso">${when}</div>
                    </div>
                </div>
                <div class="mgpd">
                    <div class="vlt"><span class="vlts1 vlts">Voluntarios:</span><span class="vlts1 vlts">${capacity}</span>
                    </div>
                    <div class="vlt"><span class="vlts">Interested:</span><span class="vlts">${numinterested}</span></div>
                </div>
            </div>
        </div>
        <div class="dlfx imgs_dv evnts_pcs"><img class="" src=${eventPic} alt="eventimage1"></div>
    </div>
</div>`;
}
showGetEventsBlock();
showFinishedEventsBlock();
handleGetEventsButton();