package com.sprout.wi.stoflo.Activity

import android.annotation.TargetApi
import android.app.Activity
import android.app.DialogFragment
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.*
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.Toast
import cn.finalteam.galleryfinal.GalleryFinal
import cn.finalteam.galleryfinal.model.PhotoInfo
import com.avos.avoscloud.*
import com.sprout.wi.stoflo.*
import com.sprout.wi.stoflo.R
import com.sprout.wi.stoflo.fragment.ChooseChapterDialogFragment
import com.sprout.wi.stoflo.fragment.ChooseChapterMultipleFragment
import com.sprout.wi.stoflo.fragment.CreateGameDialogFragment

import java.util.ArrayList
import java.util.Collections

/**
 * Created by purebluesong on 2016/6/24.
 */
class CreateStoryActivity : Activity(), CreateGameDialogFragment.CreateGameListener {

    private var mChapterNameEdit: EditText? = null
    private var mChapterContentEdit: EditText? = null
    private var mChapterName: Button? = null
    private var mChapterContentBackground: Button? = null
    private var mChapterContentText: Button? = null
    private var mChaptersNext: Button? = null
    private var mSave: Button? = null
    private var mCancel: Button? = null
    private var mSaveNext: Button? = null
    private var mNextChaptersContainer: HorizontalScrollView? = null
    private var mChapterChooser: ChapterChooser? = null
    private var mChapter: AVObject? = null
    private var mGame: AVObject? = null
    private var mNextChapters: List<AVObject>? = null
    private var mBackground: Drawable? = null

    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                SAVE_CHAPTER_PIC -> mBackground = BitmapDrawable(resources, msg.obj as Bitmap)
                SET_CHAPTER_PIC -> mChapterContentEdit!!.background = mBackground
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.sprout.wi.stoflo.R.layout.activity_create_story)
        if (AVUser.getCurrentUser() == null) {
            jumpTo(LoginActivity::class.java)
        }
        mGame = AVUser.getCurrentUser().getAVObject<AVObject>(getString(R.string.info_key_self_game))
        if (mGame == null) {
            showCreateGameDialog()
        }
    }

    override fun onStart() {
        super.onStart()
        if (mGame != null) {
            initData()
            initView()
            fillContentWith(mChapter as AVObject)
            registerOCL()
        }
    }

    private fun initData() {
        val user = AVUser.getCurrentUser()
        mBackground = null
        startNewThread(Runnable {
            try {
                mNextChapters = AVUser.getCurrentUser().getRelation<AVObject>(getString(R.string.info_table_chapter_nexts)).query.find()
            } catch (e: AVException) {
                e.printStackTrace()
            }
        })

        mChapter = user.getAVObject<AVObject>(getString(R.string.info_self_game_current_edit))
    }

    private fun startNewThread(runnable: Runnable) {
        val thread = Thread(runnable)
        thread.start()
    }

    private fun fillContentWith(chapter: AVObject) {
        val name = chapter.getString(getString(R.string.info_table_chapter_name))
        val content = chapter.getString(getString(R.string.info_table_chapter_content))
        val picID = chapter.getString(getString(R.string.info_table_chapter_background))


        AVFile.withObjectIdInBackground(picID, object : GetFileCallback<AVFile>() {
            override fun done(avFile: AVFile, e: AVException?) {
                if (e == null) {
                    var pic: Bitmap? = null
                    try {
                        val data = avFile.data
                        if (data != null)
                            pic = BitmapFactory.decodeByteArray(data, 0, data.size)
                    } catch (e1: AVException) {
                        e1.printStackTrace()
                    }

                    mHandler.sendMessage(mHandler.obtainMessage(SAVE_CHAPTER_PIC, pic))
                    mHandler.sendMessage(mHandler.obtainMessage(SET_CHAPTER_PIC))
                } else {
                    e.printStackTrace()
                    Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT)
                }
            }
        })

        mChapterNameEdit!!.setText(name)
        mChapterContentEdit!!.setText(content)
        fillNextChaptersContainer(mNextChapters as List<AVObject>)
    }

    private fun fillNextChaptersContainer(chapters: List<AVObject>) {
        for (nextChapters in chapters) {
            val title = nextChapters.getString(getString(R.string.info_table_chapter_name))
            addButtonToNextChaptersContainer(title)
        }
    }

    private fun addButtonToNextChaptersContainer(title: String) {
        val button = Button(this)
        button.text = title
        button.setBackgroundColor(Color.CYAN)
        button.background.alpha = 255
        button.setSingleLine()
        button.gravity = Gravity.CENTER
        val layoutparams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        button.layoutParams = layoutparams
        button.minimumWidth = 200
        mNextChaptersContainer!!.addView(button)
    }

    private fun showCreateGameDialog() {
        val dialog = CreateGameDialogFragment()
        dialog.show(fragmentManager, "CreateGameDialogFragment")
    }

    private fun initView() {
        mChapterNameEdit = findViewById(R.id.chapter_name_edit) as EditText
        mChapterContentEdit = findViewById(R.id.chapter_content_edit) as EditText
        mChapterContentBackground = findViewById(R.id.edit_function_chapter_background) as Button
        mChapterContentText = findViewById(R.id.chapter_content_text) as Button
        mChaptersNext = findViewById(R.id.chapter_next_chooser) as Button
        mChapterName = findViewById(R.id.chapter_name_button) as Button
        mNextChaptersContainer = findViewById(R.id.function_next_chapters_scroll) as HorizontalScrollView

        mSave = findViewById(R.id.create_save) as Button
        mSaveNext = findViewById(R.id.create_save_next) as Button
        mCancel = findViewById(R.id.create_cancel) as Button

        mChapterChooser = ChapterChooser()
    }

    private fun registerOCL() {
        mSave!!.setOnClickListener { attemptSave() }
        mCancel!!.setOnClickListener { cancel() }
        mSaveNext!!.setOnClickListener {
            attemptSave()
            clearPage()
            fillContentWith(mChapterChooser!!.chooseSingle())
        }
        mChaptersNext!!.setOnClickListener { mChapterChooser!!.showChooseMultipleDialog() }
        mChapterContentBackground!!.setOnClickListener { setChapterBackground() }
        mChapterContentText!!.setOnClickListener { }
        mChapterName!!.setOnClickListener { mChapterChooser!!.chooseSingle() }
    }

    private fun setChapterBackground() {
        GalleryFinal.openGallerySingle(Global.REQUSET_CODE_GALLERY, object : GalleryFinal.OnHanlderResultCallback {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onHanlderSuccess(reqeustCode: Int, resultList: List<PhotoInfo>) {
                val path = resultList[0].photoPath
                mBackground = Drawable.createFromPath(path)
                mChapterContentEdit!!.background = mBackground
            }

            override fun onHanlderFailure(requestCode: Int, errorMsg: String) {
                Toast.makeText(applicationContext, errorMsg, Toast.LENGTH_LONG)
            }
        })
    }

    private fun clearPage() {
        mChapterContentEdit!!.setText("")
        mChapterNameEdit!!.setText("")
    }

    private fun cancel() {
        clearPage()
        jumpTo(StoFloActivity::class.java)
    }

    private fun jumpTo(activityClass: Class<*>) {
        startActivity(Intent(this, activityClass))
        finish()
    }

    private fun attemptSave(): Boolean {
        var cancel = false
        var focusView: View? = null

        if (isInvalidateChapterName) {
            focusView = mChapterNameEdit
            cancel = true
        }

        if (isInvalidateChapterContent) {
            focusView = mChapterContentEdit
            cancel = true
        }

        if (cancel) {
            focusView!!.requestFocus()
        } else {
            try {
                saveChapterInstance()
            } catch (e: AVException) {
                cancel = true
                e.printStackTrace()
            }

        }

        return !cancel
    }

    @Throws(AVException::class)
    private fun saveChapterInstance(): AVObject {
        val chapterTable = mGame!!.getString(getString(R.string.info_table_chapter_table_name))
        val title = mChapterNameEdit!!.text.toString()
        val content = mChapterContentEdit!!.text.toString()
        val pic = Global.Bitmap2Bytes((mBackground as BitmapDrawable).bitmap)

        return saveChapter(chapterTable, title, content, pic, null, mNextChapters as List<AVObject>)
    }

    private val isInvalidateChapterContent: Boolean
        get() {
            val success = mChapterContentEdit!!.text.length != 0
            if (!success) {
                mChapterContentEdit!!.error = getString(R.string.error_invalid_chapter_content)
            }
            return success
        }

    private val isInvalidateChapterName: Boolean
        get() {
            val success = mChapterNameEdit!!.text.length != 0
            if (!success) {
                mChapterContentEdit!!.error = getString(R.string.error_invalid_chapter_name)
            }
            return success
        }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
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
            val startChapter = createStartChapterSilence(chapterTable)
            if (startChapter != null) {
                game.put(getString(R.string.info_table_start_chapter), startChapter)
            }
            game.saveInBackground(object : SaveCallback() {
                override fun done(e: AVException) {
                    Toast.makeText(applicationContext, R.string.success_create_game, Toast.LENGTH_SHORT)
                }
            })
            mGame = game
            val user = AVUser.getCurrentUser()
            user.put(getString(R.string.info_table_owner_game), game.objectId)
            user.put(getString(R.string.info_table_current_edit), startChapter)
            user.saveInBackground()
        } else {
            Toast.makeText(this, R.string.error_create_game_failed, Toast.LENGTH_LONG)
        }
    }

    private fun createStartChapterSilence(chapterTable: String): AVObject? {
        val chapter: AVObject
        try {
            chapter = saveChapter(chapterTable, getString(R.string.info_chapter_start_name), "", null,null)
        } catch (e: AVException) {
            e.printStackTrace()
            return null
        }

        return chapter
    }

    @Throws(AVException::class)
    private fun saveChapter(chapterTable: String, title: String, content: String, background: ByteArray?, callback: SaveCallback?, nextChapters: List<AVObject> = ArrayList()): AVObject {
        val chapter = AVObject(chapterTable)
        chapter.put(getString(R.string.info_table_chapter_name), title)
        chapter.put(getString(R.string.info_table_chapter_content), content)
        val relation = chapter.getRelation<AVObject>(getString(R.string.info_table_chapter_nexts))
        for (nextChapter in nextChapters) {
            relation.add(nextChapter)
        }
        chapter.saveInBackground(callback)
        saveBackgroundFile(background)
        return chapter
    }

    private fun saveBackgroundFile(background: ByteArray?) {
        if (background != null) {
            val thread = Thread(Runnable {
                val backgroundFile = AVFile(getString(R.string.info_table_chapter_background), background)
                try {
                    backgroundFile.save()
                } catch (e: AVException) {
                    e.printStackTrace()
                }
            })
            thread.start()
        }
    }

    internal inner class ChapterChooser {
        var chooses: MutableList<Int> = ArrayList()
        var allChapters: List<AVObject>? = null

        fun chooseSingle(): AVObject {
            val chapter_table_name = getString(R.string.info_game_prefix) + mGame!!.getString(getString(R.string.info_table_game_name))
            val query = AVQuery<AVObject>(chapter_table_name)
            try {
                allChapters = query.find()
            } catch (e: AVException) {
                Toast.makeText(this@CreateStoryActivity, getString(R.string.error_query_failed), Toast.LENGTH_LONG)
                e.printStackTrace()
            }

            val chooseSingleDialogFragment = ChooseChapterDialogFragment(
                    allChapters, object : ChooseChapterDialogFragment.ChooseSingleListener {
                override fun onItemClick(dialog: DialogInterface, which: Int) {
                    chooses[0] = which
                }

                override fun onPositiveClick(dialog: DialogInterface, which: Int) {

                }

                override fun onNegativeClick(dialog: DialogInterface, which: Int) {
                    dialog.cancel()
                }
            })
            chooseSingleDialogFragment.show(fragmentManager, "ChooseChapterDialogFragment")
            if (!chooses.isEmpty() && chooses[0] > 0) {
                return allChapters!![chooses[0] - 1]
            } else {
                return createEmptyChapter() as AVObject
            }
        }

        fun showChooseMultipleDialog() {
            val chapter_table_name = getString(R.string.info_game_prefix) + mGame!!.getString(getString(R.string.info_table_game_name))
            val query = AVQuery<AVObject>(chapter_table_name)
            try {
                allChapters = query.find()
            } catch (e: AVException) {
                Toast.makeText(this@CreateStoryActivity, getString(R.string.error_query_failed), Toast.LENGTH_LONG)
                e.printStackTrace()
                return
            }

            val chooseMultipleDialogFragment = ChooseChapterMultipleFragment(
                    allChapters, object : ChooseChapterMultipleFragment.ChooseMultipleListener {

                override fun onItemClick(dialog: DialogInterface, which: Int, isChecked: Boolean) {
                    if (isChecked) {
                        chooses.add(which)
                    } else if (chooses.contains(which)) {
                        chooses.remove(Integer.valueOf(which))
                    }
                }

                override fun onPositiveClick(dialog: DialogInterface, which: Int) {
                    val nexts = ArrayList<AVObject>()
                    for (point in chooses) {
                        nexts.add(allChapters!![point])
                    }
                    fillNextChaptersContainer(nexts)
                }

                override fun onNegativeClick(dialog: DialogInterface, which: Int) {
                    dialog.cancel()
                }
            })
            chooseMultipleDialogFragment.show(fragmentManager, "ChooseChapterDialogFragment")
        }
    }

    private fun createEmptyChapter(): AVObject? {
        try {
            return saveChapter(
                    mGame!!.getString(getString(R.string.info_table_chapter_table_name)),
                    getString(R.string.info_new_chapter),
                    "", null, null)
        } catch (e: AVException) {
            e.printStackTrace()
            Toast.makeText(this, R.string.error_create_empty_chapter_failed, Toast.LENGTH_SHORT)
            return null
        }

    }

    companion object {

        private val SAVE_CHAPTER_PIC = 1
        private val SET_CHAPTER_PIC = 2
    }
}
