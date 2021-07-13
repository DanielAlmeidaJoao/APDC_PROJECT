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
 function handleNumberOfEventsButton(numberEventsBtnId,dispBlockId,endpoint) {
    let path="/rest/events/view/"+endpoint;
    let numberEventsBtn=document.getElementById(numberEventsBtnId);
    let dispBlock = document.getElementById(dispBlockId);

    numberEventsBtn.onclick=()=>{
        if(dispBlock.childElementCount==0&&dispBlock.parentElement.classList.contains("usr_evts")){
            //load
            fetch(path).then(response => response.json()).then( data => {
                    let chld;
                    for(let x=0; x<data.length;x++){
                        //makeSolidarityAction(data[x]);
                        chld =  eventDivBlock(data[x],endpoint=="myevents"); //singleEventBlock(data[x],false);
                        dispBlock.appendChild(chld);
                    }
                }
                )
                .catch((error) => {
                    console.log('Error: '+ error);
                });
        }
        selectNavBarButton(numberEventsBtn);
        hideAllDivButOne(dispBlockId);
        dispBlock.parentElement.classList.remove("usr_evts");
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
showAboutDiv();
handleNumberOfEventsButton("num_evts","shusevnts","myevents"); //show events made by the user
handleNumberOfEventsButton("intrdevts","shusevntsitrd","interested"); //show events the user has interests