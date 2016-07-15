import React from 'react'
import Dialog from 'material-ui/Dialog'
import Paper from 'material-ui/Paper'
import TextField from 'material-ui/TextField'
import FlatButton from 'material-ui/FlatButton'
import {List, ListItem} from 'material-ui/List'
import {alert} from "../common/util"
import ActionAST from "../common/ActionAST"

const styles = {
    listItem: {
        fontSize: 12
    },
    textField: {
        width: '85%',
        marginLeft: '7.5%'
    }
}

export default class BranchOptionEditor extends React.Component {
    static propTypes = {
        entry: React.PropTypes.any.isRequired,
        actions: React.PropTypes.any.isRequired,
        setLastFocus: React.PropTypes.func.isRequired
    }

    state = {
        ast: null,
        name: '',
        editing: null
    }

    componentWillMount() {
        this.setState({
            ast: ActionAST.fromAction(this.props.entry, this.props.actions),
            name: this.props.entry.get('title')
        })
    }

    renderItem = (item, children, key) => {
        switch (item.type) {
            case 'if':
                return (
                    <ListItem
                        key={key}
                        style={styles.listItem}
                        primaryText={`if ${item.node.op1} ${item.node.op} ${item.node.op2}:`}
                        initiallyOpen={true}
                        nestedItems={children}
                    />
                )
            case 'else':
                return (
                    <ListItem
                        key={key}
                        style={styles.listItem}
                        primaryText={`else:`}
                        initiallyOpen={true}
                        nestedItems={children}
                    />
                )
            case '=':
                return (
                    <ListItem
                        key={key}
                        style={styles.listItem}
                        primaryText={`${item.node.left} = ${item.node.op1} ${item.node.op} ${item.node.op2};`}
                        initiallyOpen={true}
                        nestedItems={children}
                    />
                )
            case '+':
                return (
                    <ListItem
                        key={key}
                        style={styles.listItem}
                        primaryText={`点击增加语句`}
                        initiallyOpen={true}
                        nestedItems={children}
                    />
                )
            case 'goto':
                return (
                    <ListItem
                        key={key}
                        style={styles.listItem}
                        primaryText={`goto ${item.node.chapterId};`}
                        onTouchTap={()=>this.setState({ editing: item })}
                    />
                )
        }
    }

    renderTree = (indent, rest) => {
        const items = []
        while(rest.length){
            let current = rest[0]
            if (current.indent < indent) {
                return items
            } else {
                rest.shift()
                items.push(this.renderItem(current, this.renderTree(indent+1, rest), rest.length))
            }
        }
        return items
    }

    renderEditor = () => {
        const item = this.state.editing
        switch (item.type) {
            case 'if':
            case 'else':
                return [
                    <TextField
                        key="op"
                        style={styles.textField}
                        floatingLabelText="op:"
                        value={item.node.op}
                        onFocus={e=>this.props.setLastFocus(arg => {
                            item.node.op = arg
                            this.setState({ast: this.state.ast})
                        })}
                        onChange={e=>{
                            item.node.op = e.target.value
                            this.setState({ast: this.state.ast})
                        }}
                    />,
                    <TextField
                        key="op1"
                        style={styles.textField}
                        floatingLabelText="op1:"
                        value={item.node.op1}
                        onFocus={e=>this.props.setLastFocus(arg => {
                            item.node.op1 = arg
                            this.setState({ast: this.state.ast})
                        })}
                        onChange={e=>{
                            item.node.op1 = e.target.value
                            this.setState({ast: this.state.ast})
                        }}
                    />,
                    <TextField
                        key="op2"
                        style={styles.textField}
                        floatingLabelText="op2:"
                        value={item.node.op2}
                        onFocus={e=>this.props.setLastFocus(arg => {
                            item.node.op2 = arg
                            this.setState({ast: this.state.ast})
                        })}
                        onChange={e=>{
                            item.node.op2 = e.target.value
                            this.setState({ast: this.state.ast})
                        }}
                    />
                ]
            case '=':
                return [
                    <TextField
                        key="left"
                        style={styles.textField}
                        floatingLabelText="left:"
                        value={item.node.left}
                        onFocus={e=>this.props.setLastFocus(arg => {
                            item.node.left = arg
                            this.setState({ast: this.state.ast})
                        })}
                        onChange={e=>{
                            item.node.left = e.target.value
                            this.setState({ast: this.state.ast})
                        }}
                    />,
                    <TextField
                        key="op"
                        style={styles.textField}
                        floatingLabelText="op:"
                        value={item.node.op}
                        onFocus={e=>this.props.setLastFocus(arg => {
                            item.node.op = arg
                            this.setState({ast: this.state.ast})
                        })}
                        onChange={e=>{
                            item.node.op = e.target.value
                            this.setState({ast: this.state.ast})
                        }}
                    />,
                    <TextField
                        key="op1"
                        style={styles.textField}
                        floatingLabelText="op1:"
                        value={item.node.op1}
                        onFocus={e=>this.props.setLastFocus(arg => {
                            item.node.op1 = arg
                            this.setState({ast: this.state.ast})
                        })}
                        onChange={e=>{
                            item.node.op1 = e.target.value
                            this.setState({ast: this.state.ast})
                        }}
                    />,
                    <TextField
                        key="op2"
                        style={styles.textField}
                        floatingLabelText="op2:"
                        value={item.node.op2}
                        onFocus={e=>this.props.setLastFocus(arg => {
                            item.node.op2 = arg
                            this.setState({ast: this.state.ast})
                        })}
                        onChange={e=>{
                            item.node.op2 = e.target.value
                            this.setState({ast: this.state.ast})
                        }}
                    />
                ]
            case '+':
                return [
                    <FlatButton
                        key="condition"
                        label="条件语句"
                    />,
                    <FlatButton
                        key="assignment"
                        label="赋值语句"
                    />
                ]
            case 'goto':
                return [
                    <TextField
                        key="chapterId"
                        style={styles.textField}
                        floatingLabelText="chapterId:"
                        value={item.node.chapterId}
                        onFocus={e=>this.props.setLastFocus(arg => {
                            item.node.chapterId = arg
                            this.setState({ast: this.state.ast})
                        })}
                        onChange={e=>{
                            item.node.chapterId = e.target.value
                            this.setState({ast: this.state.ast})
                        }}
                    />
                ]
        }


    }

    render = () => (
        <Paper style={this.props.style}>
            <TextField
                style={styles.textField}
                id={this.props.entry.getObjectId()}
                value={this.state.name}
                onChange={e=>{
                    this.props.entry.set('title', e.target.value)
                    this.setState({name: e.target.value})
                }}
                underlineStyle={{ borderColor: 'transparent' }}
            />
            {
                !this.state.editing ? (
                    <List>{
                        this.state.ast ? this.renderTree(0, ActionAST.toList(this.state.ast)) : ''
                    }</List>
                ) : (
                    this.renderEditor().concat(
                        <FlatButton
                            key="OK"
                            label="OK"
                            style={{ float: 'right', marginRight: '10%' }}
                            primary={true}
                            onTouchTap={()=>{
                                this.setState({ editing: null })
                                this.props.setLastFocus(null)
                                this.state.ast.saveAsActions(()=>{})
                            }}
                        />
                    )
                )
            }

        </Paper>
    )
}
