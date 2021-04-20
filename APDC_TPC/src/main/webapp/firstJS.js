const endpoint = document.location.host+"/rest/";
console.log("OK OK");

let myStorage = window.sessionStorage;

function getHttpXmlRequest(){
	let xmlHttpReq;
	if(window.XMLHttpRequest){
		xmlHttpReq = new XMLHttpRequest();
	}else{
		xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
	}
	return xmlHttpReq;
}
function registerUser(name,email,password){
    let spanText= document.getElementById("rs_sn");
	let xmlHttpReq = getHttpXmlRequest();
	xmlHttpReq.onreadystatechange = function(){
		if(xmlHttpReq.readyState == 4 && xmlHttpReq.status == 200){
			let rt = xmlHttpReq.responseText;
            console.log(rt);
            redirectOnLogin(rt);
		}
	}
	let postString =JSON.stringify({
        name: name,
        email: email,
        password: password 
    });
	xmlHttpReq.open("POST","rest/login/op1",true);
	xmlHttpReq.setRequestHeader("Content-Type", "application/json");
	xmlHttpReq.send(postString);
}
function redirectOnLogin(rt) {
    rt = JSON.parse(rt);
    if(rt.status=="1"){
        localStorage.setItem("email",rt.email);
        localStorage.setItem("name",rt.name);
        localStorage.setItem("token",rt.token);
        localStorage.setItem("gbo",rt.gbo);
        if(rt.additionalAttributes!="0"){
            localStorage.setItem("ad_attr",rt.additionalAttributes);
            console.log(rt.additionalAttributes);
        }
        window.location.href="/logged/logged.html";
    }else if(rt.status=="2"){
        alert("THERE WAS AN ERROR GENERATING THE TOKEN! PLEASE RETRY, IF THE ERROR CONTUNES, PLEASE REPORT!");
    }else if(rt.status=="3"){
        alert("REGISTER SUCCESS!, BUT THERE WAS AN ERROR GENERATING THE TOKEN, PLEASE RE-LOGIN!");
    }else if(rt.status=="-2"){
        alert("USER REGISTERED ALREADY!");
    }else if(rt.status=="-1"){
        alert("INTERNAL ERROR");
    }else
    {
        alert("LOGIN FAILED!");
    }
}
function processLoginData(email,password) {
    let xmlHttpReq = getHttpXmlRequest();
	xmlHttpReq.onreadystatechange = function(){
		if(xmlHttpReq.readyState == 4 && xmlHttpReq.status == 200){
			let rt = xmlHttpReq.responseText;
            redirectOnLogin(rt);
		}
	}
	let postString =JSON.stringify({
        email: email,
        password: password 
    });
	xmlHttpReq.open("POST","rest/login/op2",true);
	xmlHttpReq.setRequestHeader("Content-Type", "application/json");
	xmlHttpReq.send(postString);
}
function validateInputs() {
    let name = document.getElementById("inp").value;
    let email = document.getElementById("inp2").value;
    let password = document.getElementById("inp3").value;
    let conf_password = document.getElementById("inp4").value;
  
    if(name.trim()===""){
      alert("DON'T YOU DARE!");
      return;
    }
  
    if(password!==conf_password){
      alert("LET'S US BE PROFESSIONALS FELLAS!");
      return;
    }
  
    registerUser(name,email,password);
}

function handleSubmitButton() {
    let btn =  document.getElementById("sbm");
    btn.onclick=()=>{
        validateInputs();
    }
}

function handleLogin() {
    let formS=document.getElementById("login_form");
    console.log("HHAA");
    formS.addEventListener('submit', function(e) {
        e.preventDefault();
    });
    formS.onsubmit=()=>{
        let email = document.getElementById("login_email").value.trim();
        let password= document.getElementById("login_password").value.trim();
        if(email==""||password==""){
            alert("PLEASE, INSERT VALID DATA!");
        }
        processLoginData(email,password);
        return false;
    }
}

function handleForm(){

    let myform = document.getElementById("myForm");
    myform.addEventListener('submit', function(e) {
        e.preventDefault();
    });
    myform.onsubmit=()=>{
        validateInputs();
        console.log("HAHHAHA");
        return false;
    }
}
//handleForm();


addEventListener('beforeunload', function (event) {
	//localStorage.clear();
	return undefined;
});

handleLogin();
handleSubmitButton();

console.log("VERSION 40");