import {alert} from './util'

export const AVModel = (() => {
    const cache = {}
    return function AVModel(x) {
        const T = cache[x] = cache[x] || AV.Object.extend(x)
        return this instanceof AVModel ? new T() : T
    }
})()

export const createChapter = function(done) {
    const chapter = new AVModel('Chapter')
    chapter.set('gameId', this.getObjectId())
    chapter.set('content', '')
    chapter.save().try(chapter => {
        const act = JSON.stringify({ assignments: [], chapterId: chapter.getObjectId() })
        const action = new AVModel('Action')
        action.set('gameId', this.getObjectId())
        action.set('op', '=')
        action.set('op1', '1')
        action.set('op2', '1')
        action.set('true', act)
        action.set('false', act)
        action.set('title', '选项名称')
        action.save().try(action => {
            chapter.relation('actions').add(action)
            chapter.save().try(done).catch(alert)
        }).catch(alert)
    }).catch(alert)
}
