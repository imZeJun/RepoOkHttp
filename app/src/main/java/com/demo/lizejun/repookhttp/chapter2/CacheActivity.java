package com.demo.lizejun.repookhttp.chapter2;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.demo.lizejun.repookhttp.R;
import com.demo.lizejun.repookhttp.chapter1.SimpleActivity;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CacheActivity extends AppCompatActivity {

    private static final String URL = "http://www.weather.com.cn/adat/sk/101010100.html";
    private static final long CACHE_SIZE = 1024 * 1024 * 20;
    private static String CACHE_DIRECTORY = Environment.getExternalStorageDirectory() + "/caches";
    private static final int MSG_UPDATE_UI = 0;

    private Button mBtRequest;
    private TextView mTvResult;
    private Cache mCache;
    private MainHandler mMainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache);
        mBtRequest = (Button) findViewById(R.id.bt_request);
        mTvResult = (TextView) findViewById(R.id.tv_result);
        mBtRequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startRequest();
            }
        });
        mCache = new Cache(new File(CACHE_DIRECTORY), CACHE_SIZE);
        mMainHandler = new MainHandler();
    }

    private void startRequest() {
        OkHttpClient client = new OkHttpClient.Builder().cache(mCache).build();
        Request request = new Request.Builder().url(URL).cacheControl(CacheControl.FORCE_CACHE).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("startRequest", "onFailure=" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.d("startRequest", "onResponse=" + result);
                Message message = mMainHandler.obtainMessage(MSG_UPDATE_UI, result);
                mMainHandler.sendMessage(message);
            }
        });
    }

    private class MainHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //主线程获取到结果之后进行更新。
            String result = (String) msg.obj;
            mTvResult.setText(result);
        }
    }
}
