package com.sprout.wi.stoflo.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import com.avos.avoscloud.AVObject;
import com.sprout.wi.stoflo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by purebluesong on 2016/7/2.
 */
public class ChooseChapterMultipleFragment extends DialogFragment {

    String[] items;
    public interface ChooseMultipleListener {
        void onItemClick(DialogInterface dialog, int which, boolean isChecked);
        void onPositiveClick(DialogInterface dialog, int which);
        void onNegativeClick(DialogInterface dialog, int which);
    }

    ChooseMultipleListener mCML;

    public ChooseChapterMultipleFragment(List<AVObject> chapters,ChooseMultipleListener cml) {
        List<String> options = new ArrayList<>();
        for (AVObject chapter: chapters) {
            options.add(chapter.getString(getString(R.string.info_table_chapter_name)));
        }
        mCML = cml;
        String[] tmp = {};
        items = options.toArray(tmp);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.info_choose_chapter)
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        mCML.onItemClick(dialog, which, isChecked);
                    }
                })
                .setPositiveButton(R.string.info_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCML.onPositiveClick(dialog, which);
                    }
                })
                .setNegativeButton(R.string.info_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCML.onNegativeClick(dialog, which);
                    }
                });
        return builder.create();
    }
}
