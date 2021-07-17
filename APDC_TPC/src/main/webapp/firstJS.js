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
function registerUser(name,email,password,vcode){
    let user={
        name: name,
        email: email,
        password: password,
        vcode: vcode
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
        }else if(response.status==406){
            alert("Ooops, wrong code!");
        }else{
            alert("Invalid Data!");
        }
    }).catch((e)=>{
        console.log(e);
    });
}
function isTheUserLogged() {
    email = localStorage.getItem("email");
    if(email!=null){
        window.location.href="/logged/logged.html";
    }
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
    let submitVcode=document.getElementById("sbmvc");
    let regform = document.getElementById("myForm");
    let name = document.getElementById("inp").value;
    let email = document.getElementById("inp2").value;
    let password = document.getElementById("inp3").value;
    let conf_password = document.getElementById("inp4").value;
    regform.onsubmit=(e)=>{
        e.preventDefault();
        
        name = document.getElementById("inp").value;
        email = document.getElementById("inp2").value;
        password = document.getElementById("inp3").value;
        conf_password = document.getElementById("inp4").value;
    
        if(name.trim()===""){
            alert("Name is invalid!");
            return false;
        }
    
        if(password!==conf_password){
            alert("Passwords do not match!");
            return false;
        }
        if(!validatePassword(password)){
            return false;
        }
        /*
        if(password.length<6||password.length>15){ //Password Length Restriction
            alert("Password length must be greater than 6 and less than 16!");
            return false;
        }*/
        //Handle Verification Code Ops
        {
            fetch(`/rest/login/vcd/${email}?n=${email}`)
            .then(response=>{
                if(response.ok){
                    document.getElementById("emplh").textContent=email;
                    document.getElementById("myForm").classList.add("hidregfrm");
                    document.getElementById("myForm2").classList.remove("hidregfrm");
                }else if(response.status==409){
                    alert("Email Already Registered!")
                }else {
                    alert("Unexpected Error!");
                }
            }).catch(err => {
                console.log("ERROR: "+err);
            })
        }
        //Try Again Button on Verification Code Div
        document.getElementById("trgrg").onclick=()=>{
            document.getElementById("myForm").classList.remove("hidregfrm");
            document.getElementById("myForm2").classList.add("hidregfrm");

        }
        return false;
    }
    submitVcode.onclick=()=>{
        registerUser(name,email,password,document.getElementById("inpvc").value);
    }
}

function handleVerificationCodeOps(){
    document.getElementById("myForm").classList.add("hidregfrm");
    document.getElementById("myForm2").classList.remove("hidregfrm");
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
        document.getElementById("rgdv").classList.toggle(hideClassName);
    }
    goToLoginBtn.onclick=()=>{
        openAccountBtn.click();
    }
}
function validatePassword(pass) {
    let errors = [];
    if (pass.length < 8) {
        errors.push("Your password must be at least 8 characters");
    }
    if(pass.length>15){
        errors.push("Your password must be at less than 15 characters");
    }
    if (pass.search(/[a-z]/i) < 0) {
        errors.push("Your password must contain at least one letter."); 
    }
    if (pass.search(/[0-9]/) < 0) {
        errors.push("Your password must contain at least one digit.");
    }
    if (errors.length > 0) {
        alert(errors.join("\n"));
        return false;
    }
    return true;
}
handleLogin();
registerUserForm();
showLoginForm();
isTheUserLogged();