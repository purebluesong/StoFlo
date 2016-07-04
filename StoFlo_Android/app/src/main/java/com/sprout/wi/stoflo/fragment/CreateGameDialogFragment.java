package com.sprout.wi.stoflo.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.sprout.wi.stoflo.R;
import com.sprout.wi.stoflo.StoFloActivity;

/**
 * Created by purebluesong on 2016/6/30.
 */
public class CreateGameDialogFragment extends DialogFragment {

    public interface CreateGameListener {
        void onDialogPositiveClick(DialogFragment dialog);
    }

    private View mDefinedView;
    CreateGameListener mGameListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mGameListener = (CreateGameListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()+"must implement CreateGAmeListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        mDefinedView = inflater.inflate(R.layout.custom_create_game, null);
        builder.setView(mDefinedView)
                .setTitle(R.string.create_new_game)
                .setPositiveButton(R.string.info_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mGameListener.onDialogPositiveClick(CreateGameDialogFragment.this);
                        getDialog().dismiss();
                    }
                })
                .setNegativeButton(R.string.info_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getDialog().cancel();
                        jumpTo(StoFloActivity.class);
                    }
                });

        return builder.create();
    }

    private void jumpTo(Class<?> activityClass) {
        startActivity(new Intent(getActivity(),activityClass));
        getActivity().finish();
    }

    public View findViewById(int id) {
        return mDefinedView.findViewById(id);
    }

}
