package com.example.alice.theapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class FullScreenActivity extends AppCompatActivity{

    int timer1 = 0, timer2 = 0, timer3 = 0, timer4 = 0,timer5=0,timer6=0;
    Bitmap bitmap;
    DisplayMetrics dm2;
    float scaleMode1;
    //ImageView pic;
    ImageView pic;
    String ip;
    String port;

    ClientOnly sendBySocket;
    int mode;
    String picName;

    private static final int TOP = 0x15;
    private static final int LEFT = 0x16;
    private static final int BOTTOM = 0x17;
    private static final int RIGHT = 0x18;
    private static final int LEFT_TOP = 0x11;
    private static final int RIGHT_TOP = 0x12;
    private static final int LEFT_BOTTOM = 0x13;
    private static final int RIGHT_BOTTOM = 0x14;
    private static final int TOUCH_TWO = 0x21;
    private static final int CENTER = 0x19;

    protected int screenWidth;
    protected int screenHeight;
    protected int lastX;
    protected int lastY;
    private int oriLeft;
    private int oriRight;
    private int oriTop;
    private int oriBottom;
    private int dragDirection;
    private static final int touchDistance = 80; //触摸边界的有效距离
    private float oriDis = 1f;
    float singleScale=1;
    int flagforScale=0;

    public static byte shortToByte(short number,int index) {
        int temp = number;
        byte[] b = new byte[2]; // 将最低位保存在最低位
        b[0] = (byte)(temp & 0xff);
        temp = temp >> 8; // 向右移8位
        b[1] = (byte)(temp & 0xff);
        return b[index];
    }

    private DialogInterface.OnClickListener click1 = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            Toast.makeText(FullScreenActivity.this, "message sent", Toast.LENGTH_SHORT).show();
            //sendBySocket.picinfo = "close".getBytes();
            sendBySocket.picinfo=new byte[2];
            sendBySocket.picinfo[0]=5;
            sendBySocket.picinfo[1]=100;
            sendBySocket.play();
            finish();
        }
    };
    private DialogInterface.OnClickListener click2 = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            // keep
            //sendBySocket.picinfo = "keep".getBytes();
            sendBySocket.picinfo=new byte[2];
            sendBySocket.picinfo[0]=6;
            sendBySocket.picinfo[1]=100;
            sendBySocket.play();
            finish();
        }
    };

    // for flexible toast show time
    public void showMyToast(final Toast toast, final int cnt) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt);
    }


    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen);
        pic = (ImageView) findViewById(R.id.image);
        // ***
        mode=0;

        Button btn1 = (Button) findViewById(R.id.Btn1);
        Button btn2 = (Button) findViewById(R.id.Btn2);
        Button btn3 = (Button) findViewById(R.id.Btn3);
        Button btn4 = (Button) findViewById(R.id.Btn4);
        Button enlarge=(Button) findViewById(R.id.Btn5);
        Button cursor=(Button) findViewById(R.id.Btn6);

        Intent intent = getIntent();
        String data = intent.getStringExtra("uri");
        Uri uri = Uri.parse((String) data);
        picName=data;

        bitmap = PhotoUtils.getBitmapFromUri(uri, this);
        pic.setImageBitmap(bitmap);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

        ip=intent.getStringExtra("ip");
        intent.getStringExtra("port");
        if(ip == null || ip.length() == 0 || port == null || port.length() == 0 )
            sendBySocket = new ClientOnly("192.168.1.49", 2000);
        else
            sendBySocket=new ClientOnly(ip,Integer.valueOf(port));

        byte[] pics=baos.toByteArray();
        sendBySocket.picinfo=new byte[35+pics.length];
        java.util.Arrays.fill(sendBySocket.picinfo, (byte) 0);
        sendBySocket.picinfo[0]=0;

        System.arraycopy(picName.getBytes(),0,sendBySocket.picinfo,1,picName.getBytes().length);

        String[] aa = picName.split("\\.");
        byte[] test=aa[aa.length-1].getBytes();
        System.arraycopy(test,0,sendBySocket.picinfo,21,test.length);

        System.out.println(picName);
        System.out.println(aa[aa.length-1]);

        // 获得图片的宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        sendBySocket.picinfo[26]=shortToByte((short)width,0);
        sendBySocket.picinfo[27]=shortToByte((short)width,1);
        sendBySocket.picinfo[28]=shortToByte((short)height,0);
        sendBySocket.picinfo[29]=shortToByte((short)height,1);

        byte[] sizes=ByteBuffer.allocate(4).putInt(pics.length).array();
        for(int i=0;i<4;i++)
            sendBySocket.picinfo[30+i]=sizes[i];
        System.arraycopy(pics,0,sendBySocket.picinfo,34,pics.length);

        sendBySocket.picinfo[34+pics.length]=100;
        sendBySocket.play();
        Toast.makeText(FullScreenActivity.this, "picture sent~", Toast.LENGTH_SHORT).show();


        dm2 = getResources().getDisplayMetrics();
        System.out.println("width :" + width);
        System.out.println("heigth :" + height);
        System.out.println("width-dm2 :" + dm2.widthPixels);
        System.out.println("heigth-dm2 :" + dm2.heightPixels);

        scaleMode1 = (float) 1.5;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleMode1, scaleMode1);

        int startx = (width - dm2.widthPixels > 0) ? width - dm2.widthPixels : 0;
        int starty = (height - dm2.heightPixels > 0) ? height - dm2.heightPixels : 0;
        int allwidth = dm2.widthPixels, allheight = dm2.heightPixels;
        if (startx == 0)
            allwidth = width;
        if (starty == 0)
            allheight = height;

        System.out.println("width :" + startx);
        System.out.println("heigth :" + starty);
        System.out.println("width-dm2 :" + allwidth);
        System.out.println("heigth-dm2 :" + allheight);

// 得到新的图片

        final Bitmap newbm1 = Bitmap.createBitmap(bitmap, startx, starty, allwidth, allheight);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出Toast提示按钮被点击了
                timer1 = 1 ^ timer1;
                sendBySocket.picinfo=new byte[3];
                sendBySocket.picinfo[0]=1;
                sendBySocket.picinfo[2]=100;
                if (timer1 == 1) {
                    Toast toast = Toast.makeText(FullScreenActivity.this, "Mode 1 on", Toast.LENGTH_LONG);
                    showMyToast(toast, 500);
                    pic.setImageBitmap(newbm1);
                    sendBySocket.picinfo[1]=0;
                    sendBySocket.play();
                } else {
                    showMyToast(Toast.makeText(FullScreenActivity.this, "Mode 1 off", Toast.LENGTH_LONG), 500);
                    pic.setImageBitmap(bitmap);
                    sendBySocket.picinfo[1]=1;
                    sendBySocket.play();
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer2 = 1 ^ timer2;
                sendBySocket.picinfo=new byte[3];
                sendBySocket.picinfo[0]=1;
                sendBySocket.picinfo[2]=100;
                if (timer2 == 1) {
                    showMyToast(Toast.makeText(FullScreenActivity.this, "Mode 2 on", Toast.LENGTH_LONG), 500);
                    sendBySocket.picinfo[1]=2;
                    sendBySocket.play();
                } else {
                    showMyToast(Toast.makeText(FullScreenActivity.this, "Mode 2 off", Toast.LENGTH_LONG), 500);
                    sendBySocket.picinfo[1]=3;
                    sendBySocket.play();
                }

            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer3 = 1 ^ timer3;
                sendBySocket.picinfo=new byte[3];
                sendBySocket.picinfo[0]=1;
                sendBySocket.picinfo[2]=100;
                if (timer3 == 1) {
                    showMyToast(Toast.makeText(FullScreenActivity.this, "Mode 3 on", Toast.LENGTH_LONG), 500);
                    sendBySocket.picinfo[1]=4;
                    sendBySocket.play();
                } else {
                    showMyToast(Toast.makeText(FullScreenActivity.this, "Mode 3 off", Toast.LENGTH_LONG), 500);
                    sendBySocket.picinfo[1]=5;
                    sendBySocket.play();
                }
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer4 = 1 ^ timer4;
                sendBySocket.picinfo=new byte[3];
                sendBySocket.picinfo[0]=1;
                sendBySocket.picinfo[2]=100;
                if (timer4 == 1) {
                    showMyToast(Toast.makeText(FullScreenActivity.this, "Mode 4 on", Toast.LENGTH_LONG), 500);
                    sendBySocket.picinfo[1]=6;
                    sendBySocket.play();
                } else {
                    showMyToast(Toast.makeText(FullScreenActivity.this, "Mode 4 off", Toast.LENGTH_LONG), 500);
                    sendBySocket.picinfo[1]=7;
                    sendBySocket.play();
                }
            }
        });

        enlarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer5 = 1 ^ timer5;
                singleScale=1;
                flagforScale=0;
                if (timer5 == 1) {
                    showMyToast(Toast.makeText(FullScreenActivity.this, "enlarge mode on", Toast.LENGTH_LONG), 500);
                } else {
                    showMyToast(Toast.makeText(FullScreenActivity.this, "enlarge off", Toast.LENGTH_LONG), 500);

                }
            }
        });

        cursor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer6 = 1 ^ timer6;
                if (timer6 == 1) {
                    showMyToast(Toast.makeText(FullScreenActivity.this, "cursor on", Toast.LENGTH_LONG), 500);

                } else {
                    showMyToast(Toast.makeText(FullScreenActivity.this, "cursor off", Toast.LENGTH_LONG), 500);
                }
            }
        });
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            //返回事件监听
            AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(this);
            alertdialogbuilder.setMessage("是否要关闭AR中的窗口？");
            alertdialogbuilder.setPositiveButton("关闭", click1);
            alertdialogbuilder.setNegativeButton("保留", click2);
            AlertDialog alertdialog1 = alertdialogbuilder.create();
            alertdialog1.show();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        //System.out.println("*** "+event.getAction()+action);
        if (action == MotionEvent.ACTION_DOWN)
        {
            oriLeft = pic.getLeft();
            oriRight = pic.getRight();
            oriTop = pic.getTop();
            oriBottom = pic.getBottom();
            lastY = (int) event.getRawY();
            lastX = (int) event.getRawX();
            dragDirection = getDirection(pic, (int) event.getX(),
                    (int) event.getY());
            if(timer5==1)
            {
                if(flagforScale==0)
                {
                    flagforScale=1;
                    String tosent="enlarge "+lastX+" "+lastY;
                    //sendBySocket.picinfo = tosent.getBytes();
                    sendBySocket.picinfo=new byte[6];
                    sendBySocket.picinfo[0]=2;
                    sendBySocket.picinfo[1]=shortToByte((short)lastX,0);
                    sendBySocket.picinfo[2]=shortToByte((short)lastX,1);
                    sendBySocket.picinfo[3]=shortToByte((short)lastY,0);
                    sendBySocket.picinfo[4]=shortToByte((short)lastY,1);
                    sendBySocket.picinfo[5]=100;
                    sendBySocket.play();
                    System.out.println(tosent);
                }
                singleScale=1;
            }

        }
        if (action == MotionEvent.ACTION_POINTER_DOWN)
        {
            oriLeft = pic.getLeft();
            oriRight = pic.getRight();
            oriTop = pic.getTop();
            oriBottom = pic.getBottom();
            lastY = (int) event.getRawY();
            lastX = (int) event.getRawX();
            dragDirection = TOUCH_TWO;
            oriDis = distance(event);
        }

        switch (action)
        {
            case MotionEvent.ACTION_MOVE:
                switch (dragDirection)
                {

                    case LEFT: // 左边缘
                    case RIGHT: // 右边缘
                    case BOTTOM: // 下边缘
                    case TOP: // 上边缘
                    case CENTER: // 点击中心-->>移动
                    case LEFT_BOTTOM: // 左下
                    case LEFT_TOP: // 左上
                    case RIGHT_BOTTOM: // 右下
                    case RIGHT_TOP: // 右上
                        if(timer6==1)
                        {
                            String sent="cursor "+lastX+" "+lastY;
                            sendBySocket.picinfo = sent.getBytes();
                            sendBySocket.picinfo=new byte[6];
                            sendBySocket.picinfo[0]=4;
                            sendBySocket.picinfo[1]= shortToByte((short)lastX,0);
                            sendBySocket.picinfo[2]= shortToByte((short)lastX,1);
                            sendBySocket.picinfo[1]= shortToByte((short)lastY,0);
                            sendBySocket.picinfo[2]= shortToByte((short)lastY,1);
                            sendBySocket.picinfo[5]=100;
                            System.out.println(sendBySocket.picinfo);
                            sendBySocket.play();
                        }
                        break;

                    case TOUCH_TWO: //双指操控
                        if(timer5==1)
                        {
                            float newDist = distance(event);
                            float scale = newDist / oriDis;
                            //控制双指缩放的敏感度
                            //int distX = (int) (scale * (oriRight - oriLeft) - (oriRight - oriLeft)) / 50;
                            //int distY = (int) (scale * (oriBottom - oriTop) - (oriBottom - oriTop)) / 50;
                            if (newDist > 10f)
                            {//当双指的距离大于10时，开始相应处理
                                singleScale=scale;

                            }
                        }
                        break;

                }
                if (dragDirection != CENTER)
                {
                    //pic.layout(oriLeft, oriTop, oriRight, oriBottom);
                }
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                dragDirection = 0;
                break;
        }
        if(action==MotionEvent.ACTION_UP)
        {
            if(timer5==1&&singleScale!=1)
            {
                System.out.println(singleScale);
                String sent="enlargescale "+singleScale;
                //sendBySocket.picinfo = sent.getBytes();
                sendBySocket.picinfo=new byte[10];
                sendBySocket.picinfo[0]=3;
                byte []temp=ByteBuffer.allocate(8).putDouble((double)singleScale).array();
                for(int i=0;i<8;i++)
                    sendBySocket.picinfo[i+1]=temp[i];
                sendBySocket.picinfo[9]=100;
                sendBySocket.play();
            }
        }
        return super.onTouchEvent(event);
    }

        protected int getDirection(View v, int x, int y) {
            int left = v.getLeft();
            int right = v.getRight();
            int bottom = v.getBottom();
            int top = v.getTop();
            if (x < touchDistance && y < touchDistance) {
                return LEFT_TOP;
            }
            if (y < touchDistance && right - left - x < touchDistance) {
                return RIGHT_TOP;
            }
            if (x < touchDistance && bottom - top - y < touchDistance) {
                return LEFT_BOTTOM;
            }
            if (right - left - x < touchDistance && bottom - top - y < touchDistance) {
                return RIGHT_BOTTOM;
            }
            if (x < touchDistance) {
                return LEFT;
            }
            if (y < touchDistance) {
                return TOP;
            }
            if (right - left - x < touchDistance) {
                return RIGHT;
            }
            if (bottom - top - y < touchDistance) {
                return BOTTOM;
            }
            return CENTER;
        }

    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);//两点间距离公式
    }
}
