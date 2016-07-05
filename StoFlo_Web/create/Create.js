import React from 'react'
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider'
import Login from '../common/Login'
import GameChooser from './GameChooser'

export default class Create extends React.Component {
    state = {
        gameId: '',

        isChoosingGame: false
    }

    onLoginFinished = (user) => {
        this.setState({isChoosingGame: true})
    }

    onChooseGameFinished = (game) => {
        this.setState({isChoosingGame: false})
    }

    render = () => (
        <MuiThemeProvider><div>
            <Login onFinished={this.onLoginFinished} />
            <GameChooser
                open={this.state.isChoosingGame}
                onFinished={this.onChooseGameFinished}
            />
        </div></MuiThemeProvider>
    )
}

