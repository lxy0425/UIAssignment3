package com.example.apple.locationandcamera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by apple on 15-01-11.
 */
public class OtherFragment extends Fragment {
    Bitmap bitmap;
    ImageView imageView;
    String picturename;
    String pictureName;
    ImageView imageTrash;
    ArrayList<String> item;
    ArrayList<String> item1;
    LocationFragment.IconicAdapter adapter;
    ImageView cameraView;
    ImageView galleryView;
    public View onCreateView(LayoutInflater inflater,ViewGroup viewGroup,Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.photo, viewGroup, false);
    }
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        imageView = (ImageView)getView().findViewById(R.id.picture);
        cameraView = (ImageView)getView().findViewById(R.id.camera1);
        galleryView = (ImageView)getView().findViewById(R.id.to_list1);
        imageTrash = (ImageView)getView().findViewById(R.id.trash);
//        picturename = intent1.getStringExtra("picturename");
        picturename = ((LocationFragment)(getFragmentManager().findFragmentByTag("location"))).getPictureName();
        Bitmap bitmapBefore = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/finger/"+picturename+"/");
        Matrix m = new Matrix();
        m.setRotate(90);
        bitmap = Bitmap.createBitmap(bitmapBefore, 0, 0, bitmapBefore.getWidth(), bitmapBefore.getHeight(), m, true);
        imageView.setImageBitmap(bitmap);
        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setTitle("Delete?");
                int position = ((LocationFragment)(getFragmentManager().findFragmentByTag("location"))).getPosition();
                adb.setMessage("Are you sure you want to delete " + position);
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        adapter = ((LocationFragment)(getFragmentManager().findFragmentByTag("location"))).getAdapter();
                        item = ((LocationFragment)(getFragmentManager().findFragmentByTag("location"))).getItem();
                        adapter.remove(item.get(positionToRemove));
                        adapter.notifyDataSetChanged();
                        //item.remove(item.get(positionToRemove));
                        String path = Environment.getExternalStorageDirectory()+"/finger/location.txt/";
                        try {
                            File file = new File(Environment.getExternalStorageDirectory(),"/finger/location.txt/");
                            BufferedWriter wr = new BufferedWriter(new FileWriter(file));
                            for(int i = 0 ; i < item.size(); i++)
                                wr.write(item.get(i)+"\n");
                            wr.close();
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                        try{
                            File file = new File(Environment.getExternalStorageDirectory(), "/finger/picture.txt/");
                            BufferedReader br = new BufferedReader(new FileReader(file));
                            String readline = "";
                            item1 = ((LocationFragment)(getFragmentManager().findFragmentByTag("location"))).getItem1();
                            item1.clear();
                            while ((readline = br.readLine()) != null) {
                                item1.add(readline);
                            }
                            pictureName = item1.get(positionToRemove);
                            item1.remove(item1.get(positionToRemove));
                            br.close();
                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                        try{
                            File out = new File(Environment.getExternalStorageDirectory(),"/finger/picture.txt");
                            BufferedWriter wr = new BufferedWriter(new FileWriter(out));
                            for(int i = 0 ; i < item1.size(); i++ )
                                wr.write(item1.get(i)+"\n");
                            wr.close();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                        File file1 = new File(Environment.getExternalStorageDirectory(),"/finger/"+pictureName+"/");
                        deleteFile(file1);
                        ((LocationFragment)(getFragmentManager().findFragmentByTag("location"))).setAdapter(adapter);
                        ((LocationFragment)(getFragmentManager().findFragmentByTag("location"))).setItem(item);
                        ((LocationFragment)(getFragmentManager().findFragmentByTag("location"))).setItem1(item1);
                        getFragmentManager().beginTransaction().add(android.R.id.content,new LocationFragment(),"location").commit();
                        getFragmentManager().beginTransaction().remove(OtherFragment.this).commit();
                    }
                });
                adb.show();
            }
        };
        imageTrash.setOnClickListener(l);
        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getFragmentManager().beginTransaction().remove(OtherFragment.this).commit();
            }
        });
        galleryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(OtherFragment.this).commit();
                getFragmentManager().beginTransaction().add(android.R.id.content,new LocationFragment(),"location").commit();
            }
        });
    }
    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }
}

