import React from 'react'
import Dialog from 'material-ui/Dialog'
import FlatButton from 'material-ui/FlatButton'
import {List, ListItem} from 'material-ui/List'
import GameInfoEditor from './GameInfoEditor'

export default class GameChooser extends React.Component {
    static propTypes = {
        open: React.PropTypes.bool.isRequired,
        onFinished: React.PropTypes.func.isRequired
    }

    state = {
        gamelist: [],
        isEditing: false
    }

    componentDidMount = () => {
        this.queryMyGames()
    }

    queryMyGames = () => {

    }

    newGameButton = (
        <FlatButton
            label="New Game"
            primary={true}
            onTouchTap={() => this.setState({isEditing: true})}
        />
    )

    render = () => (
        <Dialog
            title="Choose a Game"
            actions={this.newGameButton}
            modal={true}
            open={this.props.open}
        >
            <GameInfoEditor
                open={this.state.isEditing}
                onFinished={id => {
                    this.setState({isEditing: false})
                    this.props.onFinished(id)
                }}
                onCanceled={() => this.setState({isEditing: false})}
            />
            <List>{
                this.state.gamelist.map(game => {
                    <ListItem
                        primaryText={game.title}
                        secondaryText={this.props.getSecondaryInfo(game)}
                        onTouchTap={() => this.props.onFinished(game)}
                    />
                })
            }</List>
        </Dialog>
    )
}

