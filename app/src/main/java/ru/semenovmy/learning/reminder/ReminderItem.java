package ru.semenovmy.learning.reminder;

/**
 * Класс для элементов recycler view
 *
 * @author Maxim Semenov on 2019-11-15
 */
class ReminderItem {

    public final String mTitle;
    public final String mDateTime;
    public final String mRepeat;
    public final String mRepeatNo;
    public final String mRepeatType;
    public final String mActive;

    public ReminderItem(String Title, String DateTime, String Repeat, String RepeatNo, String RepeatType, String Active) {
        this.mTitle = Title;
        this.mDateTime = DateTime;
        this.mRepeat = Repeat;
        this.mRepeatNo = RepeatNo;
        this.mRepeatType = RepeatType;
        this.mActive = Active;
    }
}
