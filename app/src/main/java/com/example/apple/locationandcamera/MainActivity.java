package com.example.apple.locationandcamera;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Handler;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import android.hardware.Camera;


import junit.framework.Test;

import static android.location.LocationManager.GPS_PROVIDER;


public class MainActivity extends Activity implements SurfaceHolder.Callback,View.OnClickListener{
    private double latitude = 0.0;
    private double longitude = 0.0;
    private SensorManager sensorManager;
    private Vibrator vibrator;
    private LocationManager locationManager;
    static String provider1;
    static String provider2;
    private ImageView back, position;
    private SurfaceView surface;
    private ImageButton shutter;
    private SurfaceHolder holder;
    private Camera camera;
    private String filepath = "";
    private int cameraPosition = 1;
    Bitmap bitmap;
    ImageView galleryButton;
    Camera.Parameters params;
    int sensor = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        FragmentManager fragmentManager1 = getFragmentManager();
//        FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
//        photoFragment = new PhotoFragment();
//        fragmentTransaction1.replace(R.id.content,photoFragment).commit();
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        galleryButton = (ImageView) findViewById(R.id.to_list);
        galleryButton.setOnClickListener(this);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//拍照过程屏幕一直处于高亮
        //设置手机屏幕朝向，一共有7种
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        surface = (SurfaceView) findViewById(R.id.camera_surface);
        holder = surface.getHolder();//获得句柄
        holder.addCallback(this);//添加回调
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//surfaceview不维护自己的缓冲区，等待屏幕渲染引擎将内容推送到用户面前
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
    protected  void onResume(){
        super.onResume();
        if(sensorManager!= null){
            sensorManager.registerListener(sensorEventListener,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),sensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    protected  void onPause(){
        super.onPause();
        if(sensorManager != null){
            sensorManager.unregisterListener(sensorEventListener);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = null;
            values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];
            int check = 20;
            if((Math.abs(x) > check || Math.abs(y) > check || Math.abs(z) > check)&& (sensor == 0)){
                sensor++;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getPosition();
                                initiateCamera();
                            }
                        }, 1000);
                    }
                });
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
        public void getPosition(){
             locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
             provider1 = LocationManager.GPS_PROVIDER;
             if(locationManager.isProviderEnabled(provider1)) {
                  Location location = locationManager.getLastKnownLocation(provider1);
                  if (location != null) {
                     latitude = location.getLatitude();
                     longitude = location.getLongitude();
                  }
             }
             else{
                 provider2 = LocationManager.NETWORK_PROVIDER;
                 locationManager.requestLocationUpdates(provider2,1000,0,locationListener);
                 Location location1 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                 if (location1 != null) {
                     latitude = location1.getLatitude();
                     longitude = location1.getLongitude();
                 }
             }
        }

    public void initiateCamera(){
        if(camera != null) {
            vibrator.vibrate(200);
            Toast toast = new Toast(getApplicationContext());
            toast.makeText(getApplicationContext(), "Picture will be taken 3...2...", Toast.LENGTH_SHORT).show();
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    // TODO Auto-generated method stub
                    if (success) {
                        params = camera.getParameters();
                        params.setPictureFormat(PixelFormat.JPEG);
                        params.setPreviewSize(800, 480);
                        camera.setParameters(params);
                        camera.takePicture(null, null, jpeg);
                    }
                }
            });
        }

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        params = camera.getParameters();
        params.setPictureFormat(PixelFormat.JPEG);
        params.setPreviewSize(width, height);
        params.setPreviewFrameRate(5);
        params.setPictureSize(width, height);
        params.setJpegQuality(80);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        if(camera == null) {
            camera = Camera.open();
            try {
                camera.setPreviewDisplay(holder);
                camera.setDisplayOrientation(getPreviewDegree(MainActivity.this));
                camera.startPreview();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static int getPreviewDegree(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera.stopPreview();
        camera.release();
        camera = null;
        holder = null;
        surface = null;
    }


    Camera.PictureCallback jpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            try {
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                String filename = format.format(date) + ".jpg";
                String locationname = "location.txt";
                String picturename = "picture.txt";
                File fileFolder = new File(Environment.getExternalStorageDirectory()
                        + "/finger/");
                if (!fileFolder.exists()) {
                    fileFolder.mkdir();
                }
                File jpgFile = new File(fileFolder, filename);
                FileOutputStream outputStream = new FileOutputStream(jpgFile);
                outputStream.write(data);
                outputStream.close();
                DecimalFormat df = new DecimalFormat("0.000");
                method2((Environment.getExternalStorageDirectory()+"/finger/"+locationname+"/"),"Lat: "+df.format(new Double(latitude)).toString()+" Lon: "+df.format(new Double(longitude)).toString()+"\n");
                method2((Environment.getExternalStorageDirectory()+"/finger/"+picturename+"/"),filename+"\n");
                Toast toast1 = new Toast(getApplicationContext());
                toast1.makeText(getApplicationContext(),"Picture has been taken",Toast.LENGTH_SHORT).show();
                camera.startPreview();
                sensor = 0 ;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    public static void method2(String fileName, String content) {
        try {
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void onClick(View view){
        getFragmentManager().beginTransaction().add(android.R.id.content,new LocationFragment(),"location").commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
