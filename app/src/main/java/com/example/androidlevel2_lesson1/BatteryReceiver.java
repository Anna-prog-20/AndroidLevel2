package com.example.androidlevel2_lesson1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentManager;

import com.example.androidlevel2_lesson1.dialog.MessageDialogFragment;

public class BatteryReceiver extends BroadcastReceiver {
    private MessageDialogFragment messageDialogFragment;
    private FragmentManager currentFragmentManager;

    public void setCurrentFragmentManager(FragmentManager currentFragmentManager) {
        this.currentFragmentManager = currentFragmentManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        messageDialogFragment = new MessageDialogFragment();
        messageDialogFragment.setTextMessage(R.string.text_message_battary);
        if (currentFragmentManager != null) {
            if (!currentFragmentManager.isDestroyed()) {
                messageDialogFragment.show(currentFragmentManager, "MessageFragment");
            }
        }
    }

}