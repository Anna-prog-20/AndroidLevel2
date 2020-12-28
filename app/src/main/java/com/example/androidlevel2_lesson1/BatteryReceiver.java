package com.example.androidlevel2_lesson1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import com.example.androidlevel2_lesson1.dialog.MessageDialogFragment;
import com.example.androidlevel2_lesson1.dialog.OnFragmentDialogListener;

import java.util.logging.LogRecord;

public class BatteryReceiver extends BroadcastReceiver {
    private MessageDialogFragment messageDialogFragment;
    private FragmentManager currentFragmentManager;

    public void setCurrentFragmentManager(FragmentManager currentFragmentManager) {
        this.currentFragmentManager = currentFragmentManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        messageDialogFragment = new MessageDialogFragment();
        messageDialogFragment.setTextMessage("Уровень зарада батареи очень низкий! Рекомендуем зарядить свой гаджет :) !");
        if (currentFragmentManager != null) {
            if (!currentFragmentManager.isDestroyed()) {
                messageDialogFragment.show(currentFragmentManager, "MessageFragment");
            }
        }
    }

}