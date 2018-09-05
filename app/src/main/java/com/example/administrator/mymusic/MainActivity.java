package com.example.administrator.mymusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button localmusic;
    private SeekBar seekBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        localmusic = (Button) findViewById(R.id.localmusic);
        localmusic.setOnClickListener(this);

        seekBar = (SeekBar) findViewById(R.id.seekBar);


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("seekbarmaxprogress");
        intentFilter.addAction("seekbarprogress");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.localmusic:
                Intent intent = new Intent(MainActivity.this, LocalMusicActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private static boolean isExit = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }

        return super.onKeyDown(keyCode, event);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出",
                    Toast.LENGTH_SHORT).show();

            handler.sendEmptyMessageDelayed(0, 2000);
            return;
        }

        finish();
        System.exit(0);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("seekbarmaxprogress")) {
                seekBar.setMax(intent
                        .getIntExtra("seekbarmaxprogress", 100));
            } else if (intent.getAction().equals("seekbarprogress")) {
                seekBar.setProgress(intent
                        .getIntExtra("seekbarprogress", 0));
            }
        }
    };


}
