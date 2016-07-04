import React from 'react'
import ReactDOM from 'react-dom'
import injectTapEventPlugin from 'react-tap-event-plugin'
import Play from './Play'

injectTapEventPlugin()

ReactDOM.render(<Play />, document.getElementById('app'))
