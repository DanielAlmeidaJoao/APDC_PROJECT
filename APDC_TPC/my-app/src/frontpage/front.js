import { useState } from "react";
import '../frontpage/front.css';


function SignInForm() {
    const [logged, setLogged]=useState(false);
    const [username, setUserName]=useState("");
    const [password, setPassword]=useState("");

    function handleChangeUserName(e:ChangeEvent<HTMLInputElement>) {
        setUserName(e.target.value);
    }
    function handleChangePassword(e:ChangeEvent<HTMLInputElement>) {
        setPassword(e.target.value);
    }
    function register() {
        
    }
    function cancel(){

    }
    function handleSubmitForm(e:any) {
        e.preventDefault();
    }
    return (
        <div>
            {!logged && 
            <div>
                <button>Sign In Here</button> 
                <button>Register</button>
            </div>
            }
            {
            <form onSubmit={handleSubmitForm} className={"login_form"}>
                <label>UserName</label>
                <input type={"text"} value={username} onChange={handleChangeUserName}/>
                <label>Password</label>
                <input type={"password"} value={password} onChange={handleChangePassword}/>
                <button onClick={register}>Register</button>
                <button onClick={cancel}>Cancel</button>
            </form>
            }
            
        </div>
        
    );
}


export default SignInForm;