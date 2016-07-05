import React from 'react'
import Dialog from 'material-ui/Dialog'
import FlatButton from 'material-ui/FlatButton'
import TextField from 'material-ui/TextField'

class ProjectChooser extends React.Component {
    static propTypes = {where: React.PropTypes.string}
    static defaultProps = {where: "1=1"}

    constructor(props, context) {
        super(props, context)

        this.state = {}

        this.query = this.query.bind(this)
    }

    componentDidMount() {
        this.query()
    }

    query() {
        AV.Query.doCloudQuery(`select * from game where ${this.props.where}`)
    }

    render() {
        return (
            <MuiThemeProvider>
                <Login onFinished={x=>alert(JSON.stringify(x))} />
            </MuiThemeProvider>
        )
    }
}

export default ProjectChooser
