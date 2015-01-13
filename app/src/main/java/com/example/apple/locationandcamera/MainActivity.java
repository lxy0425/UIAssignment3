package com.example.apple.locationandcamera;

import android.app.Activity;
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
    TextView lat;
    TextView lon;
    static String provider1;
    static String provider2;
    private ImageView back, position;//返回和切换前后置摄像头
    private SurfaceView surface;
    private ImageButton shutter;//快门
    private SurfaceHolder holder;
    private Camera camera;//声明相机
    private String filepath = "";//照片保存路径
    private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头
    Bitmap bitmap;
    Button button;
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
        lat = (TextView)findViewById(R.id.lat);
        lon = (TextView)findViewById(R.id.lon);
        button = (Button) findViewById(R.id.to_list);
        button.setOnClickListener(this);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//拍照过程屏幕一直处于高亮
        //设置手机屏幕朝向，一共有7种
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        surface = (SurfaceView) findViewById(R.id.camera_surface);
        holder = surface.getHolder();//获得句柄
        holder.addCallback(this);//添加回调
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//surfaceview不维护自己的缓冲区，等待屏幕渲染引擎将内容推送到用户面前
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                // Image captured and saved to fileUri specified in the Intent
//                Toast.makeText(this, "Image saved to:\n" +
//                        data.getData(), Toast.LENGTH_LONG).show();
//            } else if (resultCode == RESULT_CANCELED) {
//                // User cancelled the image capture
//            } else {
//                // Image capture failed, advise user
//            }
//        }
//    }
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
//        if (camera != null) {
//            camera.release();
//        }
        super.onDestroy();
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];
            int check = 20;
            if((Math.abs(x) > check || Math.abs(y) > check || Math.abs(z) > check)&& (sensor == 0)){
                sensor++;
                vibrator.vibrate(200);
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
                     lat.setText(new Double(latitude).toString());
                     lon.setText(new Double(longitude).toString());
                  }
             }
             else{
                 provider2 = LocationManager.NETWORK_PROVIDER;
                 locationManager.requestLocationUpdates(provider2,1000,0,locationListener);
                 Location location1 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                 if (location1 != null) {
                     latitude = location1.getLatitude();
                     longitude = location1.getLongitude();
                     lat.setText(new Double(latitude).toString());
                     lon.setText(new Double(longitude).toString());
                 }
             }
        }

    public void initiateCamera(){
        camera.autoFocus(new Camera.AutoFocusCallback() {//自动对焦
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                // TODO Auto-generated method stub
                if(success) {
                    //设置参数，并拍照
                    params = camera.getParameters();
                    params.setPictureFormat(PixelFormat.JPEG);//图片格式
                    params.setPreviewSize(800, 480);//图片大小
                    camera.setParameters(params);//将参数设置到我的camera
                    camera.takePicture(null, null, jpeg);//将拍摄到的照片给自定义的对象
                }
            }
        });
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        params = camera.getParameters(); // 获取各项参数
        params.setPictureFormat(PixelFormat.JPEG); // 设置图片格式
        params.setPreviewSize(width, height); // 设置预览大小
        params.setPreviewFrameRate(5);  //设置每秒显示4帧
        params.setPictureSize(width, height); // 设置保存的图片尺寸
        params.setJpegQuality(80); // 设置照片质量
    }

//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//        // TODO Auto-generated method stub
//        //当surfaceview创建时开启相机
//        if(camera == null) {
//            camera = Camera.open();
//            try {
//                camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
//                camera.setDisplayOrientation(getPreviewDegree(MainActivity.this));
//                camera.startPreview();//开始预览
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        //当surfaceview创建时开启相机
        if(camera == null) {
            camera = Camera.open();
            try {
                camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                camera.setDisplayOrientation(getPreviewDegree(MainActivity.this));
                camera.startPreview();//开始预览
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static int getPreviewDegree(Activity activity) {
        // 获得手机的方向
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degree = 0;
        // 根据手机的方向计算相机预览画面应该选择的角度
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
        //当surfaceview关闭时，关闭预览并释放资源
        camera.stopPreview();
        camera.release();
        camera = null;
        holder = null;
        surface = null;
    }

    //创建jpeg图片回调数据对象
    Camera.PictureCallback jpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            try {

//                Bitmap bitmap = byte2Bitmap();
//                // 根据拍摄的方向旋转图像（纵向拍摄时要需要将图像选择90度)
//                Matrix matrix = new Matrix();
//                matrix.setRotate(getPreviewDegree(MainActivity.this));
//                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
//                bitmap.getHeight(), matrix, true);
////                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                //自定义文件保存路径  以拍摄时间区分命名
//                String filename = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".jpg";
//                File fileFolder = new File(Environment.getExternalStorageDirectory()
//                        + "/finger/");
//                if (!fileFolder.exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
//                    fileFolder.mkdir();
//                }
//                File file = new File(fileFolder,filename);
//                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩的流里面
//                bos.flush();// 刷新此缓冲区的输出流
//                bos.close();// 关闭此输出流并释放与此流有关的所有系统资源
//                camera.stopPreview();//关闭预览 处理数据
//                camera.startPreview();//数据处理完后继续开始预览
//                bitmap.recycle();//回收bitmap空间
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
                String filename = format.format(date) + ".jpg";
                String locationname = "location.txt";
                String picturename = "picture.txt";
                File fileFolder = new File(Environment.getExternalStorageDirectory()
                        + "/finger/");
                if (!fileFolder.exists()) { // 如果目录不存在，则创建一个名为"finger"的目录
                    fileFolder.mkdir();
                }
                File jpgFile = new File(fileFolder, filename);
                FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流
                outputStream.write(data); // 写入sd卡中
                outputStream.close(); // 关闭输出流
                method2((Environment.getExternalStorageDirectory()+"/finger/"+locationname+"/"),"Latitude: "+(new Double(latitude).toString()+" Longitude: "+new Double(longitude).toString()+"\n"));
                method2((Environment.getExternalStorageDirectory()+"/finger/"+picturename+"/"),filename+"\n");
//                OutputStreamWriter out = new OutputStreamWriter(openFileOutput((Environment.getExternalStorageDirectory()+"/finger/"+locationname+"/"),MODE_APPEND));
//                out.write(new Double(latitude).toString()+" "+new Double(longitude).toString()+"\n");
//                out.close();
//                OutputStreamWriter out1 = new OutputStreamWriter(openFileOutput((Environment.getExternalStorageDirectory()+"/finger/"+picturename+"/"),MODE_APPEND));
//                out1.write(filename+"\n");
//                out1.close();
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
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//
//    public static void saveFile(String toSaveString, String filePath){
//        try{
//            File saveFile = new File(filePath);
//            if (!saveFile.exists())
//            {
//                 File dir = new File(saveFile.getParent());
//                 dir.mkdirs();
//                 saveFile.createNewFile();
//            }
//            FileOutputStream outStream = new FileOutputStream(saveFile);
//            saveFile.
//            outStream.write(toSaveString.getBytes());
//            outStream.close();
//            }
//        catch (FileNotFoundException e){
//            e.printStackTrace();
//            }
//        catch (IOException e){
//            e.printStackTrace();
//            }
//        }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                lat.setText(new Double(latitude).toString());
                lon.setText(new Double(longitude).toString());
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
        startActivity(new Intent(this, LocationActivity.class));
//        String filename = "20150109222411"+ ".jpg";
//        File fileFolder = new File(Environment.getExternalStorageDirectory()
//                + "/finger/");
//        File jpgFile = new File(fileFolder, filename);
//        Bitmap bitmap;
//        if(jpgFile.exists())
//            bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageState(jpgFile));
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
