package ru.semenovmy.learning.reminder;

/**
 * Модель для отображения DateTimeSorter данных
 *
 * @author Maxim Semenov on 2019-11-15
 */
class DateTimeSorter {

    private final int mIndex;
    private final String mDateTime;

    DateTimeSorter(int index, String DateTime) {
        this.mIndex = index;
        this.mDateTime = DateTime;
    }

    int getIndex() {
        return mIndex;
    }

    String getDateTime() {
        return mDateTime;
    }
}
