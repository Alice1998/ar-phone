package com.example.alice.theapp;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClientOnly {
	Socket Socket = null;//Socket
	boolean RD = false;//用于控制读数据线程是否执行
	boolean linked=false;

	String iptoedit;
	int porttoedit;
	public String edittotext;
	public String datatoedit;

	OutputStream OutputStream = null;//定义数据输出流，用于发送数据
    InputStream InputStream = null;//定义数据输入流，用于接收数据

    ClientOnly()
    {

    }

    public void play()
    {
    	ThreadSendData t1 = new ThreadSendData();
        t1.start();
    }

    public void link()
    {
    	if(linked==false)
    	{
    		RD = true;
    		linked=true;
            //并创建一个新的线程，用于初始化socket
            Connect_Thread Connect_thread = new Connect_Thread();
            Connect_thread.start();
    	}
    	else
    	{
    		try{
                //取消socket
                Socket.close();
                //socket设置为空
                Socket = null;
                //读数据线程不执行
                RD = false;
                linked=false;
            }catch (Exception e){
                //如果想写的严谨一点，可以自行改动
                e.printStackTrace();
            }
    	}
    }

    //用线程创建Socket连接
    class Connect_Thread extends Thread{
        public void run(){
            //定义一个变量用于储存ip
            InetAddress ipAddress;
            try {
                //判断socket的状态，防止重复执行
                if (Socket == null) {
                    //如果socket为空则执行
                    //获取输入的IP地址
                    ipAddress = InetAddress.getByName(iptoedit);
                    //获取输入的端口
                    int port = porttoedit;
                    //新建一个socket
                    Socket = new Socket(ipAddress, port);
                    //获取socket的输入流和输出流
                    InputStream = Socket.getInputStream();
                    OutputStream = Socket.getOutputStream();

                    //新建一个线程读数据
                    ThreadReadData t1 = new ThreadReadData();
                    t1.start();
                }
            } catch (Exception e) {
                //如果有错误则在这里返回
                e.printStackTrace();
            }
        }
    }

    //用线程执行读取服务器发来的数据
    class ThreadReadData extends Thread{
        public void run() {
            //定义一个变量用于储存服务器发来的数据
            String textdata;
            //根据RD变量的值判断是否执行读数据
            while (RD) {
                try {
                    //定义一个字节集，存放输入的数据，缓存区大小为2048字节
                    final byte[] ReadBuffer = new byte[2048];
                    //用于存放数据量
                    final int ReadBufferLengh;

                    //从输入流获取服务器发来的数据和数据宽度
                    //ReadBuffer为参考变量，在这里会改变为数据
                    //输入流的返回值是服务器发来的数据宽度
                    ReadBufferLengh = InputStream.read(ReadBuffer);

                    //验证数据宽度，如果为-1则已经断开了连接
                    if (ReadBufferLengh == -1) {
                        //重新归位到初始状态
                        RD = false;
                        Socket.close();
                        Socket = null;
                    } else {
                        //如果有数据正常返回则进行处理显示

                        /*
                            这个地方有个很大的坑，让我搞了不少的时间
                            我用其他语言写的Web服务器程序，默认编码是gb2312
                            AS的默认编码是utf-8
                            在获取服务器发来的数据的时候，程序已经对这段gb2312的数据进行编码...
                            至于编码是什么就不知道了
                            我研究了很长时间，怎么转码也不对，越转越乱
                            最后测试出来是gb2312编码已经被转码了，我就先恢复gb2312编码
                            然后转成程序不会乱码的utf-8
                            如果目标服务器编码是utf8的话就不用转了
                        */

                        //先恢复成GB2312编码
                        textdata = new String(ReadBuffer,0,ReadBufferLengh,"GB2312");//原始编码数据
                        //转为UTF-8编码后显示在编辑框中
                        edittotext=new String(textdata.getBytes(),"UTF-8");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            
        }
    }//用线程发送数据
    class ThreadSendData extends Thread{
        public void run(){
            try {
                //用输出流发送数据
                OutputStream.write(datatoedit.getBytes());
                //发送数据之后会自动断开连接，所以，恢复为最初的状态
                //有个坑要说一下，因为发送完数据还得等待服务器返回，所以，不能把Socket也注销掉

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



}
