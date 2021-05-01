import React from 'react'
import {Link} from 'react-router-dom';
import "./front.css";

function LoginNav() {
    return(
        <nav className="pr_nav">
            <div>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
            </div>
        </nav>
    );  
}
export default LoginNav;