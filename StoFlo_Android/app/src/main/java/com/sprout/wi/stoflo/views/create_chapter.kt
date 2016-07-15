package com.sprout.wi.stoflo.views

import android.annotation.TargetApi
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.*
import cn.finalteam.galleryfinal.GalleryFinal
import cn.finalteam.galleryfinal.model.PhotoInfo
import com.avos.avoscloud.*
import com.sprout.wi.stoflo.Activity.CreateStoryActivity
import com.sprout.wi.stoflo.Activity.createInter
import com.sprout.wi.stoflo.Global
import com.sprout.wi.stoflo.R
import org.jetbrains.anko.toast
import java.util.*

/**
 * Created by sprout on 16-7-13
 */
class CreateChapter(createStoryActivity: CreateStoryActivity) :createInter {
    private val context: CreateStoryActivity = createStoryActivity
    private val rootView :LinearLayout = context.layoutInflater.inflate(R.layout.create_chapter) as LinearLayout

    private val mSave: Button = rootView.findViewById(R.id.create_save) as Button
    private val mReturn: Button = rootView.findViewById(R.id.create_chapter_return) as Button
    private val mSaveNext: Button = rootView.findViewById(R.id.create_save_next) as Button
    private val mChapterTitle: Button = rootView.findViewById(R.id.edit_chapter_title) as Button
    private val mChapterAction: Button = rootView.findViewById(R.id.edit_chapter_action) as Button
    private val mChapterListView: LinearLayout = rootView.findViewById(R.id.edit_chapters_container) as LinearLayout
    private val mChapterContentText: Button = rootView.findViewById(R.id.edit_chapter_content_text) as Button
    private val mChapterContentEdit: EditText = rootView.findViewById(R.id.edit_chapter_content) as EditText
    private val mChapterContentBackground: Button = rootView.findViewById(R.id.edit_function_chapter_background) as Button

    private var mChapter: AVObject? = null
    private var mBackground: Drawable? = null
    private var chapterTableName = ""
    private var chapterTableQuery: AVQuery<AVObject>? = null
    private var chapterList = ArrayList<AVObject>()

    internal val handler: Handler = object : Handler(){
        override fun handleMessage(msg: Message?) {
            (msg?.obj as Runnable).run()
            super.handleMessage(msg)
        }
    }

    private fun getString(resID: Int): String? {
        return context.getString(resID)
    }

    private fun findViewById(resID: Int): View? {
        return context.findViewById(resID)
    }

    override fun iniData() {
        if (context.mGame == null) {
            context.reLoadView()
        } else {
            val name = (context.mGame as AVObject).getString(getString(R.string.info_table_game_chapter_table_name))
            if (name == null){
                chapterTableName = getString(R.string.info_game_prefix) + context.mGame?.objectId
                mChapter = newChapter()
                chapterTableQuery = AVQuery<AVObject>(chapterTableName)
                context.mGame!!.put(getString(R.string.info_table_game_start_chapter), mChapter)
                context.mGame!!.saveInBackground()
            } else {
                chapterTableName = name
                chapterTableQuery = AVQuery<AVObject>(chapterTableName)
                mChapter = chapterTableQuery!!.first
            }
        }
    }


    override fun iniView() {
        fillChapterList()
//        edit(mChapter as AVObject)
        registerOCL()
    }

    private fun fillChapterList() {
        Thread(Runnable {
            chapterList = chapterTableQuery?.find() as ArrayList<AVObject>
            handler.sendMessage(handler.obtainMessage(0, Runnable {
                for (chapter in chapterList) {
                    val chapterView = context.layoutInflater?.inflate(R.layout.edit_chapter_shortcut_template)as TextView
                    val title = chapter.getString(getString(R.string.info_table_chapter_title))
                    if (title != null) {
                        chapterView.text = title
                    } else {
                        chapterView.text = chapter.getString(getString(R.string.info_table_chapter_content))
                    }
                    chapterView.setOnClickListener { view -> edit(chapterList[mChapterListView.indexOfChild(view)]) }
                    mChapterListView.addView(chapterView)
                }
            }))
        }).start()
    }

    private fun edit(chapter: AVObject) {
        mChapter = chapter
        val content = chapter.getString(getString(R.string.info_table_chapter_content))
        mChapterContentEdit.setText(content)
        Thread(Runnable {
            val background = (mChapter as AVObject).getAVFile<AVFile>(getString(R.string.info_table_chapter_background)).data
            mBackground = Global.bitmap2Drawable(Global.Bytes2Bimap(background) as Bitmap, context.resources as Resources)
            handler.sendMessage(handler.obtainMessage(0, Runnable {
                mChapterContentEdit.background = mBackground
            }))
        }).start()
    }

    override fun getRootView(): View {
        return rootView
    }

    override fun haveFlag(): Boolean {
        return context.mGame != null && !(context.isActionEdit)
    }

    override fun onCreate() {
    }

    override fun onStop() {
    }

    override fun onDestory() {
    }

    override fun onStart() {
    }

    private fun registerOCL() {
        mSave.setOnClickListener { attemptSave() }
        mReturn.setOnClickListener { returnListPage() }
        mSaveNext.setOnClickListener {
            attemptSave()
            val chapter  = newChapter()
            if (chapter!= null){
                edit(chapter)
            }
        }
        mChapterContentBackground.setOnClickListener { setChapterBackground() }
        mChapterContentText.setOnClickListener { }
        mChapterAction.setOnClickListener {
            context.isActionEdit = true
            context.reLoadView()
        }
        mChapterTitle.setOnClickListener {  }
    }

    private fun newChapter(): AVObject? {
        try {
            return saveChapter(chapterTableName, getString(R.string.info_new_chapter) as String, "")
        } catch (e: AVException) {
            e.printStackTrace()
            context.toast(R.string.error_create_empty_chapter_failed)
        }
        return null
    }

    private fun setChapterBackground() {
        GalleryFinal.openGallerySingle(Global.REQUSET_CODE_GALLERY, object : GalleryFinal.OnHanlderResultCallback {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onHanlderSuccess(reqeustCode: Int, resultList: List<PhotoInfo>) {
                val path = resultList[0].photoPath
                mBackground = Drawable.createFromPath(path)
                mChapterContentEdit.background = mBackground
            }

            override fun onHanlderFailure(requestCode: Int, errorMsg: String) {
                context.toast(errorMsg)
            }
        })
    }

    private fun returnListPage() {
        context.mGame = null
        context.reLoadView()
    }

    private fun attemptSave(): Boolean {
        var cancel = false
        var focusView: View? = null

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
        val content = mChapterContentEdit.text.toString()
        val pic = Global.Bitmap2Bytes((mBackground as BitmapDrawable).bitmap)
        return saveChapter(chapterTableName, "", content, pic)
    }

    private val isInvalidateChapterContent: Boolean
        get() {
            val success = mChapterContentEdit.text.length != 0
            if (!success) {
                mChapterContentEdit.error = getString(R.string.error_invalid_chapter_content)
            }
            return success
        }

    @Throws(AVException::class)
    private fun saveChapter(chapterTable: String, title: String, content: String, background: ByteArray? = null, callback: SaveCallback? = null, nextChapters: List<AVObject> = ArrayList()): AVObject {
        val chapter = AVObject(chapterTable)
        chapter.put(getString(R.string.info_table_chapter_title), title)
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
            Thread(Runnable {
                val backgroundFile = AVFile(getString(R.string.info_table_chapter_background), background)
                try {
                    backgroundFile.save()
                } catch (e: AVException) {
                    e.printStackTrace()
                }
            }).start()
        }
    }

//    internal inner class ChapterChooser {
//        var chooses: MutableList<Int> = ArrayList()
//        var allChapters: List<AVObject>? = null
//
//        fun chooseSingle(): AVObject {
//            val chapter_table_name = getString(R.string.info_game_prefix) + mGame!!.getString(getString(R.string.info_table_game_name))
//            val query = AVQuery<AVObject>(chapter_table_name)
//            try {
//                allChapters = query.find()
//            } catch (e: AVException) {
//                Toast.makeText(this@CreateStoryActivity, getString(R.string.error_query_failed), Toast.LENGTH_LONG)
//                e.printStackTrace()
//            }
//
//            val chooseSingleDialogFragment = ChooseChapterDialogFragment(
//                    allChapters, object : ChooseChapterDialogFragment.ChooseSingleListener {
//                override fun onItemClick(dialog: DialogInterface, which: Int) {
//                    chooses[0] = which
//                }
//
//                override fun onPositiveClick(dialog: DialogInterface, which: Int) {
//
//                }
//
//                override fun onNegativeClick(dialog: DialogInterface, which: Int) {
//                    dialog.cancel()
//                }
//            })
//            chooseSingleDialogFragment.show(fragmentManager, "ChooseChapterDialogFragment")
//            if (!chooses.isEmpty() && chooses[0] > 0) {
//                return allChapters!![chooses[0] - 1]
//            } else {
//                return createEmptyChapter() as AVObject
//            }
//        }
//
//        fun showChooseMultipleDialog() {
//            val chapter_table_name = getString(R.string.info_game_prefix) + mGame!!.getString(getString(R.string.info_table_game_name))
//            val query = AVQuery<AVObject>(chapter_table_name)
//            try {
//                allChapters = query.find()
//            } catch (e: AVException) {
//                Toast.makeText(this@CreateStoryActivity, getString(R.string.error_query_failed), Toast.LENGTH_LONG)
//                e.printStackTrace()
//                return
//            }
//
//            val chooseMultipleDialogFragment = ChooseChapterMultipleFragment(
//                    allChapters, object : ChooseChapterMultipleFragment.ChooseMultipleListener {
//
//                override fun onItemClick(dialog: DialogInterface, which: Int, isChecked: Boolean) {
//                    if (isChecked) {
//                        chooses.add(which)
//                    } else if (chooses.contains(which)) {
//                        chooses.remove(Integer.valueOf(which))
//                    }
//                }
//
//                override fun onPositiveClick(dialog: DialogInterface, which: Int) {
//                    val nexts = ArrayList<AVObject>()
//                    for (point in chooses) {
//                        nexts.add(allChapters!![point])
//                    }
//                    fillNextChaptersContainer(nexts)
//                }
//
//                override fun onNegativeClick(dialog: DialogInterface, which: Int) {
//                    dialog.cancel()
//                }
//            })
//            chooseMultipleDialogFragment.show(fragmentManager, "ChooseChapterDialogFragment")
//        }
//    }

}