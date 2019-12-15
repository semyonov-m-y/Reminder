package ru.semenovmy.learning.reminder;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import ru.semenovmy.learning.reminder.data.model.ReminderItem;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainRecyclerViewActivityTest {

    private FilterRecyclerView filterRecyclerView;
    private MainRecyclerViewActivity mainRecyclerViewActivity;
    @Mock
    private Context context;
    @Mock
    private OnItemClickListener onItemClickListener;
    private String mDateTime1 = "charCode1";
    private String mRepeatType1 = "Hour";
    private String mDateTime2 = "charCode2";
    private String mRepeatType2 = "Week";
    private String mRepeat1 = "once";
    private String mRepeat2 = "twice";
    private String active = "true";
    private List<ReminderItem> mItems;

    @Before
    public void setUp() {
        filterRecyclerView = new FilterRecyclerView(context, onItemClickListener);
        mainRecyclerViewActivity = new MainRecyclerViewActivity();
        String mTitle1 = "id1";
        String mRepeatNo1 = "1";
        String mTitle2 = "id2";
        String mRepeatNo2 = "2";
        mItems = Arrays.asList(
                new ReminderItem(
                        mTitle1,
                        mDateTime1,
                        mRepeat1,
                        mRepeatNo1,
                        mRepeatType1,
                        active
                ),
                new ReminderItem(
                        mTitle2,
                        mDateTime2,
                        mRepeat2,
                        mRepeatNo2,
                        mRepeatType2,
                        active
                )
        );
    }

    @Test
    public void testPerformFiltering() {
        boolean output = mainRecyclerViewActivity.onQueryTextChange("1");
        when(output).thenReturn(true);
    }

    @Test
    public void testMethodsCalling() {
        verify(mainRecyclerViewActivity).createRecyclerView();
        verify(mainRecyclerViewActivity).getDefaultItemCount();
        verify(mainRecyclerViewActivity).getLayoutManager();
        verify(mainRecyclerViewActivity).setUpDefaultSetting();
    }
}