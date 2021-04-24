
import React from 'react';
import {Link} from 'react-router-dom';
function DoLogin() {
    
}

function Login() {
    return(
    <div className={"colmidl login_div"}>
        <form className={"colmidl login_form"}>
            <label className="cmnst">UserName</label>
            <input className="cmnst" type={"text"}/>
            <label className="cmnst" >Password</label>
            <input className="cmnst" type={"password"}/>
            <button className="lgbtn" onClick={DoLogin}>Login</button>
        </form>
    <Link to="/register" className="lgbtn rgbtn">Register</Link>
    </div>
    )
}

export default Login;