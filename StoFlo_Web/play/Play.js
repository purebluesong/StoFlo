import React from 'react'
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider'
import Paper from 'material-ui/Paper'
import {List, ListItem} from 'material-ui/List'
import {Tabs, Tab} from 'material-ui/Tabs'
import TextField from 'material-ui/TextField'
import FlatButton from 'material-ui/FlatButton'
import Snackbar from 'material-ui/Snackbar'
import Login from '../common/Login'
import GameWindow from '../common/GameWindow'
import {alert} from '../common/util'
import {AVModel, createChapter} from '../common/model'
import GameChooser from './GameChooser'

class Play extends React.Component {
    state = {
        game: new AVModel('Game'),
        chapters: {},
        actions: [],
        variables: {},

        chapter: new AVModel('Chapter'),

        modal: ''
    }

    onLoginFinished = (user) => {
        this.setState({modal: 'choose_game'})
    }

    onChooseGameFinished = (game) => {
        this.setState({ modal: '', game: game })
        AV.Query.doCloudQuery(`select * from Chapter where gameId='${game.getObjectId()}'`)
                .try(response => {
                    const chapters = {}
                    response.results.forEach(chapter => {
                        chapters[chapter.getObjectId()] = chapter
                    })
                    this.setState({ chapters: chapters })
                    this.start(chapters[game.get('start_chapter').getObjectId()])
                })
                .catch(console.info.bind(console))
        AV.Query.doCloudQuery(`select * from Action where gameId='${game.getObjectId()}'`)
                .try(response => {
                    const actions = {}
                    response.results.forEach(action => {
                        actions[action.getObjectId()] = action
                    })
                    this.setState({ actions: actions })
                })
                .catch(console.info.bind(console))
    }

    start(chapter) {
        this.setState({ chapter: chapter })
    }

    render() {
        return (
            <MuiThemeProvider><div>
                <Login onFinished={this.onLoginFinished} />
                <GameChooser open={this.state.modal == 'choose_game'} onFinished={this.onChooseGameFinished} />
                <GameWindow
                    chapter={this.state.chapter}
                    resources={this.state.resources}
                    variables={{}}
                    onAction={()=>{}}
                />
            </div></MuiThemeProvider>
        )
    }
}

export default Play
