package com.sprout.wi.stoflo.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import com.avos.avoscloud.AVObject;
import com.sprout.wi.stoflo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by purebluesong on 2016/7/2.
 */
public class ChooseChapterDialogFragment extends DialogFragment{
    String[] items;

    public interface ChooseSingleListener{
        public void onItemClick(DialogInterface dialog, int which);
        public void onPositiveClick(DialogInterface dialog, int which);
        public void onNegativeClick(DialogInterface dialog, int which);
    }

    ChooseSingleListener mCSL;

    public ChooseChapterDialogFragment(List<AVObject> chapters,ChooseSingleListener csl) {
        List<String> options = new ArrayList<>();
        options.add(getString(R.string.info_new_chapter));
        for (AVObject chapter: chapters) {
            options.add(chapter.getString(getString(R.string.info_table_chapter_name)));
        }
        mCSL = csl;
        String[] tmp = {};
        items = options.toArray(tmp);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.info_choose_chapter)
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCSL.onItemClick(dialog, which);
                    }
                }).setPositiveButton(R.string.info_confirm, new DialogInterface.OnClickListener() {
                   @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCSL.onPositiveClick(dialog, which);
                    }
                }).setNegativeButton(R.string.info_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCSL.onNegativeClick(dialog, which);
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
