package com.example.androidlevel2_lesson1.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.androidlevel2_lesson1.R;

import java.util.List;

public class MessageDialogFragment extends DialogFragment {

    OnFragmentDialogListener onFragmentDialogListener;
    private String textMessage = "";
    private boolean visibleOkButton = true;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        onFragmentDialogListener = searchFragment(getActivity().getSupportFragmentManager().getFragments());
        Log.d("TAG", "onFragmentDialogListener " + onFragmentDialogListener);
    }

    private OnFragmentDialogListener searchFragment(List<Fragment> fragments) {
        for (Fragment fragment : fragments) {
            if (fragment instanceof OnFragmentDialogListener) {
                return (OnFragmentDialogListener) fragment;
            }
        }
        return null;
    }

    public void setVisibleOkButton(boolean visibleOkButton) {
        this.visibleOkButton = visibleOkButton;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = null;
        if (visibleOkButton) {
            builder = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.title_message)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    })
                    .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                            onFragmentDialogListener.onDialogResult(R.string.exit);
                        }
                    })
                    .setMessage(textMessage);
        } else
        {
            builder = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.title_dialog)
                    .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                            onFragmentDialogListener.onDialogResult(R.string.exit);
                        }
                    })
                    .setMessage(textMessage);
        }
        return builder.create();
    }

}
