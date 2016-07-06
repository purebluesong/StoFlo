import React from 'react'
import Dialog from 'material-ui/Dialog'
import FlatButton from 'material-ui/FlatButton'
import TextField from 'material-ui/TextField'
import {AVModel} from '../common/util'

export default class GameInfoEditor extends React.Component {
    static propTypes = {
        open: React.PropTypes.bool.isRequired,
        onFinished: React.PropTypes.func.isRequired,
        onCanceled: React.PropTypes.func.isRequired,
        game: React.PropTypes.any
    }

    state = {
        game: null,
        editable: false
    }

    componentWillMount = () => {
        this.setState({game: this.props.game || new AVModel("Game")})
    }

    submit = () => {
        this.state.game.set("name",         this.refs.title.getValue())
                       .set("description",  this.refs.description.getValue())
                       .set("game_creator", AV.User.current())
                       .save()
        this.props.onFinished(this.state.game)
    }

    actions = [
        <FlatButton
            label="Cancel"
            secondary={true}
            onTouchTap={this.props.onCanceled}
        />,
        <FlatButton
            label="Submit"
            primary={true}
            onTouchTap={this.submit}
        />
    ]

    render = () => (
        <Dialog
            title="Edit Game Info"
            actions={this.actions}
            modal={true}
            open={this.props.open}
        >
            <TextField
                ref="title"
                defaultValue={this.state.game.get("name")||''}
                floatingLabelText="Game Title"
            />
            <TextField
                ref="description"
                defaultValue={this.state.game.get("description")||''}
                floatingLabelText="Descriptions"
                multiLine={true}
            />
        </Dialog>
    )
}

