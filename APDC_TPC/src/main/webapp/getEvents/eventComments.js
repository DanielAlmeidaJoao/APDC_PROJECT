let hideDivClassName="hidecmts";
function removeComment(commentId,btn) {
    fetch("/rest/comments/remove/"+commentId,{
        method:"DELETE"
    })
    .then(response=>{
        if(response.ok){
            let commentCounter = btn.parentElement.parentElement.parentElement.parentElement.querySelector(".cnt_cmts");
            updateNumberOfElements(null,false,commentCounter);
            btn.parentElement.remove();
        }else{
            alert("BAD REQUEST!");
        }
    }).catch(err => {
        console.log("Error "+err);
    })
}
function goToProfilePageFromComments(userid,btn){
    btn.setAttribute("target","_blank");
    btn.setAttribute("href","../profile/profile.html");
    localStorage.setItem("ot",userid);
    btn.click();
}
function singleCommentBlockHtmlStr(element){
    let removeBtn="";
    if(element.owner){
        removeBtn=`<button class="rmvcmt" onclick=removeComment(${element.commentid},this)>REMOVE</button>`;
    }
    return `<div class="snglsmt">
        <a class="orgd cmtpfp" onclick=goToProfilePageFromComments(${element.ownerId},this)><img class="nav_img_prfl cmtpfimg" src=${element.urlProfilePicture} alt="profile-pic"></a>
        <div>
            <div class="cmtername">
                <a class="cmtauthnm" onclick=goToProfilePageFromComments(${element.ownerId},this)>${element.ownerName}</a>
                <span class="cmtdate">${element.date}</span>
            </div>
            <div>${element.comment}</div>
        </div>
        ${removeBtn}
    </div>`;
}
function handleShowCommentsButton(btn,eventid){
    const LOAD_DATA_CURSOR="data-crsk";
    btn.parentElement.children[1].classList.toggle(hideDivClassName);
    let allcomentsBlock = btn.parentElement.children[1].querySelector(".allcmts");
    let cursor = allcomentsBlock.getAttribute(LOAD_DATA_CURSOR);
    if(!cursor){
        cursor="";
    }
    fetch(`/rest/comments/load/${eventid}?c=${cursor}`)
    .then(response=>{
        return response.json();
    }).then(obj=>{
        let list = obj.comments;
        for (let index = 0; index < list.length; index++) {
            const element = list[index];
            let str = singleCommentBlockHtmlStr(element);
            allcomentsBlock.appendChild(stringToDom(str));
        }
        allcomentsBlock.setAttribute(LOAD_DATA_CURSOR,obj.cursor);
    }).catch(err=>{
        console.log("ERROR -> "+err);
    });
}
function publishComment(btn,eventid){
    let textArea = btn.parentElement.children[0];
    if(textArea.value.trim()==""){
        alert("Write Something!");
        return;
    }

    fetch("/rest/comments/create",{
        method:"POST",
        headers:{
            "Content-Type":cTypes.GSON
        },
        body:JSON.stringify({comment:textArea.value, eventid:eventid})
    }).then(response=>{
        if(response.status==OKOK){
            return response.json();
        }else{
            alert("Unexpected Error Ocurred!");
            return null;
        }
    }).then(commentObj=>{
        if(commentObj){
            let commentBlock=singleCommentBlockHtmlStr(commentObj);
            btn.parentElement.parentElement.children[1].appendChild(stringToDom(commentBlock));
            textArea.value="";
            let commentCounter = btn.parentElement.parentElement.parentElement.querySelector(".cnt_cmts");
            updateNumberOfElements(null,true,commentCounter);
        }
    })
    .catch(e=>{
        console.log(e);
    })
}