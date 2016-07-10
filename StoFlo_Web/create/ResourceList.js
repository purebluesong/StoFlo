import React from 'react'
import {List, ListItem} from 'material-ui/List'
import RaisedButton from 'material-ui/RaisedButton'

export default props => (
    <div>
        <List>{
            (props.resources||[]).map(resource => (
                <ListItem
                    key={resource.getObjectId()}
                    primaryText={resource.get('key')}
                    onTouchTap={() => props.onTouchTap(resource)}
                />
            ))
        }</List>
        <RaisedButton
            label="Upload Image"
            secondary={true}
            style={{ float: 'right', marginRight: '12px' }}
        />
    </div>
)
