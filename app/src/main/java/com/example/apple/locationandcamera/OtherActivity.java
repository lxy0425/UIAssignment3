package com.example.apple.locationandcamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by apple on 15-01-11.
 */
public class OtherActivity extends Activity {
    Bitmap bitmap;
    ImageView imageView;
    String picturename;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo);
        imageView = (ImageView)findViewById(R.id.picture);
        Intent intent1 = this.getIntent();
        picturename = intent1.getStringExtra("picturename");
//        try {
//            FileInputStream fis = new FileInputStream(Environment.getExternalStorageDirectory()
//                    + "/finger/"+picturename+"/");
            Bitmap bitmapBefore = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/finger/"+picturename+"/");
            Matrix m = new Matrix();
            m.setRotate(90);
            bitmap = Bitmap.createBitmap(bitmapBefore, 0, 0, bitmapBefore.getWidth(), bitmapBefore.getHeight(), m, true);
//            bitmap = BitmapFactory.decodeStream(fis);
//        }
//        catch (FileNotFoundException e){
//
//        }
        imageView.setImageBitmap(bitmap);
    }
}
