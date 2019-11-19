package ru.semenovmy.learning.reminder;

/**
 * Модель для отображения данных TitleSorter
 *
 * @author Maxim Semenov on 2019-11-15
 */
class TitleSorter {

    private final int mIndex;
    private final String mTitle;

    TitleSorter(int mIndex, String mTitle) {
        this.mIndex = mIndex;
        this.mTitle = mTitle;
    }

    int getIndex() {
        return mIndex;
    }

    String getTitle() {
        return mTitle;
    }
}
