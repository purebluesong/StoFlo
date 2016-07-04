import React from 'react'
import ReactDOM from 'react-dom'
import injectTapEventPlugin from 'react-tap-event-plugin'
import Profile from './Profile'

injectTapEventPlugin()

ReactDOM.render(<Profile />, document.getElementById('app'))
