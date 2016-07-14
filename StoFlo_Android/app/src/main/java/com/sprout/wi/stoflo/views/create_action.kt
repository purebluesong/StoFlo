package com.sprout.wi.stoflo.views

import android.os.Message
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import com.sprout.wi.stoflo.Activity.CreateStoryActivity
import com.sprout.wi.stoflo.Activity.createInter
import com.sprout.wi.stoflo.R
import android.os.Handler
import android.widget.Button

/**
 * Created by sprout on 16-7-13.
 */

class CreateAction(createStoryActivity: CreateStoryActivity) : createInter{
    private val content:CreateStoryActivity = createStoryActivity
    private val rootView:View = content.layoutInflater.inflate(R.layout.create_action) as View
    private val actionListView : LinearLayout = rootView.findViewById(R.id.edit_action_list) as LinearLayout
    private val variablesListView : LinearLayout = rootView.findViewById(R.id.edit_variable_list) as LinearLayout
    private val opVarOneView : Spinner = rootView.findViewById(R.id.edit_op_var_1) as Spinner
    private val opVarTwoView : Spinner = rootView.findViewById(R.id.edit_op_var_2) as Spinner
    private val opView : Spinner = rootView.findViewById(R.id.edit_op) as Spinner
    private val successOpEdit : EditText = rootView.findViewById(R.id.edit_action_succeed) as EditText
    private val failedOpEdit : EditText = rootView.findViewById(R.id.edit_action_failed) as EditText
    private val returnButton : Button = rootView.findViewById(R.id.edit_action_return) as Button
    private val saveButton : Button = rootView.findViewById(R.id.edit_action_save) as Button

    internal val functionHandler : Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            (msg?.obj as Runnable).run()
            super.handleMessage(msg)
        }
    }

    private fun getString(resID :Int) :String{
        return content.getString(resID)
    }

    override fun iniData() {

    }

    override fun iniView() {
        returnButton.setOnClickListener {
            content.isActionEdit = false
            content.reLoadView()
        }
    }

    override fun getRootView(): View {
        return rootView
    }

    override fun haveFlag(): Boolean {
        return content.mGame != null && content.isActionEdit
    }

    override fun onStart() {
    }

    override fun onCreate() {
    }

    override fun onStop() {
    }

    override fun onDestory() {
    }


}
