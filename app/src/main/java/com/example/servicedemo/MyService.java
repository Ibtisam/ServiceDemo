package com.example.servicedemo;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MyService extends Service {
    private ServiceHandler serviceHandler;
    private static final String CHANNEL_ID = "NOTIF_C_1";
    private NotificationManagerCompat notificationManagerCompat;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Starting a new background thread
        HandlerThread handlerThread = new HandlerThread("ServiceHandler", Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();

        serviceHandler = new ServiceHandler(handlerThread.getLooper());

        createNotificationChannel();
        notificationManagerCompat = NotificationManagerCompat.from(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String data = intent.getStringExtra("DATA");
        //Creating and sending the message to message queue
        Message msg = new Message();
        msg.obj = data;
        msg.what = 0;
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);
        //OR
        //posting a runnable to message queue
        /*serviceHandler.post(new Runnable() {
            @Override
            public void run() {
                String hash = hashData(data);
                Toast.makeText(MyService.this, hash, Toast.LENGTH_SHORT).show();
            }
        });
        stopSelf();*/

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public String hashData(Object obj){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String hashString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            byte[] hash = md.digest(baos.toByteArray());
            BigInteger number = new BigInteger(1, hash);
            //radix 16 for base 16 (hexadecimal)
            StringBuilder hexString = new StringBuilder(number.toString(16));
            // Pad with leading zeros
            while (hexString.length() < 64)
            {
                hexString.insert(0, '0');
            }
            hashString = hexString.toString();
            oos.close();
            oos.flush();
            baos.close();
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashString;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            //will be used instead of priority
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public class ServiceHandler extends Handler{
        public ServiceHandler(@NonNull Looper looper) {
            super(looper);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    String hash = hashData(msg.obj);
                    Toast.makeText(MyService.this, hash, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }
}