import React, { Component } from 'react';
import axios from 'axios';
import { Link, Redirect } from 'react-router-dom';
import {BASE} from './App.jsx';
// import { capitalize } from "./App.jsx";
import { Loader } from './Loader.jsx';

const BASE_AUTH = "http://localhost:8081/"
// const status = resp => {
//     console.log(resp);
//     return (resp.status >= 200 && resp.status < 300) ? 
//     Promise.resolve(resp) : 
//     Promise.reject(new Error(resp.statusText));
// } 
const json = resp => resp.data;
// const log = resp => console.log(resp);
const error = (e, th) => {
    var err = "";
    console.log(e);
    console.log(e.response);
    if (!e.response) {
        err = 'Network error';
    } else {
        if (e.response.status === 404)
            err = "404 error"
        else
            err = typeof e.response.data === "string" ? e.response.data : e.response.data.message;
    }
    th.setState({
        error: err,
        isReady: true
    })
};

const command_props = ["Name","Description","Phrase","Price","Is open","Lang","Arguments", "Modified"];
const args_props = ["Name", "Description", "Regex pattern", "Type"]
const lang_props = ["Name", "Description", "Short name"]
const cc_props = ["Name", "Description", "Phrase", "Given arguments", "Commands"]
const cbox_props = ["Name", "Description", "Price", "Commands"]

class Error extends Component {
    render() {
        return (
            <div className="alert alert-danger m-5" role="alert">
              {this.props.text}
            </div>
            );
    }
  }

// class Success extends Component {
//     render() {
//         return (
//             <div className="alert alert-success m-5" role="alert">
//               {this.props.text}
//             </div>
//             );
//     }
//   }


export default class List extends Component {
    constructor(props) {
        super(props);
        this.state = {
            entities: [],
            isReady: false,
            myEnts: [],
            error: ""
        };
    }
    componentDidMount() {
        axios.get(BASE + this.props.source, JSON.parse(localStorage.getItem("key")))
        .then(json)
        .then(data => {
            if (this.props.notMy) {
                axios.get(BASE+this.props.source+"/byuser/" + localStorage.getItem("id"), JSON.parse(localStorage.getItem("key")))
                .then(mys => {
                    this.setState({
                        entities: data, 
                        myEnts: mys.data.map(m => (m.id ? m.id : m)),
                        isReady: true
                    });
                })
                .catch(e => error(e, this));
            } else
                this.setState({
                    entities: data, 
                    isReady: true
                });
        })
        .catch(e => error(e, this));
    }
    render() {
        if (!localStorage.length) {
            return <Error text="You need to login or sign up" />;
        }
        if(!this.state.isReady)
            return <Loader />; 
        if(this.state.error) 
            return <Error text={this.state.error} />;

        let data;
        let head;
        let url;
        switch(this.props.type) {
            case "c":
                data = this.state.entities.map(e => <CommandRow command={e} key={e.id} need={this.props.notMy} notMy={(this.props.notMy ? !this.state.myEnts.includes(e.id) : false)} />);
                head = command_props.map(h => <th>{h}</th>);
                url = "command";
                break;
            case "a":
                data = this.state.entities.map(e => <ArgRow arg={e} key={e.id} />);
                head = args_props.map(h => <th>{h}</th>);
                url = "args";
                break;
            case "l":
                data = this.state.entities.map(e => <LangRow lang={e} key={e.id} />);
                head = lang_props.map(h => <th>{h}</th>);
                url = "langs";
                break;
            case "cc":
                data = this.state.entities.map(e => <ComplexCommandRow command={e} key={e.id} />);
                head = cc_props.map(h => <th>{h}</th>);
                url = "ccommand";
                break;
            case "cb":
                data = this.state.entities.map(e => <CBoxRow box={e} key={e.id} notMy={this.props.notMy ? !this.state.myEnts.includes(e.id) : false} />);
                head = cbox_props.map(h => <th>{h}</th>);
                url = "cbox";
                break;
            default:
            break;
        }
        document.title = this.props.title;
        let edit, del, add;
        edit = this.props.notMy === false ? <th>Modify</th> : "";
        if(localStorage.getItem("role") === "Admin"){
            del = <th>Delete</th>;
            edit = <th>Modify</th>;
            add = <Link className="btn btn-success" to={url+"/edit/"}>Add</Link>;
        }
        const buyButton = this.props.notMy ? <th>Buy</th> : "";
        return (
            <div>
                <h1>{this.props.title}</h1>
                {add}
                <table className="table table-bordered">
                    <thead className="thead-dark">
                        <tr>
                            {head}
                            {buyButton}
                            {edit}{del}
                        </tr>
                    </thead>
                    <tbody>
                        {data}
                    </tbody>
                </table>
            </div>            
        )
    }
}

class CommandRow extends Component {
    constructor(props) {
        super(props);
        this.state = {
            error: ""
        };
    }
    handleSubmit = e => {
        e.preventDefault();
        axios.delete(BASE+"commands/" + this.props.command.id, JSON.parse(localStorage.getItem("key")))
        .then(r => this.setState({
            redirect: true
        }))
        .catch(e => error(e, this));
    }
    buy = e => {
        e.preventDefault();
        axios.post(BASE_AUTH+"addcommands", {
            username: localStorage.getItem("name"),
            commands: [this.props.command.id]
        }, JSON.parse(localStorage.getItem("key")))
        .then(r => this.setState({
            redirect: true
        }))
        .catch(e => error(e, this));
    }
    render() {
        if (this.state.redirect) {
            return <Redirect to='/' />;
        }
        let error;
        if(this.state.error) 
            error = <div className='alert alert-danger'>{this.state.error}</div>;
        const command = this.props.command; 
        const args = command.arguments ? command.arguments.map(a => a.name).join(" | ") : "No args";

        let edit, del;
        edit = !this.props.need ? <td><Link className="btn btn-warning" to={"/command/edit/"+command.id}>Modify</Link></td> : "";
        if(localStorage.getItem("role") === "Admin"){
            del = <td><button className="btn btn-danger" onClick={this.handleSubmit}>Delete</button></td>;
            edit = <td><Link className="btn btn-warning" to={"/command/edit/"+command.id}>Modify</Link></td>;
        } 
        let buyButton = "";
        if (this.props.need)
            buyButton = this.props.notMy ? <td><button className="btn btn-primary" onClick={this.buy}>Buy</button></td> : <td>Have it</td>;
        return (
            <tr>
                <td><Link to={"command/detail/" + command.id}>{command.name}</Link></td>
                <td>{command.description}</td>
                <td>{command.phrase}</td>
                <td>{command.price}</td>
                <td>{command.open ? "Yes" : "No"}</td>
                <td>{command.lang ? command.lang.name : ""}</td>
                <td>{args}</td>
                <td>{command.parent ? "Yes" : "No"}</td>
                {buyButton}
                {edit}{del}
                <td>{error}</td>
            </tr>
        );
    }
}

class ComplexCommandRow extends Component {
    constructor(props) {
        super(props);
        this.state = {
            args: [],
            isNotReady: false,
            error: ""
        };
    }
    componentDidMount() {
        axios.get(BASE + "args", JSON.parse(localStorage.getItem("key")))
        .then(res => {
            this.setState({
                args: res.data, 
                isNotReady: true
            });
        })
        .catch(e => error(e, this));
    }
    handleSubmit = e => {
        e.preventDefault();
        axios.delete(BASE+"complexcommand/" + this.props.command.id, JSON.parse(localStorage.getItem("key")))
        .then(r => this.setState({
            redirect: true
        }))
        .catch(e => error(e, this));
    }
    render() {
        if (this.state.redirect) {
            return <Redirect to='/' />;
        }
        let error;
        if(this.state.error) 
            error = <div className='alert alert-danger'>{this.state.error}</div>;
        const command = this.props.command; 
        const commands = command.commands.map((c, i) => <Link to={"command/detail/"+c.id}>{c.name + (i === command.commands.length-1 ? "":" → ")}</Link>);
        const args = command.args ?
        <table className="table">
        <thead>
            <tr>
                <th scope="col">Key</th>
                <th scope="col">Value</th>
            </tr>
        </thead>
        <tbody>
            {Object.entries(command.args).map(([k, v]) => <tr><td>{k}</td><td>{v}</td></tr>)}
        </tbody>
        </table> : "No args";

        
        let edit, del;
        if(localStorage.getItem("role") === "Admin"){
            edit = <td><Link className="btn btn-warning" to={"/ccommand/edit/"+command.id}>Edit</Link></td>;
            del = <td><button className="btn btn-danger" onClick={this.handleSubmit}>Delete</button></td>;
        }
        return (
            <tr>
                <td>{command.name}</td>
                <td>{command.description}</td>
                <td>{command.phrase}</td>
                <td>{args}</td>
                <td>{commands}</td>
                {edit}{del}
                <td>{error}</td>
            </tr>
        );
    }
}

class CBoxRow extends Component {
    constructor(props) {
        super(props);
        this.state = {
            error: ""
        };
    }
    handleSubmit = e => {
        e.preventDefault();
        axios.delete(BASE+"cbox/" + this.props.box.id, JSON.parse(localStorage.getItem("key")))
        .then(r => this.setState({
            redirect: true
        }))
        .catch(e => error(e, this));
    }
    buy = e => {
        e.preventDefault();
        axios.post(BASE_AUTH+"addbox", {
            userId: localStorage.getItem("id"),
            boxId: this.props.box.id
        }, JSON.parse(localStorage.getItem("key")))
        .then(r => this.setState({
            redirect: true
        }))
        .catch(e => error(e, this));
    }
    render() {
        if (this.state.redirect) {
            return <Redirect to='/' />;
        }
        let error;
        if(this.state.error) 
            error = <div className='alert alert-danger'>{this.state.error}</div>;
        const box = this.props.box; 
        const commands = box.commands.map(c => <Link to={"command/detail/"+c.id}>{c.name + " "}</Link>);

        let edit, del;
        if(localStorage.getItem("role") === "Admin"){
            edit = <td><Link className="btn btn-warning" to={"/cbox/edit/"+box.id}>Edit</Link></td>;
            del = <td><button className="btn btn-danger" onClick={this.handleSubmit}>Delete</button></td>;
        }
        let buyButton = "";
        buyButton = this.props.notMy ? <td><button className="btn btn-primary" onClick={this.buy}>Buy</button></td> : <td>Have it</td>;
        return (
            <tr>
                <td>{box.name}</td>
                <td>{box.description}</td>
                <td>{box.price}</td>
                <td>{commands}</td>
                {buyButton}
                {edit}{del}
                <td>{error}</td>
            </tr>
        );
    }
}

class ArgRow extends Component {
    constructor(props) {
        super(props);
        this.state = {
            error: ""
        };
    }
    handleSubmit = e => {
        e.preventDefault();
        axios.delete(BASE+"args/" + this.props.arg.id, JSON.parse(localStorage.getItem("key")))
        .then(r => this.setState({
            redirect: true
        }))
        .catch(e => error(e, this));
    }
    render() {
        if (this.state.redirect) {
            return <Redirect to='/' />;
        }
        let error;
        if(this.state.error) 
            error = <div className='alert alert-danger'>{this.state.error}</div>;
        const arg = this.props.arg; 

        let edit, del;
        if(localStorage.getItem("role") === "Admin"){
            edit = <td><Link className="btn btn-warning" to={"/args/edit/"+arg.id}>Edit</Link></td>;
            del = <td><button className="btn btn-danger" onClick={this.handleSubmit}>Delete</button></td>;
        }
        return (
            <tr>
                <td>{arg.name}</td>
                <td>{arg.description}</td>
                <td>{arg.regex_pattern}</td>
                <td>{arg.type}</td>
                {edit}{del}
                <td>{error}</td>
            </tr>
        );
    }
}

class LangRow extends Component {
    constructor(props) {
        super(props);
        this.state = {
            error: ""
        };
    }
    handleSubmit = e => {
        e.preventDefault();
        axios.delete(BASE+"lang/" + this.props.lang.id, JSON.parse(localStorage.getItem("key")))
        .then(r => this.setState({
            redirect: true
        }))
        .catch(e => error(e, this));
    }
    render() {
        if (this.state.redirect) {
            return <Redirect to='/' />;
        }
        let error;
        if(this.state.error) 
            error = <div className='alert alert-danger'>{this.state.error}</div>;
        const lang = this.props.lang; 

        let edit, del;
        if(localStorage.getItem("role") === "Admin"){
            edit = <td><Link className="btn btn-warning" to={"/langs/edit/"+lang.id}>Edit</Link></td>;
            del = <td><button className="btn btn-danger" onClick={this.handleSubmit}>Delete</button></td>;
        }
        return (
            <tr>
                <td>{lang.name}</td>
                <td>{lang.description}</td>
                <td>{lang.shortName}</td>
                {edit}{del}
                <td>{error}</td>
            </tr>
        );
    }
}


class KeyValue extends Component {
    constructor(props) {
        super(props);
        this.state = {
            entity: [],
            isReady: false,
            error: ""
        };
    }
    handleSubmit = e => {
        e.preventDefault();
        axios.delete(BASE+"commands/" + this.state.entity.id, JSON.parse(localStorage.getItem("key")))
        .then(r => this.setState({
            redirect: true
        }))
        .catch(e => error(e, this));
    }
    componentDidMount() {
        const id = this.props.match.params.id;
        axios.get(BASE+this.props.source+id, JSON.parse(localStorage.getItem("key")))
        .then(json)
        .then(data => {
            this.setState({
                entity: data, 
                isReady: true
            })
        })
        .catch(e => error(e, this));
    }
    render() {
        if(!this.state.isReady)
            return <Loader />;
        let error;
        if(this.state.error) 
            error = <Error text={this.state.error} />;
        if (this.state.redirect) {
            return <Redirect to='/' />;
        }

        const command = this.state.entity;
        document.title = this.props.title + " " + command.name;
        const args = command.arguments ? command.arguments.map(a => a.name).join(" | ") : "No args";

        let edit, del;
        if(localStorage.getItem("role") === "Admin"){
            edit = <Link className="btn btn-warning" to={"../edit/"+command.id}>Edit</Link>;
            del = <button className="btn btn-danger" onClick={this.handleSubmit}>Delete</button>;
        }
        let mod, parent;
        if (command.parent) {
            mod = <tr><th>Modified by</th><td>{command.user.username}</td></tr>;
            parent = <tr><th>Parent command</th><td><Link to={"../detail/" + command.parent.id}>{command.parent.name}</Link></td></tr>;
        }
        return (
            <div>
                <h1>{this.props.title + " " + command.name}</h1>
                {error}
                <table className="table table-bordered">
                    <thead className="thead-dark">
                        <tr>
                            <th>Property</th>
                            <th>Value</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr><th>Name</th><td>{command.name}</td></tr>
                        <tr><th>Description</th><td>{command.description}</td></tr>
                        <tr><th>Phrase</th><td>{command.phrase}</td></tr>
                        <tr><th>Price</th><td>{command.price}</td></tr>
                        <tr><th>Is open</th><td>{command.open ? "Yes" : "No"}</td></tr>
                        <tr><th>Language</th><td>{command.lang.name}</td></tr>
                        <tr><th>Arguments</th><td>{args}</td></tr>
                        <tr><th>Code</th><td>{command.code}</td></tr>
                        {mod}{parent}
                    </tbody>
                </table>
                {edit}{del}
            </div>            
        )
    }
}

export {List, KeyValue, Error, error};