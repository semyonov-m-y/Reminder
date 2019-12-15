package ru.semenovmy.learning.reminder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import ru.semenovmy.learning.reminder.data.database.Reminder;

import static org.mockito.Mockito.verify;

public class ReminderEditActivityTest {

    @Mock
    private Reminder reminder;
    private ReminderEditActivity reminderEditActivity;

    @Before
    public void setUp() {
        reminderEditActivity = Mockito.mock(ReminderEditActivity.class);
    }

    @Test
    public void testMethodsCalling() {
        verify(reminderEditActivity).updatePhotoView();
        verify(reminderEditActivity).getPhotoFile(reminder);
    }
}