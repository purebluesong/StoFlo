package com.sprout.wi.stoflo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.*;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.Toast;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import com.avos.avoscloud.*;
import com.sprout.wi.stoflo.fragment.ChooseChapterDialogFragment;
import com.sprout.wi.stoflo.fragment.ChooseChapterMultipleFragment;
import com.sprout.wi.stoflo.fragment.CreateGameDialogFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * Created by purebluesong on 2016/6/24.
 */
public class CreateStoryActivity extends Activity implements CreateGameDialogFragment.CreateGameListener {

    private EditText mChapterNameEdit;
    private EditText mChapterContentEdit;
    private Button mChapterName;
    private Button mChapterContentBackground;
    private Button mChapterContentText;
    private Button mChaptersNext;
    private Button mSave;
    private Button mCancel;
    private Button mSaveNext;
    private HorizontalScrollView mNextChaptersContainer;
    private ChapterChooser mChapterChooser;
    private AVObject mChapter;
    private AVObject mGame;
    private List<AVObject> mNextChapters;
    private Drawable mBackground;

    private static final int SAVE_CHAPTER_PIC = 1;
    private static final int SET_CHAPTER_PIC = 2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SAVE_CHAPTER_PIC:
                    mBackground = new BitmapDrawable(getResources(), (Bitmap) msg.obj);
                    break;
                case SET_CHAPTER_PIC:
                    mChapterContentEdit.setBackground(mBackground);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);
        if (AVUser.getCurrentUser() == null) {
            jumpTo(LoginActivity.class);
        }
        mGame = AVUser.getCurrentUser().getAVObject(getString(R.string.info_key_self_game));
        if (mGame == null) {
            showCreateGameDialog();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGame!= null){
            initData();
            initView();
            fillContentWith(mChapter);
            registerOCL();
        }
    }

    private void initData() {
        AVUser user = AVUser.getCurrentUser();
        mBackground = null;
        startNewThread(new Runnable() {
            @Override
            public void run() {
                try {
                    mNextChapters = AVUser.getCurrentUser().getRelation(getString(R.string.info_table_chapter_nexts)).getQuery().find();
                } catch (AVException e) {
                    e.printStackTrace();
                }
            }
        });

        mChapter = user.getAVObject(getString(R.string.info_self_game_current_edit));
    }

    private void startNewThread(Runnable runnable){
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void fillContentWith(AVObject chapter) {
        String name = chapter.getString(getString(R.string.info_table_chapter_name));
        String content = chapter.getString(getString(R.string.info_table_chapter_content));
        String picID = chapter.getString(getString(R.string.info_table_chapter_background));


        AVFile.withObjectIdInBackground(picID, new GetFileCallback<AVFile>() {
            @Override
            public void done(AVFile avFile, AVException e) {
                if (e == null) {
                    Bitmap pic = null;
                    try {
                        byte[] data = avFile.getData();
                        if (data != null)
                            pic = BitmapFactory.decodeByteArray(data, 0, data.length);
                    } catch (AVException e1) {
                        e1.printStackTrace();
                    }
                    mHandler.sendMessage(mHandler.obtainMessage(SAVE_CHAPTER_PIC, pic));
                    mHandler.sendMessage(mHandler.obtainMessage(SET_CHAPTER_PIC));
                } else {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT);
                }
            }
        });

        mChapterNameEdit.setText(name);
        mChapterContentEdit.setText(content);
        fillNextChaptersContainer(mNextChapters);
    }

    private void fillNextChaptersContainer(List<AVObject> chapters) {
        for (AVObject nextChapters : chapters) {
            String title = nextChapters.getString(getString(R.string.info_table_chapter_name));
            addButtonToNextChaptersContainer(title);
        }
    }

    private void addButtonToNextChaptersContainer(String title) {
        Button button = new Button(this);
        button.setText(title);
        button.setBackgroundColor(Color.CYAN);
        button.getBackground().setAlpha(255);
        button.setSingleLine();
        button.setGravity(Gravity.CENTER);
        ViewGroup.LayoutParams layoutparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        button.setLayoutParams(layoutparams);
        button.setMinimumWidth(200);
        mNextChaptersContainer.addView(button);
    }

    private void showCreateGameDialog() {
        DialogFragment dialog = new CreateGameDialogFragment();
        dialog.show(getFragmentManager(), "CreateGameDialogFragment");
    }

    private void initView() {
        mChapterNameEdit = (EditText) findViewById(R.id.chapter_name_edit);
        mChapterContentEdit = (EditText) findViewById(R.id.chapter_content_edit);
        mChapterContentBackground = (Button) findViewById(R.id.edit_function_chapter_background);
        mChapterContentText = (Button) findViewById(R.id.chapter_content_text);
        mChaptersNext = (Button) findViewById(R.id.chapter_next_chooser);
        mChapterName = (Button) findViewById(R.id.chapter_name_button);
        mNextChaptersContainer = (HorizontalScrollView) findViewById(R.id.function_next_chapters_scroll);

        mSave = (Button) findViewById(R.id.create_save);
        mSaveNext = (Button) findViewById(R.id.create_save_next);
        mCancel = (Button) findViewById(R.id.create_cancel);

        mChapterChooser = new ChapterChooser();
    }

    private void registerOCL() {
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSave();
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        mSaveNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSave();
                clearPage();
                fillContentWith(mChapterChooser.chooseSingle());
            }
        });
        mChaptersNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChapterChooser.showChooseMultipleDialog();
            }
        });
        mChapterContentBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChapterBackground();
            }
        });
        mChapterContentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mChapterName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChapterChooser.chooseSingle();
            }
        });
    }

    private void setChapterBackground() {
        GalleryFinal.openGallerySingle(Global.REQUSET_CODE_GALLERY, new GalleryFinal.OnHanlderResultCallback() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                String path = resultList.get(0).getPhotoPath();
                mBackground = Drawable.createFromPath(path);
                mChapterContentEdit.setBackground(mBackground);
            }

            @Override
            public void onHanlderFailure(int requestCode, String errorMsg) {
                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG);
            }
        });
    }

    private void clearPage() {
        mChapterContentEdit.setText("");
        mChapterNameEdit.setText("");
    }

    private void cancel() {
        clearPage();
        jumpTo(StoFloActivity.class);
    }

    private void jumpTo(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass));
        finish();
    }

    private boolean attemptSave() {
        boolean cancel = false;
        View focusView = null;

        if (isInvalidateChapterName()) {
            focusView = mChapterNameEdit;
            cancel = true;
        }

        if (isInvalidateChapterContent()) {
            focusView = mChapterContentEdit;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            try {
                saveChapterInstance();
            } catch (AVException e) {
                cancel = true;
                e.printStackTrace();
            }
        }

        return !cancel;
    }

    private AVObject saveChapterInstance() throws AVException {
        String chapterTable = mGame.getString(getString(R.string.info_table_chapter_table_name));
        String title = mChapterNameEdit.getText().toString();
        String content = mChapterContentEdit.getText().toString();
        byte[] pic = Global.Bitmap2Bytes(((BitmapDrawable) mBackground).getBitmap());

        return saveChapter(chapterTable, title, content, pic, null, mNextChapters);
    }

    private boolean isInvalidateChapterContent() {
        boolean success = mChapterContentEdit.getText().length() != 0;
        if (!success) {
            mChapterContentEdit.setError(getString(R.string.error_invalid_chapter_content));
        }
        return success;
    }

    private boolean isInvalidateChapterName() {
        boolean success = mChapterNameEdit.getText().length() != 0;
        if (!success) {
            mChapterContentEdit.setError(getString(R.string.error_invalid_chapter_name));
        }
        return success;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText nameEdit = (EditText) ((CreateGameDialogFragment)dialog).findViewById(R.id.create_game_name);
        String gameName = nameEdit.getText().toString();
        String description = ((EditText) ((CreateGameDialogFragment)dialog).findViewById(R.id.create_game_description)).getText().toString();
        boolean cancel = false;

        if (gameName.length() == 0) {
            nameEdit.setError(getString(R.string.error_invalid_game_name));
            cancel = true;
        }

        if (!cancel) {
            AVObject game = new AVObject(getString(R.string.info_table_game));
            String chapterTable = getString(R.string.info_game_prefix) + gameName.hashCode();
            game.put(getString(R.string.info_table_game_name), gameName);
            game.put(getString(R.string.info_table_game_description), description);
            game.put(getString(R.string.info_table_chapter_table_name), chapterTable);
            AVObject startChapter = createStartChapterSilence(chapterTable);
            if (startChapter!=null){
                game.put(getString(R.string.info_table_start_chapter), startChapter);
            }
            game.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    Toast.makeText(getApplicationContext(), R.string.success_create_game, Toast.LENGTH_SHORT);
                }
            });
            mGame = game;
            AVUser user = AVUser.getCurrentUser();
            user.put(getString(R.string.info_table_owner_game), game.getObjectId());
            user.put(getString(R.string.info_table_current_edit), startChapter);
            user.saveInBackground();
        } else {
            Toast.makeText(this, R.string.error_create_game_failed, Toast.LENGTH_LONG);
        }
    }

    private AVObject createStartChapterSilence(String chapterTable) {
        AVObject chapter;
        try {
            chapter = saveChapter(chapterTable, getString(R.string.info_chapter_start_name), "", null, null);
        } catch (AVException e) {
            e.printStackTrace();
            return null;
        }
        return chapter;
    }

    private AVObject saveChapter(String chapterTable, String title, String content, byte[] background, SaveCallback callback) throws AVException {
        return saveChapter(chapterTable, title, content, background, callback, Collections.EMPTY_LIST);
    }

    private AVObject saveChapter(String chapterTable, String title, String content, byte[] background, SaveCallback callback, List<AVObject> nextChapters) throws AVException {
        AVObject chapter = new AVObject(chapterTable);
        chapter.put(getString(R.string.info_table_chapter_name), title);
        chapter.put(getString(R.string.info_table_chapter_content), content);
        AVRelation relation = chapter.getRelation(getString(R.string.info_table_chapter_nexts));
        for (AVObject nextchapter : nextChapters) {
            relation.add(nextchapter);
        }
        chapter.saveInBackground(callback);
        saveBackgroundFile(background);
        return chapter;
    }

    private void saveBackgroundFile(final byte[] background) {
        if (background!=null){
            Thread thread  = new Thread(new Runnable() {
                @Override
                public void run() {
                    AVFile backgroundFile = new AVFile(getString(R.string.info_table_chapter_background),background);
                    try {
                        backgroundFile.save();
                    } catch (AVException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    class ChapterChooser {
        List<Integer> chooses = new ArrayList<>();
        List<AVObject> allChapters = null;

        AVObject chooseSingle() {
            String chapter_table_name = getString(R.string.info_game_prefix) + mGame.getString(getString(R.string.info_table_game_name));
            AVQuery<AVObject> query = new AVQuery<>(chapter_table_name);
            try {
                allChapters = query.find();
            } catch (AVException e) {
                Toast.makeText(CreateStoryActivity.this, getString(R.string.error_query_failed), Toast.LENGTH_LONG);
                e.printStackTrace();
            }
            DialogFragment chooseSingleDialogFragment = new ChooseChapterDialogFragment(
                    allChapters, new ChooseChapterDialogFragment.ChooseSingleListener() {
                @Override
                public void onItemClick(DialogInterface dialog, int which) {
                    chooses.set(0, which);
                }

                @Override
                public void onPositiveClick(DialogInterface dialog, int which) {

                }

                @Override
                public void onNegativeClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            chooseSingleDialogFragment.show(getFragmentManager(), "ChooseChapterDialogFragment");
            if (!chooses.isEmpty() && chooses.get(0) > 0) {
                return allChapters.get(chooses.get(0) - 1);
            } else {
                return createEmptyChapter();
            }
        }

        public void showChooseMultipleDialog() {
            String chapter_table_name = getString(R.string.info_game_prefix) + mGame.getString(getString(R.string.info_table_game_name));
            AVQuery<AVObject> query = new AVQuery<>(chapter_table_name);
            try {
                allChapters = query.find();
            } catch (AVException e) {
                Toast.makeText(CreateStoryActivity.this, getString(R.string.error_query_failed), Toast.LENGTH_LONG);
                e.printStackTrace();
                return;
            }
            DialogFragment chooseMultipleDialogFragment = new ChooseChapterMultipleFragment(
                    allChapters, new ChooseChapterMultipleFragment.ChooseMultipleListener() {

                @Override
                public void onItemClick(DialogInterface dialog, int which, boolean isChecked) {
                    if (isChecked) {
                        chooses.add(which);
                    } else if (chooses.contains(which)){
                        chooses.remove(Integer.valueOf(which));
                    }
                }

                @Override
                public void onPositiveClick(DialogInterface dialog, int which) {
                    List<AVObject> nexts = new ArrayList<>();
                    for (Integer point :chooses) {
                        nexts.add(allChapters.get(point));
                    }
                    fillNextChaptersContainer(nexts);
                }

                @Override
                public void onNegativeClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            chooseMultipleDialogFragment.show(getFragmentManager(), "ChooseChapterDialogFragment");
        }
    }

    private AVObject createEmptyChapter() {
        try {
            return saveChapter(
                    mGame.getString(getString(R.string.info_table_chapter_table_name)),
                    getString(R.string.info_new_chapter),
                    "", null, null
            );
        } catch (AVException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.error_create_empty_chapter_failed, Toast.LENGTH_SHORT);
            return null;
        }
    }
}
