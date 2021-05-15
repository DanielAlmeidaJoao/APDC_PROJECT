const NO_CONTENT = 204;
const OKOK = 200;
const RMV_EVENT_TEXT = "REMOVE";
const EDIT_EVENT_TEXT="EDIT";
function showGetEventsBlock() {
    let createEvent = document.getElementById("get_evts_btn");
    createEvent.onclick=()=>{
        hideAllBlocksButOne("show_events_blk");
        hideMap();
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
let viewOnTheMap = document.getElementById("sotm");
let markers = [];
/*viewOnTheMap.onclick=()=>{
    showMap();
    autoCompDirection.showOnTheMap(origin,destination);
}*/
const handleGetEventsButton = function () {
    let load_mr = document.getElementById("load_mr");
    load_mr.onclick=()=>{
        loadEvents();
    };
    function loadEvents() {
        let path = `/rest/events/view`;
        fetch(path)
        .then(response => response.json())
        .then( data => {
            //console.log("RESPONSE:");
            //console.log(data);
            let mainBlock = document.getElementById("events_blk");
            let chld;
            //let arr = JSON.parse(data);
            for(let x=0; x<data.length;x++){
                chld = singleEventBlock(data[x]);
                //chld = makeEventBlock(data[x]);
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
        function organizerDiv(name,parent){
            let d=me(dv.DIV,"dlfx mgpd abt_orzr");
            let dp=me(dv.DIV,"orgd","USER PROFILE PIC");
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
            let dt = me(dv.DIV,"",txt);
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
         * is going to create a div block to have the information about the volunteers in the event
         * @param {number of valunteers need to take part in the event} volunteers 
         * @param {html parent element} parent 
         */
        function volunteersDiv(volunteers,parent){
            let mnd = me(dv.DIV,"");
            
            let son1 = me(dv.DIV,"");
            let span1 = me(dv.SPAN,"","Voluntarios");
            let span2=me(dv.SPAN,"",volunteers);

            son1.appendChild(span1);
            son1.appendChild(span2);
            mnd.appendChild(son1);
            parent.appendChild(mnd);
        }
        /**
         * the div containing the date and location of the event
         * @param {*} where where the event is taking place
         * @param {*} when when the event is taking place
         * @param {*} grdpa parent element that will contain the new created div
         */
        function eventDateLocationDiv(from,where,when,grdpa){
            from = JSON.parse(from);
            where =JSON.parse(where);
            let vonMap = showOnTheMapButton(from,where);
            from=from.name;
            where=where.name;
            let parent = me(dv.DIV,"dlfx mgpd abt_evt");
            let dt0 =  littleDetails("mrd plc",from,"From (optional):");
            let dt =  littleDetails("mrd plc",where,"Where:");
            let dt2 = littleDetails("mrd dte",when,"When:");
            parent.appendChild(dt0);
            parent.appendChild(dt);
            parent.appendChild(vonMap);
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
        function removeEventButton(eventId,parent,eventObj) {
            let d = me(dv.DIV,"rmv_evt");
            let rmv = document.createElement("button");
            parent.appendChild(d);
            rmv.textContent=RMV_EVENT_TEXT;
            rmv.onclick=()=>{
                deleteEvent(eventId,rmv);
            }

            let editButton=me(dv.BUTTON,"",EDIT_EVENT_TEXT);
            editButton.onclick=()=>{
                editEvent(eventObj);
            }
            d.appendChild(rmv);
            d.appendChild(editButton);

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
        function singleEventBlock(eventObj){
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
            organizerDiv(eventObj.organizer,organizerAndDescParent);
    
            let descriptionBlock = me(dv.DIV,"dlfx mgpd abt_evt");
            eventDescDiv(eventObj.name,eventObj.description,descriptionBlock);
            eventDateLocationDiv(eventObj.meetingPlace,eventObj.location,eventObj.startDate+" Until "+eventObj.endDate,descriptionBlock)
            volunteersDiv(eventObj.volunteers,descriptionBlock);

            organizerAndDescParent.appendChild(descriptionBlock);
    
            let organizerAndDescParentGrandPa=me(dv.DIV,"evt_disp");
            organizerAndDescParentGrandPa.appendChild(organizerAndDescParent);
            eventsImagesDiv(organizerAndDescParentGrandPa,eventObj.images);
    
            let mainS1 = me(dv.DIV,"one_ev");
            mainS1.appendChild(organizerAndDescParentGrandPa);
            if(eventObj.owner){
                removeEventButton(eventObj.eventId,mainS1,eventObj);
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
    mainBlock.appendChild(chld); */
}
showGetEventsBlock();
handleGetEventsButton();