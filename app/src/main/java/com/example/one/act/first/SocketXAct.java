package com.example.one.act.first;

import static android.widget.Toast.LENGTH_SHORT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.example.one.R;
import com.example.one.bean.DevicesBean;
import com.example.one.bean.HandShakeBean;
import com.example.one.bean.MsgDataBean;
import com.example.one.bean.WebBean;
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

    private Button btTest;

    private Handler handler;

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
        btTest = (Button) findViewById(R.id.bt_socket_test);
        initMan();
        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 0){
                    SystemClock.sleep(200);
                    initMan();
                    MsgDataBean msgDataBean = new MsgDataBean(msg.arg1+"");
                    mManager.send(msgDataBean);
                    LogUtils.d(msg.arg1);
                }
            }
        };
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
//                    WebBean webBean = new WebBean("scrv","open","#333","#333","时空隧道");
//                    mManager.send(webBean);
                    DevicesBean devicesBean = new DevicesBean("用户画像","close");
                    mManager.send(devicesBean);
                    mSendET.setText("");
                }
            }
        });

        btTest.setOnClickListener(v -> {
            for (int i = 0;i<20;i++){
                Message message = new Message();
                message.what =0;
                message.arg1 =i;
                handler.sendMessage(message);
            }
        });
    }

    private void initMan(){
        //不涉及view就不用开启handler
        mInfo = new ConnectionInfo("10.0.2.15",8080);
        mManager = OkSocket.open(mInfo);
    }
}