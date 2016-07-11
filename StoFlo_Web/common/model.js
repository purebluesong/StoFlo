export const AVModel = (() => {
    const cache = {}
    return function AVModel(x) {
        const T = cache[x] = cache[x] || AV.Object.extend(x)
        return this instanceof AVModel ? new T() : T
    }
})()

export const getChapters = () =>
    (new AV.Query('Chapter' + this.getObjectId())).find()
