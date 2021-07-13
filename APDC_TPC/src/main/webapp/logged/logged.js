let loggedUserEMail;
let profilePictureURL;
let loggedUserName;
function getHttpXmlRequest(){
	let xmlHttpReq;
	if(window.XMLHttpRequest){
		xmlHttpReq = new XMLHttpRequest();
	}else{
		xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
	}
	return xmlHttpReq;
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
function updateNavAttribues() {
    loggedUserEMail = localStorage.getItem("email");

    //if(email==null){
        //location.replace("login.register.html");
        //window.location.href="/register.html";
        //return;
    //}
    let nameSpan = document.getElementById("name_sp");
    let emailSpan = document.getElementById("email_sp");
    nameSpan.textContent=localStorage.getItem("name");
    loggedUserName=localStorage.getItem("name");
    profilePictureURL = localStorage.getItem("profilePictureURL");
    document.getElementById("nav_profile_pic").setAttribute("src",profilePictureURL);
    emailSpan.textContent=loggedUserEMail
}
//	String perfil, telephone, cellphone, address, more_address, locality;
/**
 * updates users additional information
 * @param {Object} additionalAttributes 
 */
function fillAdditionalAttributes(additionalAttributes) {
    let attributes = document.getElementsByClassName("sp_attr");
    attributes[0].value=additionalAttributes.perfil;
    attributes[1].value=additionalAttributes.telephone;
    attributes[2].value=additionalAttributes.cellphone;
    attributes[3].value=additionalAttributes.address;
    attributes[4].value=additionalAttributes.more_address;
    attributes[5].value=additionalAttributes.locality;
    document.getElementById("evt_counter").textContent=additionalAttributes.events;
    document.getElementById("evtintr_counter").textContent=additionalAttributes.interestedEvents;
}
/**
 * make form inputs readonly if value is true else editable 
 * @param {boolean} value 
 */
function disableInfoEditions(value) {
    let inputs = document.getElementsByClassName("sp_attr");
    for(let x=0;x<inputs.length;x++){
        inputs[x].readOnly=value;
    }
}
/*
function updateInfos(obj){
    let postString=JSON.stringify(obj);
    let path="../rest/login/op3";
    fetch(path,{
        method:"POST",
        headers:{
            "Content-Type":"application/json"
        },
        body:postString
    }).then(response=>{
        if(response.ok){
            alert("UPDAETD WITH SUCCESS!");
        }else if(response.status===401){
            alert("Session Expired!");
        }else{
            alert("Invalid Data!");
        }
    }).catch(e=>{
        alert(e);
    })
}*/
function updateAditionalAttributes() {
    let attributes = document.getElementsByClassName("sp_attr");
    let obj={
        perfil:attributes[0].textContent,
        telefone:attributes[1].textContent,
        telemovel:attributes[2].textContent,
        morada:attributes[3].textContent,
        morada_complementar:attributes[4].textContent,
        localidade:attributes[5].textContent
    }
    alert(loggedUserEMail);
    updateInfos(obj);
}
function updateAttributes() {
    let additionalAttributesForm=document.getElementById("my_inf");
    let editbtn = document.getElementById("edtadt");
    let updatebtn = document.createElement("button");
    updatebtn.setAttribute("id","updateadt");
    updatebtn.textContent="UPDATE";
    //document.getElementById("updateadt");
/** <button type="submit" id="updateadt">UPDATE</button> */
    editbtn.onclick=()=>{
        editbtn.parentElement.appendChild(updatebtn);
        editbtn.remove();
        //updateAditionalAttributes();
        disableInfoEditions(false);
    }
    additionalAttributesForm.onsubmit=(e)=>{
        e.preventDefault();
        const formInputs = new FormData(e.target);
        const obj=Object.fromEntries(formInputs.entries());
        let postString=JSON.stringify(obj);
        let path="../rest/login/op3";
        fetch(path,{
            method:"POST",
            headers:{
                "Content-Type":"application/json"
            },
            body:postString
        }).then(response=>{
            if(response.ok){
                alert("UPDAETD WITH SUCCESS!");
                disableInfoEditions(true);
                updatebtn.parentElement.appendChild(editbtn);
                updatebtn.remove();
            }else if(response.status==401){
                alert("Session Expired!");
            }else{
                alert("Invalid Data!");
            }
        }).catch(e=>{
            alert(e);
        })
    }
    disableInfoEditions(true);
}
function logOff() {
    let legoffButton = document.getElementById("logoff");
    legoffButton.onclick=()=>{
        console.log("CLIKED");
        sendLogOff();
    }
}
function removeAccount() {
    let legoffButton = document.getElementById("rmv");
    let password;
    let obj;

    legoffButton.onclick=()=>{
        password=prompt("Insert Your Password?");
        if(password.trim()==""){
            alert("Insert a valid password!");
            return;
        }
        let path="../rest/login/op8";
        fetch(path,{
            method:"DELETE",
            headers:{
                "Content-Type":"application/x-www-form-urlencoded"
            },
            body:"p="+password
        }).then(response=>{
            if(response.ok){
                localStorage.clear();
                window.location.href="/";
            }else if(response.status===401){
                alert("Password is Wrong!");
            }else{
                alert("Unexpected Error!");
            }
        }).catch(e=>{
            alert(e);
        })
    }
}

function sendLogOff() {
    let spanText= document.getElementById("rs_sn");
    let xmlHttpReq = getHttpXmlRequest();
    xmlHttpReq.onreadystatechange = function(){
        if(xmlHttpReq.readyState == 4 && xmlHttpReq.status == 200){
            let rt = xmlHttpReq.responseText;
            localStorage.clear();
            window.location.href="/";
        }
    }
    xmlHttpReq.open("GET","../rest/login/op7",true);
    xmlHttpReq.setRequestHeader("Content-Type", "application/json");
    xmlHttpReq.send();
}

function isLogged() {
    email = localStorage.getItem("email");
    if(email==null){
        localStorage.clear();
        window.location.href="/register.html";
    }
}
/**** SECOND NAV BUTTONS */
const dispb="dispb";
function hideAllBlocksButOne(elementToShow) {
    let blocks  = document.getElementsByClassName(dispb);
    while(blocks.length>0){
        blocks[0].classList.remove(dispb);
    }
    document.getElementById(elementToShow).classList.add(dispb);
}

//isLogged();
updateNavAttribues();
updateAttributes();
logOff();
removeAccount();