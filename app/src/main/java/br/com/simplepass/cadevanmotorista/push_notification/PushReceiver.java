package br.com.simplepass.cadevanmotorista.push_notification;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import br.com.simplepass.cadevanmotorista.R;
import br.com.simplepass.cadevanmotorista.domain_realm.Path;
import br.com.simplepass.cadevanmotorista.dto.PathShare;
import br.com.simplepass.cadevanmotorista.dto.PushMessage;
import br.com.simplepass.cadevanmotorista.retrofit.ServiceGenerator;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;


/**
 * Created by leandro on 4/19/16.
 */
public class PushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String message;
        String type;

        if (intent.getStringExtra("type") != null && intent.getStringExtra("message") != null) {
            type = intent.getStringExtra("type");
            message = intent.getStringExtra("message");

            switch (type){
                case PushMessage.TYPE_SHARE_PATH:
                    Gson gson = new GsonBuilder()
                            .setExclusionStrategies(new ExclusionStrategy() {
                                @Override
                                public boolean shouldSkipField(FieldAttributes f) {
                                    return f.getDeclaringClass().equals(RealmObject.class);
                                }

                                @Override
                                public boolean shouldSkipClass(Class<?> clazz) {
                                    return false;
                                }
                            })
                            .create();

                    PathShare pathShare = gson.fromJson(message, PathShare.class);

                    Realm realm = Realm.getDefaultInstance();

                    RealmResults<Path> paths = realm.where(Path.class).findAll();

                    long lastPathId;
                    if (paths.size() == 0) {
                        lastPathId = 0;
                    } else {
                        lastPathId = paths.last().getId();
                    }

                    realm.beginTransaction();
                    pathShare.getPath().setId(++lastPathId);
                    realm.copyToRealm(pathShare);
                    realm.close();

                    sendNotification(context, String.format(
                            context.getString(R.string.shared_path_received), pathShare.getDriverName()));
                    break;
                default:
                    sendNotification(context, message);
            }
        }

    }

    private void sendNotification(Context context, String message){
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_icon)
                        .setContentText(message)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setSound(alarmSound);

        /*Intent notificationIntentDefault = new Intent(this, RequestRideActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntentDefault,
                PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(contentIntent);*/

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(8780, mBuilder.build());
    }
}
