package com.sprout.wi.stoflo.Activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import cn.finalteam.galleryfinal.*
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVQuery
import com.avos.avoscloud.AVUser
import com.sprout.wi.stoflo.R
import com.sprout.wi.stoflo.component.GlideImageLoader

import java.util.ArrayList

/**
 * Created by purebluesong on 2016/6/21.
 */
class StoFloActivity : Activity() {
    private var mUsernameView: EditText? = null
    private var mCreateNewGameButton: Button? = null
    private var mContinueLastGameButton: Button? = null
    private var mLogoutButton: Button? = null
    private var mGameListView: ListView? = null
    private var mGameList: List<AVObject>? = null
    private val FILL_VIEW_DATA: Int=1

    internal var mHandler: Handler = object : Handler() {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                FILL_VIEW_DATA -> fillViewWithData()
            }
            super.handleMessage(msg)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.sprout.wi.stoflo.R.layout.activity_stoflo)

        initView()
        initComponent()
    }

    override fun onStart() {
        super.onStart()
        initData()
    }

    private fun initData() {
        val thread = Thread(Runnable {
            val query = AVQuery<AVObject>(getString(R.string.info_table_game))
            try {
                mGameList = query.find()
            } catch (e: AVException) {
                Toast.makeText(applicationContext, getString(R.string.error_require_gamelist_failed), Toast.LENGTH_LONG)
            }

            mHandler.sendMessage(mHandler.obtainMessage(FILL_VIEW_DATA))
        })
        thread.start()
    }

    private fun fillViewWithData() {
        val gameNames = ArrayList<String>()
        for (game in mGameList!!) {
            gameNames.add(game.getString(getString(R.string.info_table_game_name)))
        }
        mGameListView!!.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, gameNames)

    }

    private fun initComponent() {

        //galleryImage
        val functionConfig = FunctionConfig.Builder().setEnableCamera(true).setEnableEdit(true).setEnableCrop(true).setEnableRotate(true).setCropSquare(true).setEnablePreview(true).build()
        val imageLoader = GlideImageLoader()
        val coreConfig = CoreConfig.Builder(this, imageLoader, ThemeConfig.CYAN).setFunctionConfig(functionConfig).build()
        GalleryFinal.init(coreConfig)
    }


    private fun startGame(game: AVObject) {
        jumpToGamePage(game.objectId, game.getAVObject<AVObject>(getString(R.string.info_table_start_chapter)).objectId)
    }

    private fun logout() {
        AVUser.logOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun jumpToCreatePage() {
        startActivity(Intent(this, CreateStoryActivity::class.java))
    }

    private fun jumpToGamePage(game: String, chapter: String) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra(getString(R.string.info_intent_game), game)
        intent.putExtra(getString(R.string.info_intent_chapter), chapter)
        startActivity(intent)
    }

    private fun initView() {
        mUsernameView = findViewById(R.id.status_username) as EditText
        mUsernameView!!.setText(AVUser.getCurrentUser().username)
        mCreateNewGameButton = findViewById(R.id.create_new_game) as Button
        mContinueLastGameButton = findViewById(R.id.continue_last_button) as Button
        mLogoutButton = findViewById(R.id.logout_button) as Button
        mGameListView = findViewById(R.id.GAME_LIST_VIEW) as ListView

        mCreateNewGameButton!!.setOnClickListener { jumpToCreatePage() }
//        mContinueLastGameButton!!.setOnClickListener { //                jumpToGamePage(0,0); }
        mLogoutButton!!.setOnClickListener { logout() }

        mGameListView!!.choiceMode = ListView.CHOICE_MODE_SINGLE
//        mGameListView!!.onItemClickListener = { OnItemClickListener{ parent, view, i, l -> Unit } }
    }
}
