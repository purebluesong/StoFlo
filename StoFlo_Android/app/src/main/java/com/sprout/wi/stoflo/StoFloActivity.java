package com.sprout.wi.stoflo;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;
import cn.finalteam.galleryfinal.*;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.sprout.wi.stoflo.component.GlideImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by purebluesong on 2016/6/21.
 */
public class StoFloActivity extends Activity {

    private static final int FILL_VIEW_DATA = 1;
    private EditText mUsernameView;
    private Button mCreateNewGameButton;
    private Button mContinueLastGameButton;
    private Button mLogoutButton;
    private ListView mGameListView;
    private List<AVObject> mGameLsit;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FILL_VIEW_DATA:
                    fillViewWithData();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stoflo);

        initView();
        initComponent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    private void initData() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                AVQuery<AVObject> query = new AVQuery<>(getString(R.string.info_table_game));
                try {
                    mGameLsit = query.find();
                } catch (AVException e) {
                    Toast.makeText(getApplicationContext(),getString(R.string.error_require_gamelist_failed),Toast.LENGTH_LONG);
                    e.printStackTrace();
                }
                mHandler.sendMessage(mHandler.obtainMessage(FILL_VIEW_DATA));
            }
        });
        thread.start();
    }

    private void fillViewWithData() {
        List<String> gameNames = new ArrayList<>();
        for (AVObject game: mGameLsit) {
            gameNames.add(game.getString(getString(R.string.info_table_game_name)));
        }
        mGameListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1 ,gameNames));

    }

    private void initComponent() {

        //galleryImage
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(true)
                .setEnableEdit(true)
                .setEnableCrop(true)
                .setEnableRotate(true)
                .setCropSquare(true)
                .setEnablePreview(true)
                .build();
        ImageLoader imageLoader = new GlideImageLoader();
        CoreConfig coreConfig = new CoreConfig.Builder(this, imageLoader, ThemeConfig.CYAN)
                .setFunctionConfig(functionConfig)
                .build();
        GalleryFinal.init(coreConfig);
    }

    private void initView() {
        mUsernameView = (EditText) findViewById(R.id.status_username);
        mUsernameView.setText(AVUser.getCurrentUser().getUsername());
        mCreateNewGameButton = (Button) findViewById(R.id.create_new_game);
        mContinueLastGameButton = (Button) findViewById(R.id.continue_last_button);
        mLogoutButton = (Button) findViewById(R.id.logout_button);
        mGameListView = (ListView) findViewById(R.id.GAME_LIST_VIEW);

        mCreateNewGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToCreatePage();
            }
        });
        mContinueLastGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                jumpToGamePage(0,0);
            }
        });
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        mGameListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mGameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startGame(mGameLsit.get(position));
            }
        });
    }

    private void startGame(AVObject game) {
        jumpToGamePage(game.getObjectId(),game.getAVObject(getString(R.string.info_table_start_chapter)).getObjectId());
    }

    private void logout() {
        AVUser.logOut();
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }

    private void jumpToCreatePage(){
        startActivity(new Intent(this,CreateStoryActivity.class));
    }

    private void jumpToGamePage(String game, String chapter) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(getString(R.string.info_intent_game), game);
        intent.putExtra(getString(R.string.info_intent_chapter), chapter);
        startActivity(intent);
    }
}
