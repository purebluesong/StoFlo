import React from 'react'
import Dialog from 'material-ui/Dialog'
import FlatButton from 'material-ui/FlatButton'
import TextField from 'material-ui/TextField'

export default class GameInfoEditor extends React.Component {
    static propTypes = {
        open: React.PropTypes.bool.isRequired,
        onFinished: React.PropTypes.func.isRequired,
        onCanceled: React.PropTypes.func.isRequired
    }

    state = {
        game: null
    }

    submit = () => {
        //this.state.game.submit()
        this.props.onFinished(game)
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

        </Dialog>
    )
}

