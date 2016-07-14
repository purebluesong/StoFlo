import {alert} from './util'

export default class ActionAST {
    static fromAction = (action, actions) => {
        return new ActionAST_Condition({
            action: action,
            t: ActionAST.fromObject(JSON.parse(action.get('true')),  actions),
            f: ActionAST.fromObject(JSON.parse(action.get('false')), actions),
            op:  action.get('op'),
            op1: action.get('op1'),
            op2: action.get('op2')
        })
    }

    static fromObject = (o, actions) => {
        if (o.assignments.length) {
            const assignment = o.assignment.shift()
            return new ActionAST_Assignment({
                next: ActionAST.fromObject(o, actions),
                ...assignment
            })
        } else if (o.chapterId) {
            return new ActionAST_Goto({ chapterId: o.chapterId })
        } else if (o.actionId) {
            return ActionAST.fromAction(actions[o.actionId], actions)
        }
    }

    static readVariable = (name, variables) => {
        const i = parseInt(name, 10)
        return !isNaN(i) ? i
                         : (name == 'rand' ? Math.floor(Math.random() * 100)
                                           : variables[name])
    }

    static funcDict = {
        '>':  (x,y) => x >  y,
        '=':  (x,y) => x == y,
        '<':  (x,y) => x <  y,
        '>=': (x,y) => x >= y,
        '<=': (x,y) => x <= y,
        '!=': (x,y) => x != y,

        '+': (x,y) => x + y,
        '-': (x,y) => x - y,
        '*': (x,y) => x * y,
        '/': (x,y) => Math.floor(x / y + 0.5)
    }

    static toList = (node) => {
        const l = []
        node.toList(0, l)
        return l
    }

    static create = (chapter, game) => {
        const action = new AVModel('Action')
        action.set('game', game.getObjectId())
        action.save()
        return new ActionAST_Condition({
            op: '=',
            op1: '1',
            op2: '1',
            t: new ActionAST_Goto({ chapterId: chapter.chapterId }),
            f: new ActionAST_Goto({ chapterId: chapter.chapterId }),
            action: action
        })
    }

    constructor(o) {
        for (let i in o) {
            this[i] = o[i]
        }
    }
}

class ActionAST_Condition extends ActionAST {
    saveAsActions(done) {
        this.action.set('op', this.op)
        this.action.set('op1', this.op1)
        this.action.set('op2', this.op2)
        this.t.saveAsActions(o => {
            this.action.set('true', JSON.stringify(o))
            this.f.saveAsActions(o => {
                this.action.set('false', JSON.stringify(o))
                this.action.save().try(action => {
                    done({
                        assignments: [],
                        actionId: action.getObjectId()
                    })
                }).catch(alert)
            })
        })
    }

    toList(indent, list) {
        list.push({
            type: 'if',
            indent: indent,
            node: this
        })
        this.t.toList(indent+1, list)
        list.push({
            type: 'else',
            indent: indent,
            node: this
        })
        this.f.toList(indent+1, list)
    }

    eval(variables) {
        const op  = ActionAST.funcDict[this['op']]
        const op1 = ActionAST.readVariable(this['op1'], variables)
        const op2 = ActionAST.readVariable(this['op2'], variables)

        if ([op, op1, op2].some(x=>x==null)) {
            throw new Error("bug")
        }

        if (op(op1, op2)) {
            return this.t.eval(variables)
        } else {
            return this.f.eval(variables)
        }
    }
}

class ActionAST_Assignment extends ActionAST {
    saveAsActions(done) {
        this.next.saveAsActions(o => {
            o.assignments.push({
                left : this.left,
                op   : this.op,
                op1  : this.op1,
                op2  : this.op2
            })
            done(o)
        })
    }

    toList(indent, list) {
        list.push({
            type: '=',
            indent: indent,
            node: this
        })
        this.next.toList(indent, list)
    }

    eval(variables) {
        const op  = ActionAST.funcDict[this['op']]
        const op1 = ActionAST.readVariable(this['op1'], variables)
        const op2 = ActionAST.readVariable(this['op2'], variables)

        if ([op, op1, op2].some(x=>x==null)) {
            throw new Error("bug")
        }

        variables[left] = op(op1, op2)

        return this.next.eval(variables)
    }
}

class ActionAST_Goto extends ActionAST {
    saveAsActions(done) {
        done({
            assignments: [],
            chapterId: this.chapterId
        })
    }

    toList(indent, list) {
        list.push({
            type: 'insert',
            indent: indent,
            node: this
        })
        list.push({
            type: 'goto',
            indent: indent,
            node: this
        })
    }

    insert(type, game, root) {
        let n
        switch (type) {
            case 'condition':
                const action = new AVModel('Action')
                action.set('game', game.getObjectId())
                action.save()
                n = new ActionAST_Condition({
                    op: '=',
                    op1: '1',
                    op2: '1',
                    t: this,
                    f: new ActionAST_Goto({ chapterId: this.chapterId }),
                    action: action
                })
                break
            case 'assignment':
                n = new ActionAST_Assignment({
                    left: 'count',
                    op: '+',
                    op1: 'count',
                    op2: '1',
                    next: this
                })
                break
        }

        const _fixrefs = node => {
            if (node.next != null) {
                if (node.next == this) {
                    return node.next = n
                } else {
                    _fixrefs(node.next)
                }
            } else if (node.t != null) {
                if (node.t == this) {
                    return node.t = n
                } else {
                    _fixrefs(node.t)
                }
            } else if (node.f != null) {
                if (node.f == this) {
                    return node.f = n
                } else {
                    _fixrefs(node.f)
                }
            }
        }

        _fixrefs(root)
    }

    eval(variables) {
        return this.chapterId
    }
}


