package com.sprout.wi.stoflo.views

import android.app.DialogFragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVUser
import com.sprout.wi.stoflo.Activity.CreateStoryActivity
import com.sprout.wi.stoflo.Activity.createInter
import com.sprout.wi.stoflo.R
import com.sprout.wi.stoflo.fragment.CreateGameDialogFragment
import org.jetbrains.anko.find
import org.jetbrains.anko.toast


/**
 * Created by sprout on 16-7-13.
 */
class CreateList(createStoryActivity: CreateStoryActivity) : CreateGameDialogFragment.CreateGameListener,createInter {

    var rootView : LinearLayout? = null
    var context : CreateStoryActivity? = null
    var gamelist : List<AVObject>? = null
    var gameListView : LinearLayout? = null

    init {
        context = createStoryActivity
    }


    private fun getString(resid: Int): String? {
        return context?.getString(resid)
    }

    private fun findView(resid : Int): View? {
        return context?.find(resid)
    }

    override fun haveFlag(): Boolean {
        throw UnsupportedOperationException()
    }

    override fun iniData() {
        val games = AVUser.getCurrentUser().getRelation<AVObject>(getString(R.string.info_table_user_have_game))
        Thread(Runnable {  gamelist = games.query.find() }).start()
    }

    override fun iniView() {
        val inflater = context?.layoutInflater
        rootView = inflater?.inflate(R.layout.create_story_list, null) as LinearLayout?
        val newGameButton :Button = findView(R.id.create_new_game) as Button
        newGameButton.setOnClickListener { newGameOnClick() }
        gameListView = findView(R.id.create_game_list_container) as LinearLayout?
    }

    private fun newGameOnClick() {
        val dialog = CreateGameDialogFragment()
        dialog.show(context?.fragmentManager, "CreateGameDialogFragment")
    }

    override fun getRootView(): View? {
        return rootView
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
        val nameEdit = (dialog as CreateGameDialogFragment).findViewById(R.id.create_game_name) as EditText
        val gameName = nameEdit.text.toString()
        val description = (dialog.findViewById(R.id.create_game_description) as EditText).text.toString()
        var cancel = false

        if (gameName.length == 0) {
            nameEdit.error = getString(R.string.error_invalid_game_name)
            cancel = true
        }

        if (!cancel) {
            val game = AVObject(getString(R.string.info_table_game))
            val chapterTable = getString(R.string.info_game_prefix) + gameName.hashCode()
            game.put(getString(R.string.info_table_game_name), gameName)
            game.put(getString(R.string.info_table_game_description), description)
            game.put(getString(R.string.info_table_chapter_table_name), chapterTable)
            context?.mGame = game
            val user = AVUser.getCurrentUser()
            user.put(getString(R.string.info_table_owner_game), game.objectId)
            user.saveInBackground()
        } else {
            context?.toast(R.string.error_create_game_failed)
        }
    }

}
