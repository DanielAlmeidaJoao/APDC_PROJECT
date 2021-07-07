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
function singleCommentBlockHtmlStr(element){
    let removeBtn="";
    if(element.owner){
        removeBtn=`<button class="rmvcmt" onclick=removeComment(${element.commentid},this)>REMOVE</button>`;
    }
    return `<div class="snglsmt">
        <div class="orgd"><img class="nav_img_prfl cmtpfimg" src=${element.urlProfilePicture} alt="profile-pic"></div>
        <div>
            <div class="cmtername">
                <span class="cmtauthnm">${element.ownerName}</span>
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
        console.log(list);
        for (let index = 0; index < list.length; index++) {
            const element = list[index];
            console.log(element);
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