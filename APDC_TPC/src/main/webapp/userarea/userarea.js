let profileFile;
const imgeleParent = document.getElementById("pfpc_prnt");
const imgele = document.getElementById("prfl_img");
const prfpc_btns=document.getElementById("prfpc_btns");
const ShowProfilePicBtnsclassName="showProileBtns";


function uploadProfilePicture(){
    let uploadImg = document.getElementById("profileimg");
    let caption = document.getElementById("cpt");
    uploadImg.onchange=function(){
        const file = this.files[0];
        profileFile=file;
        if(file){
            const reader = new FileReader();
            reader.onload=function () {
                imgele.setAttribute("src",this.result);
                imgele.setAttribute("alt",file.name);
                document.getElementById("updateImgPic").classList.remove("updateImgPic");
            }
            reader.readAsDataURL(file);
        }
    }
}
imgeleParent.onmouseover=()=>{
    prfpc_btns.classList.toggle(ShowProfilePicBtnsclassName);
}
imgeleParent.onmouseout=()=>{
    prfpc_btns.classList.remove(ShowProfilePicBtnsclassName);
}
uploadProfilePicture();

function formDataForProfilePicture(){
    let formData = new FormData();	
    formData.append("profilePicture",profileFile);
    return formData;
}
function updateProfilePicture() {
    const updatePic = document.getElementById("updateImgPic");
    let path="/rest/login/savep";
    updatePic.onclick=()=>{
        //do the update
        fetch(path,{
            method: 'POST', // or 'PUT'
            body:formDataForProfilePicture()
        }).then(response=>{
            profileFile=null;
            return response.json();
        }).then(text=>{
            console.log("Text "+text);
            if(text){
                document.getElementById("updateImgPic").classList.add("updateImgPic");
                document.getElementById("nav_profile_pic").setAttribute("src",text);
            }else{
                alert("Unexpected Error!");
            }
        }).catch(err=>{
            console.log(err);
        })
    }
}
function handleEditName(ownProfile) {
    if(!ownProfile){
        return;
    }
    let editBtn = document.createElement("button");
    editBtn.textContent="EDIT";
    let saveBtn = document.createElement("button");
    saveBtn.textContent="SAVE";
    let nameHolder = document.getElementById("vsb_btn");
    document.getElementById("nmdv").appendChild(editBtn);

    editBtn.onclick=()=>{
        nameHolder.classList.add("ctedspn");
        nameHolder.setAttribute("contenteditable","true");
        editBtn.parentElement.appendChild(saveBtn);
        editBtn.remove();
    }

    saveBtn.onclick=()=>{
        fetch(`/rest/login/updatename/${nameHolder.textContent}`,{
            method:'PUT'
        }).then(response=>{
            nameHolder.classList.remove("ctedspn");
            nameHolder.setAttribute("contenteditable","false");
            if(response.ok){
                alert("Name Updated!");
                saveBtn.parentElement.appendChild(editBtn);
            }else if(response.status==401){
                alert("Session Ended!");
            }else if(response.status==404){
                alert("Unknown User!");
            }
            saveBtn.remove();
        });
    }
}
function handleEditBtn(ownProfile) {
    let editBtn = document.getElementById("edt_inf");
    if(!ownProfile){
        editBtn.remove();
        return;
    }
    let saveBtn = document.createElement("button");
    saveBtn.textContent="SAVE";
    editBtn.onclick=()=>{
        let textas=document.getElementsByClassName("txta");
        for (let index = 0; index < textas.length; index++) {
            const element = textas[index];
            element.classList.add("shwtxa");
            element.previousElementSibling.classList.add("hdtxa");
            element.value=element.previousElementSibling.innerHTML;
        }
        let links = document.getElementById("contacts");
        let linkInputs = document.getElementById("scl_ntwks");
        links.classList.add("hdtxa");
        linkInputs.classList.remove("hdtxa");
        for(let x = 0; x<links.childElementCount;x++){
            const ele = links.children[x];
            if(ele.hasAttribute("href")){
                linkInputs.children[x].value = ele.getAttribute("href");
            }
        }
        editBtn.parentElement.replaceChild(saveBtn,editBtn);
    }
    let postData = {
        quote:"",
        bio:"",
        instagram:"",
        facebook:"",
        twitter:"",
        website:""
    }
    saveBtn.onclick=()=>{
        let textas=document.getElementsByClassName("txta");
        for (let index = 0; index < textas.length; index++) {
            const element = textas[index];
            element.classList.remove("shwtxa");
            element.previousElementSibling.classList.remove("hdtxa");
            element.previousElementSibling.innerHTML=element.value;
            postData[element.getAttribute("name")]=element.value;

        }
        
        let links = document.getElementById("contacts");
        let linkInputs = document.getElementById("scl_ntwks");
        links.classList.remove("hdtxa");
        linkInputs.classList.add("hdtxa");
        for(let x = 0; x<linkInputs.childElementCount;x++){
            const ele = linkInputs.children[x];
            if(ele.value.trim()){
                links.children[x].setAttribute("href",ele.value);
                postData[ele.getAttribute("name")]=ele.value;
            }
        }
        saveBtn.parentElement.replaceChild(editBtn,saveBtn);
        updateInfos(postData);
    }
}
function updateInfos(obj){
    let postString=JSON.stringify(obj);
    let path="../rest/login/op3";
    fetch(path,{
        method:"POST",
        headers:{
            "Content-Type":"application/json",
            "Accept-Charset":"utf-8"
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
}
function handleEditEmailButton(ownProfile) {
    let emailParentDiv = document.getElementById("wrprt");
    let editBtn = document.getElementById("dtamil");
    if(!ownProfile){
        document.getElementById("dtmlla").remove();
        editBtn.parentElement.remove();
        return;
    }
    emailParentDiv.classList.remove("hdtxa");
    let firstInputs = document.getElementById("frstpts");
    let secInputs = document.getElementById("secpts");
    let changeBtn = document.getElementById("gobtn");
    let submitVerificationCodeBtn = document.getElementById("sbmvc");
    let obj=null;
    editBtn.onclick=()=>{
        firstInputs.classList.remove("hdtxa");
    }
    changeBtn.onclick=()=>{
        obj={
            newEmail:firstInputs.children[0].value,
            password:firstInputs.children[1].value
        }
        fetch(`../rest/login/chgmailvcd`,{
            method:'POST',
            headers:{
                "Content-Type":"application/json",
            },
            body:JSON.stringify(obj)
        }).then(response => {
            if(response.ok){
                secInputs.classList.remove("hdtxa");
                firstInputs.remove();
            }else if(response.status==409){
                alert("Email Registered Already!");
            }else if(response.status==404){
                alert("Unknown User!");
            }else if(response.status==403){
                alert("Wrong Password!");
            }else if(response.status==401){
                alert("Session Expired!");
            }
        })
    }
    submitVerificationCodeBtn.onclick=()=>{
        obj.password=document.getElementById("vcdpt").value;
        fetch(`../rest/login/chgmail`,{
            method:'PUT',
            headers:{
                "Content-Type":"application/json",
            },
            body:JSON.stringify(obj)
        }).then(response => {
            if(response.ok){
                alert("Email Changed!");
                document.getElementById("emlspn").textContent=obj.newEmail;
                document.getElementById("dtmlla").remove();
                editBtn.remove();
            }else if(response.status==404){
                alert("Unknown User!");
            }else if(response.status==406){
                alert("Wrong Code!");
            }else if(response.status==401){
                alert("Session Expired!");
            }
        })
    }
}
//handleEditEmailButton(true);
updateProfilePicture();