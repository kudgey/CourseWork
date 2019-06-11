import React, { Component } from 'react';
import axios from 'axios';
import { Redirect } from 'react-router-dom';
import {BASE} from './App.jsx';
import {Error, error} from './List.jsx';
import Select from 'react-select';
import { Loader } from './Loader.jsx';


class AddArgument extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: '',
			name: "",
			description: "",
            regex_pattern: "",
            type: "",
            error: ""
        }
    }
    componentDidMount() {    
        if (this.props.match.params.id)
            axios.get(BASE + "args/" + this.props.match.params.id, JSON.parse(localStorage.getItem("key")))
            .then(res => {
                const arg = res.data;                
                this.setState({
                    id: arg.id,
                    name: arg.name,
                    description: arg.description,
                    type: arg.type,
                    regex_pattern: arg.regex_pattern,
                    enumOpts: arg.type.startsWith("enum") ? arg.type.split(' ')[1] : ""
                });
            })
            .catch(e => error(e, this));
	}
    handleInputChange = event => {
		const target = event.target;
		const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

		this.setState({
		  [name]: value
		});
    }
    handleSelectChange = type => {
        this.setState({
            type: type.value
        });
	}
    postArg = arg => {
        axios.post(BASE + "args", arg, JSON.parse(localStorage.getItem("key")))
        .then(r => {
            console.log(r);
            this.setState({
                redirect: true
            })
        })
        .catch(e => error(e, this));
    }
    putArg = arg => {
        axios.put(BASE + "args/" + this.state.id, arg, JSON.parse(localStorage.getItem("key")))
        .then(r => {
            console.log(r);
            this.setState({
                redirect: true
            })
        })
        .catch(e => error(e, this));
    }
    handleSubmit = e => {
        e.preventDefault();
        let type = this.state.type;
        let error;
        if (type === "enum"){
            if (this.state.enumOpts && new Set(this.state.enumOpts.split(',')).size > 1) {
                type += " "+this.state.enumOpts;
            } else {
                error = "You need to specify at least two different comma-separated enum options";
            }
        }
        try{
            RegExp(this.state.regex_pattern);
        } catch (e){
            error = e.message;
        }
        if (error) {
            this.setState({error});
        } else {
            const arg = {
                name: this.state.name,
                description: this.state.description,
                regex_pattern: this.state.regex_pattern,
                type: type,
            }
            if(this.state.id)
                this.putArg(arg);                                    
            else 
                this.postArg(arg);
        }
    }
    render() {
        if (this.state.redirect) 
            return <Redirect to='../../args' />;
        var error;
        if(this.state.error) 
            error = <Error text={this.state.error} />;

        const types = [
            { value: 'int', label: "Integer"},
            { value: 'str', label: "String"},
            { value: 'array int', label: "Array of integers"},
            { value: 'array str', label: "Array of strings"},
            { value: 'enum', label: "Enum"},
        ];
        const selectedType = types.find(t => this.state.type.startsWith(t.value));

        let enumOpts;
        if(this.state.type.startsWith("enum")) {
            enumOpts = 
            <div>
                <label htmlFor="enumOpts">Comma-separated options for enum: </label>
                <Input type="text" name="enumOpts" text="Options" onChange={this.handleInputChange} 
                    value={this.state.enumOpts} />
            </div>
        }
        const buttonName = this.state.id ? "Edit" : "Add";
        document.title = "Edit argument";
        return (
            <div>
                <h1>Argument</h1>
                {error}
                <form className="form-horizontal m-5" onSubmit={this.handleSubmit}>
                    <Input type="text" name="name" value={this.state.name} 
                        text="Name" onChange={this.handleInputChange} />
                    <Input type="desc" name="description" value={this.state.description} 
                        text="Description" onChange={this.handleInputChange} />                    
                    <Input type="text" name="regex_pattern" value={this.state.regex_pattern} 
                        text="Regex pattern" onChange={this.handleInputChange}/>                    
                    <Input type="select" closeMenuOnSelect={true} options={types} value={selectedType}
                        name="type" text="Тип" onChange={this.handleSelectChange} /> 
                    {enumOpts}                 
                    <Input type="submit" name={buttonName} onChange={this.handleSubmit} />                        
                </form>
            </div>
            );
    }
}


class AddLang extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: '',
			name: "",
			description: "",
            shortName: ""
        }
    }
    componentDidMount() {    
        if (this.props.match.params.id)
            axios.get(BASE + "lang/" + this.props.match.params.id, JSON.parse(localStorage.getItem("key")))
            .then(res => {
                const arg = res.data;                
                this.setState({
                    id: arg.id,
                    name: arg.name,
                    description: arg.description,
                    shortName: arg.shortName
                });
            })
            .catch(e => error(e, this));
	}
    handleInputChange = event => {
		const target = event.target;
		const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

		this.setState({
		  [name]: value
		});
    }
    handleSelectChange = type => {
        this.setState({
            type: type.value
        });
	}
    postArg = arg => {
        axios.post(BASE + "lang", arg, JSON.parse(localStorage.getItem("key")))
        .then(r => {
            console.log(r);
            this.setState({
                redirect: true
            })
        })
        .catch(e => error(e, this));
    }
    putArg = arg => {
        axios.put(BASE + "lang/" + this.state.id, arg, JSON.parse(localStorage.getItem("key")))
        .then(r => {
            console.log(r);
            this.setState({
                redirect: true
            })
        })
        .catch(e => error(e, this));
    }
    handleSubmit = e => {
        e.preventDefault();
        const arg = {
            name: this.state.name,
            description: this.state.description,
            shortName: this.state.shortName
        }
        if(this.state.id)
            this.putArg(arg);                                    
        else 
            this.postArg(arg);
    }
    render() {
        if (this.state.redirect) 
            return <Redirect to='../../langs' />;
        var error;
        if(this.state.error) 
            error = <Error text={this.state.error} />;
        const buttonName = this.state.id ? "Edit" : "Add";
        document.title = "Edit language";
        return (
            <div>
                <h1>Language</h1>
                {error}
                <form className="form-horizontal m-5" onSubmit={this.handleSubmit}>
                    <Input type="text" name="name" value={this.state.name} 
                        text="Name" onChange={this.handleInputChange} />
                    <Input type="desc" name="description" value={this.state.description} 
                        text="Description" onChange={this.handleInputChange} />                                       
                    <Input type="text" name="shortName" value={this.state.shortName} 
                        text="Short name" onChange={this.handleInputChange} />                                                             
                    <Input type="submit" name={buttonName} onChange={this.handleSubmit} />                        
                </form>
            </div>
            );
    }
}


class AddCommand extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: '',
			name: "",
			description: "",
            phrase: "",
            price: "",
            open: false,
            code: "",
            lang: "",
            args: [],
            arguments: [],
            langs: [],
            mod: true
        }
    }
    componentDidMount() { 
        axios.get(BASE + "args", JSON.parse(localStorage.getItem("key")))
		.then(res => {
			this.setState({
				args: res.data
			});
        })
		.then(res => {
            axios.get(BASE + "lang", JSON.parse(localStorage.getItem("key")))
            .then(res => {
                this.setState({
                    langs: res.data
                });
            })
            .catch(e => error(e, this));
        })
        .then(res => {   
            if (this.props.match.params.id)
                axios.get(BASE + "commands/" + this.props.match.params.id, JSON.parse(localStorage.getItem("key")))
                .then(res => {
                    const cmd = res.data;                
                    this.setState({
                        id: cmd.id,
                        name: cmd.name,
                        description: cmd.description,
                        phrase: cmd.phrase,
                        price: cmd.price,
                        code: cmd.code,
                        open: cmd.open,
                        lang: cmd.lang.id,
                        arguments: cmd.arguments ? cmd.arguments.map(a => a.id) : []
                    });
                })
                .catch(e => error(e, this));
            }
        )
        .catch(e => error(e, this))
	}
    handleInputChange = event => {
		const target = event.target;
		const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

		this.setState({
		  [name]: value
		});
    }
    handleSelectLangChange = type => {
        this.setState({
            lang: type.value
        });
	}
    handleMultiSelectChange = ags => {
        this.setState({
            arguments: ags ? ags.map(p => p.value) : []
        });
	}
    post = arg => {
        axios.post(BASE + "commands", arg, JSON.parse(localStorage.getItem("key")))
        .then(r => {
            console.log(r);
            this.setState({
                redirect: true
            })
        })
        .catch(e => error(e, this));
    }
    put = arg => {
        axios.put(BASE + "commands/" + this.state.id, arg, JSON.parse(localStorage.getItem("key")))
        .then(r => {
            console.log(r);
            this.setState({
                redirect: true
            })
        })
        .catch(e => error(e, this));
    }
    modify = command => {
        command['user_id'] = localStorage.getItem("id");
        command['parent_id'] = this.state.id;
        axios.post(BASE + "commands", command, JSON.parse(localStorage.getItem("key")))
        .then(r => {
            console.log(r.response);
            this.setState({
                redirect: true
            })
        })
        .catch(e => error(e, this));
    }
    handleSubmit = e => {
        e.preventDefault();
        const cmd = {
            command: {
                name: this.state.name,
                description: this.state.description,
                phrase: this.state.phrase,
                price: this.state.price,
                open: this.state.open,
                code: this.state.code
            },
            args_ids: this.state.arguments,
            lang_id: this.state.lang
        }
        if(this.state.id)
            if (this.state.mod)
                this.modify(cmd); 
            else
                this.put(cmd);                               
        else 
            this.post(cmd);
        // console.log(this.state);
    }
    render() {
        if (this.state.redirect) 
            return <Redirect to='../../command' />;
        var error;
        if(this.state.error) 
            error = <Error text={this.state.error} />;
        
        const args = this.state.args.map(a => ({
            value: a.id,
            label: a.name
        }));
        const langs = this.state.langs.map(l => ({
            value: l.id,
            label: l.name
        }));

        const selectedLang = langs.find(t => this.state.lang === t.value);
        const selectedArgs = args.filter(c => this.state.arguments.includes(c.value));

        const buttonName = this.state.id ? "Edit" : "Add";
        const modify = localStorage.getItem("role") === "Admin" && this.state.id ? 
            <Input type="checkbox" name="mod" checked={this.state.mod} text="Modify" onChange={this.handleInputChange}/> :
            "";
        document.title = "Edit command";
        return (
            <div>
                <h1>Command</h1>
                {error}
                <form className="form-horizontal m-5" onSubmit={this.handleSubmit}>
                    <Input type="text" name="name" value={this.state.name} 
                        text="Name" onChange={this.handleInputChange} />
                    <Input type="desc" name="description" value={this.state.description} 
                        text="Description" onChange={this.handleInputChange} />                    
                    <Input type="text" name="phrase" value={this.state.phrase} 
                        text="Phrase" onChange={this.handleInputChange}/>                    
                    <Input type="number" name="price" value={this.state.price} 
                        text="Price" onChange={this.handleInputChange}/>                    
                    <Input type="checkbox" name="open" checked={this.state.open} 
                        text="Open" onChange={this.handleInputChange}/>                    
                    <Input type="select" closeMenuOnSelect={true} options={args} value={selectedArgs}
                        name="arguments" isMulti={true} text="Arguments" onChange={this.handleMultiSelectChange} />                        
                    <Input type="select" closeMenuOnSelect={true} options={langs} value={selectedLang}
                        name="lang" text="Language" onChange={this.handleSelectLangChange} />
                    <Input type="desc" name="code" value={this.state.code} 
                        text="Code" onChange={this.handleInputChange}/>  
                    {modify}

                    <Input type="submit" name={buttonName} onChange={this.handleSubmit} />                      
                </form>
            </div>
            );
    }
}

class AddCommandBox extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: '',
			name: "",
			description: "",
            price: "",
            commands: [],
            all_commands: []
        }
    }
    componentDidMount() { 
        axios.get(BASE + "commands", JSON.parse(localStorage.getItem("key")))
		.then(res => {
			this.setState({
				all_commands: res.data
			});
        })
        .then(res => {   
            if (this.props.match.params.id)
                axios.get(BASE + "cbox/" + this.props.match.params.id, JSON.parse(localStorage.getItem("key")))
                .then(res => {
                    const box = res.data;                
                    this.setState({
                        id: box.id,
                        name: box.name,
                        description: box.description,
                        price: box.price,
                        commands: box.commands.map(a => a.id)
                    });
                })
                .catch(e => error(e, this));
            }
        )
        .catch(e => error(e, this))
	}
    handleInputChange = event => {
		const target = event.target;
		const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

		this.setState({
		  [name]: value
		});
    }
    handleMultiSelectChange = ags => {
        this.setState({
            commands: ags ? ags.map(p => p.value) : []
        });
	}
    post = arg => {
        axios.post(BASE + "cbox", arg, JSON.parse(localStorage.getItem("key")))
        .then(r => {
            console.log(r);
            this.setState({
                redirect: true
            })
        })
        .catch(e => error(e, this));
    }
    put = arg => {
        axios.put(BASE + "cbox/" + this.state.id, arg, JSON.parse(localStorage.getItem("key")))
        .then(r => {
            console.log(r);
            this.setState({
                redirect: true
            })
        })
        .catch(e => error(e, this));
    }
    handleSubmit = e => {
        e.preventDefault();
        const cmd = {
            cbox: {
                name: this.state.name,
                description: this.state.description,
                price: this.state.price,
            },
            commands_ids: this.state.commands
        }
        if(this.state.id)
            this.put(cmd);                                    
        else 
            this.post(cmd);
    }
    render() {
        if (this.state.redirect) 
            return <Redirect to='../../cbox' />;
        var error;
        if(this.state.error) 
            error = <Error text={this.state.error} />;
        
        const commands = this.state.all_commands.map(a => ({
            value: a.id,
            label: a.name
        }));

        const selectedCommands = commands.filter(c => this.state.commands.includes(c.value));

        const buttonName = this.state.id ? "Edit" : "Add";
        document.title = "Edit command box";
        return (
            <div>
                <h1>Command Box</h1>
                {error}
                <form className="form-horizontal m-5" onSubmit={this.handleSubmit}>
                    <Input type="text" name="name" value={this.state.name} 
                        text="Name" onChange={this.handleInputChange} />
                    <Input type="desc" name="description" value={this.state.description} 
                        text="Description" onChange={this.handleInputChange} />                                      
                    <Input type="number" name="price" value={this.state.price} 
                        text="Price" onChange={this.handleInputChange}/>                                     
                    <Input type="select" closeMenuOnSelect={true} options={commands} value={selectedCommands}
                        name="commands" isMulti={true} text="Commands" onChange={this.handleMultiSelectChange} />                                               
                    <Input type="submit" name={buttonName} onChange={this.handleSubmit} />                        
                </form>
            </div>
            );
    }
}

class AddCCommand extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: '',
			name: "",
			description: "",
            args: [],
            phrase: "",
            commands: [],
            arguments: [],
            all_commands: [],
            arg_key: "",
            arg_val: "",
            arg_type: "",
            ready: false,
            error: ""
        }
    }
    componentDidMount() { 
        axios.get(BASE + "commands", JSON.parse(localStorage.getItem("key")))
		.then(res => {
			this.setState({
				all_commands: res.data
			});
        })
        .then(res => {
            axios.get(BASE + "args", JSON.parse(localStorage.getItem("key")))
            .then(res => {
                this.setState({
                    arguments: res.data
                });
            })
            .catch(e => error(e, this));
        })
        .then(res => {   
            if (this.props.match.params.id)
                axios.get(BASE + "complexcommand/" + this.props.match.params.id, JSON.parse(localStorage.getItem("key")))
                .then(res => {
                    const cmd = res.data;                
                    this.setState({
                        id: cmd.id,
                        name: cmd.name,
                        description: cmd.description,
                        phrase: cmd.phrase,
                        args: cmd.args ? Object.entries(cmd.args) : [],
                        commands: cmd.commands.map(a => ({
                            value: a.id,
                            label: a.name
                        })),
                        ready: true
                    });
                })
                .catch(e => error(e, this));
            else {
                this.setState({
                    ready: true
                })
            }
        })
        .catch(e => error(e, this))
	}
    handleInputChange = event => {
		const target = event.target;
		const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;

		this.setState({
		  [name]: value
		});
    }
    handleCommandsSelectChange = cmd => {
        this.setState({
            commands: [...this.state.commands, cmd]
        });
	}
    post = arg => {
        axios.post(BASE + "complexcommand", arg, JSON.parse(localStorage.getItem("key")))
        .then(r => {
            console.log(r);
            this.setState({
                redirect: true
            })
        })
        .catch(e => error(e, this));
    }
    put = arg => {
        axios.put(BASE + "complexcommand/" + this.state.id, arg, JSON.parse(localStorage.getItem("key")))
        .then(r => {
            console.log(r);
            this.setState({
                redirect: true
            })
        })
        .catch(e => error(e, this));
    }
    handleSubmit = e => {
        e.preventDefault();
        const rA = {};
        this.state.args.forEach(([k, v]) => {rA[k] = v});
        const cmd = {
            complexCommand: {
                name: this.state.name,
                description: this.state.description,
                phrase: this.state.phrase
            },
            commands_ids: this.state.commands.map(c => c.value),
            readyArgs: rA
        }
        if(this.state.id)
            this.put(cmd);                                    
        else 
            this.post(cmd);
    }
    popCmd = (e) => {
        e.preventDefault();
        const selected = [...this.state.commands];
        selected.pop();
        this.setState({
            commands: selected
        })
    }
    handleArgSelectChange = ags => {
        this.setState({ arg_key: ags.value, arg_type: ags.type });
    }
    pushArg = (e) => {
        e.preventDefault();
        let error = "";
        let value = this.state.arg_val;
        switch(this.state.arg_type) {
            case "int":
                value = Number(value);
                if(!Number.isInteger(value))
                    error = "Argument type must be integer";
                break;
            case "str":
                break;
            case "array int":
                const arr = value.split(',');
                for (let i = 0; i < arr.length; i++) {
                    arr[i] = Number(arr[i]);
                    if(!Number.isInteger(arr[i])) {
                        error = "Argument type must be array of integers";
                        break;
                    }
                }
                value = arr;
                break;
            case "array str":
                value = value.split(',');
                break;        
            default:
                if (this.state.arg_type.startsWith("enum")) {
                    const ops = this.state.arg_type.split(' ')[1].split(',');
                    if (!ops.includes(value))
                        error = "Argument value must be from in " + ops;
                }    
                break;
        }
        if (error)
            this.setState({error})
        else {
            const args = [...this.state.args];
            const k = [this.state.arg_key, value];
            args.push(k);
            this.setState({ args })
        }
    }
    popArg = (e) => {
        e.preventDefault();
        const args = [...this.state.args];
        args.pop();
        this.setState({ args })
    }
    render() {
        console.log(this.state);
        if(!this.state.ready) {
            return <Loader />;
        }
        if (this.state.redirect) 
            return <Redirect to='../../ccommand' />;
        var error;
        if(this.state.error) 
            error = <Error text={this.state.error} />;
        
        const commands = this.state.all_commands.map(a => ({
            value: a.id,
            label: a.name
        }));
        const args = this.state.arguments.filter(a => this.state.args.every(ar => ar[0] !== a.name)).map(a => ({
            value: a.name,
            label: a.name,
            type: a.type
        }));

        const selectedArgs = 
        <table className="table">
            <thead>
                <tr>
                    <th scope="col">Key</th>
                    <th scope="col">Value</th>
                </tr>
            </thead>
            <tbody>
                {this.state.args.map(([k, v]) => <tr><td>{k}</td><td>{v}</td></tr>)}
            </tbody>
            </table>

        const selectedCommands = 
        <ul className='list-group'>
            {this.state.commands.map(c => <li className='list-group-item'>{c.label}</li>)}
        </ul>

        let type = "";
        switch(this.state.arg_type){
            case "int":
                type = "Integer";
                break;
            case "str":
                type = "String";
                break;
            case "array int":
                type = "Сomma-separated array of integers";
                break;
            case "array str":
                type = "Сomma-separated array of strings";
                break;        
            default:
                if (this.state.arg_type.startsWith("enum")) {
                    const ops = this.state.arg_type.split(' ')[1];
                    type = "Choose one option " + ops;
                }    
                break;
        }
        const buttonName = this.state.id ? "Edit" : "Add";
        document.title = "Edit complex command";
        return (
            <div>
                <h1>Complex command</h1>
                {error}
                <form className="form-horizontal m-5" onSubmit={this.handleSubmit}>
                    <Input type="text" name="name" value={this.state.name} 
                        text="Name" onChange={this.handleInputChange} />
                    <Input type="desc" name="description" value={this.state.description} 
                        text="Description" onChange={this.handleInputChange} />                                                                         
                    <Input type="text" name="phrase" value={this.state.phrase} 
                        text="Phrase" onChange={this.handleInputChange} />

                    <Input type="select" closeMenuOnSelect={true} options={commands}
                        name="commands" text="Commands" onChange={this.handleCommandsSelectChange} />  
                    <span>Selected commands: {selectedCommands}</span>                                             
                    <button className='btn btn-primary' onClick={this.popCmd}>Pop command</button>  

                    <Input type="select" closeMenuOnSelect={true} options={args}
                        name="args" text="Argument" onChange={this.handleArgSelectChange} />
                    <span>Type: {type}</span>
                    <Input type="text" name="arg_val" 
                        text="Value" onChange={this.handleInputChange} required={false}/> 

                    <span>Selected arguments: {selectedArgs}</span>
                    <button className='btn btn-primary' onClick={this.pushArg}>Push argument</button> 
                    <button className='btn btn-primary' onClick={this.popArg}>Pop argument</button> 
                    <Input type="submit" name={buttonName} onChange={this.handleSubmit} />                        
                </form>
            </div>
            );
    }
}


class Input extends Component {
    render() {
        const type= this.props.type;
        const name = this.props.name;
        const value = this.props.value;
        const text = this.props.text;
        const onChange = this.props.onChange;
        const cName = name;
        const options = this.props.options;
        const isMulti = this.props.isMulti;
        const closeMenuOnSelect = this.props.closeMenuOnSelect;
        const checked = this.props.checked;
        const required = typeof this.props.required === "undefined" ? true : this.props.required; 
        const inpProps = {
            name,
            className: "form-control",
            id: "input"+cName,
            onChange,
        }
        let item;
        switch(type) {
            case "text": 
                item = <input type="text" value={value}
                        placeholder={text} required={required} 
                        {...inpProps} />
                break;
            case "desc":
                item = <textarea value={value ? value : ""} placeholder={text}
                        {...inpProps} rows="10" />;
                break;
            case "select":
                item = <Select className='selectC' options={options}
                        isMulti={isMulti} closeMenuOnSelect={closeMenuOnSelect} 
                        onChange={onChange} value={value}/>;
                break;
            case "img":
                item = <input type="file" {...inpProps} className='form-control-file' />;
                break;
            case "submit":
                item = <button type="submit" className="btn btn-success" onSubmit={onChange}>{name}</button>;
                break;
            case "number":
                item = <input type="number" {...inpProps} min="0" value={value} />;
                break;
            case "datetime":
                item = <input type="datetime-local" {...inpProps} value={value} />;
                break;                
            case "checkbox":
                item = <input type="checkbox" {...inpProps} checked={checked} />;
                break;                
            default:
                item = "";
        }
        return (
            <div className="form-group row">
                <label htmlFor={"input"+cName} className="col-sm-2 col-form-label">{text}</label>
                <div className="col-sm-10">
                    {item}
                </div>
            </div>
        );
    }
}

export {AddArgument, AddLang, AddCommand, AddCommandBox, AddCCommand}    