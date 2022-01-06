package com.example.one.act.first;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.one.R;
import com.example.one.bean.MsgDataBean;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClient;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClientIOCallback;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IClientPool;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerManager;
import com.xuhao.didi.socket.common.interfaces.common_interfacies.server.IServerShutdown;
import com.xuhao.didi.socket.server.action.ServerActionAdapter;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;

public class SocketAct extends AppCompatActivity implements IClientIOCallback {
    private Button btStart;
    private Button btSample;
    private TextView textView;
    private int mPort = 8080;

    private TextView tvRece;

    private IServerManager mServerManager;

    private String stringRece;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        init();
        runFlow();
    }

    private void init(){
        btSample = (Button) findViewById(R.id.bt_Sample);
        textView = (TextView) findViewById(R.id.tv_Socket_ip);
        btStart = (Button) findViewById(R.id.bt_Socket_Start);
        tvRece = (TextView) findViewById(R.id.tv_rece);
        textView.setText("当前IP："+getIPAddress());

        mServerManager = OkSocket.server(mPort).registerReceiver(new ServerActionAdapter() {
            @Override
            public void onServerListening(int serverPort) {
                Log.i("ServerCallback", Thread.currentThread().getName() + " onServerListening,serverPort:" + serverPort);
                flushServerText();
            }

            @Override
            public void onClientConnected(IClient client, int serverPort, IClientPool clientPool) {
                Log.i("ServerCallback", Thread.currentThread().getName() + " onClientConnected,serverPort:" + serverPort + "--ClientNums:" + clientPool.size() + "--ClientTag:" + client.getUniqueTag());
                client.addIOCallback(SocketAct.this);
            }

            @Override
            public void onClientDisconnected(IClient client, int serverPort, IClientPool clientPool) {
                Log.i("ServerCallback", Thread.currentThread().getName() + " onClientDisconnected,serverPort:" + serverPort + "--ClientNums:" + clientPool.size() + "--ClientTag:" + client.getUniqueTag());
                client.removeIOCallback(SocketAct.this);
            }

            @Override
            public void onServerWillBeShutdown(int serverPort, IServerShutdown shutdown, IClientPool clientPool, Throwable throwable) {
                Log.i("ServerCallback", Thread.currentThread().getName() + " onServerWillBeShutdown,serverPort:" + serverPort + "--ClientNums:" + clientPool
                        .size());
                shutdown.shutdown();
            }

            @Override
            public void onServerAlreadyShutdown(int serverPort) {
                Log.i("ServerCallback", Thread.currentThread().getName() + " onServerAlreadyShutdown,serverPort:" + serverPort);
                flushServerText();
            }
        });
    }

    private void runFlow(){
        btSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SocketAct.this, SocketSampleAct.class);
                startActivity(intent);
            }
        });

        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mServerManager.isLive()) {
                    mServerManager.listen();
                } else {
                    mServerManager.shutdown();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        flushServerText();
        textView.setText("当前IP："+getIPAddress());
        tvRece.setText(stringRece);
    }

    private void flushServerText() {
        if (mServerManager.isLive()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    btStart.setText(mPort + "服务器关闭" + mPort + " Stop)");
                }
            });
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    btStart.setText(mPort + "服务器启动" + mPort + " Start)");
                }
            });
        }
    }



    @Override
    public void onClientRead(OriginalData originalData, IClient client, IClientPool<IClient, String> clientPool) {
        String str = new String(originalData.getBodyBytes(), Charset.forName("utf-8"));
        JsonObject jsonObject = null;
        try {
            jsonObject = new JsonParser().parse(str).getAsJsonObject();
            int cmd = jsonObject.get("cmd").getAsInt();
            if (cmd == 54) {//登陆成功
                String handshake = jsonObject.get("handshake").getAsString();
                Log.i("onClientIOServer", Thread.currentThread().getName() + " 接收到:" + client.getHostIp() + " 握手信息:" + handshake);
                stringRece = handshake;
            } else if (cmd == 14) {//心跳
                Log.i("onClientIOServer", Thread.currentThread().getName() + " 接收到:" + client.getHostIp() + " 收到心跳");
            } else {
                Log.i("onClientIOServer", Thread.currentThread().getName() + " 接收到:" + client.getHostIp() + " " + str);
                stringRece = str;
            }
        } catch (Exception e) {
            Log.i("onClientIOServer", Thread.currentThread().getName() + " 接收到:" + client.getHostIp() + " " + str);
            stringRece = str;
        }
        MsgDataBean msgDataBean = new MsgDataBean(str);
        clientPool.sendToAll(msgDataBean);
    }

    @Override
    public void onClientWrite(ISendable sendable, IClient client, IClientPool<IClient, String> clientPool) {
        byte[] bytes = sendable.parse();
        bytes = Arrays.copyOfRange(bytes, 4, bytes.length);
        String str = new String(bytes, Charset.forName("utf-8"));
        JsonObject jsonObject = null;
        try {
            jsonObject = new JsonParser().parse(str).getAsJsonObject();
            int cmd = jsonObject.get("cmd").getAsInt();
            switch (cmd) {
                case 54: {
                    String handshake = jsonObject.get("handshake").getAsString();
                    Log.i("onClientIOServer", Thread.currentThread().getName() + " 发送给:" + client.getHostIp() + " 握手数据:" + handshake);
                    break;
                }
                default:
                    Log.i("onClientIOServer", Thread.currentThread().getName() + " 发送给:" + client.getHostIp() + " " + str);
            }
        } catch (Exception e) {
            Log.i("onClientIOServer", Thread.currentThread().getName() + " 发送给:" + client.getHostIp() + " " + str);
        }
    }

    public String getIPAddress() {
        NetworkInfo info = ((ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                if (ipAddress == 0) return "未连接wifi";
                return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                        + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
            }
        } else {
            //当前无网络连接,请在设置中打开网络
            return "当前无网络连接,请在设置中打开网络";
        }
        return "IP获取失败";
    }
}