package com.rabin.mygallery;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;
    public MusicService() {
    }
    int MUSIC = R.raw.seclusion;

    @Override
    public void onCreate(){
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, MUSIC);
        mediaPlayer.setLooping(true);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        mediaPlayer.start();
        return START_STICKY;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }
}