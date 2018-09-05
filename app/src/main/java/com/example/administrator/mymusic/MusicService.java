package com.example.administrator.mymusic;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by HM on 2018/9/4.
 */

public class MusicService extends Service {
    private static final int SET_SEEKBAR_MAX = 3;
    private static final int UPDATE_PROGRESS = 1;

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private MyBinder myBinder;

    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = new MediaPlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        myBinder = new MyBinder();
        return myBinder;
    }

    public void start() {
        if (mediaPlayer == null)
            return;

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Intent intent1 = new Intent("pauseimage");
            sendBroadcast(intent1);
        } else {
            mediaPlayer.start();
            Intent intent2 = new Intent("playimage");
            sendBroadcast(intent2);
        }
    }

    public void startNew(String path) throws IOException {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        mediaPlayer.release();
        mediaPlayer = null;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(path);
        mediaPlayer.prepare();
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e("HM", "结束歌曲，进行下一曲");
                Intent intent2 = new Intent("nextsong");
                sendBroadcast(intent2);
            }
        });

        Intent intent1 = new Intent("playimage");
        sendBroadcast(intent1);

        handler.sendEmptyMessage(SET_SEEKBAR_MAX);
        handler.sendEmptyMessage(UPDATE_PROGRESS);
    }

    private Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message message) {
            switch (message.what) {
                case UPDATE_PROGRESS:
                    Intent intent = new Intent("seekbarprogress");
                    intent.putExtra("seekbarprogress", mediaPlayer.getCurrentPosition());
                    sendBroadcast(intent);

                    handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000);
                    break;
                case SET_SEEKBAR_MAX:
                    intent = new Intent("seekbarmaxprogress");
                    intent.putExtra("seekbarmaxprogress", mediaPlayer.getDuration());
                    sendBroadcast(intent);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if ("startNew".equals(intent.getAction())) {
            try {
                Toast.makeText(getApplicationContext(), intent.getStringExtra("title"), Toast.LENGTH_SHORT).show();

                startNew(intent.getStringExtra("url"));

                Intent intent1 = new Intent("gettitle");
                intent1.putExtra("title", intent.getStringExtra("title"));
                intent1.putExtra("url", intent.getStringExtra("url"));
                intent1.putExtra("artist", intent.getStringExtra("artist"));
                sendBroadcast(intent1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("changed".equals(intent.getAction())) {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(intent.getIntExtra("seekbarprogress", 0));
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        super.onDestroy();
    }
}
