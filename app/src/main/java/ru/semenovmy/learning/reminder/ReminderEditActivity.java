package ru.semenovmy.learning.reminder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.util.Calendar;
import java.util.List;

import ru.semenovmy.learning.reminder.database.ReminderDatabase;
import ru.semenovmy.learning.reminder.database.Reminder;
import ru.semenovmy.learning.reminder.receiver.NotificationReceiver;

/**
 * Класс для редактирования элемента Recycler View
 *
 * @author Maxim Semenov on 2019-11-15
 */
public class ReminderEditActivity extends ReminderAddActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String EXTRA_REMINDER_ID = "Reminder_ID";

    private int mReceivedID;
    private NotificationReceiver mNotificationReceiver;
    private Reminder mReceivedReminder, mReminder;
    private ReminderDatabase mReminderDatabase;
    private String[] mDateSplit, mTimeSplit;
    private Switch mRepeatSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        mPhotoView = findViewById(R.id.note_photo);
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
        mRepeatText.setText(getString(R.string.every) + " " + mRepeatAmount + " " + mRepeatType);

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
        mPhotoButton.setOnClickListener(view -> {
            Uri uri = FileProvider.getUriForFile(getApplicationContext(),
                    "ru.semenovmy.learning.reminder", mPhotoFile);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.choose_action));
            builder.setNegativeButton(getString(R.string.make_photo),
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
            builder.setNeutralButton(getString(R.string.add_from_gallery),
                    (dialog, which) -> pickFromGallery());
           // mPhotoView.setImageURI(uri);
            builder.show();
        });

        updatePhotoView();

        setUpColorSetting();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Результат RESULT_OK только если выбрано фото
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

/*                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();*/

                    mPhotoView.setImageURI(selectedImage);
                    break;
            }
        }
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

        // Проверяем тип повторения
        if (mRepeatType.equals(getString(R.string.minute))) {
            mRepeatTime = Integer.parseInt(mRepeatAmount) * sMilMinute;
        } else if (mRepeatType.equals(getString(R.string.hour))) {
            mRepeatTime = Integer.parseInt(mRepeatAmount) * sMilHour;
        } else if (mRepeatType.equals(getString(R.string.day))) {
            mRepeatTime = Integer.parseInt(mRepeatAmount) * sMilDay;
        } else if (mRepeatType.equals(getString(R.string.week))) {
            mRepeatTime = Integer.parseInt(mRepeatAmount) * sMilWeek;
        } else if (mRepeatType.equals(getString(R.string.month))) {
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
        Toast.makeText(getApplicationContext(), getString(R.string.edited_text), Toast.LENGTH_SHORT).show();

        onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.save_reminder:
                mTitleText.setText(mTitle);

                if (mTitleText.getText().toString().length() == 0)
                    mTitleText.setError(getString(R.string.not_blank));

                else {
                    updateReminder();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
}