export const isemail = x => /^[-a-z0-9~!$%^&*_=+}{\'?]+(\.[-a-z0-9~!$%^&*_=+}{\'?]+)*@([a-z0-9_][-a-z0-9_]*(\.[-a-z0-9_]+)*\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,5})?$/i.test(x)
export const ispassword = x => x.length >= 8 && /\d/.test(x) && /[a-zA-Z]/.test(x)

export const AVModel = (() => {
    const cache = {}
    return function AVModel(x) {
        const T = cache[x] = cache[x] || AV.Object.extend(x)
        return this instanceof AVModel ? new T() : T
    }
})()

export const alert = x => window.alert(JSON.stringify(x))

