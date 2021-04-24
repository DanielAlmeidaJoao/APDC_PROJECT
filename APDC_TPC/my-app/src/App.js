import logo from './logo.svg';
import './App.css';
import Home from './Home';
import Contacts from './Contacts';
import About from './About';
import NavBar from './NavBar';

import {Route, Link} from 'react-router-dom'


function App() {
  return (
    <div className="App">
      <NavBar/>
      <Route exact path="/" component={Home} />
      <Route exact path="/about" component={About} />
      <Route exact path="/contacts" component={Contacts} />
    </div>
  );
}

export default App;
