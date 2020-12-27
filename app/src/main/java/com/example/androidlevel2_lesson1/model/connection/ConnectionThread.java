package com.example.androidlevel2_lesson1.model.connection;

import com.example.androidlevel2_lesson1.dialog.DialogBuilderFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ConnectionThread extends Thread{

    private URL uri;
    private DialogBuilderFragment dlgBuilder;

    private HttpsURLConnection urlConnection = null;
    private volatile boolean connect = false;
    private volatile BufferedReader obtainedData = null;

    public ConnectionThread(URL uri) {
        this.uri = uri;
        initDialog();
    }

    private void initDialog() {
        dlgBuilder = new DialogBuilderFragment();
        dlgBuilder.setVisibleAddButton(false);
    }

    public URL getUri() {
        return uri;
    }

    public void setUri(URL uri) {
        this.uri = uri;
    }

    @Override
    public void run() {
        try {
            urlConnection = (HttpsURLConnection) uri.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            getData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void getData() throws Exception {
        InputStream inputStream = urlConnection.getInputStream();
        if (inputStream != null) {
            obtainedData = new BufferedReader(new InputStreamReader(inputStream));
            connect = true;
        }
        else {
            connect = false;
        }
    }

    public synchronized void stopConnection() {
        if (null != urlConnection) {
            urlConnection.disconnect();
            try {
                obtainedData.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public synchronized boolean getConnect() {
        return connect;
    }

    public synchronized BufferedReader getObtainedData() {
        return obtainedData;
    }
}

