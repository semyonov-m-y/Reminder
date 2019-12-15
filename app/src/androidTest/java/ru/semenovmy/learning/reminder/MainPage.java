package ru.semenovmy.learning.reminder;

import androidx.test.espresso.ViewInteraction;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

class MainPage {

    ViewInteraction mAddReminderButton = onView(withId(R.id.add_reminder));
    ViewInteraction mFilterRecyclerView = onView(withId(R.id.reminder_list));

    public ViewInteraction getmAddReminderButton() {
        return mAddReminderButton;
    }

    public ViewInteraction getmFilterRecyclerView() {
        return mFilterRecyclerView;
    }
}
