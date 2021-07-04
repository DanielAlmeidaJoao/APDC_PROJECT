const endpoint = document.location.host+"/rest/";
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
    let user={
        name: name,
        email: email,
        password: password
    };
	let postString = JSON.stringify(user);
    let path="rest/login/op1";
    fetch(path,{
        method:"POST",
        headers:{
            "Content-Type":"application/json"
        },
        body:postString
    }).then(response=>{
        if(response.ok){
            user.password=null;
            user["profilePictureURL"]="/imgs/Profile_avatar_placeholder_large.png";
            redirectOnLogin(user);
        }else if(response.status===409){
            alert("Email already registered!");
        }else{
            alert("Invalid Data!");
        }
    }).catch((e)=>{
        console.log(e);
    });
}
function redirectOnLogin(rt) {
    localStorage.setItem("email",rt.email);
    localStorage.setItem("name",rt.name);
    localStorage.setItem("profilePictureURL",rt.profilePictureURL);

    window.location.href="/logged/logged.html";
}
function processLoginData(email,password) {
    let postString =JSON.stringify({
        email: email,
        password: password 
    });
    let path="rest/login/op2";
    fetch(path,{
        method:"POST",
        headers:{
            "Content-Type":"application/json"
        },
        body:postString
    }).then(response=>{
        if(response.ok){
            return response.json();
        }else if(response.status===401){
            alert("Email or Password is wrong!");
        }else{
            alert("Invalid Data!");
        }
    }).then(obj=>{
        if(obj){
            redirectOnLogin(obj);
        }
    }).catch(e=>{
        console.log(e);
    });
}

function registerUserForm() {
    let regform = document.getElementById("myForm");
    regform.onsubmit=(e)=>{
        e.preventDefault();
        
        let name = document.getElementById("inp").value;
        let email = document.getElementById("inp2").value;
        let password = document.getElementById("inp3").value;
        let conf_password = document.getElementById("inp4").value;
    
        if(name.trim()===""){
            alert("Name is invalid!");
            return false;
        }
    
        if(password!==conf_password){
            alert("Passwords do not match!");
            return false;
        }
    
        registerUser(name,email,password);

        return false;
    }
}

function handleLogin() {
    let formS=document.getElementById("login_form");
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

function showLoginForm() {
    let hideClassName="hidregfrm";
    let openAccountBtn = document.getElementById("open_acc");
    let goToLoginBtn=document.getElementById("gtlogin");
    openAccountBtn.onclick=()=>{
        openAccountBtn.parentElement.classList.toggle(hideClassName);
        document.getElementById("myForm").classList.toggle(hideClassName);
    }
    goToLoginBtn.onclick=()=>{
        openAccountBtn.click();
    }
}

handleLogin();
registerUserForm();
showLoginForm();