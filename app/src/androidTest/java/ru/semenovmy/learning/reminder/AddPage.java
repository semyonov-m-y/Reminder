package ru.semenovmy.learning.reminder;

import androidx.test.espresso.ViewInteraction;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

class AddPage {

    private final ViewInteraction mTitleText = onView(withId(R.id.reminder_title));
    private final ViewInteraction mSaveButton = onView(withId(R.id.save_reminder));
    private final ViewInteraction mPhotoButton = onView(withId(R.id.note_camera));
    private final ViewInteraction mDate = onView(withId(R.id.date_icon));
    private final ViewInteraction mTime = onView(withId(R.id.time_icon));
    private final ViewInteraction mRepeatSwitch = onView(withId(R.id.repeat_switch));
    private final ViewInteraction mRepeatNo = onView(withId(R.id.repeat_no_icon));
    private final ViewInteraction mRepeatType = onView(withId(R.id.repeat_type_icon));

    ViewInteraction getTitleText() {
        return mTitleText;
    }

    ViewInteraction getSaveButton() {
        return mSaveButton;
    }

    ViewInteraction getPhotoButton() {
        return mPhotoButton;
    }

    ViewInteraction getDate() {
        return mDate;
    }

    ViewInteraction getTime() {
        return mTime;
    }

    ViewInteraction getRepeatSwitch() {
        return mRepeatSwitch;
    }

    ViewInteraction getRepeatNo() {
        return mRepeatNo;
    }

    ViewInteraction getRepeatType() {
        return mRepeatType;
    }
}
