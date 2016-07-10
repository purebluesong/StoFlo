package com.sprout.wi.stoflo.Activity

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.avos.avoscloud.AVException
import com.avos.avoscloud.AVFile
import com.avos.avoscloud.AVObject
import com.avos.avoscloud.AVQuery
import com.sprout.wi.stoflo.Global
import com.sprout.wi.stoflo.R

import java.util.ArrayList

/**
 * Created by purebluesong on 2016/6/24.
 */
class GameActivity : Activity() {

    private var mChapterList: MutableList<AVObject>? = null
    private var mGame: AVObject? = null
    private var mCurrentChapter: AVObject? = null
    private var mBackground: Bitmap? = null
    private var mBackgroundFile: AVFile? = null
    private var mQuery: AVQuery<AVObject>? = null
    private var mCurrentNextChapters: List<AVObject>? = null
    private var mContentView: TextView? = null
    private var mNextsView: LinearLayout? = null
    private var gameViewContainer: LinearLayout? = null
    private var mRootView: ScrollView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRootView = layoutInflater.inflate(R.layout.activity_game, null) as ScrollView
        setContentView(mRootView)
        val gameID = intent.getStringExtra(getString(R.string.info_intent_game))
        val chapterID = intent.getStringExtra(getString(R.string.info_intent_chapter))
        iniData(gameID, chapterID)
        gameViewContainer = findViewById(R.id.game_view_container) as LinearLayout
    }

    private fun addChapterToView(chapter: AVObject) {
        val content = chapter.getString(getString(R.string.info_table_chapter_content))
        mBackgroundFile = chapter.getAVFile<AVFile>(getString(R.string.info_table_chapter_background))
        mQuery = chapter.getRelation<AVObject>(getString(R.string.info_table_chapter_nexts)).query
        mCurrentNextChapters = null
        mChapterList!!.add(chapter)
        val thread = Thread(Runnable {
            try {
                if (mBackgroundFile != null) {
                    mBackground = Global.Bytes2Bimap(mBackgroundFile!!.data)
                } else {
                    mBackground = null
                }
                mCurrentNextChapters = mQuery!!.find()
                mHandler.sendMessage(mHandler.obtainMessage(SET_BACKGROUND))
                mHandler.sendMessage(mHandler.obtainMessage(SET_NEXT_CHAPTERS))
            } catch (e: AVException) {
                e.printStackTrace()
            }
        })
        thread.start()

        val inflater = layoutInflater
        val container = inflater.inflate(R.layout.chapter_show_asset, null) as LinearLayout
        mContentView = container.findViewById(R.id.chapter_content_show) as TextView
        mNextsView = container.findViewById(R.id.chapter_next_container) as LinearLayout
        mContentView!!.text = content

        gameViewContainer!!.addView(container)
    }

    private fun iniData(gameID: String, chapterID: String) {
        mChapterList = ArrayList<AVObject>()
        val thread = Thread(Runnable {
            try {
                mGame = AVObject.createWithoutData(getString(R.string.info_table_game), gameID)
                mGame!!.fetch()
                mCurrentChapter = AVObject.createWithoutData(mGame!!.getString(getString(R.string.info_table_chapter_table_name)), chapterID)
                mCurrentChapter!!.fetch()
                mHandler.sendMessage(mHandler.obtainMessage(INIT_ADD_CHAPTER))
            } catch (e: AVException) {
                Toast.makeText(applicationContext, getString(R.string.error_fetch_game_failed), Toast.LENGTH_LONG)
                e.printStackTrace()
            }
        })
        thread.start()
    }

    private fun setNexts() {
        for (chapter in mCurrentNextChapters!!) {
            addNextChapterButtonTo(chapter, mNextsView as LinearLayout)
        }
        mNextsView!!.setOnClickListener { v -> v.isClickable = false }
    }

    private fun addNextChapterButtonTo(chapter: AVObject, buttonContainer: LinearLayout): Button {
        val button = Button(this)
        button.text = chapter.getString(getString(R.string.info_table_chapter_name))
        button.gravity = Gravity.CENTER
        button.setSingleLine()
        button.setBackgroundColor(Color.CYAN)
        button.background.alpha = 0
        button.setTextColor(Color.WHITE)
        button.setShadowLayer(10f, 0f, 4f, Color.WHITE)
        button.left = 20
        button.right = 20
        button.setOnClickListener {
            addChapterToView(chapter)
            scrollToBottom()
        }
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        button.layoutParams = params

        buttonContainer.addView(button)
        return button
    }

    private fun scrollToBottom() {
        mHandler.post { mRootView!!.fullScroll(ScrollView.FOCUS_DOWN) }
    }

    internal var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                SET_BACKGROUND -> {
                    if (mBackground != null) {
                        mContentView!!.setBackgroundDrawable(BitmapDrawable(resources, mBackground))
                    }
                    scrollToBottom()
                }
                SET_NEXT_CHAPTERS -> {
                    setNexts()
                    scrollToBottom()
                }
                INIT_ADD_CHAPTER -> addChapterToView(mCurrentChapter as AVObject)
            }
        }
    }

    companion object {
        private val SET_BACKGROUND = 1
        private val SET_NEXT_CHAPTERS = 2
        private val INIT_ADD_CHAPTER = 3
    }

}
