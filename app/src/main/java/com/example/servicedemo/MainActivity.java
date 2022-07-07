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

    private boolean serviceBouded = false;
    private MyServiceConn myServiceConn;
    private MyService myService;

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
                if(serviceBouded){
                    //IPC
                    Message msg = new Message();
                    msg.obj = editText.getText().toString();
                    msg.what = 0;
                    myService.getThreadHandler().sendMessage(msg);
                    //textView.append(hash+"\n");
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        myServiceConn = new MyServiceConn();
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, myServiceConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(myServiceConn);
        serviceBouded = false;
        //stopService(servIntent);
    }

    public class MyServiceConn implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.MyServiceBinder binder = (MyService.MyServiceBinder) iBinder;
            myService = binder.getMyService();
            serviceBouded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            serviceBouded = false;
        }
    }

}