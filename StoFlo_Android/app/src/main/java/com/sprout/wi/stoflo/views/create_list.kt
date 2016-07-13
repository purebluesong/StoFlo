package com.sprout.wi.stoflo.views

import android.app.DialogFragment
import android.widget.LinearLayout
import com.sprout.wi.stoflo.Activity.CreateStoryActivity
import com.sprout.wi.stoflo.Activity.createInter
import com.sprout.wi.stoflo.fragment.CreateGameDialogFragment

/**
 * Created by sprout on 16-7-13.
 */
class CreateList(createStoryActivity: CreateStoryActivity) : CreateGameDialogFragment.CreateGameListener,createInter {

    var rootView : LinearLayout? = null
    var context : CreateStoryActivity? = null

    init {
        context = createStoryActivity
    }
    override fun haveFlage(): Boolean {
        throw UnsupportedOperationException()
    }

    override fun iniData() {
        throw UnsupportedOperationException()
    }

    override fun iniView() {
        var inflater = context?.layoutInflater
    }

    override fun getRootView() {
        return
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

    override fun onStart() {
        throw UnsupportedOperationException()
    }


    override fun onDialogPositiveClick(dialog: DialogFragment?) {
        throw UnsupportedOperationException()
    }

}
