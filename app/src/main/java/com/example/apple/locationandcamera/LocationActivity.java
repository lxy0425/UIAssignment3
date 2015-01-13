package com.example.apple.locationandcamera;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/**
 * Created by apple on 15-01-12.
 */
public class LocationActivity extends ListActivity{
    ArrayList<String> item = new ArrayList<String>();
    ArrayList<String> item1 = new ArrayList<String>();
    ArrayList<String> itemPicture = new ArrayList<String>();
    Intent intent;
    IconicAdapter adapter;
    String pictureName;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
//        String path = Environment.getExternalStorageDirectory()+"/finger/location.txt/";
//        try {
//            FileInputStream inputStream = new FileInputStream(path);
//            if(inputStream != null) {
//                InputStreamReader buffer = new InputStreamReader(inputStream);
//                BufferedReader reader  = new BufferedReader(buffer);
//                String s;
//                item.clear();
//                while((s = reader.readLine())!= null){
//                    item.add(s);
//                }
//                setListAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,item));
//            }
//        }
//        catch(FileNotFoundException e){
//        }
//        catch(Throwable t){
//            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
//        }
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "/finger/location.txt/");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String readline = "";
            item.clear();
            while ((readline = br.readLine()) != null) {
                item.add(readline);
            }
            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,item);
            //setListAdapter(adapter);

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        AdapterView.OnItemClickListener l = new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String picturename;
//                try {
//                    File file = new File(Environment.getExternalStorageDirectory(), "/finger/picture.txt/");
//                    BufferedReader br = new BufferedReader(new FileReader(file));
//                    String readline = "";
//                    item1.clear();
//                    while ((readline = br.readLine()) != null) {
//                        item1.add(readline);
//                    }
//                    picturename = item1.get(position);
//                    intent = new Intent(LocationActivity.this, OtherActivity.class);
//                    intent.putExtra("picturename", picturename);
//                    startActivity(intent);
//                    br.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String picturename;
                try {
                    File file = new File(Environment.getExternalStorageDirectory(), "/finger/picture.txt/");
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String readline = "";
                    item1.clear();
                    while ((readline = br.readLine()) != null) {
                        item1.add(readline);
                    }
                    picturename = item1.get(position);
                    intent = new Intent(LocationActivity.this, OtherActivity.class);
                    intent.putExtra("picturename", picturename);
                    startActivity(intent);
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        AdapterView.OnItemLongClickListener l = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(LocationActivity.this);
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


//                        try {
//                            File file = new File(Environment.getExternalStorageDirectory(), "/finger/picture.txt/");
//                            BufferedReader br = new BufferedReader(new FileReader(file));
//                            String readline = "";
//                            item1.clear();
//                            while ((readline = br.readLine()) != null) {
//                                item1.add(readline);
//                            }
//                            String picturename = item1.get(positionToRemove);
                            File file1 = new File(Environment.getExternalStorageDirectory(),"/finger/"+pictureName+"/");
                            deleteFile(file1);
//                            br.close();
//                        }
//                        catch (Exception e) {
//                            e.printStackTrace();
//                        }
                    }
                });
                adb.show();
                return true;
            }
        };
        getListView().setOnItemLongClickListener(l);
        adapter = new IconicAdapter();
        setListAdapter(adapter);
    }

//    @Override
//    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//        AlertDialog.Builder adb = new AlertDialog.Builder(LocationActivity.this);
//        adb.setTitle("Delete?");
//        adb.setMessage("Are you sure you want to delete " + position);
//        final int positionToRemove = position;
//        adb.setNegativeButton("Cancel", null);
//        adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                adapter.remove(item.get(positionToRemove));
//                adapter.notifyDataSetChanged();
//                String picturename = item1.get(positionToRemove);
//                File file = new File(Environment.getDataDirectory()+"/finger/"+picturename+"/");
//                deleteFile(file);
//            }
//        });
//        return true;
//    }

    class IconicAdapter extends ArrayAdapter<String> {
        IconicAdapter() {
            super(LocationActivity.this, android.R.layout.simple_list_item_1, item);
        }
//        public void onListItemClick(ListView parent, View v, int position, long id) {
//            String picturename;
////        try {
////            File file = new File(Environment.getExternalStorageDirectory(),"/finger/picture.txt/");
////
////            InputStream inputStream = openFileInput(filename);
////            if(inputStream != null) {
////                InputStreamReader buffer = new InputStreamReader(inputStream);
////                BufferedReader reader  = new BufferedReader(buffer);
////                String s;
////                item1.clear();
////                while((s = reader.readLine())!= null){
////                    item1.add(s);
////                }
////            }
////        }
////        catch(FileNotFoundException e){
////        }
////        catch(Throwable t){
////            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
////        }
//            try {
//                File file = new File(Environment.getExternalStorageDirectory(), "/finger/picture.txt/");
//                BufferedReader br = new BufferedReader(new FileReader(file));
//                String readline = "";
//                item1.clear();
//                while ((readline = br.readLine()) != null) {
//                    item1.add(readline);
//                }
//                picturename = item1.get(position);
//                intent = new Intent(this, OtherActivity.class);
//                intent.putExtra("picturename", picturename);
//                startActivity(intent);
//                br.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }
}
