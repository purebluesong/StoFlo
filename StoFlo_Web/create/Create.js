import React from 'react'
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider'
import Login from '../common/Login'

class Create extends React.Component {
    constructor(props, context) {
        super(props, context)

        this.state = {}


    }

    render() {
        return (
            <MuiThemeProvider>
                <Login onFinished={x=>alert(JSON.stringify(x))} />
            </MuiThemeProvider>
        )
    }
}

export default Create
