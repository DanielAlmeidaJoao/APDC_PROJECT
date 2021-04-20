function handleFormSubmition() {
    let inputForm = document.getElementById("form_ele");
    let inputElement = document.getElementById("oth_email");
    inputForm.addEventListener('submit', function(e) {
        e.preventDefault();
        if(inputElement.value.trim()!=""){
            submitForm(inputElement.value.trim());
        }
    });

    let formDis = document.getElementById("form_dis");
    formDis.addEventListener('submit',(e)=>{
        e.preventDefault();
        disableUser();
    });
}

function submitForm(otherUserEmail) {
    console.log("I WAS CLICKED!");
    console.log(token);
    console.log(otherUserEmail);
    let xmlHttpReq = getHttpXmlRequest();
	xmlHttpReq.onreadystatechange = function(){
		if(xmlHttpReq.readyState == 4 && xmlHttpReq.status == 200){
			let rt = xmlHttpReq.responseText;
            rt = JSON.parse(rt);
            if(rt.status=="1"){
                fillOtherUserInfo(rt);
            }else if(rt.status=="-1"||rt=="-2"){
                alert("SESSION ENDED!");
                localStorage.clear();
                window.location.href="/register.html";
            }else if(rt.status=="-5"){
                alert("UNEXCPECTED ERROR, PLEASE REPORT TO THE SUPPORT TEAM!");
            }else if(rt.status=="-3"){
                alert("ONLY GBO USERS ARE ALLOWED!");
            }else if (rt.status=="-4"){
                alert("UNKNOWN USER EMAIL!");
            }else{
                console.log(rt);
            }
		}
	}
	let postString =JSON.stringify({
        email: token,
        password: otherUserEmail 
    });
	xmlHttpReq.open("POST","../rest/login/op9",true);
	xmlHttpReq.setRequestHeader("Content-Type", "application/json");
	xmlHttpReq.send(postString);
}

function fillOtherUserInfo(obj) {
    let elems = document.getElementsByClassName("srch_attr_val");
    elems[0].textContent=obj.name;
    elems[1].textContent=obj.email;
    elems[2].textContent=obj.role;
    elems[3].textContent=obj.state;
}
function getCheckedAttribute() {
    let checkss = document.getElementsByName("attrs");
    for(let x=0;x<checkss.length;x++){
        if(checkss[x].checked){
            return checkss[x].value;
        }
    }
    
}
function clearInputs(params) {
    for(let x=0;x<params.length;x++){
        params[x].value="";
    }
}
function disableUser() {
    let otherEmail = document.getElementById("dis_us1").value.trim();
    let password = document.getElementById("dis_us2").value.trim();
    let attributeValue = document.getElementById("dis_us3").value.trim();
    let attribute = getCheckedAttribute();
    alert(attribute+"-- checkde");
    if(otherEmail==""||password==""||attributeValue==""){
        alert("No EMpty Strings!");
        return;
    }
    let xmlHttpReq = getHttpXmlRequest();
	xmlHttpReq.onreadystatechange = function(){
		if(xmlHttpReq.readyState == 4 && xmlHttpReq.status == 200){
			let rt = xmlHttpReq.responseText;
            if(rt=="1"){
                clearInputs(params);
            }else if(rt=="-1"||rt=="-2"){
                alert("SESSION ENDED!");
                localStorage.clear();
                window.location.href="/register.html";
            }else if(rt=="-5"){
                alert("UNEXCPECTED ERROR, PLEASE REPORT TO THE SUPPORT TEAM!");
            }else if(rt=="-3"){
                alert("ONLY GBO USERS ARE ALLOWED!");
            }else if (rt=="-4"){
                alert("UNKNOWN USER EMAIL!");
            }else{
            }
            console.log(rt);
            console.log(postString);
		}
	}
    //attribute, email, password, token, attributeValue;
	let postString =JSON.stringify({
        attribute: attribute,
        email: otherEmail,
        password: password,
        token: token,
        attributeValue: attributeValue
    });
	xmlHttpReq.open("POST","../rest/login/op10",true);
	xmlHttpReq.setRequestHeader("Content-Type", "application/json");
	xmlHttpReq.send(postString);
}
//console.log("I AM TOKEN!!!!!");
//handleFormSubmition();