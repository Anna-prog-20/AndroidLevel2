package com.example.androidlevel2_lesson1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import com.example.androidlevel2_lesson1.dialog.MessageDialogFragment;

public class NetworkReceiver extends BroadcastReceiver {
    private MessageDialogFragment messageDialogFragment;
    private FragmentManager currentFragmentManager;

    public void setCurrentFragmentManager(FragmentManager currentFragmentManager) {
        this.currentFragmentManager = currentFragmentManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        messageDialogFragment = new MessageDialogFragment();
        messageDialogFragment.setVisibleOkButton(false);
        messageDialogFragment.setTextMessage("Отсутствует связь! Приложение закроется!");
        if (currentFragmentManager != null) {
            if (!currentFragmentManager.isDestroyed()) {
                messageDialogFragment.show(currentFragmentManager, "MessageFragment");
            }
        }
    }
}