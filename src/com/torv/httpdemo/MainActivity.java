package com.torv.httpdemo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

    private final static String tag = "MainActivity";

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

        mPbLoading = (ProgressBar) findViewById(R.id.pb_load);
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

            Log.e(tag, "connect");
            conn.connect();

            Log.e(tag, "getResponseCode");
            int response = conn.getResponseCode();
            Log.e(tag, "response code = " + response);

            Map<String, List<String>> headersMap = conn.getHeaderFields();
            Log.e(tag, "headers = " + headersMap.toString());
            handleHeadersMap(headersMap);

            is = conn.getInputStream();
            Log.e(tag, "getInputStream");

            return readIt(is, len);
        } finally {
            if (is != null)
                is.close();
        }
    }

    private void handleHeadersMap(Map<String, List<String>> headersMap) {
        if (null == headersMap || headersMap.isEmpty()) {
            return;
        }

        Set<String> keys = headersMap.keySet();
        Iterator<String> iter = keys.iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            if (TextUtils.isEmpty(key))
                continue;

            Log.e(tag, "key = " + key);
            List<String> valueList = headersMap.get(key);
            Log.e(tag, "--------------------------------------------------valueList");
            for (int i = 0; i < valueList.size(); i++) {
                String node = valueList.get(i);
                Log.e(tag, "index = "+i+", note = "+node);
            }
            Log.e(tag, "--------------------------------------------------");
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
