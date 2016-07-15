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
    private var mGameList : CreateList? = null
    private var mChapterCreate : CreateChapter? = null
    private var mActionCreate : CreateAction? = null

    private var mCurrent: createInter? = null
    private var views :List<createInter?>? = null

    var mGame: AVObject? = null
    var isActionEdit = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AVOSCloud.initialize(this, "L0j4qz7SOIcy99SP8ykDNoCl-gzGzoHsz", "ui7WUKdwtwvbXBgxic0fnwVd")

        if (AVUser.getCurrentUser() == null) {
            jumpTo(LoginActivity::class.java)
        }
        mGameList = CreateList(this)
        mChapterCreate = CreateChapter(this)
        mActionCreate = CreateAction(this)
        views = listOf(mGameList,mChapterCreate,mActionCreate)
        loadView()
        mCurrent?.onCreate()
    }

    private fun getCurrent(): createInter {
        for (view in views!!){
            if (view != null && view.haveFlag()) {
                return view
            }
        }
        return mGameList!!
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

    override fun onStart() {
        super.onStart()
        mCurrent?.onStart()
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