package ru.semenovmy.learning.reminder;

import androidx.test.espresso.ViewInteraction;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

class MainPage {

    private final ViewInteraction mAddReminderButton = onView(withId(R.id.add_reminder));

    ViewInteraction getAddReminderButton() {
        return mAddReminderButton;
    }
}
