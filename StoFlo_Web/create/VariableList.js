import React from 'react'
import {List, ListItem} from 'material-ui/List'
import RaisedButton from 'material-ui/RaisedButton'

export default props => (
    <div>
        <List>{
            Object.keys(props.variables||{}).map(variable => (
                <ListItem
                    key={variable}
                    primaryText={variable}
                    onTouchTap={() => props.onTouchTap(variable)}
                />
            ))
        }</List>
        <RaisedButton
            label="New Variable"
            secondary={true}
            style={{ float: 'right', marginRight: '12px' }}
        />
    </div>
)
