// import logo from './logo.svg';
import React, { Component } from 'react';
import { BrowserRouter as Router, Route } from 'react-router-dom';
import './App.css';
import { List, KeyValue } from './List.jsx';
import { AddArgument, AddLang, AddCommand, AddCommandBox, AddCCommand } from './Actions.jsx';
import {Menu} from './Menu.jsx';
import {Logout, Sign} from "./Auth.jsx";

const BASE = "http://localhost:8080/api/";


class App extends Component {
  render() {
  return (
    <Router basename="/">
      <div >
        <Menu />

        <Route exact path="/" render={() => 
        <div className="alert alert-success" role="alert">
          <h4 className="alert-heading">Welcome</h4>
          <p>I am very glad to see you here</p>
          <hr />
        </div>
        } />

        <Route exact path="/command" render={() => <List source={'commands'} type={"c"} title="Commands" notMy={true} />} />
        <Route exact path="/mycommand" render={() => <List source={'commands/byuser/' + localStorage.getItem("id")} notMy={false} type={"c"} title="My commands" />} />
        <Route exact path="/command/detail/:id" render={(props) => <KeyValue {...props} source={'commands/'} title="Command" />} />
        
        <Route exact path="/args" render={() => <List source={'args'} type={"a"} title="Arguments" />} />
        <Route exact path="/langs" render={() => <List source={'lang'} type={"l"} title="Languages" />} />
        <Route exact path="/ccommand" render={() => <List source={'complexcommand'} type={"cc"} title="Complex commands" />} />
        <Route exact path="/cbox" render={() => <List source={'cbox'} type={"cb"} title="Commands boxes" notMy={true} />} />

        <Route exact path="/args/edit/:id" component={AddArgument} />  
	      <Route exact path="/args/edit" component={AddArgument} />  

        <Route exact path="/langs/edit/:id" component={AddLang} />  
	      <Route exact path="/langs/edit" component={AddLang} />  

        <Route exact path="/command/edit/:id" component={AddCommand} />  
	      <Route exact path="/command/edit" component={AddCommand} />  

        <Route exact path="/cbox/edit/:id" component={AddCommandBox} />  
	      <Route exact path="/cbox/edit" component={AddCommandBox} />  

        <Route exact path="/ccommand/edit/:id" component={AddCCommand} />  
	      <Route exact path="/ccommand/edit" component={AddCCommand} />  

        <Route exact path="/logout" component={Logout} />
        <Route exact path="/login" render={(props) => <Sign {...props} isIn={true} />} />
        <Route exact path="/signup" render={(props) => <Sign {...props} isIn={false} />} /> 
      </div>
    </Router>
  );
  }
}

export { App, BASE };
