package com.example.one.act.first;

import static android.widget.Toast.LENGTH_SHORT;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.example.one.R;
import com.example.one.bean.HandShakeBean;
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

import org.w3c.dom.Text;

import java.nio.charset.Charset;

public class SocketXAct extends AppCompatActivity {

    private ConnectionInfo mInfo;
    private IConnectionManager mManager;
    private OkSocketOptions mOkOptions;


    private Button mConnect;
    private Button mSendBtn;
    private EditText mSendET;
    private TextView tvSend,tvRice;

    private SocketActionAdapter adapter = new SocketActionAdapter() {
        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            mManager.send(new HandShakeBean());
            LogUtils.d("DisConnect");
//            mConnect.setText("DisConnect");
        }

        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            if (e != null) {
//                tvSend.setText("异常断开："+e.getMessage());
                LogUtils.d("异常断开");
            } else {
//                tvSend.setText("正常断开");
                LogUtils.d("正常断开");
            }
//            mConnect.setText("Connect");
            LogUtils.d("Connect");

        }

        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
//            tvSend.setText("连接失败");
//            mConnect.setText("Connect");
            LogUtils.d("连接失败");
        }

        @Override
        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
            String str = new String(data.getBodyBytes(), Charset.forName("utf-8"));
//            tvRice.setText(str);
            LogUtils.d(str);

        }

        @Override
        public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
            String str = new String(data.parse(), Charset.forName("utf-8"));
//            tvSend.setText(str);
            LogUtils.d(str);
        }

        @Override
        public void onPulseSend(ConnectionInfo info, IPulseSendable data) {
            String str = new String(data.parse(), Charset.forName("utf-8"));
//            tvSend.setText(str);
            LogUtils.d(str);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_xact);
        init();
        runFlow();
    }

    private void init(){
        mConnect = (Button) findViewById(R.id.bt_connectX);
        mSendBtn = (Button) findViewById(R.id.bt_sendX);
        mSendET = (EditText) findViewById(R.id.et_sendX);
        tvSend = (TextView) findViewById(R.id.tv_sendX);
        tvRice = (TextView) findViewById(R.id.tv_receX);
        initMan();
    }

    private void runFlow(){
        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d("开始连接");
                if (mManager == null) {
                    return;
                }
                if (!mManager.isConnect()) {
                    initMan();
                    mManager.connect();
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
    }

    private void initMan(){
        //不涉及view就不用开启handler
        final Handler handler = new Handler();
        mInfo = new ConnectionInfo("10.1.37.215",8080);
//        mOkOptions = new OkSocketOptions.Builder()
//                .setReconnectionManager(new NoneReconnect())
//                .setConnectTimeoutSecond(10)
//                .setCallbackThreadModeToken(new OkSocketOptions.ThreadModeToken() {
//                    @Override
//                    public void handleCallbackEvent(ActionDispatcher.ActionRunnable runnable) {
//                        handler.post(runnable);
//                    }
//                })
//                .build();
        mManager = OkSocket.open(mInfo);
        mManager.registerReceiver(adapter);
    }
}