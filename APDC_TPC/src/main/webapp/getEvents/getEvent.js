function showGetEventsBlock() {
    let createEvent = document.getElementById("get_evts_btn");
    createEvent.onclick=()=>{
        hideAllBlocksButOne("show_events_blk");
    }
}

showGetEventsBlock();