package com.example.weather.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.weather.R;

import java.util.List;

public class MessageDialogFragment extends DialogFragment {

    OnFragmentDialogListener onFragmentDialogListener;
    private int idRes = -1;
    private String textMessage = "";
    private boolean visibleOkButton = true;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        onFragmentDialogListener = searchFragment(getActivity().getSupportFragmentManager().getFragments());
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

    public void setTextMessage(int textMessage) {
        this.idRes = textMessage;
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
                    });
            if (idRes == -1) {
                builder.setMessage(textMessage);
            } else {
                builder.setMessage(idRes);
            }

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
                    });
            if (idRes == -1) {
                builder.setMessage(textMessage);
            } else {
                builder.setMessage(idRes);
            }
        }
        return builder.create();
    }

}
