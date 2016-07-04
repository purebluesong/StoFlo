package com.sprout.wi.stoflo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by purebluesong on 2016/6/24.
 */
public class GameActivity extends Activity{
    private static final int SET_BACKGROUND = 1;
    private static final int SET_NEXT_CHAPTERS = 2;
    private static final int INIT_ADD_CHAPTER = 3;

    private List<AVObject> mChapterList;
    private AVObject mGame;
    private AVObject mCurrentChapter;
    private Bitmap mBackground;
    private AVFile mBackgroundFile;
    private AVQuery<AVObject> mQuery;
    private List<AVObject> mCurrentNextChapters;
    private TextView mContentView;
    private LinearLayout mNextsView;
    private LinearLayout gameViewContainer;
    private ScrollView mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView = (ScrollView) getLayoutInflater().inflate(R.layout.activity_game,null);
        setContentView(mRootView);
        String gameID = getIntent().getStringExtra(getString(R.string.info_intent_game));
        String chapterID = getIntent().getStringExtra(getString(R.string.info_intent_chapter));
        iniData(gameID,chapterID);
        gameViewContainer = (LinearLayout) findViewById(R.id.game_view_container);
    }

    private void addChapterToView(AVObject chapter) {
        String content = chapter.getString(getString(R.string.info_table_chapter_content));
        mBackgroundFile = chapter.getAVFile(getString(R.string.info_table_chapter_background));
        mQuery = chapter.getRelation(getString(R.string.info_table_chapter_nexts)).getQuery();
        mCurrentNextChapters = null;
        mChapterList.add(chapter);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mBackgroundFile!= null){
                        mBackground = Global.Bytes2Bimap(mBackgroundFile.getData());
                    } else {
                        mBackground = null;
                    }
                    mCurrentNextChapters = mQuery.find();
                    mHandler.sendMessage(mHandler.obtainMessage(SET_BACKGROUND));
                    mHandler.sendMessage(mHandler.obtainMessage(SET_NEXT_CHAPTERS));
                } catch (AVException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        LayoutInflater inflater = getLayoutInflater();
        LinearLayout container = (LinearLayout) inflater.inflate(R.layout.chapter_show_asset,null);
        mContentView = (TextView) container.findViewById(R.id.chapter_content_show);
        mNextsView = (LinearLayout) container.findViewById(R.id.chapter_next_container);
        mContentView.setText(content);

        gameViewContainer.addView(container);
    }

    private void iniData(final String gameID, final String chapterID) {
        mChapterList = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mGame = AVObject.createWithoutData(getString(R.string.info_table_game), gameID);
                    mGame.fetch();
                    mCurrentChapter = AVObject.createWithoutData(mGame.getString(getString(R.string.info_table_chapter_table_name)) ,chapterID);
                    mCurrentChapter.fetch();
                    mHandler.sendMessage(mHandler.obtainMessage(INIT_ADD_CHAPTER));
                } catch (AVException e) {
                    Toast.makeText(getApplicationContext(),getString(R.string.error_fetch_game_failed),Toast.LENGTH_LONG);
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void setNexts() {
        for (AVObject chapter:mCurrentNextChapters) {
            addNextChapterButtonTo(chapter, mNextsView);
        }
        mNextsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setClickable(false);
            }
        });
    }

    private Button addNextChapterButtonTo(final AVObject chapter, LinearLayout buttonContainer) {
        Button button = new Button(this);
        button.setText(chapter.getString(getString(R.string.info_table_chapter_name)));
        button.setGravity(Gravity.CENTER);
        button.setSingleLine();
        button.setBackgroundColor(Color.CYAN);
        button.getBackground().setAlpha(0);
        button.setTextColor(Color.WHITE);
        button.setShadowLayer(10,0,4,Color.WHITE);
        button.setLeft(20);
        button.setRight(20);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addChapterToView(chapter);
                scrollToBottom();
            }
        });
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(params);

        buttonContainer.addView(button);
        return button;
    }

    private void scrollToBottom() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mRootView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SET_BACKGROUND:
                    if (mBackground != null){
                        mContentView.setBackgroundDrawable(new BitmapDrawable(getResources(),mBackground));
                    }
                    scrollToBottom();
                    break;
                case SET_NEXT_CHAPTERS:
                    setNexts();
                    scrollToBottom();
                    break;
                case INIT_ADD_CHAPTER:
                    addChapterToView(mCurrentChapter);
                    break;
            }
        }
    };

}
