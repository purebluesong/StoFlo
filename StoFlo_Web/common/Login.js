import React from 'react'
import Dialog from 'material-ui/Dialog'
import FlatButton from 'material-ui/FlatButton'
import TextField from 'material-ui/TextField'
import {isemail, ispassword} from '../common/util'

export default class Login extends React.Component {
    static propTypes = {onFinished: React.PropTypes.func.isRequired}

    state = {
        finished: false,
        emailError: '',
        passwordError: ''
    }

    componentWillMount = () => {
        this.checkCachedUser()
    }

    checkCachedUser = () => {
        const currentUser = AV.User.current()
        currentUser && this.finish(currentUser)
    }

    checkFormat = () => {
        const email    = isemail(this.refs.email.getValue())
        const password = ispassword(this.refs.password.getValue())

        email    || this.setState({emailError: "invalid email format"})
        password || this.setState({passwordError: "atleast 8 chars, with number and letter"})

        return email && password
    }

    onSignUp = () => {
        if (!this.checkFormat()) return

        const user = new AV.User()
        user.setEmail(this.refs.email.getValue())
        user.setUsername(this.refs.email.getValue())
        user.setPassword(this.refs.password.getValue())

        user.signUp().try(this.finish).catch(e => {
            if (e.code == 203) {
                return this.setState({emailError: "Email already taken"})
            } else {
                return alert(JSON.stringify(e))
            }
        })
    }

    onLogIn = () => {
        if (!this.checkFormat()) return

        const username = this.refs.email.value
        const password = this.refs.password.value

        AV.User.logIn(username, password).try(this.finish).catch(e => {
            switch (e.code) {
                case 210:
                    return this.setState({passwordError: "Wrong password"})
                case 211:
                    return this.setState({emailError: "User not exists"})
                case 216:
                    return this.setState({emailError: "Email isn't verified"})
                default:
                    return alert(JSON.stringify(e))
            }
        })
    }

    finish = (user) => {
        this.setState({finished: true})
        this.props.onFinished(user)
    }

    actions = [
        <FlatButton
            label="Sign up"
            secondary={true}
            onTouchTap={this.onSignUp}
        />,
        <FlatButton
            label="Log in"
            primary={true}
            onTouchTap={this.onLogIn}
        />
    ]

    render = () => (
        <Dialog
            title="Login"
            actions={this.actions}
            modal={true}
            open={!this.state.finished}
        >
            <TextField
                hintText="example@hit.edu.cn"
                floatingLabelText="Email:"
                type="email"
                errorText={this.state.emailError}
                onFocus={()=>this.setState({emailError:''})}
                onBlur={this.checkFormat}
                ref="email"
            />
            <br />
            <TextField
                hintText="********"
                floatingLabelText="Password:"
                type="password"
                errorText={this.state.passwordError}
                onFocus={()=>this.setState({passwordError:''})}
                onBlur={this.checkFormat}
                ref="password"
            />
        </Dialog>
    )
}
