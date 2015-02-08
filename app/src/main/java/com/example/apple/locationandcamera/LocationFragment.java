package com.example.apple.locationandcamera;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Created by apple on 15-02-07.
 */
public class LocationFragment extends Fragment{
    ArrayList<String> item = new ArrayList<String>();
    ArrayList<String> item1 = new ArrayList<String>();
    ArrayList<String> itemPicture = new ArrayList<String>();
    Intent intent;
    IconicAdapter adapter;
    String pictureName;
    String picturename;
    int positionRemove;
    ImageView imageView;
    //    private ArrayStringTask task = null;
//    String picture;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup vp,Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.list, vp, false);
    }
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        imageView = (ImageView)getView().findViewById(R.id.camera);
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "/finger/location.txt/");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String readline = "";
            item.clear();
            while ((readline = br.readLine()) != null) {
                item.add(readline);
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        GridView gridView = (GridView)getView().findViewById(R.id.grid);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    File file = new File(Environment.getExternalStorageDirectory(), "/finger/picture.txt/");
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String readline = "";
                    item1.clear();
                    while ((readline = br.readLine()) != null) {
                        item1.add(readline);
                    }
                    picturename = item1.get(position);
                    positionRemove = position;
//                    intent = new Intent(LocationActivity.this, OtherActivity.class);
//                    intent.putExtra("picturename", picturename);
//                    startActivity(intent);
                    br.close();
                    getFragmentManager().beginTransaction().add(android.R.id.content, new OtherFragment()).commit();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        AdapterView.OnItemLongClickListener l = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete " + position);
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
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
                    }
                });
                adb.show();
                return true;
            }
        };
        gridView.setOnItemLongClickListener(l);
        adapter = new IconicAdapter();
        gridView.setAdapter(adapter);
        View.OnClickListener l1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(LocationFragment.this).commit();
                Intent intent = new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
            }
        };
        imageView.setOnClickListener(l1);
    }



    class IconicAdapter extends ArrayAdapter<String> {
        IconicAdapter() {
            //          super(LocationActivity.this, android.R.layout.simple_list_item_1, item);
            super(getActivity(), R.layout.row,R.id.label,item);
        }
        public View getView(int position, View view, ViewGroup viewGroup){
            View row = super.getView(position,view,viewGroup);
            ImageView image = (ImageView)row.findViewById(R.id.image);
            try{
                File file = new File(Environment.getExternalStorageDirectory(), "/finger/picture.txt/");
                BufferedReader br = new BufferedReader(new FileReader(file));
                String readline = "";
                item1.clear();
                while ((readline = br.readLine()) != null) {
                    item1.add(readline);
                }
                br.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            String picture = item1.get(position);
            //           Bitmap bitmapBefore = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/finger/" + picture + "/");
            Bitmap bitmapBefore = getImageThumbnail(Environment.getExternalStorageDirectory() + "/finger/" + picture + "/",200,200);
            Matrix m = new Matrix();
            m.setRotate(90);
            Bitmap bitmap = Bitmap.createBitmap(bitmapBefore, 0, 0, bitmapBefore.getWidth(), bitmapBefore.getHeight(), m, true);
            image.setImageBitmap(bitmap);

            return row;
        }

    }

    private Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false;
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }
    public String getPictureName(){
        return picturename;
    }
    public int getPosition(){
        return positionRemove;
    }
    public ArrayList<String> getItem(){
        return item;
    }
    public void setItem(ArrayList<String> a){
        item = a;
    }
    public ArrayList<String> getItem1(){
        return item1;
    }
    public void setItem1(ArrayList<String> i){
        item1 = i;
    }
    public IconicAdapter getAdapter(){
        return adapter;
    }
    public void setAdapter(IconicAdapter ada){
        adapter = ada;
    }
}
