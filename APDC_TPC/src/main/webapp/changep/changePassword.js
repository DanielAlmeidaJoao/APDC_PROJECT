const toggleClassName="hidregfrm";
function handleChangePassword(){
    let postData=null;
    let changeBtn = document.getElementById("chgbtn");
    let submitVcode=document.getElementById("sbm");
    changeBtn.onclick=()=>{
        postData=validPassword();
        if(postData!=null){
            fetch("/rest/login/vcd/"+postData.email)
            .then(response=>{
                if(response.status==404){
                    alert("Email Not REgistered!");
                }else{
                    changeBtn.parentElement.classList.add(toggleClassName);
                    document.getElementById("myForm").classList.remove(toggleClassName);
                }
            }).catch(err => {
                console.log("ERROR: "+err);
            })
        }
    }
    submitVcode.onclick=()=>{
        postData["vcode"]=document.getElementById("inp4").value;
        fetch("/rest/login/chgpwd",{
            method:"POST",
            headers:{
                "Content-Type":"application/json"
            },
            body:JSON.stringify(postData)
        })
        .then(response=>{
            if(response.status==200){
                alert("Password Changed!");
                window.location.href="../";
            }else if(response.status==404){
                alert("Ooops, something went wrong!");
            }else if(response.status==406){
                alert("Ooops, wrong code!");
            }
        }).catch(err => {
            console.log("ERROR: "+err);
        })
    }
}
/**
 * {
                
    }
 */
function tryAgainButton() {
    let tryAgain = document.getElementById("gtlogin");
    tryAgain.onclick=()=>{
        document.getElementById("myForm").classList.toggle(toggleClassName);
        document.getElementById("login_form").classList.toggle(toggleClassName);
    }
}
function validPassword(){
    let pass1 = document.getElementById("login_password").value;
    let pass2 = document.getElementById("confirm_login_password").value;
    if(pass1!=pass2){
        alert("Passwords must match!");
        return null;
    }
    if(pass1.length<6){
        alert("Password length must be greater than 6!");
        return null;
    }
    let email = document.getElementById("login_email").value;
    return {email:email,password:pass1};
}
handleChangePassword();
tryAgainButton();