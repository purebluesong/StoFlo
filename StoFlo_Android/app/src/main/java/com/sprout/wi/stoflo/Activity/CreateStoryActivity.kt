package com.sprout.wi.stoflo.Activity

import android.app.Activity
import android.content.Intent
import android.os.*
import android.view.View
import com.avos.avoscloud.*
import com.sprout.wi.stoflo.Global
import com.sprout.wi.stoflo.views.CreateAction
import com.sprout.wi.stoflo.views.CreateChapter
import com.sprout.wi.stoflo.views.CreateList

/**
 * Created by purebluesong on 2016/6/24.
 */
class CreateStoryActivity : Activity(){

    private var mGameList: CreateList? = null;
    private var mChapterCreate: CreateChapter? = null;
    private var mActionCreate: CreateAction? = null;

    private var mCurrent: createInter? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AVUser.getCurrentUser() == null) {
            jumpTo(LoginActivity::class.java)
        }
        loadContentView()
        mCurrent = getCurrent()
        setContentView(getCurrentView())
    }

    private fun getCurrentView(): View? {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getCurrent(): Global.standardAvtivityInterface? {

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

    fun jumpTo(activityClass: Class<LoginActivity>) {
        startActivity(Intent(this, activityClass))
        finish()
    }

}

interface createInter :Global.standardAvtivityInterface{

}