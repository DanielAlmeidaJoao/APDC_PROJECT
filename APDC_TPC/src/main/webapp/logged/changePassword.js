function changePasswordFunc() {
    let inputFormPwd = document.getElementById("chg_pwd");

    inputFormPwd.addEventListener("submit",(e)=>{
        e.preventDefault();
        submitPasswords();
    });
}

function submitPasswords() {
    let inpts = document.getElementsByName("chg_pwd");
    let oldPass = inpts[0].value.trim();
    let newPass = inpts[1].value.trim(); 
    let newPassConf = inpts[2].value.trim();
    if(oldPass==""||newPass==""||newPassConf==""){
        alert("PLEASE, INSERT VALID DATA!");
        return;
    }
    if(newPass!=newPassConf){
        alert("THE NEW AND THE PASSWORD CONFIRMATION DO NOT MATCH!");
        return;
    }

    let xmlHttpReq = getHttpXmlRequest();
	xmlHttpReq.onreadystatechange = function(){
		if(xmlHttpReq.readyState == 4 && xmlHttpReq.status == 200){
			let rt = xmlHttpReq.responseText;
            if(rt=="1"){
                alert("PASSOWRD UPDATED!")
            }else if(rt=="-1"){
                alert("OLD PASSWORD DO NOT MATCH!")
            }else{
                localStorage.clear();
                window.location.href="/register.html";
            }
            clearInputs(inpts);
		}
	}
    //attribute, email, password, token, attributeValue;
	let postString =JSON.stringify({
        name: oldPass,
        password: newPass,
        email: token,
    });
	xmlHttpReq.open("POST","../rest/login/op11",true);
	xmlHttpReq.setRequestHeader("Content-Type", "application/json");
	xmlHttpReq.send(postString);
}
//changePasswordFunc();