import React, { Component } from 'react';
import { NavLink } from 'react-router-dom';
// import logout from './images/logout.png';
// import login from './images/login1.png';
// import axios from 'axios';
// import { BASE } from './constants';

class Menu extends Component {
    render() {
        const isLogged = localStorage.length;
        let login, signup, logout, hail;
        if (isLogged) {
            hail = "Hello, " + localStorage.getItem("name");
            logout = <NavLink className='nav-link nav-item' to={{
                pathname: "/logout",
                myProp: this
            }}>Logout</NavLink>;
        } else {
            hail = "Hello, Anonymous";
            login = <NavLink className='nav-link nav-item' to={{
                pathname: "/login",
                myProp: this
            }}>Login</NavLink>;
            signup = <NavLink className='nav-link nav-item' to={{
                pathname: "/signup",
                myProp: this
            }}>Signup</NavLink>;
        }
        return (
            <nav className='nav'>
                {<NavLink className='nav-link nav-item' to="/command">Commands</NavLink>}
                {<NavLink className='nav-link nav-item' to="/mycommand">My Commands</NavLink>}
                {<NavLink className='nav-link nav-item' to="/args">Arguments</NavLink>}
                {<NavLink className='nav-link nav-item' to="/langs">Languages</NavLink>}
                {<NavLink className='nav-link nav-item' to="/ccommand">Complex command</NavLink>}
                {<NavLink className='nav-link nav-item' to="/cbox">Commands boxes</NavLink>}
                {login}
                {signup}
                <span className='welcomeText nav-link nav-item'>{hail}</span>
                {logout}
            </nav>
        );
    }
}

export { Menu };