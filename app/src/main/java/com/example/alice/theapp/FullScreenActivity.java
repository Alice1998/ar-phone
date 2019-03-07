package com.example.alice.theapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FullScreenActivity extends AppCompatActivity {

    int timer1=0,timer2=0,timer3=0,timer4=0;
    Bitmap bitmap;
    DisplayMetrics dm2;
    float scaleMode1;
    ImageView pic;


    // for flexible toast show time
    public void showMyToast(final Toast toast, final int cnt) {
        final Timer timer =new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        },0,3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen);
        pic = (ImageView)findViewById(R.id.image);

        Button btn1=(Button)findViewById(R.id.Btn1);
        Button btn2=(Button)findViewById(R.id.Btn2);
        Button btn3=(Button)findViewById(R.id.Btn3);
        Button btn4=(Button)findViewById(R.id.Btn4);

        Intent intent=getIntent();
        String data=intent.getStringExtra("uri");
        Uri uri = Uri.parse((String) data);
        bitmap = PhotoUtils.getBitmapFromUri(uri,this);
        pic.setImageBitmap(bitmap);

        dm2 = getResources().getDisplayMetrics();
        System.out.println("width-display :" + dm2.widthPixels);
        System.out.println("heigth-display :" + dm2.heightPixels);

        scaleMode1=(float)1.5;

// 获得图片的宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(scaleMode1,scaleMode1);

// 得到新的图片

        final Bitmap newbm1 = Bitmap.createBitmap(bitmap, width-dm2.widthPixels, height-dm2.heightPixels, dm2.widthPixels, dm2.heightPixels);
        final Bitmap newbm2 = Bitmap.createBitmap(bitmap, width-dm2.widthPixels, height-dm2.heightPixels, dm2.widthPixels, dm2.heightPixels);
        final Bitmap newbm3 = Bitmap.createBitmap(bitmap, width-dm2.widthPixels, height-dm2.heightPixels, dm2.widthPixels, dm2.heightPixels);
        final Bitmap newbm4 = Bitmap.createBitmap(bitmap, width-dm2.widthPixels, height-dm2.heightPixels, dm2.widthPixels, dm2.heightPixels);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出Toast提示按钮被点击了
                timer1=1^timer1;
                if(timer1==1)
                {
                    Toast toast=Toast.makeText(FullScreenActivity.this,"Mode 1 on",Toast.LENGTH_LONG);
                    showMyToast(toast,500);
                    pic.setImageBitmap(newbm1);
                }
                else
                {
                    showMyToast(Toast.makeText(FullScreenActivity.this,"Mode 1 off",Toast.LENGTH_LONG),500);
                    pic.setImageBitmap(bitmap);
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer2=1^timer2;
                if(timer2==1)
                    showMyToast(Toast.makeText(FullScreenActivity.this,"Mode 2 on",Toast.LENGTH_LONG),500);
                else
                    showMyToast(Toast.makeText(FullScreenActivity.this,"Mode 2 off",Toast.LENGTH_LONG),500);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer3=1^timer3;
                if(timer3==1)
                    showMyToast(Toast.makeText(FullScreenActivity.this,"Mode 3 on",Toast.LENGTH_LONG),500);
                else
                    showMyToast(Toast.makeText(FullScreenActivity.this,"Mode 3 off",Toast.LENGTH_LONG),500);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer4=1^timer4;
                if(timer4==1)
                    showMyToast(Toast.makeText(FullScreenActivity.this,"Mode 4 on",Toast.LENGTH_LONG),500);
                else
                    showMyToast(Toast.makeText(FullScreenActivity.this,"Mode 4 off",Toast.LENGTH_LONG),500);
            }
        });
    }
}