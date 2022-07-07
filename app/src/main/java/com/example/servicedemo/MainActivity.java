package com.example.servicedemo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private TextView textView;
    private Button startB, bindB;
    private Intent servIntent;

    private MyService myService;
    private boolean serviceBounded = false;
    private ServiceConnection serviceConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.dataET);
        textView = findViewById(R.id.hashTV);
        startB = findViewById(R.id.startB);
        bindB = findViewById(R.id.boundB);

        startB.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String data = editText.getText().toString();
                servIntent = new Intent(MainActivity.this, MyService.class);
                servIntent.putExtra("DATA", data);
                startService(servIntent);
                //startForegroundService(servIntent);
            }
        });

        bindB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(serviceBounded){
                    myService.setContext(MainActivity.this);
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = editText.getText().toString();
                    myService.getThreadHandler().sendMessage(msg);
                    //String hash = myService.hashData(editText.getText().toString());
                    //textView.append(hash+"\n");
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        serviceConnection = new MyServiceConnection();
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(servIntent);
        unbindService(serviceConnection);
        serviceBounded = false;
    }

    public class MyServiceConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.ServiceBider binder = (MyService.ServiceBider) iBinder;
            myService = binder.getMyService();
            serviceBounded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceBounded = false;
        }
    }

}