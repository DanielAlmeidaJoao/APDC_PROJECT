if(sessionStorage.getItem("ot")&&!localStorage.getItem("ot")){
    localStorage.setItem("ot",sessionStorage.getItem("ot"));
}
const otheruser=localStorage.getItem("ot");
console.log("I AM OTHER USER!");
sessionStorage.setItem("ot",otheruser);
localStorage.removeItem("ot");
function hideAllDivButOne(theOne) {
    let hideClassList="usr_evts"
    let childs = document.getElementById("mbses").children;
    for (let index = 0; index < childs.length; index++) {
        const element = childs[index];
        element.classList.add(hideClassList);
    }
    document.getElementById(theOne).classList.remove(hideClassList);
}
function selectNavBarButton(btn){
    let className = "sltdnvb";
    let elems = document.getElementsByClassName(className);
    for (let index = 0; index < elems.length; index++) {
        const element = elems[index];
        element.classList.remove(className);
    }
    btn.classList.add(className);
}
/**
 * shows the number of events the user has posted and also loads all the events the user has posted
 * @param {*} numberEventsBtnId 
 * @param {*} dispBlockId 
 * @param {*} endpoint 
 */
 function handleNumberOfEventsButton(numberEventsBtnId,dispBlockId,endpoint,moreEventId) {
    let numberEventsBtn=document.getElementById(numberEventsBtnId);
    let dispBlock = document.getElementById(dispBlockId);
    let cursor="";

    function loadEvents() {
        let path=`/rest/events/view/${endpoint}/?userid=${otheruser}&cursor=${cursor}`;
        fetch(path).then(response => {
            console.log(response);
            return response.json();
        }).then( data => {
            if(data){
                cursor = data[1];
                data = JSON.parse(data[0]);
                let chld;
                for(let x=0; x<data.length;x++){
                    chld =  eventDivBlock(data[x],false); //singleEventBlock(data[x],false);
                    dispBlock.appendChild(chld);
                }
            }
        }
        ).catch((error) => {
            console.log('Error: '+ error);
        });
    }
    numberEventsBtn.onclick=()=>{
        if(dispBlock.childElementCount==0&&dispBlock.parentElement.classList.contains("usr_evts")){
            //load
            loadEvents();
        }
        selectNavBarButton(numberEventsBtn);
        hideAllDivButOne(dispBlockId);
        dispBlock.parentElement.classList.remove("usr_evts");
    }
    document.getElementById(moreEventId).onclick=()=>{
        loadEvents();
    }
}
function eventDivBlock(eventObj,ownEvents) {
    let str = makeShowInfoString(eventObj,eventObj.eventAddress,ownEvents);
    return stringToHTML(str);
}

function showAboutDiv() {
    let showAboutBtn = document.getElementById("about_nvb");
    showAboutBtn.onclick=()=>{
        hideAllDivButOne("usr_bta76");
        selectNavBarButton(showAboutBtn);
    }
}
function showUserInfos(){
    let other ="";
    if(otheruser){
        other = otheruser;
    }else{
        other=loggedUserEMail;
    }
    fetch("/rest/login/infos/"+other).then(response=>{
        if(response.ok){
            return response.json();
        }else if(response.status==404){
            alert("User No Longer Exists!");
        }
        return null;
    }).then(data => {
        if(data){
            document.getElementById("pscrnr").textContent=data.participationScore;
            document.getElementById("vsb_btn").textContent=data.name;
            document.getElementById("evt_counter").textContent = data.events;
            document.getElementById("evtintr_counter").textContent = data.interestedEvents;
            document.getElementById("qtshdv").textContent = data.quote;
            document.getElementById("bioshdv").textContent = data.bio;
            document.getElementById("prfl_img").setAttribute("src",data.profilePicture);
            let socialMedias = document.getElementById("contacts");
            for (let index = 0; index < socialMedias.childElementCount; index++) {
                const element = socialMedias.children[index];
                let str = data[element.getAttribute("name")];
                if(str){
                    element.setAttribute("href",str);
                }else{
                    element.removeAttribute("href");
                }
            }
            if(data.viewingOwnProfile){
                document.getElementById("emlspn").textContent=data.email;
            }
            handleEditBtn(data.viewingOwnProfile);
            handleEditName(data.viewingOwnProfile);
            handleEditEmailButton(data.viewingOwnProfile);
            console.log(data.viewingOwnProfile);
        }
    }).catch(err => {
        console.log(err);
    })
}
showUserInfos();
showAboutDiv();
handleNumberOfEventsButton("num_evts","shusevnts","myevents","load_mr_user_evnts"); //show events made by the user
handleNumberOfEventsButton("intrdevts","shusevntsitrd","interested","load_mr_itrstd"); //show events the user has interests