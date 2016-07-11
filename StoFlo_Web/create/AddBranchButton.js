import React from 'react'
import FloatingActionButton from 'material-ui/FloatingActionButton'
import AddIcon from 'material-ui/svg-icons/content/add'

export default props => (
    <div style={{ position: 'relative', ...props.style }}>
        <div style={{ position: 'absolute', bottom: '12px', left: '12px' }}>
            <FloatingActionButton
                secondary={true}
                style={{ margin: '12px', float: 'left' }}
                onTouchTap={props.onTouchTap}
            >
                <AddIcon />
            </FloatingActionButton>
        </div>
    </div>
)
