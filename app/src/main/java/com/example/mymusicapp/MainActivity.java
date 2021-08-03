package com.example.mymusicapp;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<File> getMySongs() {
        return mySongs;
    } //method for returning arraylist of songs
    public static String[] getItems() {
        return items;
    } //method for returning string array

    static ArrayList<File> mySongs; //declaring static ArrayList
    static String[] items; //declaring string array

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RecyclerView recyclerView; //Declaring recycler view

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Dexter for asking permission to read external storage
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        mySongs= fetchSongs(Environment.getExternalStorageDirectory());
                        items = new String[mySongs.size()];

                        //Loop for removing song extension
                        for(int i = 0; i<mySongs.size();i++){
                            if (mySongs.get(i).getName().endsWith(".mp3")){
                                //replacing .mp3 with empty string
                                items[i]= mySongs.get(i).getName().replace(".mp3", "");
                            }
                            if (mySongs.get(i).getName().endsWith(".m4a")){
                                //replacing .m4a with empty string
                                items[i]= mySongs.get(i).getName().replace(".m4a", "");
                            }
                        }
                        customAdapter adapter = new customAdapter(items, MainActivity.this);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                })
                .check();


    }


    //Storing music files in ArrayList
    public ArrayList<File> fetchSongs (File file){
        ArrayList arrayList = new ArrayList();
        File [] songs = file.listFiles();
        if (songs!=null){
            for (File myFile: songs){
                if(!myFile.isHidden() && myFile.isDirectory()){ //checking if the file is hidden or a directory
                    arrayList.addAll(fetchSongs(myFile));
                }
                else{
                    //Checking if the file extension is .m4a or .mp3
                    if (myFile.getName().endsWith(".m4a") || myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith("..") &&
                    !myFile.getName().equals("viber_message.mp3")){
                        arrayList.add(myFile);
                    }
                }
            }
        }
        return arrayList;
    }
}