package ru.semenovmy.learning.reminder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.File;
import java.util.Calendar;
import java.util.List;

/**
 * Класс для добавления элемента Recycler View
 *
 * @author Maxim Semenov on 2019-11-15
 */
public class ReminderAddActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private final int GALLERY_REQUEST_CODE = 0;
    private static final int REQUEST_PHOTO = 2;

    private static ReminderDatabase sBase;

    private Button mReportButton;
    private Calendar mCalendar;
    private EditText mTitleText;
    private File mPhotoFile;
    private FloatingActionButton mFloatingActionButton1, mFloatingActionButton2;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private int mYear, mMonth, mHour, mMinute, mDay, mID;
    private long mRepeatTime;
    private Reminder mReminder;
    private String mTitle, mTime, mDate, mRepeat, mRepeatAmount, mRepeatType, mActive;
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

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_add_reminder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Устанавливаем значения по умолчанию
        mActive = "true";
        mRepeat = "true";
        mRepeatAmount = Integer.toString(1);
        mRepeatType = "Hour";

        mCalendar = Calendar.getInstance();
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mDay = mCalendar.get(Calendar.DATE);

        mDate = mDay + "/" + mMonth + "/" + mYear;
        mTime = mHour + ":" + mMinute;

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

        // Устанавливаем TextViews, используя значения напоминания
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

        mReportButton = findViewById(R.id.note_report);
        mReportButton.setOnClickListener(v12 -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, mTitleText.getText() + " - " + mDateText.getText() + " - " + mTimeText.getText());
            i = Intent.createChooser(i, getString(R.string.send_report));
            startActivity(i);
        });

        // Обрабатываем работу с картинкой
        mReminder = get(getApplicationContext()).getAllReminders().get(mID);
        mPhotoFile = getPhotoFile(mReminder);

        PackageManager packageManager = getPackageManager();
        mPhotoButton = findViewById(R.id.note_camera);

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;

        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(v13 -> {
            Uri uri = FileProvider.getUriForFile(getApplicationContext(),
                    "ru.semenovmy.learning.reminder",
                    mPhotoFile);

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

    /**
     * Метод для получения фото с камеры
     */
    File getPhotoFile(Reminder reminder) {
        File filesDir = getApplicationContext().getFilesDir();
        return new File(filesDir, reminder.getPhotoFilename());
    }

    /**
     * Метод для получения базы
     */
    static ReminderDatabase get(Context context) {
        if (sBase == null) {
            sBase = new ReminderDatabase(context);
        }
        return sBase;
    }

    /**
     * Метод для получения фото из галереи
     */
    void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        // Устанавливаем допустимые типы фото
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Результат RESULT_OK только если выбрано фото
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();

                    mPhotoView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
                    break;
            }
    }

    /**
     * Метод для реагирования на изменение прав установки цвета страницы
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String backgroundColour = sharedPreferences.getString("set_color", "Default");
        if (backgroundColour.equals("Green")) {
            findViewById(R.id.activity_add).setBackgroundColor(getResources().getColor(R.color.colorGreen));
        } else if (backgroundColour.equals("Pink")) {
            findViewById(R.id.activity_add).setBackgroundColor(getResources().getColor(R.color.colorPink));
        } else if (backgroundColour.equals("Blue")) {
            findViewById(R.id.activity_add).setBackgroundColor(getResources().getColor(R.color.colorBlue));
        } else {
            findViewById(R.id.activity_add).setBackgroundColor(getResources().getColor(R.color.primary_dark));
        }
    }

    /**
     * Метод для установки времени
     */
    public void setTime(View v) {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.setThemeDark(false);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    /**
     * Метод для установки даты
     */
    public void setDate(View v) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
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
     * Метод для задания реакциии на нажатие кнопки сохранения записи
     */
    private void saveReminder() {
        ReminderDatabase rb = new ReminderDatabase(this);

        mID = rb.addReminder(new Reminder(mTitle, mDate, mTime, mRepeat, mRepeatAmount, mRepeatType, mActive));

        // Устанавливаем календарь
        mCalendar.set(Calendar.MONTH, --mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);

        // Проверяем тип повторения
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
                new NotificationReceiver().setRepeatNotification(getApplicationContext(), mCalendar, mID, mRepeatTime);
            } else if (mRepeat.equals("false")) {
                new NotificationReceiver().setNotification(getApplicationContext(), mCalendar, mID);
            }
        }

        // Показываем что напоминание сохранено
        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();

        onBackPressed();
    }

    /**
     * Метод для нажатия на кнопку Назад по умолчанию
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Метод для создания нового меню
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        return true;
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
                    saveReminder();
                }
                return true;

            case R.id.discard_reminder:
                Toast.makeText(getApplicationContext(), "Discarded",
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
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

    /**
     * Метод для установки цвета страницы
     */
    void setUpDefaultSetting() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        String backgroundColour = sharedPreferences.getString("set_color", "Default");
        if (backgroundColour.equals("Green")) {
            findViewById(R.id.activity_add).setBackgroundColor(getResources().getColor(R.color.colorGreen));
        } else if (backgroundColour.equals("Pink")) {
            findViewById(R.id.activity_add).setBackgroundColor(getResources().getColor(R.color.colorPink));
        } else if (backgroundColour.equals("Blue")) {
            findViewById(R.id.activity_add).setBackgroundColor(getResources().getColor(R.color.colorBlue));
        } else {
            findViewById(R.id.activity_add).setBackgroundColor(getResources().getColor(R.color.primary_dark));
        }
    }
}