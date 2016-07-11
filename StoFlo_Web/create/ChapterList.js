import React from 'react'
import {List, ListItem} from 'material-ui/List'
import RaisedButton from 'material-ui/RaisedButton'

export default props => (
    <div>
        <List>{
            props.chapters.map(chapter => (
                <ListItem
                    key={chapter.getObjectId()}
                    primaryText={chapter.get('content').slice(0, 20)}
                    onTouchTap={() => props.onTouchTap(chapter)}
                />
            ))
        }</List>
        <RaisedButton
            label="New Chapter"
            secondary={true}
            style={{ float: 'right', marginRight: '12px' }}
        />
    </div>
)
