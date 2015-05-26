package com.torv.httpdemo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

    private Button mBtnStart;
    private TextView mTvShowTextView;
    private ProgressBar mPbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnStart = (Button) findViewById(R.id.btn_start);
        mBtnStart.setOnClickListener(this);

        mTvShowTextView = (TextView) findViewById(R.id.tv_show);
        
        mPbLoading = (ProgressBar)findViewById(R.id.pb_load);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_start:

                mPbLoading.setVisibility(View.VISIBLE);
                startHttpSession();

                break;

            default:
                break;
        }
    }

    private void startHttpSession() {

        String url = "http://www.baidu.com";

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(url);
        } else {
            mTvShowTextView.setText("No network connection available.");
            mPbLoading.setVisibility(View.GONE);
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (Exception e) {
                e.printStackTrace();
                mPbLoading.setVisibility(View.GONE);
                return "fail";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            mTvShowTextView.setText(result);
            mPbLoading.setVisibility(View.GONE);
        }

    }

    public String downloadUrl(String urlString) throws IOException {

        InputStream is = null;
        int len = 500;

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();

            int response = conn.getResponseCode();
            Log.e("torv", "response code = " + response);

            is = conn.getInputStream();

            return readIt(is, len);
        } finally {
            if (is != null)
                is.close();
        }
    }

    private String readIt(InputStream is, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(is, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
