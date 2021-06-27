/**
 * <div class="user_disp">
        <div class="users_d">
            <span>NAME</span>
            <span>EMAIL</span>
            <span>USERID</span>
            <span>ROLE</span>
            <span>STATE</span>
        </div>
        <div class="users_d ops">
            <button>REMOVE</button>
            <button>CHANGE STATE</button>
        </div>
    </div>
 */
function spanAttribues(parent,txtValue) {
    let spn = document.createElement("span");
    spn.textContent=txtValue;
    parent.appendChild(spn);
}
function makeDivAux(tag,className) {
    let divv = document.createElement(tag);
    divv.setAttribute("class",className);
    return divv;
}
function removeUserBtn(parent,userid) {
    let path="/rest/super/users/"+userid;
    let rmvb = document.createElement("button");
    rmvb.textContent="REMOVE";
    rmvb.setAttribute("class","subt subtrm");

    rmvb.onclick=()=>{
        //DO REMOVE THE USER
        let password=prompt("Insert Your Password!");
        if(password.length==0){
            alert("Password invalid!");
            return;
        }
        fetch(path,
        {
            method:"DELETE",
            headers:{
                "Content-Type":"application/x-www-form-urlencoded"
            },
            body:"p="+password
        }).then(response=>{
            if(response.ok){
                alert("User Deleted!");
                parent.parentElement.remove();
            }else if(response.status===401){
                alert("Unauthorized")
            }
        })
    }
    parent.appendChild(rmvb);
}
function changeUserStateBtn(parent,userid,stateBlock) {
    let path="/rest/super/change";
    let changestate = document.createElement("button");
    changestate.textContent="CHANGE STATE";
    changestate.setAttribute("class","subt subtch");

    changestate.onclick=()=>{
        //DO REMOVE THE USER
        let password=prompt("Insert Your Password!");
        if(password.length==0){
            alert("Password invalid!");
            return;
        }
        fetch(path,
        {
            method:"PUT",
            headers:{
                "Content-Type":"application/x-www-form-urlencoded"
            },
            body:"p="+password+"&u="+userid
        }).then(response=>{
            console.log(response);
            if(response.ok){
                alert("State Changed!");
                return response.text();
            }else if(response.status===401){
                alert("Unauthorized")
            }
        }).then(newState=>{
            if(newState){
                stateBlock.textContent=newState;
            }
        }).catch(e=>{
            console.log(e);
        })
    }
    parent.appendChild(changestate);
}
function userblock(userObj) {
    let main=makeDivAux(dv.DIV,"user_disp");
    
    let firstchild=makeDivAux(dv.DIV,"users_d");

    //(String name, String email, long userid, String role, String state)		
    spanAttribues(firstchild,userObj.name);
    spanAttribues(firstchild,userObj.email);
    spanAttribues(firstchild,userObj.userid);
    spanAttribues(firstchild,userObj.role);
    spanAttribues(firstchild,userObj.state);
    
    let secchild=makeDivAux(dv.DIV,"users_d ops");
    removeUserBtn(secchild,userObj.userid);
    changeUserStateBtn(secchild,userObj.userid,firstchild.lastChild);

    main.appendChild(firstchild);
    main.appendChild(secchild);
    
    return main;
}

function loadusers() {
    let loadUsersBtn=document.getElementById("mr_rged_users");
    let usersDivBlock=document.getElementById("rgtd_users");

    loadUsersBtn.onclick=()=>{
        let path= '/rest/super/users';
        fetch(path).then(response=>{
            if(response.ok){
                return response.json();
            }
        }).then(obj=>{
            if(obj){
                for (let index = 0; index < obj.length; index++) {
                    usersDivBlock.appendChild(userblock(obj[index]));
                }
            }
        }).catch(e=>{console.log(e)})
    }
}
function superUser() {
    let superUser = document.getElementById("superu");
    superUser.style.display="block";
    superUser.onclick=()=>{
        hideAllBlocksButOne("suplc_blk");
        hideMap();
        selectNavBarButton(superUser);
    }
}
function handleRole() {
    let path= '/rest/super/role';
    fetch(path).then(response=>{
        if(response.ok){
            return response.text();
        }else{
            return null;
        }
    }).then(res=>{
        if(res=="true"){
            superUser();
        }else{
            document.getElementById("superu").remove();
            document.getElementById("suplc_blk").remove();
        }
    })
}

function handleNumberOfEventsButton() {
    let path="/rest/events/view/myevents";
    let numberEventsBtn=document.getElementById("num_evts");
    let dispBlock = document.getElementById("shusevnts");
    numberEventsBtn.onclick=()=>{
        if(dispBlock.childElementCount==0&&dispBlock.parentElement.classList.contains("usr_evts")){
            //load
            
            fetch(path).then(response => response.json()).then( data => {
                    let chld;
                    for(let x=0; x<data.length;x++){
                        //makeSolidarityAction(data[x]);
                        chld = singleEventBlock(data[x],false);
                        dispBlock.appendChild(chld);
                    }
                }
                )
                .catch((error) => {
                    console.log('Error: '+ error);
                });
        }
        dispBlock.parentElement.classList.toggle("usr_evts");
    }
    
}
handleNumberOfEventsButton();
loadusers();
handleRole();