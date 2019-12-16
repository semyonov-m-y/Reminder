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

    private final AddPage addPage = new AddPage();
    private final MainRecyclerViewActivityTest mainRecyclerViewActivityTest = new MainRecyclerViewActivityTest();

    @Rule
    public ActivityTestRule<MainRecyclerViewActivity> activityTestRule = new ActivityTestRule<>(MainRecyclerViewActivity.class);

    @Test
    public void checkEditTextAndSaveButton() {
        mainRecyclerViewActivityTest.checkAddButton();

        String blank = "";

        addPage.getTitleText().perform(typeText(blank));
        addPage.getSaveButton().perform(click());
        addPage.getTitleText().perform(click());

        String first_name = InstrumentationRegistry.getTargetContext().getString(R.string.create_reminder_first);

        addPage.getTitleText().perform(typeText(first_name));
        addPage.getSaveButton().perform(click());
    }

    @Test
    public void checkImageButtonAndText() {
        mainRecyclerViewActivityTest.checkAddButton();

        addPage.getPhotoButton().perform(click());

        String create = InstrumentationRegistry.getTargetContext().getString(R.string.create_reminder_first);
        onView(withText(create)).check(matches(isDisplayed()));
    }

    @Test
    public void checkDate() {
        mainRecyclerViewActivityTest.checkAddButton();

        addPage.getDate().perform(click());

        String ok = InstrumentationRegistry.getTargetContext().getString(R.string.ok).toUpperCase();
        onView(withText(ok)).check(matches(isDisplayed())).perform(click());

        addPage.getDate().perform(click());

        String cancel = InstrumentationRegistry.getTargetContext().getString(R.string.cancel).toUpperCase();
        onView(withText(cancel)).check(matches(isDisplayed())).perform(click());
    }

    @Test
    public void checkTime() {
        mainRecyclerViewActivityTest.checkAddButton();

        addPage.getTime().perform(click());

        String ok = InstrumentationRegistry.getTargetContext().getString(R.string.ok).toUpperCase();
        onView(withText(ok)).check(matches(isDisplayed())).perform(click());

        addPage.getTime().perform(click());

        String cancel = InstrumentationRegistry.getTargetContext().getString(R.string.cancel).toUpperCase();
        onView(withText(cancel)).check(matches(isDisplayed())).perform(click());
    }

    @Test
    public void checkRepeatSwitch() {
        mainRecyclerViewActivityTest.checkAddButton();

        addPage.getRepeatSwitch().perform(click());

        String once = InstrumentationRegistry.getTargetContext().getString(R.string.repeat_off);
        onView(withText(once)).check(matches(isDisplayed())).perform(click());

        addPage.getRepeatSwitch().perform(click());

        String onceInHour = InstrumentationRegistry.getTargetContext().getString(R.string.every) + " " + 1
                + " " + InstrumentationRegistry.getTargetContext().getString(R.string.hour);
        onView(withText(onceInHour)).check(matches(isDisplayed())).perform(click());
    }

    @Test
    public void checkRepeatNo() {
        mainRecyclerViewActivityTest.checkAddButton();

        addPage.getRepeatNo().perform(click());

        String enterNum = InstrumentationRegistry.getTargetContext().getString(R.string.enter_number);
        onView(withText(enterNum)).check(matches(isDisplayed()));

        String two = "2";
        onView(withText("1")).perform(typeText(two));

        String ok = InstrumentationRegistry.getTargetContext().getString(R.string.cancel).toUpperCase();
        onView(withText(ok)).check(matches(isDisplayed())).perform(click());

        addPage.getRepeatNo().perform(click());

        String cancel = InstrumentationRegistry.getTargetContext().getString(R.string.ok).toUpperCase();
        onView(withText(cancel)).check(matches(isDisplayed())).perform(click());
    }

    @Test
    public void checkRepeatType() {
        mainRecyclerViewActivityTest.checkAddButton();

        onView(ViewMatchers.withId(R.id.scroll_view)).perform(ViewActions.swipeUp());

        addPage.getRepeatType().perform(click());

        String selectType = InstrumentationRegistry.getTargetContext().getString(R.string.select_type);
        onView(withText(selectType)).check(matches(isDisplayed()));

        String minute = InstrumentationRegistry.getTargetContext().getString(R.string.minute);
        onView(withText(minute)).check(matches(isDisplayed())).perform(click());

        addPage.getRepeatType().perform(click());

        String hour = InstrumentationRegistry.getTargetContext().getString(R.string.hour);
        onView(withText(hour)).check(matches(isDisplayed())).perform(click());

        addPage.getRepeatType().perform(click());

        String day = InstrumentationRegistry.getTargetContext().getString(R.string.day);
        onView(withText(day)).check(matches(isDisplayed())).perform(click());

        addPage.getRepeatType().perform(click());

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