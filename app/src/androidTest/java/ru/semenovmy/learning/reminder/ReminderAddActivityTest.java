package ru.semenovmy.learning.reminder;

import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ReminderAddActivityTest {

    AddPage addPage = new AddPage();
    MainRecyclerViewActivityTest mainRecyclerViewActivityTest = new MainRecyclerViewActivityTest();

    @Rule
    public ActivityTestRule<MainRecyclerViewActivity> activityTestRule = new ActivityTestRule<>(MainRecyclerViewActivity.class);

    @Test
    public void checkEditTextAndSaveButton() {
        mainRecyclerViewActivityTest.checkAddButton();

        String blank = "";

        addPage.getmTitleText().perform(typeText(blank));
        addPage.getmSaveButton().perform(click());
        addPage.getmTitleText().perform(click());

        String first_name = InstrumentationRegistry.getTargetContext().getString(R.string.create_reminder_first);

        addPage.getmTitleText().perform(typeText(first_name));
        addPage.getmSaveButton().perform(click());
    }

    @Test
    public void checkImageButtonAndText() {
        mainRecyclerViewActivityTest.checkAddButton();

        addPage.getmPhotoButton().perform(click());

        String create = InstrumentationRegistry.getTargetContext().getString(R.string.create_reminder_first);
        onView(withText(create)).check(matches(isDisplayed()));
    }

    @Test
    public void checkDate() {
        mainRecyclerViewActivityTest.checkAddButton();

        addPage.getmDate().perform(click());

        String ok = InstrumentationRegistry.getTargetContext().getString(R.string.ok).toUpperCase();
        onView(withText(ok)).check(matches(isDisplayed())).perform(click());

        addPage.getmDate().perform(click());

        String cancel = InstrumentationRegistry.getTargetContext().getString(R.string.cancel).toUpperCase();
        onView(withText(cancel)).check(matches(isDisplayed())).perform(click());
    }

    @Test
    public void checkTime() {
        mainRecyclerViewActivityTest.checkAddButton();

        addPage.getmTime().perform(click());

        String ok = InstrumentationRegistry.getTargetContext().getString(R.string.ok).toUpperCase();
        onView(withText(ok)).check(matches(isDisplayed())).perform(click());

        addPage.getmTime().perform(click());

        String cancel = InstrumentationRegistry.getTargetContext().getString(R.string.cancel).toUpperCase();
        onView(withText(cancel)).check(matches(isDisplayed())).perform(click());
    }

    @Test
    public void checkRepeatSwitch() {
        mainRecyclerViewActivityTest.checkAddButton();

        addPage.getmRepeatSwitch().perform(click());

        String once = InstrumentationRegistry.getTargetContext().getString(R.string.repeat_off);
        onView(withText(once)).check(matches(isDisplayed())).perform(click());

        addPage.getmRepeatSwitch().perform(click());

        String onceInHour = InstrumentationRegistry.getTargetContext().getString(R.string.every) + " " + 1
                + " " + InstrumentationRegistry.getTargetContext().getString(R.string.hour);
        onView(withText(onceInHour)).check(matches(isDisplayed())).perform(click());
    }

    @Test
    public void checkRepeatNo() {
        mainRecyclerViewActivityTest.checkAddButton();

        addPage.getmRepeatNo().perform(click());

        String enterNum = InstrumentationRegistry.getTargetContext().getString(R.string.enter_number);
        onView(withText(enterNum)).check(matches(isDisplayed()));

        String two = "2";
        onView(withText("1")).perform(typeText(two));

        String ok = InstrumentationRegistry.getTargetContext().getString(R.string.cancel).toUpperCase();
        onView(withText(ok)).check(matches(isDisplayed())).perform(click());

        addPage.getmRepeatNo().perform(click());

        String cancel = InstrumentationRegistry.getTargetContext().getString(R.string.ok).toUpperCase();
        onView(withText(cancel)).check(matches(isDisplayed())).perform(click());
    }

    @Test
    public void checkRepeatType() {
        mainRecyclerViewActivityTest.checkAddButton();

        onView(ViewMatchers.withId(R.id.scroll_view)).perform(ViewActions.swipeUp());

        addPage.getmRepeatType().perform(click());

        String selectType = InstrumentationRegistry.getTargetContext().getString(R.string.select_type);
        onView(withText(selectType)).check(matches(isDisplayed()));

        String minute = InstrumentationRegistry.getTargetContext().getString(R.string.minute);
        onView(withText(minute)).check(matches(isDisplayed())).perform(click());

        addPage.getmRepeatType().perform(click());

        String hour = InstrumentationRegistry.getTargetContext().getString(R.string.hour);
        onView(withText(hour)).check(matches(isDisplayed())).perform(click());

        addPage.getmRepeatType().perform(click());

        String day = InstrumentationRegistry.getTargetContext().getString(R.string.day);
        onView(withText(day)).check(matches(isDisplayed())).perform(click());

        addPage.getmRepeatType().perform(click());

        String week = InstrumentationRegistry.getTargetContext().getString(R.string.week);
        onView(withText(week)).check(matches(isDisplayed())).perform(click());
    }

    @Test
    public void checkReportButton() {
        mainRecyclerViewActivityTest.checkAddButton();

        onView(ViewMatchers.withId(R.id.scroll_view)).perform(ViewActions.swipeUp());

        String report = InstrumentationRegistry.getTargetContext().getString(R.string.report);
        onView(withText(report)).check(matches(isDisplayed())).perform(click());
    }
}