package com.sprout.wi.stoflo.Activity

import android.app.Activity
import android.content.Intent
import android.os.*
import android.view.View
import android.widget.Toast
import com.avos.avoscloud.*
import com.sprout.wi.stoflo.Global
import com.sprout.wi.stoflo.views.CreateAction
import com.sprout.wi.stoflo.views.CreateChapter
import com.sprout.wi.stoflo.views.CreateList
import java.util.*

/**
 * Created by purebluesong on 2016/6/24.
 */
class CreateStoryActivity : Activity(){

    private var mGameList: CreateList? = null
    private var mChapterCreate: CreateChapter? = null
    private var mActionCreate: CreateAction? = null

    private var mCurrent: createInter? = null
    private val views = listOf(mGameList,mChapterCreate,mActionCreate)

    var mGame: AVObject? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        if (AVUser.getCurrentUser() == null) {
            jumpTo(LoginActivity::class.java)
        }
        super.onCreate(savedInstanceState)
        loadContentView()
        loadView()
    }

    private fun getCurrent(): createInter {
        for (view in views){
            if (view?.haveFlag()!!) {
                return view as createInter
            }
        }
        return views[0] as createInter
    }

    fun loadView() {
        mCurrent = getCurrent()
        setContentView(mCurrent?.getRootView())
        mCurrent?.iniData()
        mCurrent?.iniView()
    }

    fun reLoadView() {
        loadView()
    }

    private fun loadContentView() {
        mGameList = CreateList(this)
        mChapterCreate = CreateChapter(this)
        mActionCreate = CreateAction(this)
    }

    override fun onStart() {
        super.onStart()
        mCurrent?.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        mCurrent?.onCreate()
    }

    override fun onStop() {
        super.onStop()
        mCurrent?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCurrent?.onDestory()
    }

    fun Activity.jumpTo(activityClass: Class<LoginActivity>) {
        startActivity(Intent(this, activityClass))
        finish()
    }

}

interface createInter :Global.standardAvtivityInterface{
    fun iniData()
    fun iniView()
    fun getRootView(): View?
    fun haveFlag():Boolean
}