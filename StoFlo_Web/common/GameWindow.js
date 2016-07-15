import React from 'react'

const styles = {
    container: {
        width: '98%',
        height: '98%',
        position: 'relative'
    },
    textBox: {
        position: 'absolute',
        zIndex: '10',
        whiteSpace: 'pre'
    },
    actionDialog: {
        position: 'absolute',
        zIndex: '15'
    }
}

export default class GameWindow extends React.Component {
    static propTypes = {
        chapter   : React.PropTypes.any.isRequired,
        onAction  : React.PropTypes.func.isRequired,
        variables : React.PropTypes.object.isRequired
    }

    state = {
        sections: [],
        step: 0
    }

    componentDidMount = () => {
        this.refs.container.addEventListener('click', e => {
            this.setState({step: this.state.step + 1})
        }, true)
    }

    componentWillReceiveProps = (nextProps) => {
        //if (nextProps.chapter == this.props.chapter) return // console.warn("not update")
        const content   = nextProps.chapter.get('content')
        const variables = nextProps.variables
        this.setState({
            sections: this.parseContent(content, variables),
            step: 0
        })
    }

    parseContent = (content, variables) => {
        const segments = (content||'').split(/(?:{{)|(?:}})/)
        const sections = [[]]
        let isCode = false
        for (let segment of segments) {
            if (isCode) {
                const code = segment.trim()
                if (code == '#') {
                    sections.push([])
                }
                sections[sections.length-1].push(variables[code]) // either int or undefined
            } else {
                sections[sections.length-1].push(segment)
            }
            isCode = !isCode
        }
        return sections.map(x=>x.join(''))
    }

    render = () => {
        const finished = this.state.step >= this.state.sections.length
        return (
            <div style={styles.container} ref="container">
                <img src={this.props.chapter.get('background')||'http://'} />
                <div style={styles.textBox}>
                    { !finished ? this.state.sections[this.state.step] : this.state.sections[this.state.sections.length-1] }
                </div>
                <div style={styles.actionDialog} hidden={!finished}>

                </div>
            </div>
        )
    }
}
