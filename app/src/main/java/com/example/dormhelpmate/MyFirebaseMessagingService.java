package com.example.dormhelpmate;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.dormhelpmate.storage.constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.dormhelpmate.activity_register.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private String userID, saveCurrentDate, saveCurrentTime;
    private CollectionReference notiRef;

    @Override
    public void onNewToken(String mToken) {
        super.onNewToken(mToken);
        Log.e("NEW_TOKEN",mToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());


        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Map<String, String> data = remoteMessage.getData();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendNotification(notification, data);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification(RemoteMessage.Notification notification, Map<String, String> data) {

        //เวลาปัจจุบัน
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        //ref

        userID = fAuth.getCurrentUser().getUid();

        notiRef = fStore.collection("หอพักทั้งหมด")
                .document("123456")
                .collection("users")
                .document(userID)
                .collection("notification");

        String title;
        String body;

        if (notification != null) {
            title = notification.getTitle();
            body = notification.getBody();

        } else {
            title = data.get("title");
            body = data.get("body");

        }



        //save notification to db
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("body", body);
        map.put("date", saveCurrentDate);
        map.put("time", saveCurrentTime);

        notiRef.document(String.valueOf(System.currentTimeMillis() / 1000))
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(MyFirebaseMessagingService.this, "save noti Successfully", Toast.LENGTH_SHORT).show();
                    }
                });


        //
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        Intent intent = new Intent(this, notification.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setLargeIcon(icon)
                .setColor(Color.BLUE)
                .setSmallIcon(R.mipmap.ic_launcher);

        try {
            String picture_url = data.get("picture_url");
            if (picture_url != null && !"".equals(picture_url)) {
                URL url = new URL(picture_url);
                Bitmap bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                notificationBuilder.setStyle(
                        new NotificationCompat.BigPictureStyle().bigPicture(bigPicture).setSummaryText(notification.getBody())
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        notificationBuilder.setLights(Color.RED, 1000, 300);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "channel_id", "channel_name", NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("channel description");
            channel.setShowBadge(true);
            channel.canShowBadge();
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            managerCompat.createNotificationChannel(channel);
        }

        managerCompat.notify(101,notificationBuilder.build());
    }

}