
import React from 'react';
import {Link} from 'react-router-dom';

function DoRegister() {
    
}

function Register() {
    return(
    <div>
        <form className={"register_form"}>
            <label>Email</label>
            <input type="email"></input>
            <label>UserName</label>
            <input type={"text"}/>
            <label>Password</label>
            <input type={"password"}/>
            <button onClick={DoRegister}>Register</button>
        </form>
        <Link to="/login">Login</Link>
    </div>

    )
}

export default Register;