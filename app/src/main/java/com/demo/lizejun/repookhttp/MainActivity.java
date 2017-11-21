package com.demo.lizejun.repookhttp;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String URL = "http://www.weather.com.cn/adat/sk/101010100.html";
    private static final int MSG_REQUEST = 0;
    private static final int MSG_UPDATE_UI = 0;

    private Button mBtRequest;
    private TextView mTvResult;
    private BackgroundHandler mBackgroundHandler;
    private MainHandler mMainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtRequest = (Button) findViewById(R.id.bt_request);
        mTvResult = (TextView) findViewById(R.id.tv_result);
        mBtRequest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //发送消息到异步线程，发起请求。
                mBackgroundHandler.sendEmptyMessage(MSG_REQUEST);
            }

        });
        HandlerThread backgroundThread = new HandlerThread("backgroundThread");
        backgroundThread.start();
        mBackgroundHandler = new BackgroundHandler(backgroundThread.getLooper());
        mMainHandler = new MainHandler();
    }

    private class BackgroundHandler extends Handler {

        BackgroundHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //在异步线程发起请求。
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(URL).build();
            Call call = client.newCall(request);
            try {
                Response response = call.execute();
                String result = response.body().string();
                //返回结果给主线程。
                Message message = mMainHandler.obtainMessage(MSG_UPDATE_UI, result);
                mMainHandler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
