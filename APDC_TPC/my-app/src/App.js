import logo from './logo.svg';
import './App.css';
import Home from './Home';
import Contacts from './Contacts';
import About from './About';
import NavBar from './NavBar';

import {Route, Link} from 'react-router-dom'
import SignInForm from './frontpage/front';
import LoginNav from './frontpage/LoginNav';
import Login from './login/Login';
import Register from './register/Register';


function App() {
  return (
    <div className="App">
      {/**
   *  <NavBar/>
      <Route exact path="/" component={Home} />
      <Route exact path="/about" component={About} />
      <Route exact path="/contacts" component={Contacts} />
   */}
   {
     /**
      *     <SignInForm/>

      */
   }
      <Route exact path="/" component={Login}/>
      <Route exact path="/login" component={Login}/>
      <Route exact path="/register" component={Register}/>
    </div>
  );
}

export default App;
