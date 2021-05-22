let loggedUserEMail;
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
    /*
    let additionalAttributes = localStorage.getItem("ad_attr");

    if(additionalAttributes!=null){
        additionalAttributes=JSON.parse(additionalAttributes);
        fillAdditionalAttributes(additionalAttributes);
    }
    */
    nameSpan.textContent=localStorage.getItem("name");;
    emailSpan.textContent=loggedUserEMail
}
//String perfil, String telefone, String telemovel, String morada, String morada_complementar, String localidade
function fillAdditionalAttributes(additionalAttributes) {
    let attributes = document.getElementsByClassName("sp_attr");
    attributes[0].textContent=additionalAttributes.perfil;
    attributes[1].textContent=additionalAttributes.telefone;
    attributes[2].textContent=additionalAttributes.telemovel;
    attributes[3].textContent=additionalAttributes.morada;
    attributes[4].textContent=additionalAttributes.morada_complementar;
    attributes[5].textContent=additionalAttributes.localidade;
}
function enableInfoEditions() {
    let inputs = document.getElementsByClassName("sp_attr");
    for(let x=0;x<inputs.length;x++){
        inputs[x].setAttribute("contenteditable","true");
        console.log("WORINK");
    }
}
function disableInfoEditions() {
    let inputs = document.getElementsByClassName("sp_attr");
    for(let x=0;x<inputs.length;x++){
        inputs[x].removeAttribute("contenteditable");
    }
}
function updateInfos(obj){
    let spanText= document.getElementById("rs_sn");
	let xmlHttpReq = getHttpXmlRequest();
	xmlHttpReq.onreadystatechange = function(){
		if(xmlHttpReq.readyState == 4 && xmlHttpReq.status == 200){
			let rt = xmlHttpReq.responseText;
            if(rt=="1"){
                alert("UPDAETD WITH SUCCESS!");
                localStorage.setItem("ad_attr",obj);
            }else{
                alert("FAILEd :"+rt);
                localStorage.clear();
                window.location.href="/register.html";
            }
		}
	}
	let postString =JSON.stringify(obj);
	xmlHttpReq.open("POST","../rest/login/op3",true);
	xmlHttpReq.setRequestHeader("Content-Type", "application/json");
	xmlHttpReq.send(postString);
}
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
    let upDateButton = document.getElementById("upDate");
    let control=false;
    upDateButton.onclick=()=>{
        control=!control;
        if(control){
            enableInfoEditions();
            upDateButton.textContent="SAVE";
        }else{
            disableInfoEditions();
            updateAditionalAttributes();
            upDateButton.textContent="UPDATE";
        }
    }
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
    if(token==null){
        localStorage.clear();
        window.location.href="/register.html";
    }
}
/**** SECOND NAV BUTTONS                                     */
const dispb="dispb";
function hideAllBlocksButOne(elementToShow) {
    let blocks  = document.getElementsByClassName(dispb);
    while(blocks.length>0){
        blocks[0].classList.remove(dispb);
    }
    document.getElementById(elementToShow).classList.add(dispb);
}
function handShowButtons() {
    let showInfo = document.getElementById("showInfo");

    let showOtherUser = document.getElementById("other_info");

    showInfo.onclick=()=>{
        hideAllBlocksButOne("my_inf");
        hideMap();
        selectNavBarButton(showInfo);
    }
    /*
    showOtherUser.onclick=()=>{
        hideAllBlocksButOne("view_user");
        hideMap();
    }*/

    /*
    let disableUser = document.getElementById("disable_user");
    disableUser.onclick=()=>{
        hideAllBlocksButOne("disable_user_b");
    } */

    /*
    let changePassword = document.getElementById("change_pwd");
    changePassword.onclick=()=>{
        hideAllBlocksButOne("change_pwd_block");
    }*/
}
//isLogged();
updateNavAttribues();
updateAttributes();
logOff();
removeAccount();
handShowButtons();