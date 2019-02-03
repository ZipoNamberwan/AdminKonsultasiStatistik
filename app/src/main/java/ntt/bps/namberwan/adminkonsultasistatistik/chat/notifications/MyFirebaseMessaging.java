package ntt.bps.namberwan.adminkonsultasistatistik.chat.notifications;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ntt.bps.namberwan.adminkonsultasistatistik.chat.ChatActivity;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "id notifikasi channel indikator strategis NTT";

    private static final int NOTIFICATION_DOWNLOAD_ID = 486279135;

    String sented;

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
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String photo = remoteMessage.getData().get("photo");
        String username = remoteMessage.getData().get("username");

        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.ID_USER_SENDER, sented);
        intent.putExtra(ChatActivity.ID_ADMIN_RECEIVER, user);
        intent.putExtra(ChatActivity.URL_PHOTO_RECEIVER, photo);
        intent.putExtra(ChatActivity.USERNAME_RECEIVER, username);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
        mBuilder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        mBuilder.setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(NOTIFICATION_DOWNLOAD_ID, mBuilder.build());
    }
}
