export const AVModel = (() => {
    const cache = {}
    return function AVModel(x) {
        const T = cache[x] = cache[x] || AV.Object.extend(x)
        return this instanceof AVModel ? new T() : T
    }
})()

export const getChapters = function() {
    const tableName ='Chapter' + this.getObjectId()
    const chapters = (new AV.Query(tableName)).find()
    if (chapters) {
        return chapters
    } else {
        const initChapter = new AVModel(tableName)
        initChapter.save().try(chapter => this.set('start_chapter', chapter))
        return [initChapter]
    }
}
