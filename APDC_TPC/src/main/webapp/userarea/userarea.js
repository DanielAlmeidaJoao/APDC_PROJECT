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
function handleEditBtn() {
    document.getElementById("edt_inf").onclick=()=>{
        let textas=document.getElementsByClassName("txta");
        for (let index = 0; index < textas.length; index++) {
            const element = textas[index];
            element.classList.add("shwtxa");
            element.previousElementSibling.classList.add("hdtxa");
            element.value=element.previousElementSibling.innerHTML;
        }
    }
}
handleEditBtn();
updateProfilePicture();