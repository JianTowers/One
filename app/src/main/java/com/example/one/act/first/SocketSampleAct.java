package com.example.one.act.first;

import static android.widget.Toast.LENGTH_SHORT;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.one.R;
import com.example.one.adapter.LogAdapter;
import com.example.one.bean.HandShakeBean;
import com.example.one.bean.LogBean;
import com.example.one.bean.MsgDataBean;
import com.xuhao.didi.core.iocore.interfaces.IPulseSendable;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.impl.client.action.ActionDispatcher;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;
import com.xuhao.didi.socket.client.sdk.client.connection.NoneReconnect;

import java.nio.charset.Charset;

public class SocketSampleAct extends AppCompatActivity {

    private ConnectionInfo mInfo;

    private Button mConnect;

    private EditText mIPET;
    private IConnectionManager mManager;
    private EditText mSendET;
    private OkSocketOptions mOkOptions;
    private Button mClearLog;
    private Button mSendBtn;

    private RecyclerView mSendList;
    private RecyclerView mReceList;

    private LogAdapter mSendLogAdapter = new LogAdapter();
    private LogAdapter mReceLogAdapter = new LogAdapter();

    private SocketActionAdapter adapter = new SocketActionAdapter() {
        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            mManager.send(new HandShakeBean());
            mConnect.setText("DisConnect");
            mIPET.setEnabled(false);
        }

        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            if (e != null) {
                logSend("异常断开(Disconnected with exception):" + e.getMessage());
            } else {
                logSend("正常断开(Disconnect Manually)");
            }
            mConnect.setText("Connect");
            mIPET.setEnabled(true);
        }

        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
            logSend("连接失败(Connecting Failed)");
            mConnect.setText("Connect");
            mIPET.setEnabled(true);
        }

        @Override
        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
            String str = new String(data.getBodyBytes(), Charset.forName("utf-8"));
            logRece(str);
        }

        @Override
        public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
            String str = new String(data.parse(), Charset.forName("utf-8"));
            logSend(str);
        }

        @Override
        public void onPulseSend(ConnectionInfo info, IPulseSendable data) {
            String str = new String(data.parse(), Charset.forName("utf-8"));
            logSend(str);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_sample);
        init();
        runFlow();
    }

    private void init() {
        mSendList = findViewById(R.id.rv_send);
        mReceList = findViewById(R.id.rv_rece);
        mIPET = findViewById(R.id.et_ip);
        mClearLog = findViewById(R.id.bt_clear);
        mConnect = findViewById(R.id.bt_connect);
        mSendET = findViewById(R.id.et_send);
        mSendBtn = findViewById(R.id.bt_send);

        LinearLayoutManager manager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mSendList.setLayoutManager(manager1);
        mSendList.setAdapter(mSendLogAdapter);

        LinearLayoutManager manager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReceList.setLayoutManager(manager2);
        mReceList.setAdapter(mReceLogAdapter);

        initManager();
    }

    private void initManager() {
        final Handler handler = new Handler();
        mInfo = new ConnectionInfo(mIPET.getText().toString(), Integer.parseInt("8080"));
        mOkOptions = new OkSocketOptions.Builder()
                .setReconnectionManager(new NoneReconnect())
                .setConnectTimeoutSecond(10)
                .setCallbackThreadModeToken(new OkSocketOptions.ThreadModeToken() {
                    @Override
                    public void handleCallbackEvent(ActionDispatcher.ActionRunnable runnable) {
                        handler.post(runnable);
                    }
                })
                .build();
        mManager = OkSocket.open(mInfo).option(mOkOptions);
        mManager.registerReceiver(adapter);
    }

    private void runFlow(){
        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mManager == null) {
                    return;
                }
                if (!mManager.isConnect()) {
                    initManager();
                    mManager.connect();
                    mIPET.setEnabled(false);
                } else {
                    mConnect.setText("Disconnecting");
                    mManager.disconnect();
                }
            }
        });
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mManager == null) {
                    return;
                }
                if (!mManager.isConnect()) {
                    Toast.makeText(getApplicationContext(), "Unconnected", LENGTH_SHORT).show();
                } else {
                    String msg = mSendET.getText().toString();
                    if (TextUtils.isEmpty(msg.trim())) {
                        return;
                    }
                    MsgDataBean msgDataBean = new MsgDataBean(msg);
                    mManager.send(msgDataBean);
                    mSendET.setText("");
                }
            }
        });
        mClearLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReceLogAdapter.getDataList().clear();
                mSendLogAdapter.getDataList().clear();
                mReceLogAdapter.notifyDataSetChanged();
                mSendLogAdapter.notifyDataSetChanged();
            }
        });
    }


    private void logSend(final String log) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            LogBean logBean = new LogBean(System.currentTimeMillis(), log);
            mSendLogAdapter.getDataList().add(0, logBean);
            mSendLogAdapter.notifyDataSetChanged();
        } else {
            final String threadName = Thread.currentThread().getName();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    logSend(threadName + " 线程打印(In Thread):" + log);
                }
            });
        }
    }

    private void logRece(final String log) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            LogBean logBean = new LogBean(System.currentTimeMillis(), log);
            mReceLogAdapter.getDataList().add(0, logBean);
            mReceLogAdapter.notifyDataSetChanged();
        } else {
            final String threadName = Thread.currentThread().getName();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    logRece(threadName + " 线程打印(In Thread):" + log);
                }
            });
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mManager != null) {
            mManager.disconnect();
            mManager.unRegisterReceiver(adapter);
        }
    }
}