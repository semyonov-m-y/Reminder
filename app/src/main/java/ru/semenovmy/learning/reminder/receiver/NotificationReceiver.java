package ru.semenovmy.learning.reminder.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import java.util.Calendar;

import ru.semenovmy.learning.reminder.R;
import ru.semenovmy.learning.reminder.ReminderEditActivity;
import ru.semenovmy.learning.reminder.data.database.Reminder;
import ru.semenovmy.learning.reminder.data.database.ReminderDatabase;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Класс для установки напоминания
 *
 * @author Maxim Semenov on 2019-11-15
 */
public class NotificationReceiver extends BroadcastReceiver {

    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        int mReceivedID = Integer.parseInt(intent.getStringExtra(ReminderEditActivity.EXTRA_REMINDER_ID));

        // Получить название напоминания из Reminder Database
        ReminderDatabase reminderDatabase = new ReminderDatabase(context);
        Reminder reminder = reminderDatabase.getReminder(mReceivedID);
        String mTitle = reminder.getTitle();

        // Создать намерение, чтобы открыть ReminderEditActivity при клике по напоминанию
        Intent editIntent = new Intent(context, ReminderEditActivity.class);
        editIntent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(mReceivedID));
        PendingIntent mClick = PendingIntent.getActivity(context, mReceivedID, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri ringtoneUri;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultToneKey = context.getString(R.string.pref_tone_default_key);

        String ringtonePreference = sharedPreferences.getString(defaultToneKey, "content://settings/system/notification_sound");
        ringtoneUri = Uri.parse(ringtonePreference);

        // Задаём звук напоминания
        try {
            Ringtone r = RingtoneManager.getRingtone(context, ringtoneUri);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Создаем напоминание
        Notification.Builder mNotification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mNotification = new Notification.Builder(context)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setSmallIcon(R.drawable.clock_white)
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setTicker(mTitle)
                    .setContentText(mTitle)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setSound(ringtoneUri)
                    .setContentIntent(mClick)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true);
        }

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        if (mNotification != null) {
            notificationManager.notify(mReceivedID, mNotification.build());
        }
    }

    /**
     * Метод для старта и рестарта напоминания
     */
    public void setNotification(Context context, Calendar calendar, int ID) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Добавить Reminder ID в намерение
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(ID));
        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Вычислить время намерения
        Calendar calendar1 = Calendar.getInstance();
        long currentTime = calendar1.getTimeInMillis();
        long diffTime = calendar.getTimeInMillis() - currentTime;

        // Запустить напоминание
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + diffTime, mPendingIntent);

        // Перезапустить напоминание, если устройство перезагружено
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager packageManager = context.getPackageManager();
        packageManager.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * Метод для установки повторений для напоминания
     */
    public void setRepeatNotification(Context context, Calendar calendar, int ID, long RepeatTime) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Добавить Reminder ID в намерение
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(ID));
        mPendingIntent = PendingIntent.getBroadcast(context, ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Вычислить время намерения
        Calendar calendar1 = Calendar.getInstance();
        long currentTime = calendar1.getTimeInMillis();
        long diffTime = calendar.getTimeInMillis() - currentTime;

        // Запустить напоминание
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + diffTime, RepeatTime, mPendingIntent);

        // Перезапустить напоминание, если устройство перезагружено
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager packageManager = context.getPackageManager();
        packageManager.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    /**
     * Метод для отмены и отключения напоминания
     */
    public void cancelNotification(Context context, int ID) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Отменить напоминание Reminder ID
        mPendingIntent = PendingIntent.getBroadcast(context, ID, new Intent(context, NotificationReceiver.class), 0);
        mAlarmManager.cancel(mPendingIntent);

        // Отключить напоминание
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager packageManager = context.getPackageManager();
        packageManager.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
}