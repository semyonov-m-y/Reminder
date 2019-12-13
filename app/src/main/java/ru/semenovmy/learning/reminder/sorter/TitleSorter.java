package ru.semenovmy.learning.reminder.sorter;

/**
 * Модель для отображения данных TitleSorter
 *
 * @author Maxim Semenov on 2019-11-15
 */
public class TitleSorter {

    private final int mIndex;
    private final String mTitle;

    public TitleSorter(int mIndex, String mTitle) {
        this.mIndex = mIndex;
        this.mTitle = mTitle;
    }

    public int getIndex() {
        return mIndex;
    }

    public String getTitle() {
        return mTitle;
    }
}
