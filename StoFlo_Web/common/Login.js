import React from 'react'
import Dialog from 'material-ui/Dialog';
import FlatButton from 'material-ui/FlatButton';
import TextField from 'material-ui/TextField';
import {isemail} from '../common/util'

class Login extends React.Component {
    constructor(props, context) {
        super(props, context)

        this.state = {
            finished: false,
            emailError: '',
            passwordError: ''
        }

        this.checkFormat = this.checkFormat.bind(this)
        this.onSignUp    = this.onSignUp.bind(this)
        this.onLogIn     = this.onLogIn.bind(this)
        this.finish      = this.finish.bind(this)

        this.checkCachedUser()
    }

    checkCachedUser() {
        const currentUser = AV.User.current()
        currentUser && this.finish(currentUser)
    }

    checkFormat() {
        const email    = isemail(this.refs.email.value)
        const password = ispassword(this.refs.password.value)

        email    || this.setState({emailError: "malformed email"})
        password || this.setState({passwordError: "malformed password"})

        return email && password
    }

    onSignUp() {
        if (!this.checkFormat()) return

        const user = new AV.User()
        user.setEmail(this.refs.email.value)
        user.setPassword(this.refs.password.value)

        user.signUp().try(this.finish).catch(e => {
            if (e.code == 203) {
                return this.setState({emailError: "Email already taken"})
            } else {
                return alert(e)
            }
        })
    }

    onLogIn() {
        if (!this.checkFormat()) return

        const email    = this.refs.email.value
        const password = this.refs.password.value

        AV.User.logIn(email, password).try(this.finish).catch(e => {
            switch (e.code) {
                case 210:
                    return this.setState({passwordError: "Wrong password"})
                case 211:
                    return this.setState({emailError: "User not exists"})
                case 216:
                    return this.setState({emailError: "Email isn't verified"})
                default:
                    return alert(e)
            }
        })
    }

    finish(user) {
        this.setState({finished: true})
        this.props.onFinished(user)
    }

    render() {
        const actions = [
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

        return (
            <Dialog
                title="Login"
                actions={actions}
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
}

export default Login
