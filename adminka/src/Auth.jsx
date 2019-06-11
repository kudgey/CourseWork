import React, { Component } from 'react';
import { Redirect } from 'react-router-dom';
import {  Error, error } from './List.jsx'
import { Loader } from './Loader.jsx';
import axios from 'axios';

const BASE = "http://localhost:8081/"

class Sign extends Component {
    constructor(props) {
        super(props);
        this.state = {
            username: "",
            password: "",
            isAuth: false,
            error: ""
        };
    }

    handleSubmit = event => {
        event.preventDefault();
        const isReg = !this.props.isIn;
        const url = BASE + (isReg ? "signup" : "login");
        const user = {
            username: this.state.username,
            password: this.state.password
        }
        console.log(this.props.location);
        axios.post(url, user)
        .then(r => {
            localStorage.setItem("key", JSON.stringify({
                headers: {"Authorization": "Basic " + window.btoa(user.username+":"+user.password)}
            }));
            localStorage.setItem("name", user.username);
            localStorage.setItem("role", r.data.role);
            localStorage.setItem("id", r.data.id)
            this.props.location.myProp.setState({ state: this.props.location.myProp.state });
            this.setState({
                isAuth: true,
            });
        })
        .catch(e => error(e, this));
        console.log(this.props.location);
    }

    handleInputChange = (event) => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

        this.setState({
          [name]: value
        });
    }

    render() {
        console.log(this.props.location);
        document.title = (this.props.isIn ? "Login" : "Sign up");
        if (this.state.isAuth) {
           return <Redirect to='/' />;
         }
        const buttonValue = this.props.isIn ? "Login" : "Sign up";
        const error = this.state.error ? <Error text={this.state.error} /> : "";
        return (
            <div>
                {error}
                <h1>{document.title}</h1>
                <form onSubmit={this.handleSubmit} className="form-vertical m-5">
                    <div className="form-group" onSubmit={this.handleSubmit}>
                        <label htmlFor="username">Username</label>
                        <input type="text" className='form-control' onChange={this.handleInputChange} name="username" id="username" />
                    </div>
                    <div className="form-group">
                        <label htmlFor="password">Password</label>
                        <input type="password" className='form-control' onChange={this.handleInputChange} name="password" id="password" />
                    </div>
                    <input type="submit" className="btn btn-primary btn-login" value={buttonValue} />
                </form>
            </div>
        );
    }
}

class Logout extends Component {
    constructor(props) {
        super(props);
        this.state = {
            success: false
        }
    }
    componentDidMount() {
        localStorage.removeItem("key");
        localStorage.removeItem("name");
        localStorage.removeItem("role");
        localStorage.removeItem("id");
        this.props.location.myProp.setState({ state: this.props.location.myProp.state });
        this.setState({
            success: true
        });
    }
    render() {
        if(this.state.success)
            return <Redirect to={{
                pathname: "/login",
                myProp: this.props.location.myProp }} />;
        return <Loader />;
        
    }
}

export { Logout, Sign };