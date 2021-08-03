package com.example.mymusicapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class playMusic extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    //Declaring variables
    TextView musicName;
    ImageView pause, previous, next, shuffle, repeat;
    ArrayList<File> mySongs;
    int position;
    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    Thread updateSeek;
    String[] songName;
    ImageView imageHolder;
    boolean shuffleBoolean,repeatBoolean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        //Connecting xml file with variable with help of id
        musicName = findViewById(R.id.musicName);
        musicName.setSelected(true);
        pause = findViewById(R.id.pause);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        shuffle = findViewById(R.id.shuffle);
        repeat = findViewById(R.id.repeat);
        seekBar = findViewById(R.id.seekBar);
        imageHolder = findViewById(R.id.imageHolder);
        repeatBoolean= false;

        mySongs= MainActivity.getMySongs();
        Intent intent = getIntent();
        songName=MainActivity.getItems();

        //getting position with intent
        position = intent.getIntExtra("position", 0);
        play();

        //syncing seekbar with the song
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try {
                    while (currentPosition<mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(800);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();

        //On click listener method for pause button
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    pause.setImageResource(R.drawable.play);
                    mediaPlayer.pause(); //pausing media player
                }else{
                    pause.setImageResource(R.drawable.pause);
                    mediaPlayer.start(); //playing media player
                }
            }
        });

        //On Click Listener method for previous button
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop(); //stopping mediaplayer for previous song
                mediaPlayer.release(); //releasing mediaplayer for previous song

                //condition if current song position is first
                if (position != 0){
                    position = position-1;
                }else{
                    position = mySongs.size() - 1;
                }
                play();
            }
        });

        //On Click Listener method for previous button
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop(); //stopping mediaplayer for next song
                mediaPlayer.release(); //releasing mediaplayer for next song
                if (shuffleBoolean){
                    Random random = new Random();
                    position = random.nextInt(mySongs.size() - 1);
                }else if (position != mySongs.size() - 1){
                    position = position+1;
                }else{
                    position = 0;
                }
                play();
            }
        });

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!shuffleBoolean) {
                    Toast.makeText(playMusic.this, "The songs are shuffled.", Toast.LENGTH_SHORT).show();
                    repeatBoolean= false;
                    repeat.setImageResource(R.drawable.repeat);
                    shuffleBoolean = true;
                    shuffle.setImageResource(R.drawable.shuffle_clicked);
                } else if (shuffleBoolean) {
                    Toast.makeText(playMusic.this, "The songs are not shuffled.", Toast.LENGTH_SHORT).show();
                    shuffleBoolean = false;
                    shuffle.setImageResource(R.drawable.shuffle);
                }
            }
        });

        //onclick listener for repeat button
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!repeatBoolean){
                    Toast.makeText(playMusic.this, "The current song is Looped.", Toast.LENGTH_SHORT).show();
                    shuffleBoolean = false;
                    shuffle.setImageResource(R.drawable.shuffle);
                    repeatBoolean= true;
                    repeat.setImageResource(R.drawable.repeat_clicked);
                }else if (repeatBoolean){
                    Toast.makeText(playMusic.this, "The current song is not Looped.", Toast.LENGTH_SHORT).show();
                    repeatBoolean= false;
                    repeat.setImageResource(R.drawable.repeat);
                }
            }
        });
    }

    //method for playing song
    public void play(){
        musicName.setText(songName[position]); //getting song from arraylist for mediaplayer
        Uri uri=Uri.parse(mySongs.get(position).toString());
        mediaPlayer=MediaPlayer.create(this,uri);
        mediaPlayer.start(); //starting mediaplayer

        //Updating seekbar
        seekBar.setProgress(0);
        seekBar.setMax(mediaPlayer.getDuration());

        //calling setImage() method for setting image
        setImage();

        //onCompletion listener method for mediaplayer
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (shuffleBoolean && !repeatBoolean){
                    Random random = new Random();
                    position = random.nextInt(mySongs.size() - 1);
                }else if (!shuffleBoolean && repeatBoolean){
                    mediaPlayer.setLooping(true);
                }else{
                    position++;
                }
                play();
            }
        });
    }

    //set image method
    public void setImage(){
        android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(mySongs.get(position).getPath());
        byte [] data = mmr.getEmbeddedPicture();
        //coverart is an Imageview object

        // convert the byte array to a bitmap
        if(data != null)
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            imageHolder.setImageBitmap(bitmap); //associated cover art in bitmap
        }else{
            imageHolder.setImageResource(R.drawable.music_note); //associating cover art if there is no image in the song file
        }
    }
}
