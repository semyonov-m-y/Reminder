package ru.semenovmy.learning.reminder;

import androidx.test.espresso.ViewInteraction;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class AddPage {

    ViewInteraction mTitleText = onView(withId(R.id.reminder_title));
    ViewInteraction mSaveButton = onView(withId(R.id.save_reminder));
    ViewInteraction mPhotoButton = onView(withId(R.id.note_camera));
    ViewInteraction mDate = onView(withId(R.id.date_icon));
    ViewInteraction mTime = onView(withId(R.id.time_icon));
    ViewInteraction mRepeatSwitch = onView(withId(R.id.repeat_switch));
    ViewInteraction mRepeatNo = onView(withId(R.id.repeat_no_icon));
    ViewInteraction mRepeatType = onView(withId(R.id.repeat_type_icon));
    ViewInteraction mReport = onView(withId(R.id.note_report));

    public ViewInteraction getmTitleText() {
        return mTitleText;
    }

    public ViewInteraction getmSaveButton() {
        return mSaveButton;
    }

    public ViewInteraction getmPhotoButton() {
        return mPhotoButton;
    }

    public ViewInteraction getmDate() {
        return mDate;
    }

    public ViewInteraction getmTime() {
        return mTime;
    }

    public ViewInteraction getmRepeatSwitch() {
        return mRepeatSwitch;
    }

    public ViewInteraction getmRepeatNo() {
        return mRepeatNo;
    }

    public ViewInteraction getmRepeatType() {
        return mRepeatType;
    }

    public ViewInteraction getmReport() {
        return mReport;
    }
}
