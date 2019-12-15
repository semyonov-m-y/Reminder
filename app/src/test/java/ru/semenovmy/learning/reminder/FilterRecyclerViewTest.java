package ru.semenovmy.learning.reminder;

import android.content.Context;
import android.widget.Filter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import ru.semenovmy.learning.reminder.data.model.ReminderItem;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FilterRecyclerViewTest {

    private FilterRecyclerView filterRecyclerView;
    @Mock
    private Context context;
    @Mock
    private Filter filter;
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
        when(filterRecyclerView.getFilter()).thenReturn(filter);
    }

    @Test
    public void testMethodsCalling() {
        verify(filterRecyclerView).getFilter();
    }
}