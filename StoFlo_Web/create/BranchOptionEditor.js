import React from 'react'
import Dialog from 'material-ui/Dialog'
import Paper from 'material-ui/Paper'
import TextField from 'material-ui/TextField'
import FlatButton from 'material-ui/FlatButton'
import {List, ListItem} from 'material-ui/List'
import {alert} from "../common/util"
import ActionAST from "../common/ActionAST"

export default class BranchOptionEditor extends React.Component {
    static propTypes = {
        entry: React.PropTypes.any.isRequired,
        actions: React.PropTypes.any.isRequired
    }

    state = {
        ast: null
    }

    componentWillMount() {
        this.setState({
            ast: ActionAST.fromAction(this.props.entry, this.props.actions)
        })
    }

    render = () => (
        <Paper style={this.props.style}>
            <TextField
                value={this.props.entry.get('title')||''}
                onChange={e=>this.props.entry.set('title', e.target.value)}
                underlineStyle={{ boderColor: 'transparent' }}
            />
            <List>

            </List>
        </Paper>
    )
}
