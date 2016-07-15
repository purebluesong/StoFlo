import React from 'react'
import {List, ListItem} from 'material-ui/List'
import RaisedButton from 'material-ui/RaisedButton'
import {alert} from '../common/util'

const styles = {
    fileInput: {
        cursor: 'pointer',
        position: 'absolute',
        top: 0,
        bottom: 0,
        right: 0,
        left: 0,
        width: '100%',
        opacity: 0,
    }
}

export default props => (
    <div>
        <List>{
            (props.resources||[]).map(resource => (
                <ListItem
                    key={resource.id}
                    primaryText={resource.name}
                    style={{ textOverflow: 'ellipsis' }}
                    onTouchTap={() => props.onTouchTap(resource)}
                />
            ))
        }</List>
        <RaisedButton
            label="Upload Image"
            secondary={true}
            style={{ float: 'right', marginRight: '12px' }}
        >
            <input
                type="file"
                style={styles.fileInput}
                onChange={e=>{
                    if (e.target.files.length) {
                        const file = e.target.files[0]
                        const AVfile = new AV.File(file.name, file)
                        AVfile.save().try(props.onButtonClick).catch(console.info.bind(console))
                    }
                }}
            />
        </RaisedButton>
    </div>
)
