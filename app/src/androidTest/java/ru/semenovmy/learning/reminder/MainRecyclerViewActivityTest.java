package ru.semenovmy.learning.reminder;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

@RunWith(AndroidJUnit4.class)
public class MainRecyclerViewActivityTest {

    MainPage mainPage = new MainPage();

    @Rule
    public ActivityTestRule<MainRecyclerViewActivity> activityTestRule = new ActivityTestRule<>(MainRecyclerViewActivity.class);

    @Test
    public void checkAddButton() {
        mainPage.getmAddReminderButton().check(matches(isDisplayed()));
        mainPage.getmAddReminderButton().perform(click());
    }
}