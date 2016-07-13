import React from 'react'
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider'
import Paper from 'material-ui/Paper'
import {Tabs, Tab} from 'material-ui/Tabs'
import TextField from 'material-ui/TextField'
import Login from '../common/Login'
import GameWindow from '../common/GameWindow'
import {AVModel, getChapters} from '../common/model'
import GameChooser from './GameChooser'
import ChapterList from './ChapterList'
import VariableList from './VariableList'
import ResourceList from './ResourceList'
import AddBranchButton from './AddBranchButton'

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
        game: new AVModel('Game'),
        chapters: [],
        chapter: new AVModel('Chapter'),
        variables: {},

        modal: ''
    }

    onLoginFinished = (user) => {
        this.setState({modal: 'choose_game'})
    }

    onChooseGameFinished = (game) => {
        this.setState({
            modal: '',
            game: game,
            chapters: game::getChapters()
        })
    }

    render = () => (
        <MuiThemeProvider><div>
            <Login onFinished={this.onLoginFinished} />
            <GameChooser open={this.state.modal == 'choose_game'} onFinished={this.onChooseGameFinished} />
            <Paper style={{ width: '20vw', height: '100vh', float: 'left', marginRight: '.666vw' }}>
                <Tabs>
                    <Tab label="章节">
                        <ChapterList
                            chapters={this.state.chapters}
                            open={this.state.modal == 'add_chapter'}
                        />
                    </Tab>
                    <Tab label="变量">
                        <VariableList
                            variables={this.state.game.get('init_vars')}
                            open={this.state.modal == 'add_variable'}
                        />
                    </Tab>
                    <Tab label="图片">
                        <ResourceList
                            resources={this.state.game.get('resources')}
                            open={this.state.modal == 'add_resource'}
                        />
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
            <div style={{ margin: '.5vh .666vw .5vh 0', height: '48.5vh', width:'78.333vw', float: 'left' }}>
                <AddBranchButton style={{ height: '100%', width: '56px' }} />
            </div>
        </div></MuiThemeProvider>
    )
}

