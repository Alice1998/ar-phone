package com.example.alice.theapp;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TCPclient extends AppCompatActivity {

    EditText iptoedit;//ip编辑框对象
    EditText porttoedit;//端口编辑框对象
    EditText datatoedit;//数据编辑框对象
    Button Button;//连接服务器按钮对象
    EditText edittotext;//接收的数据显示编辑框对象
    ClientOnly sendmessage;
    boolean buttontitle = true;//定义一个逻辑变量，用于判断连接服务器按钮状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcpclient);

        iptoedit = (EditText)findViewById(R.id.IPEdit);//获取ip地址编辑框对象
        porttoedit = (EditText)findViewById(R.id.portEdit);//获取端口编辑框对象
        datatoedit = (EditText)findViewById(R.id.dataEdit);//获取欲发送的数据编辑框对象
        Button = (Button)findViewById(R.id.Button);//获取连接服务器按钮对象
        edittotext = (EditText)findViewById(R.id.editText);//获取接收数据显示编辑框对象
        sendmessage=new ClientOnly();
    }

    //发送数据按钮按下
    public void play(View view){
        //验证编辑框用户输入内容是否合法
        if (thisif()) {
            sendmessage.play();
        } else{
            //这个地方默认是没有反应，可以自行修改成信息框提示
            return;
        }
    }

    //连接服务器按钮按下
    public void link(View view){
        //判断按钮状态
        if (buttontitle == true){
            //如果按钮没有被按下，则按钮状态改为按下
            buttontitle = false;

        }else{
            //如果按钮已经被按下，则改变按钮标题
            Button.setText("连 接 服 务 器");
            //储存状态的变量反转
            buttontitle = true;
        }
        sendmessage.link();
    }


    //验证编辑框内容是否合法
    public boolean thisif (){
        sendmessage.datatoedit=datatoedit.getText().toString();
        //定义一个信息框留作备用
        AlertDialog.Builder message = new AlertDialog.Builder(this);
        message.setPositiveButton("确定",click1);

        //分别获取ip、端口、数据这三项的内容
        String ip = iptoedit.getText().toString();
        String port = porttoedit.getText().toString();
        String data = datatoedit.getText().toString();

        //判断是否有编辑框为空
        if (ip == null || ip.length() == 0 || port == null || port.length() == 0 || data == null || data.length() == 0){
            //如果有空则弹出提示
            message.setMessage("各数据不能为空！");
            AlertDialog m1 = message.create();
            m1.show();
            return false;
        }else{
            return true;
        }
    }

    //信息框按钮按下事件
    public DialogInterface.OnClickListener click1 = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    };

}
