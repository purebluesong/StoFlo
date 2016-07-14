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
    }
}

export default class BranchOptionEditor extends React.Component {
    static propTypes = {
        entry: React.PropTypes.any.isRequired,
        actions: React.PropTypes.any.isRequired
    }

    state = {
        ast: null,
        name: ''
    }

    componentWillMount() {
        this.setState({
            ast: ActionAST.fromAction(this.props.entry, this.props.actions),
            name: this.props.entry.get('title')
        })
    }

    renderItem = (item, children) => {
        switch (item.type) {
            case 'if':
                return (
                    <ListItem
                        style={styles.listItem}
                        primaryText={`if ${item.node.op1} ${item.node.op} ${item.node.op2}:`}
                        initiallyOpen={true}
                        nestedItems={children}
                    />
                )
            case 'else':
                return (
                    <ListItem
                        style={styles.listItem}
                        primaryText={`else:`}
                        initiallyOpen={true}
                        nestedItems={children}
                    />
                )
            case 'assignment':
                return (
                    <ListItem
                        style={styles.listItem}
                        primaryText={`${item.node.left} = ${item.node.op1} ${item.node.op} ${item.node.op2};`}
                        initiallyOpen={true}
                        nestedItems={children}
                    />
                )
            case 'goto':
                return (
                    <ListItem
                        style={styles.listItem}
                        primaryText={`goto ${item.node.chapterId};`}
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
                items.push(this.renderItem(current, this.renderTree(indent+1, rest)))
            }
        }
        return items
    }

    render = () => (
        <Paper style={this.props.style}>
            <TextField
                style={{ width: '80%', marginLeft: '10%' }}
                id={this.props.entry.getObjectId()}
                value={this.state.name}
                onChange={e=>{
                    this.props.entry.set('title', e.target.value)
                    this.setState({name: e.target.value})
                }}
                underlineStyle={{ borderColor: 'transparent' }}
            />
            <List>{
                this.state.ast ? this.renderTree(0, ActionAST.toList(this.state.ast)) : ''
            }</List>
        </Paper>
    )
}
