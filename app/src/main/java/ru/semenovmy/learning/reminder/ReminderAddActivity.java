package ru.semenovmy.learning.reminder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
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

import ru.semenovmy.learning.reminder.database.ReminderDatabase;
import ru.semenovmy.learning.reminder.database.Reminder;
import ru.semenovmy.learning.reminder.receiver.NotificationReceiver;
import ru.semenovmy.learning.reminder.utils.PictureUtils;

/**
 * Класс для добавления элемента Recycler View
 *
 * @author Maxim Semenov on 2019-11-15
 */
public class ReminderAddActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener, SharedPreferences.OnSharedPreferenceChangeListener {

    // Константы, учитываемые при повороте экрана
    static final String KEY_TITLE = "title_key";
    static final String KEY_TIME = "time_key";
    static final String KEY_DATE = "date_key";
    static final String KEY_REPEAT = "repeat_key";
    static final String KEY_REPEAT_NO = "repeat_no_key";
    static final String KEY_REPEAT_TYPE = "repeat_type_key";
    static final String KEY_ACTIVE = "active_key";

    // Константы в миллисекундах
    static final long sMilMinute = 60000L;
    static final long sMilHour = 3600000L;
    static final long sMilDay = 86400000L;
    static final long sMilWeek = 604800000L;
    static final long sMilMonth = 2592000000L;

    static final int REQUEST_PHOTO = 2;
    final int GALLERY_REQUEST_CODE = 0;

    private static ReminderDatabase sBase;

    public Button mReportButton;
    public Calendar mCalendar;
    public EditText mTitleText;
    public File mPhotoFile;
    public FloatingActionButton mFloatingActionButton1, mFloatingActionButton2;
    public ImageButton mPhotoButton;
    public ImageView mPhotoView;
    public int mYear, mMonth, mHour, mMinute, mDay, mID;
    public long mRepeatTime;
    public Reminder mReminder;
    public String mTitle, mTime, mDate, mRepeat, mRepeatAmount, mRepeatType, mActive;
    public TextView mDateText, mTimeText, mRepeatText, mRepeatAmountText, mRepeatTypeText;
    public Toolbar mToolbar;

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

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_add_reminder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Устанавливаем значения по умолчанию
        mActive = "true";
        mRepeat = "true";
        mRepeatAmount = Integer.toString(1);
        mRepeatType = getString(R.string.default_repeat_type);

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

        // Добавляем кнопку отправки напоминания по почте
        mReportButton = findViewById(R.id.note_report);
        mReportButton.setOnClickListener(v12 -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, mTitleText.getText() + " - " + mDateText.getText() + " - " + mTimeText.getText());
            i = Intent.createChooser(i, getString(R.string.send_report));
            startActivity(i);
        });

        // Обрабатываем работу с картинкой
        if (get(getApplicationContext()).getAllReminders() == null) {
            mReminder = get(getApplicationContext()).getAllReminders().get(mID);
            mPhotoFile = getPhotoFile(mReminder);
        }

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
            //mPhotoView.setImageURI(uri);
            builder.show();
        });

        updatePhotoView();

        setUpColorSetting();
    }

    /**
     * Метод для получения фото с камеры
     * @param reminder напоминание для добавления фото
     * @return директория для фото
     */
    File getPhotoFile(Reminder reminder) {
        File filesDir = getApplicationContext().getFilesDir();
        return new File(filesDir, reminder.getPhotoFilename());
    }

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
        String[] mimeTypes = {"image/jpg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
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
     * Метод для реагирования на изменение прав установки цвета страницы
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String backgroundColour = sharedPreferences.getString(getString(R.string.set_color), getString(R.string.color_default));
        if (backgroundColour.equals(getString(R.string.color_green))) {
            findViewById(R.id.activity_add).setBackgroundColor(getResources().getColor(R.color.colorGreen));
        } else if (backgroundColour.equals(getString(R.string.pink_color))) {
            findViewById(R.id.activity_add).setBackgroundColor(getResources().getColor(R.color.colorPink));
        } else if (backgroundColour.equals(getString(R.string.blue_color))) {
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
     * Метод для получения времени из TimePickerDialog
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
     * Метод для получения даты из DatePickerDialog
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
            mRepeatText.setText(getString(R.string.every) + " " + mRepeatAmount + " " + mRepeatType);
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

        items[0] = getString(R.string.minute);
        items[1] = getString(R.string.hour);
        items[2] = getString(R.string.day);
        items[3] = getString(R.string.week);
        items[4] = getString(R.string.month);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.select_type));
        builder.setItems(items, (dialog, item) -> {

            mRepeatType = items[item];
            mRepeatTypeText.setText(mRepeatType);
            mRepeatText.setText(getString(R.string.every) + " " + mRepeatAmount + " " + mRepeatType);
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Метод для задания интервала повторения напоминания
     */
    public void setRepeatNo(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.enter_number));

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton(getString(R.string.ok),
                (dialog, whichButton) -> {

                    if (input.getText().toString().length() == 0) {
                        mRepeatAmount = Integer.toString(1);
                        mRepeatAmountText.setText(mRepeatAmount);
                        mRepeatText.setText(getString(R.string.every) + " " + mRepeatAmount + " " + mRepeatType);
                    } else {
                        mRepeatAmount = input.getText().toString().trim();
                        mRepeatAmountText.setText(mRepeatAmount);
                        mRepeatText.setText(getString(R.string.every) + " " + mRepeatAmount + " " + mRepeatType);
                    }
                });
        alert.setNegativeButton(getString(R.string.cancel), (dialog, whichButton) -> {
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
                new NotificationReceiver().setRepeatNotification(getApplicationContext(), mCalendar, mID, mRepeatTime);
            } else if (mRepeat.equals("false")) {
                new NotificationReceiver().setNotification(getApplicationContext(), mCalendar, mID);
            }
        }

        // Показываем что напоминание сохранено
        Toast.makeText(getApplicationContext(), getString(R.string.saved), Toast.LENGTH_SHORT).show();

        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save_reminder, menu);
        return true;
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
                    saveReminder();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

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
    public void updatePhotoView() {
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
    void setUpColorSetting() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        String backgroundColour = sharedPreferences.getString(getString(R.string.set_color), getString(R.string.color_default));
        if (backgroundColour.equals(getString(R.string.color_green))) {
            findViewById(R.id.activity_add).setBackgroundColor(getResources().getColor(R.color.colorGreen));
        } else if (backgroundColour.equals(getString(R.string.pink_color))) {
            findViewById(R.id.activity_add).setBackgroundColor(getResources().getColor(R.color.colorPink));
        } else if (backgroundColour.equals(getString(R.string.blue_color))) {
            findViewById(R.id.activity_add).setBackgroundColor(getResources().getColor(R.color.colorBlue));
        } else {
            findViewById(R.id.activity_add).setBackgroundColor(getResources().getColor(R.color.primary_dark));
        }
    }
}