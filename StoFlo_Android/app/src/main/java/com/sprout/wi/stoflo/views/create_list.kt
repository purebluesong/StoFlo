package com.sprout.wi.stoflo.views

import android.app.DialogFragment
import android.view.View
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVUser
import com.sprout.wi.stoflo.Activity.CreateStoryActivity
import com.sprout.wi.stoflo.Activity.createInter
import com.sprout.wi.stoflo.R
import com.sprout.wi.stoflo.fragment.CreateGameDialogFragment
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.widget.*
import com.avos.avoscloud.AVFile
import com.sprout.wi.stoflo.Global
import java.util.*


/**
 * Created by sprout on 16-7-13.
 */
class CreateList(createStoryActivity: CreateStoryActivity) : CreateGameDialogFragment.CreateGameListener,createInter {

    var rootView : LinearLayout? = null
    var context : CreateStoryActivity? = null
    var gamelist : List<AVObject> = ArrayList()
    var gameListView : LinearLayout? = null
    var imageViewQueue: List<ImageView> = ArrayList()
    var coverQueue: List<AVFile> = ArrayList()
    private val imageFillCount: Int = 0
    private var initOver: Boolean = false

    companion object {
        private val MSG_METHOD = 1
    }

    internal val handler: Handler = object :Handler(){
        override fun handleMessage(msg: Message?) {
            when(msg?.what) {
                MSG_METHOD -> (msg?.obj as Runnable).run()
            }
            super.handleMessage(msg)
        }
    }

    init {
        context = createStoryActivity
        val inflater = context?.layoutInflater
        rootView = inflater?.inflate(R.layout.create_story_list) as LinearLayout?
    }

    private fun getString(resid: Int): String? {
        return context?.getString(resid)
    }

    private fun findView(resid : Int): View? {
        return context?.find(resid)
    }

    override fun haveFlag(): Boolean {
        return context?.mGame == null
    }

    override fun iniData() {

    }

    override fun iniView() {
        val newGameButton :Button = findView(R.id.create_new_game) as Button
        newGameButton.setOnClickListener { newGameOnClick() }
        gameListView = findView(R.id.create_game_list_container) as LinearLayout?
        Thread(Runnable {
            val games = AVUser.getCurrentUser().getRelation<AVObject>(getString(R.string.info_table_user_have_game))
            gamelist = games.query.find()
            handler.sendMessage(handler.obtainMessage(MSG_METHOD, Runnable {
                for (game in gamelist) {
                    addNewViewToGamesView(game)
                }
                initOver = true
            }))
        }).start()
        fillCover(gameListView?.childCount as Int)
    }


    private fun fillCover(childCount: Int) {
        Thread(Runnable {
            while (imageFillCount < childCount) {
                val imageView = imageViewQueue.firstOrNull()
                val cover = coverQueue.firstOrNull()
                if ( imageView != null && cover != null) {
                    imageViewQueue - imageView
                    coverQueue - cover
                    imageView.setImageBitmap(Global.Bytes2Bimap(cover.data))
                    imageFillCount + 1
                }
            }
        }).start()
    }

    private fun addNewViewToGamesView(game: AVObject) {
        val inflator = context?.layoutInflater
        val templateView :LinearLayout = inflator?.inflate(R.layout.game_shortcut) as LinearLayout
        val cover :ImageView = templateView.findViewById(R.id.game_cover) as ImageView
        imageViewQueue + cover
        coverQueue + game.getAVFile<AVFile>(getString(R.string.info_table_game_cover))
        val titleView :TextView = templateView.findViewById(R.id.game_title) as TextView
        val descriptionView :TextView = templateView.findViewById(R.id.game_description) as TextView
        titleView.text = game.getString(getString(R.string.info_table_game_name))
        descriptionView.text = game.getString(getString(R.string.info_table_game_description))
        templateView.setOnClickListener { view -> editGame(gameListView?.indexOfChild(view)) }
        gameListView?.addView(templateView)
    }

    private fun editGame(indexOfChild: Int?) {
        context?.mGame = gamelist[indexOfChild as Int]
        context?.reLoadView()
    }

    private fun newGameOnClick() {
        val dialog = CreateGameDialogFragment()
        dialog.show(context?.fragmentManager, "CreateGameDialogFragment")
    }

    override fun getRootView(): View? {
        return rootView
    }

    override fun onCreate() {
    }

    override fun onStop() {
    }

    override fun onDestory() {
    }

    override fun onStart() {
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
            game.put(getString(R.string.info_table_game_name), gameName)
            game.put(getString(R.string.info_table_game_description), description)
            game.saveInBackground()
            addNewViewToGamesView(game)
            val user = AVUser.getCurrentUser()
            user.getRelation<AVObject>(getString(R.string.info_table_user_have_game)).add(game)
            user.saveInBackground()
        } else {
            context?.toast(R.string.error_create_game_failed)
        }
    }
}

fun LayoutInflater.inflate(resID: Int): Any {
    return inflate(resID,null)
}
