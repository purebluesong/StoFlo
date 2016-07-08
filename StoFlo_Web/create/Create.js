import React from 'react'
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider'
import Paper from 'material-ui/Paper'
import {Tabs, Tab} from 'material-ui/Tabs'
import TextField from 'material-ui/TextField'
import Login from '../common/Login'
import GameWindow from '../common/GameWindow'
import {AVModel} from '../common/util'
import GameChooser from './GameChooser'

const styles = {
    frameContainer: {
        float: 'left',
        width: '39vw',
        height: '50vh',
        margin: '.5vh .666vw 0 0',
        padding: '0 12px'
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
            <Paper style={{ width: '20vw', height: '100vh', float: 'left', marginRight: '.666vw' }}>
                <Tabs>
                    <Tab label="章节">

                    </Tab>
                    <Tab label="变量">

                    </Tab>
                    <Tab label="图片">

                    </Tab>
                </Tabs>
            </Paper>
            <div style={{ margin: '0 0 0 0' }}>
                <Paper style={styles.frameContainer}>
                    <TextField ref="textField" style={{ maxHeight: '50vh' }} textareaStyle={{ height: '47vh' }}
                               multiLine={true} fullWidth={true} hintText={'Type your story here.'} />
                </Paper>
                <Paper style={styles.frameContainer}>
                    <GameWindow chapter={this.state.chapter} onAction={()=>{}} variables={{}} />
                </Paper>
            </div>
        </div></MuiThemeProvider>
    )
}

