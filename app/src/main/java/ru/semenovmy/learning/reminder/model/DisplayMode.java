package ru.semenovmy.learning.reminder.model;

import androidx.annotation.StringRes;

import ru.semenovmy.learning.reminder.R;

/**
 * Список элементов для Spinner
 *
 * @author Maxim Semenov on 2019-11-15
 */
public enum DisplayMode {

    GROUP_BY_DATE_DESC(R.string.group_by_date_desc),
    GROUP_BY_DATE_ASC(R.string.group_by_date_asc),
    GROUP_BY_TITLE_ASC(R.string.group_by_title_asc),
    GROUP_BY_TITLE_DESC(R.string.group_by_title_desc);

    private final int mTitleStringResourceId;

    DisplayMode(@StringRes int titleStringResourceId) {
        mTitleStringResourceId = titleStringResourceId;
    }

    public int getTitleStringResourceId() {
        return mTitleStringResourceId;
    }
}
