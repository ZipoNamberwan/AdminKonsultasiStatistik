package ntt.bps.namberwan.adminkonsultasistatistik.chat.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ntt.bps.namberwan.adminkonsultasistatistik.R;
import ntt.bps.namberwan.adminkonsultasistatistik.chat.ChatActivity;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "id notifikasi admin konsultasi statistik";

    private static final int NOTIFICATION_CHAT_ID = 3424456;

    private String sented;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        sented = remoteMessage.getData().get("sented");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null && sented.equals(firebaseUser.getUid())){
            sendNotification(remoteMessage);
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        //String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String photo = remoteMessage.getData().get("photo");
        String username = remoteMessage.getData().get("username");

        String s = user.replaceAll("[\\D]", "");
        if (s.length() > 5){
            s = s.substring(0, 5);
        }

        int j = Integer.parseInt(s);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.ID_USER_SENDER, sented);
        intent.putExtra(ChatActivity.ID_ADMIN_RECEIVER, user);
        intent.putExtra(ChatActivity.URL_PHOTO_RECEIVER, photo);
        intent.putExtra(ChatActivity.USERNAME_RECEIVER, username);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        mBuilder.setSmallIcon(R.drawable.ic_bps_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Chat Notif Channel", importance);
            channel.setDescription("Chat Notification");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            manager.notify(NOTIFICATION_CHAT_ID, mBuilder.build());
        }else {
            notificationManager.notify(NOTIFICATION_CHAT_ID, mBuilder.build());
        }
    }
}
