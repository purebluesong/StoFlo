import "babel-polyfill"

import React from 'react'
import ReactDOM from 'react-dom'
import injectTapEventPlugin from 'react-tap-event-plugin'
import Create from './Create'

injectTapEventPlugin()

ReactDOM.render(<Create />, document.getElementById('app'))
