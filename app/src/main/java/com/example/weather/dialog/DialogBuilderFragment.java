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

public class DialogBuilderFragment extends DialogFragment {

    OnFragmentDialogListener onFragmentDialogListener;
    private boolean visibleAddButton = true;

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

    public void setVisibleAddButton(boolean visibleAddButton) {
        this.visibleAddButton = visibleAddButton;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = null;
        if (visibleAddButton) {
            builder = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.title_dialog)
                    .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                            onFragmentDialogListener.onDialogResult(R.string.add);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    })
                    .setMessage(R.string.message_dialog);
        } else
        {
            builder = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.title_dialog)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                            onFragmentDialogListener.onDialogResult(R.string.cancel);
                        }
                    })
                    .setMessage(R.string.message_dialog_error_cnnection);
        }
        return builder.create();
    }

}
