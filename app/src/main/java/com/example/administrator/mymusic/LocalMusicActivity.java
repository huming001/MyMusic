package com.example.administrator.mymusic;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.Manifest;

import java.util.ArrayList;
import java.util.List;

public class LocalMusicActivity extends AppCompatActivity {

    ListView listView;
    private final static int REQUEST_EXTERNAL_STORAGE = 1;
    private MusicAdapter adapter;
    private MusicService musicService;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.MyBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.localmusic);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);


        listView = (ListView) findViewById(R.id.listView);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
        }

        //查找资源
        FindMusic findMusic = new FindMusic();
        List<Music> musics1 = findMusic.getMusics(LocalMusicActivity.this.getContentResolver());

        List<Music> musics2 = new ArrayList<>();
        int i = 0;
        do {
            musics2.addAll(musics1);
            i++;
        } while (i < 100);

        final List<Music> musics = musics2;

        //新建适配器
        adapter = new MusicAdapter(this, R.layout.musicitem, musics);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("HM", "setOnItemClickListener");

                Music music = musics.get(position);
                String url = music.getUrl();
                String title = music.getTitle();
                String artist = music.getArtist();

                Intent intent = new Intent("startNew");
                intent.putExtra("url", url);
                intent.putExtra("title", title);
                intent.putExtra("artist", artist);

                final Intent eIntent = new Intent(createIntent(LocalMusicActivity.this, intent));
                bindService(eIntent, conn, Service.BIND_AUTO_CREATE);
                startService(eIntent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    public static Intent createIntent(Context context, Intent imIntent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfoS = pm.queryIntentServices(imIntent, 0);
        if (resolveInfoS == null || resolveInfoS.size() != 1) {
            return null;
        }

        ResolveInfo resolveInfo = resolveInfoS.get(0);
        String packageName = resolveInfo.serviceInfo.packageName;
        String className = resolveInfo.serviceInfo.name;
        ComponentName componentName = new ComponentName(packageName, className);

        Intent intent = new Intent(imIntent);
        intent.setComponent(componentName);
        return intent;
    }
}
