package ru.semenovmy.learning.reminder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.List;

import ru.semenovmy.learning.reminder.R;
import ru.semenovmy.learning.reminder.database.ReminderDatabase;
import ru.semenovmy.learning.reminder.database.Reminder;

/**
 * Класс для загрузки напоминания
 *
 * @author Maxim Semenov on 2019-11-15
 */
public class BootReceiver extends BroadcastReceiver {

    // Константы в миллисекундах
    private static final long sMilMinute = 60000L;
    private static final long sMilHour = 3600000L;
    private static final long sMilDay = 86400000L;
    private static final long sMilWeek = 604800000L;
    private static final long sMilMonth = 2592000000L;

    private Calendar mCalendar;
    private int mYear, mMonth, mHour, mMinute, mDay, mReceivedID;
    private NotificationReceiver mNotificationReceiver;
    private long mRepeatTime;
    private String mTime;
    private String mDate;
    private String mRepeatAmount;
    private String mRepeatType;
    private String mActive;
    private String mRepeat;
    private String[] mDateSplit;
    private String[] mTimeSplit;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(R.string.boot_completed)) {

            ReminderDatabase rb = new ReminderDatabase(context);
            mCalendar = Calendar.getInstance();
            mNotificationReceiver = new NotificationReceiver();

            List<Reminder> reminders = rb.getAllReminders();

            for (Reminder reminder : reminders) {
                mReceivedID = reminder.getID();
                mRepeat = reminder.getRepeat();
                mRepeatAmount = reminder.getRepeatAmount();
                mRepeatType = reminder.getRepeatType();
                mActive = reminder.getActive();
                mDate = reminder.getDate();
                mTime = reminder.getTime();

                mDateSplit = mDate.split("/");
                mTimeSplit = mTime.split(":");

                mDay = Integer.parseInt(mDateSplit[0]);
                mMonth = Integer.parseInt(mDateSplit[1]);
                mYear = Integer.parseInt(mDateSplit[2]);
                mHour = Integer.parseInt(mTimeSplit[0]);
                mMinute = Integer.parseInt(mTimeSplit[1]);

                mCalendar.set(Calendar.MONTH, --mMonth);
                mCalendar.set(Calendar.YEAR, mYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
                mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
                mCalendar.set(Calendar.MINUTE, mMinute);
                mCalendar.set(Calendar.SECOND, 0);

                // Отмена напоминания по ID
                mNotificationReceiver.cancelNotification(context, mReceivedID);

                // Проверка типа напоминания
                if (mRepeatType.equals(R.string.minute)) {
                    mRepeatTime = Integer.parseInt(mRepeatAmount) * sMilMinute;
                } else if (mRepeatType.equals(R.string.hour)) {
                    mRepeatTime = Integer.parseInt(mRepeatAmount) * sMilHour;
                } else if (mRepeatType.equals(R.string.day)) {
                    mRepeatTime = Integer.parseInt(mRepeatAmount) * sMilDay;
                } else if (mRepeatType.equals(R.string.week)) {
                    mRepeatTime = Integer.parseInt(mRepeatAmount) * sMilWeek;
                } else if (mRepeatType.equals(R.string.month)) {
                    mRepeatTime = Integer.parseInt(mRepeatAmount) * sMilMonth;
                }

                // Создание напоминания
                if (mActive.equals("true")) {
                    if (mRepeat.equals("true")) {
                        mNotificationReceiver.setRepeatNotification(context, mCalendar, mReceivedID, mRepeatTime);
                    } else if (mRepeat.equals("false")) {
                        mNotificationReceiver.setNotification(context, mCalendar, mReceivedID);
                    }
                }
            }
        }
    }
}