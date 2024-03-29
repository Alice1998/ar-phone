package com.example.alice.theapp;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    private ImageView photo;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private static final int CODE_IP_SETTINGS=0xa3;
    private String ip;
    private String port;
    private File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/photo.jpg");
    private File fileCropUri = new File(Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg");
    private Uri imageUri;
    private Uri cropImageUri;
    private DialogInterface.OnClickListener click1 = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            Toast.makeText(MainActivity.this, "open picture mode", Toast.LENGTH_SHORT).show();
            requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new RequestPermissionCallBack() {
                @Override
                public void granted() {
                    PhotoUtils.openPic(MainActivity.this, CODE_GALLERY_REQUEST);
                }

                @Override
                public void denied() {
                    Toast.makeText(MainActivity.this, "部分权限获取失败，正常功能受到影响", Toast.LENGTH_LONG).show();
                }
            });
        }
    };
    private DialogInterface.OnClickListener click2 = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface arg0, int arg1) {
            // 阅读器
            Toast.makeText(MainActivity.this, "open reading mode", Toast.LENGTH_SHORT).show();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnTakePhoto = (Button) findViewById(R.id.take_photo);
        Button btnTakeGallery = (Button) findViewById(R.id.take_gallery);

        Button btnRead = (Button) findViewById(R.id.read);
        Button btnShop = (Button) findViewById(R.id.shop);
        Button btnIP = (Button) findViewById(R.id.forIP);

        photo = (ImageView) findViewById(R.id.photo);
        btnTakePhoto.setOnClickListener(this);
        btnTakeGallery.setOnClickListener(this);
        btnRead.setOnClickListener(this);
        btnShop.setOnClickListener(this);
        btnIP.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.take_photo:
                requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, new RequestPermissionCallBack() {
                    @Override
                    public void granted() {
                        if (hasSdcard()) {
                            imageUri = Uri.fromFile(fileUri);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                //通过FileProvider创建一个content类型的Uri
                                imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.alice.theapp.fileprovider", fileUri);
                            PhotoUtils.takePicture(MainActivity.this, imageUri, CODE_CAMERA_REQUEST);
                        } else {
                            Toast.makeText(MainActivity.this, "设备没有SD卡！", Toast.LENGTH_SHORT).show();
                            Log.e("asd", "设备没有SD卡");
                        }
                    }

                    @Override
                    public void denied() {
                        Toast.makeText(MainActivity.this, "部分权限获取失败，正常功能受到影响", Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case R.id.take_gallery:
                requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new RequestPermissionCallBack() {
                    @Override
                    public void granted() {
                        PhotoUtils.openPic(MainActivity.this, CODE_GALLERY_REQUEST);
                    }

                    @Override
                    public void denied() {
                        Toast.makeText(MainActivity.this, "部分权限获取失败，正常功能受到影响", Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case R.id.read:
                AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(this);
                alertdialogbuilder.setMessage("请选择模式");
                alertdialogbuilder.setPositiveButton("图片", click1);
                alertdialogbuilder.setNegativeButton("阅读器", click2);
                AlertDialog alertdialog1 = alertdialogbuilder.create();
                alertdialog1.show();
                break;
            case R.id.shop:
                intent.setClass(MainActivity.this, ShopListActivity.class);
                startActivity(intent);
                break;
            case R.id.forIP:
                // to be done
                intent.putExtra("mode", 1);
                intent.setClass(MainActivity.this, TCPclient.class);
                startActivityForResult(intent, CODE_IP_SETTINGS);
                break;
        }
    }

    public void skip(String input) {
        Intent intent = new Intent();
        intent.putExtra("uri", input);
        intent.putExtra("ip",ip);
        intent.putExtra("port",port);
        intent.setClass(MainActivity.this, FullScreenActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int output_X = 480, output_Y = 480;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_CAMERA_REQUEST://拍照完成回调
                    cropImageUri = Uri.fromFile(fileCropUri);
                    //PhotoUtils.cropImageUri(this, imageUri, cropImageUri, 1, 1, output_X, output_Y, CODE_RESULT_REQUEST);
                    skip(imageUri.toString());

                    break;
                case CODE_GALLERY_REQUEST://访问相册完成回调
                    if (hasSdcard()) {
                        cropImageUri = Uri.fromFile(fileCropUri);
                        Uri newUri = Uri.parse(PhotoUtils.getPath(this, data.getData()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            newUri = FileProvider.getUriForFile(this, "com.example.alice.theapp.fileprovider", new File(newUri.getPath()));
                        //PhotoUtils.cropImageUri(this, newUri, cropImageUri, 1, 1, output_X, output_Y, CODE_RESULT_REQUEST);
                        skip(newUri.toString());

                    } else {
                        Toast.makeText(MainActivity.this, "设备没有SD卡!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CODE_RESULT_REQUEST:
                    Bitmap bitmap = PhotoUtils.getBitmapFromUri(cropImageUri, this);
                    if (bitmap != null) {
                        showImages(bitmap);
                    }
                    break;
                case CODE_IP_SETTINGS:
                    ip=data.getStringExtra("ip");
                    port=data.getStringExtra("port");
                    Toast.makeText(MainActivity.this, ip, Toast.LENGTH_LONG).show();
                    break;

            }
        }
    }


    private void showImages(Bitmap bitmap) {
        photo.setImageBitmap(bitmap);
        // to be done
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

}
