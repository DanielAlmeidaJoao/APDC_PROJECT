import React from 'react'
import {Link} from 'react-router-dom';
import './main_page/mainpage.css';


function NavBar() {
    return(
        <nav className="pr_nav">
            <div  className="nav_links">
            <Link className="lkto" to="/">Home</Link>
            <Link className="lkto" to="/about">About</Link>
            <Link className="lkto" to="/contacts">Contacts</Link>
            </div>
        </nav>
    );  
}
export default NavBar;