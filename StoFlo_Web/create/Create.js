import React from 'react'
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider'
import Paper from 'material-ui/Paper'
import TextField from 'material-ui/TextField'
import Login from '../common/Login'
import GameWindow from '../common/GameWindow'
import {AVModel} from '../common/util'
import GameChooser from './GameChooser'

const styles = {
    frameContainer: {
        width: '50%',
        padding: '0 12px'
    },
    contentEditor: {
        maxHeight: '100%'
    }
}

export default class Create extends React.Component {
    state = {
        game: '',
        chapter: new AVModel('Chapter'),
        variables: {},

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
            <GameChooser open={this.state.isChoosingGame} onFinished={this.onChooseGameFinished} />
            <Paper style={styles.frameContainer}>
                <TextField ref="textField" multiLine={true} fullWidth={true} rows={2} rowsMax={8} style={styles.contentEditor}
                           hintText={'Input your story here.\nUse {{name}} to insert variables, and {{#}} to insert a breakpoint.'} />
            </Paper>
            <Paper style={styles.frameContainer}>
                <GameWindow chapter={this.state.chapter} onAction={()=>{}} variables={{}} />
            </Paper>
        </div></MuiThemeProvider>
    )
}

