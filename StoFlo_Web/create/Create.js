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
import {AVModel} from '../common/model'
import GameChooser from './GameChooser'
import ChapterList from './ChapterList'
import VariableList from './VariableList'
import ResourceList from './ResourceList'
import AddBranchButton from './AddBranchButton'
import BranchOptionEditor from './BranchOptionEditor'

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
        chapters: {},
        actions: [],
        variables: {},

        chapter: new AVModel('Chapter'),
        options: [],
        lastFocus: ()=>{},

        modal: '',
        snack: ''
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
                    this.onSelectChapter(chapters[game.get('start_chapter').getObjectId()])
                })
                .catch(alert)
        AV.Query.doCloudQuery(`select * from Action where gameId='${game.getObjectId()}'`)
                .try(response => {
                    const actions = {}
                    response.results.forEach(action => {
                        actions[action.getObjectId()] = action
                    })
                    this.setState({ actions: actions })
                })
                .catch(alert)
    }

    onSelectChapter = (chapter) => {
        chapter.relation('actions').query().find().try(options => {
            this.setState({
                chapter: chapter,
                options: options
            })
        }).catch(alert)
    }

    save = () => {
        this.state.chapter.save().catch(alert)
        AV.Object.saveAll(this.state.actions).catch(alert)
        this.setState({ snack: '保存完毕' })
    }

    setLastFocus = (f) => {
        this.setState({ lastFocus: f })
    }

    callLastFocus = (arg) => {
        this.state.lastFocus(arg)
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
                            onTouchTap={this.callLastFocus}
                        />
                    </Tab>
                    <Tab label="变量">
                        <VariableList
                            variables={JSON.parse(this.state.game.get('init_vars')||'{}')}
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
                    <TextField ref="content" style={{ maxHeight: '50vh' }} textareaStyle={{ height: '47vh' }}
                               multiLine={true} fullWidth={true} hintText={'Type your story here.'}
                               onBlur={()=>this.state.chapter.set('content', this.refs.content.getValue())} />
                </Paper>
                <Paper style={styles.frameContainer}>
                    <GameWindow chapter={this.state.chapter} onAction={()=>{}} variables={{}} />
                </Paper>
            </div>
            <div style={{ margin: '.5vh .666vw .5vh 0', height: '48.5vh', width: '78.333vw', float: 'left' }}>
                <Paper style={{ width: '30vw', height: '100%', float: 'left', marginRight: '.666vw' }}>
                    <List>

                    </List>
                    <FlatButton
                        style={{ float: 'right', marginRight: '12px' }}
                        label="Save"
                        primary={true}
                        onTouchTap={this.save}
                    />
                </Paper>
                {
                    this.state.options.map(option => (
                        <BranchOptionEditor style={{ width: '30vw', height: '100%', float: 'left', marginRight: '.666vw' }}
                                            key={option.getObjectId()} entry={option} actions={this.state.actions}
                                            ref={option.getObjectId()} setLastFocus={this.setLastFocus} />
                    ))
                }
                <AddBranchButton style={{ height: '100%', width: '56px', float: 'left' }} />
            </div>
            <Snackbar
                open={!!this.state.snack}
                message={this.state.snack}
                autoHideDuration={3000}
                action="确定"
                onRequestClose={()=>this.setState({ snack: '' })}
                onActionTouchTap={()=>this.setState({ snack: '' })}
            />
        </div></MuiThemeProvider>
    )
}

