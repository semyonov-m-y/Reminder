package ru.semenovmy.learning.reminder;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;

import java.io.File;
import java.util.Calendar;
import java.util.List;

/**
 * Класс для редактирования элемента Recycler View
 *
 * @author Maxim Semenov on 2019-11-15
 */
public class ReminderEditActivity extends ReminderAddActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String EXTRA_REMINDER_ID = "Reminder_ID";
    private static final int REQUEST_PHOTO = 2;

    private Button mReportButton;
    private Calendar mCalendar;
    private EditText mTitleText;
    private File mPhotoFile;
    private FloatingActionButton mFloatingActionButton1, mFloatingActionButton2;
    private ImageView mPhotoView;
    private ImageButton mPhotoButton;
    private int mYear, mMonth, mHour, mMinute, mDay, mReceivedID;
    private long mRepeatTime;
    private NotificationReceiver mNotificationReceiver;
    private Reminder mReceivedReminder, mReminder;
    private ReminderDatabase mReminderDatabase;
    private String mTitle, mTime, mDate, mRepeatAmount, mRepeatType, mActive, mRepeat;
    private String[] mDateSplit, mTimeSplit;
    private Switch mRepeatSwitch;
    private TextView mDateText, mTimeText, mRepeatText, mRepeatAmountText, mRepeatTypeText;
    private Toolbar mToolbar;

    // Константы, учитываемые при повороте экрана
    private static final String KEY_TITLE = "title_key";
    private static final String KEY_TIME = "time_key";
    private static final String KEY_DATE = "date_key";
    private static final String KEY_REPEAT = "repeat_key";
    private static final String KEY_REPEAT_NO = "repeat_no_key";
    private static final String KEY_REPEAT_TYPE = "repeat_type_key";
    private static final String KEY_ACTIVE = "active_key";

    // Константы в миллисекундах
    private static final long sMilMinute = 60000L;
    private static final long sMilHour = 3600000L;
    private static final long sMilDay = 86400000L;
    private static final long sMilWeek = 604800000L;
    private static final long sMilMonth = 2592000000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        mToolbar = findViewById(R.id.toolbar);
        mTitleText = findViewById(R.id.reminder_title);
        mDateText = findViewById(R.id.set_date);
        mTimeText = findViewById(R.id.set_time);
        mRepeatText = findViewById(R.id.set_repeat);
        mRepeatAmountText = findViewById(R.id.set_repeat_no);
        mRepeatTypeText = findViewById(R.id.set_repeat_type);
        mFloatingActionButton1 = findViewById(R.id.starred1);
        mFloatingActionButton2 = findViewById(R.id.starred2);
        mRepeatSwitch = findViewById(R.id.repeat_switch);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_edit_reminder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mTitleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitle = s.toString().trim();
                mTitleText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mReceivedID = Integer.parseInt(getIntent().getStringExtra(EXTRA_REMINDER_ID));
        mReminderDatabase = new ReminderDatabase(this);
        mReceivedReminder = mReminderDatabase.getReminder(mReceivedID);

        mTitle = mReceivedReminder.getTitle();
        mDate = mReceivedReminder.getDate();
        mTime = mReceivedReminder.getTime();
        mRepeat = mReceivedReminder.getRepeat();
        mRepeatAmount = mReceivedReminder.getRepeatAmount();
        mRepeatType = mReceivedReminder.getRepeatType();
        mActive = mReceivedReminder.getActive();

        // Устанавливаем TextViews, используя значения напоминания
        mTitleText.setText(mTitle);
        mDateText.setText(mDate);
        mTimeText.setText(mTime);
        mRepeatAmountText.setText(mRepeatAmount);
        mRepeatTypeText.setText(mRepeatType);
        mRepeatText.setText("Every " + mRepeatAmount + " " + mRepeatType + "(s)");

        // Сохраняем значений для поворота экрана
        if (savedInstanceState != null) {
            String savedTitle = savedInstanceState.getString(KEY_TITLE);
            mTitleText.setText(savedTitle);
            mTitle = savedTitle;

            String savedTime = savedInstanceState.getString(KEY_TIME);
            mTimeText.setText(savedTime);
            mTime = savedTime;

            String savedDate = savedInstanceState.getString(KEY_DATE);
            mDateText.setText(savedDate);
            mDate = savedDate;

            String saveRepeat = savedInstanceState.getString(KEY_REPEAT);
            mRepeatText.setText(saveRepeat);
            mRepeat = saveRepeat;

            String savedRepeatNo = savedInstanceState.getString(KEY_REPEAT_NO);
            mRepeatAmountText.setText(savedRepeatNo);
            mRepeatAmount = savedRepeatNo;

            String savedRepeatType = savedInstanceState.getString(KEY_REPEAT_TYPE);
            mRepeatTypeText.setText(savedRepeatType);
            mRepeatType = savedRepeatType;

            mActive = savedInstanceState.getString(KEY_ACTIVE);
        }

        if (mActive.equals("false")) {
            mFloatingActionButton1.setVisibility(View.VISIBLE);
            mFloatingActionButton2.setVisibility(View.GONE);
        } else if (mActive.equals("true")) {
            mFloatingActionButton1.setVisibility(View.GONE);
            mFloatingActionButton2.setVisibility(View.VISIBLE);
        }

        if (mRepeat.equals("false")) {
            mRepeatSwitch.setChecked(false);
            mRepeatText.setText(R.string.repeat_off);
        } else if (mRepeat.equals("true")) {
            mRepeatSwitch.setChecked(true);
        }

        mCalendar = Calendar.getInstance();
        mNotificationReceiver = new NotificationReceiver();

        mDateSplit = mDate.split("/");
        mTimeSplit = mTime.split(":");

        mDay = Integer.parseInt(mDateSplit[0]);
        mMonth = Integer.parseInt(mDateSplit[1]);
        mYear = Integer.parseInt(mDateSplit[2]);
        mHour = Integer.parseInt(mTimeSplit[0]);
        mMinute = Integer.parseInt(mTimeSplit[1]);

        mReportButton = findViewById(R.id.note_report);
        mReportButton.setOnClickListener(v12 -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, mTitleText.getText() + " - " + mDateText.getText() + " - " + mTimeText.getText());
            i = Intent.createChooser(i, getString(R.string.send_report));
            startActivity(i);
        });

        // Обрабатываем работу с картинкой
        mReminder = get(getApplicationContext()).getReminder(mReceivedID);
        mPhotoFile = getPhotoFile(mReminder);

        PackageManager packageManager = getPackageManager();
        mPhotoButton = findViewById(R.id.note_camera);

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;

        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(v13 -> {
            Uri uri = FileProvider.getUriForFile(getApplicationContext(),
                    "ru.semenovmy.learning.reminder", mPhotoFile);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Choose next action:");
            builder.setNegativeButton("Make photo",
                    (dialog, which) -> {
                        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        List<ResolveInfo> cameraActivities = getApplicationContext()
                                .getPackageManager().queryIntentActivities(captureImage,
                                        PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo activity : cameraActivities) {
                            getApplicationContext().grantUriPermission(activity.activityInfo.packageName,
                                    uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        }
                        startActivityForResult(captureImage, REQUEST_PHOTO);
                    });
            builder.setNeutralButton("Add from gallery",
                    (dialog, which) -> pickFromGallery());
            builder.show();
        });

        mPhotoView = findViewById(R.id.note_photo);

        updatePhotoView();

        setUpDefaultSetting();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Метод для получения времени из TimePicker
     */
    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
        if (minute < 10) {
            mTime = hourOfDay + ":" + "0" + minute;
        } else {
            mTime = hourOfDay + ":" + minute;
        }
        mTimeText.setText(mTime);
    }

    /**
     * Метод для получения даты из TimePicker
     */
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear++;
        mDay = dayOfMonth;
        mMonth = monthOfYear;
        mYear = year;
        mDate = dayOfMonth + "/" + monthOfYear + "/" + year;
        mDateText.setText(mDate);
    }

    /**
     * Метод для получения реакции на нажатие активной кнопки
     */
    public void changeActiveButton(View v) {
        mFloatingActionButton1 = findViewById(R.id.starred1);
        mFloatingActionButton1.setVisibility(View.GONE);
        mFloatingActionButton2 = findViewById(R.id.starred2);
        mFloatingActionButton2.setVisibility(View.VISIBLE);
        mActive = "true";
    }

    /**
     * Метод для получения реакции на нажатие неактивной кнопки
     */
    public void changeInactiveButton(View v) {
        mFloatingActionButton2 = findViewById(R.id.starred2);
        mFloatingActionButton2.setVisibility(View.GONE);
        mFloatingActionButton1 = findViewById(R.id.starred1);
        mFloatingActionButton1.setVisibility(View.VISIBLE);
        mActive = "false";
    }

    /**
     * Метод для получения реакции на нажатие переключателя повторения напоминания
     */
    public void onSwitchRepeat(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            mRepeat = "true";
            mRepeatText.setText("Every " + mRepeatAmount + " " + mRepeatType + "(s)");

        } else {
            mRepeat = "false";
            mRepeatText.setText(R.string.repeat_off);
        }
    }

    /**
     * Метод для получения реакции на нажатие переключателя типа повторения напоминания
     */
    public void selectRepeatType(View v) {
        final String[] items = new String[5];

        items[0] = "Minute";
        items[1] = "Hour";
        items[2] = "Day";
        items[3] = "Week";
        items[4] = "Month";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Type");
        builder.setItems(items, (dialog, item) -> {
            mRepeatType = items[item];
            mRepeatTypeText.setText(mRepeatType);
            mRepeatText.setText("Every " + mRepeatAmount + " " + mRepeatType + "(s)");
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Метод для задания интервала повторения напоминания
     */
    public void setRepeatNo(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Enter Number");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton("Ok",
                (dialog, whichButton) -> {

                    if (input.getText().toString().length() == 0) {
                        mRepeatAmount = Integer.toString(1);
                        mRepeatAmountText.setText(mRepeatAmount);
                        mRepeatText.setText("Every " + mRepeatAmount + " " + mRepeatType + "(s)");
                    } else {
                        mRepeatAmount = input.getText().toString().trim();
                        mRepeatAmountText.setText(mRepeatAmount);
                        mRepeatText.setText("Every " + mRepeatAmount + " " + mRepeatType + "(s)");
                    }
                });
        alert.setNegativeButton("Cancel", (dialog, whichButton) -> {
            // Do nothing
        });
        alert.show();
    }

    /**
     * Метод для редактирования напоминания
     */
    private void updateReminder() {
        // Устанавливаем новые значения
        mReceivedReminder.setTitle(mTitle);
        mReceivedReminder.setDate(mDate);
        mReceivedReminder.setTime(mTime);
        mReceivedReminder.setRepeat(mRepeat);
        mReceivedReminder.setRepeatAmount(mRepeatAmount);
        mReceivedReminder.setRepeatType(mRepeatType);
        mReceivedReminder.setActive(mActive);

        mReminderDatabase.updateReminder(mReceivedReminder);

        mCalendar.set(Calendar.MONTH, --mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);

        // Отменяем существующее напоминание по ID
        mNotificationReceiver.cancelNotification(getApplicationContext(), mReceivedID);

        // Проверяем тип напоминания
        if (mRepeatType.equals("Minute")) {
            mRepeatTime = Integer.parseInt(mRepeatAmount) * sMilMinute;
        } else if (mRepeatType.equals("Hour")) {
            mRepeatTime = Integer.parseInt(mRepeatAmount) * sMilHour;
        } else if (mRepeatType.equals("Day")) {
            mRepeatTime = Integer.parseInt(mRepeatAmount) * sMilDay;
        } else if (mRepeatType.equals("Week")) {
            mRepeatTime = Integer.parseInt(mRepeatAmount) * sMilWeek;
        } else if (mRepeatType.equals("Month")) {
            mRepeatTime = Integer.parseInt(mRepeatAmount) * sMilMonth;
        }

        // Создаем новое напоминание
        if (mActive.equals("true")) {
            if (mRepeat.equals("true")) {
                mNotificationReceiver.setRepeatNotification(getApplicationContext(), mCalendar, mReceivedID, mRepeatTime);
            } else if (mRepeat.equals("false")) {
                mNotificationReceiver.setNotification(getApplicationContext(), mCalendar, mReceivedID);
            }
        }

        // Показываем уведомление о том что напоминание
        Toast.makeText(getApplicationContext(), "Edited", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    /**
     * Метод для установления реакций на нажатие пунктов меню
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.save_reminder:
                mTitleText.setText(mTitle);

                if (mTitleText.getText().toString().length() == 0)
                    mTitleText.setError("Reminder Title cannot be blank!");

                else {
                    updateReminder();
                }
                return true;

            case R.id.discard_reminder:
                Toast.makeText(getApplicationContext(), "Changes Discarded",
                        Toast.LENGTH_SHORT).show();

                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Метод для сохранения состояний при повороте экрана
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharSequence(KEY_TITLE, mTitleText.getText());
        outState.putCharSequence(KEY_TIME, mTimeText.getText());
        outState.putCharSequence(KEY_DATE, mDateText.getText());
        outState.putCharSequence(KEY_REPEAT, mRepeatText.getText());
        outState.putCharSequence(KEY_REPEAT_NO, mRepeatAmountText.getText());
        outState.putCharSequence(KEY_REPEAT_TYPE, mRepeatTypeText.getText());
        outState.putCharSequence(KEY_ACTIVE, mActive);
    }

    /**
     * Метод для обновления фото на View
     */
    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), this);
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}