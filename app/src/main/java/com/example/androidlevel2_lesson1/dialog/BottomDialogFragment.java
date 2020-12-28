package com.example.androidlevel2_lesson1.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.androidlevel2_lesson1.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomDialogFragment  extends BottomSheetDialogFragment {

    private EditText editText;
    private OnFragmentDialogListener onFragmentDialogListener;

    public static BottomDialogFragment newInstance() {
        return new BottomDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog, container, false);
        setCancelable(false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initButton(R.id.btnAdd);
        initButton(R.id.btnCancel);
        editText = getView().findViewById(R.id.entered_text);
    }

    private void initButton(final int id) {
        View view = getView().findViewById(id);
        view.findViewById(id);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFragmentDialogListener != null) {
                    onFragmentDialogListener.onDialogResult(id);
                }
                dismiss();
            }
        });
    }

    public void setOnFragmentDialogListener(OnFragmentDialogListener onFragmentDialogListener) {
        this.onFragmentDialogListener = onFragmentDialogListener;
    }

    public String getText() {
        return editText.getText().toString();
    }
}
