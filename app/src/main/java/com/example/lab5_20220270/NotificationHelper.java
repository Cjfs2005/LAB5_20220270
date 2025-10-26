package com.example.lab5_20220270;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {
    public static final String CHANNEL_THEORETICAL = "channel_teoricos";
    public static final String CHANNEL_LAB = "channel_laboratorios";
    public static final String CHANNEL_ELECTIVE = "channel_electivos";
    public static final String CHANNEL_OTHER = "channel_otros";
    public static final String CHANNEL_MOTIVATION = "channel_motivacion";

    public static void createChannels(Context ctx) {
        NotificationManager nm = ctx.getSystemService(NotificationManager.class);
        if (nm == null) return;

        NotificationChannel ch1 = new NotificationChannel(CHANNEL_THEORETICAL, "Teóricos", NotificationManager.IMPORTANCE_DEFAULT);
        ch1.setDescription("Canal para cursos teóricos");

        NotificationChannel ch2 = new NotificationChannel(CHANNEL_LAB, "Laboratorios", NotificationManager.IMPORTANCE_HIGH);
        ch2.setDescription("Canal para laboratorios");
        ch2.enableVibration(true);

        NotificationChannel ch3 = new NotificationChannel(CHANNEL_ELECTIVE, "Electivos", NotificationManager.IMPORTANCE_LOW);
        ch3.setDescription("Canal para electivos");

        NotificationChannel ch4 = new NotificationChannel(CHANNEL_OTHER, "Otros", NotificationManager.IMPORTANCE_LOW);
        ch4.setDescription("Canal para otros cursos");

        NotificationChannel ch5 = new NotificationChannel(CHANNEL_MOTIVATION, "Motivación", NotificationManager.IMPORTANCE_DEFAULT);
        ch5.setDescription("Canal para mensajes motivacionales");

        nm.createNotificationChannel(ch1);
        nm.createNotificationChannel(ch2);
        nm.createNotificationChannel(ch3);
        nm.createNotificationChannel(ch4);
        nm.createNotificationChannel(ch5);
    }

    public static void sendNotification(Context ctx, String channelId, int id, String title, String text, int iconRes) {
        int res;
        if (channelId.equals(CHANNEL_THEORETICAL)) res = R.drawable.ic_notif_theoretical;
        else if (channelId.equals(CHANNEL_LAB)) res = R.drawable.ic_notif_lab;
        else if (channelId.equals(CHANNEL_ELECTIVE)) res = R.drawable.ic_notif_elective;
        else if (channelId.equals(CHANNEL_OTHER)) res = R.drawable.ic_notif_other;
        else if (channelId.equals(CHANNEL_MOTIVATION)) res = R.drawable.ic_notif_motivational;
        else res = iconRes;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, channelId)
                .setSmallIcon(res)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat.from(ctx).notify(id, builder.build());
    }
}
