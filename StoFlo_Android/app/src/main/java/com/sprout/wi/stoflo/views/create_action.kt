package com.sprout.wi.stoflo.views

import android.view.View
import com.sprout.wi.stoflo.Activity.CreateStoryActivity
import com.sprout.wi.stoflo.Activity.createInter
import com.sprout.wi.stoflo.R

/**
 * Created by sprout on 16-7-13.
 */

class CreateAction(createStoryActivity: CreateStoryActivity) : createInter{
    private val content:CreateStoryActivity = createStoryActivity
    private val rootView:View = content.layoutInflater.inflate(R.layout.create_action) as View

    override fun iniData() {

    }

    override fun iniView() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRootView(): View {
        return rootView
    }

    override fun haveFlag(): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStart() {
        throw UnsupportedOperationException()
    }

    override fun onCreate() {
        throw UnsupportedOperationException()
    }

    override fun onStop() {
        throw UnsupportedOperationException()
    }

    override fun onDestory() {
        throw UnsupportedOperationException()
    }


}
