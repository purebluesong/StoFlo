import React from 'react'
import {List, ListItem} from 'material-ui/List'
import RaisedButton from 'material-ui/RaisedButton'

export default props => (
    <div>
        <List>{
            Object.keys(props.chapters||{}).map(chapterId => (
                <ListItem
                    key={chapterId}
                    primaryText={props.chapters[chapterId].get('content').slice(0, 20)}
                    onTouchTap={() => props.onTouchTap(props.chapters[chapterId])}
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
