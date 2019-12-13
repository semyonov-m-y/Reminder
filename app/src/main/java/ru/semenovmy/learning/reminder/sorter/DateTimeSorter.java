package ru.semenovmy.learning.reminder.sorter;

/**
 * Модель для отображения DateTimeSorter данных
 *
 * @author Maxim Semenov on 2019-11-15
 */
public class DateTimeSorter {

    private final int mIndex;
    private final String mDateTime;

    public DateTimeSorter(int index, String DateTime) {
        this.mIndex = index;
        this.mDateTime = DateTime;
    }

    public int getIndex() {
        return mIndex;
    }

    public String getDateTime() {
        return mDateTime;
    }
}
