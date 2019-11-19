package ru.semenovmy.learning.reminder;

/**
 * Модель для получения напоминания
 *
 * @author Maxim Semenov on 2019-11-15
 */
public class Reminder {

    private int mID;
    private String mTitle;
    private String mDate;
    private String mTime;
    private String mRepeat;
    private String mRepeatAmount;
    private String mRepeatType;
    private String mActive;

    public Reminder(int ID, String Title, String Date, String Time, String Repeat, String RepeatAmount, String RepeatType, String Active){
        mID = ID;
        mTitle = Title;
        mDate = Date;
        mTime = Time;
        mRepeat = Repeat;
        mRepeatAmount = RepeatAmount;
        mRepeatType = RepeatType;
        mActive = Active;
    }

    public Reminder(String Title, String Date, String Time, String Repeat, String RepeatAmount, String RepeatType, String Active){
        mTitle = Title;
        mDate = Date;
        mTime = Time;
        mRepeat = Repeat;
        mRepeatAmount = RepeatAmount;
        mRepeatType = RepeatType;
        mActive = Active;
    }

    public Reminder(){}

    public int getID() {
        return mID;
    }

    public void setID(int ID) {
        mID = ID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getRepeatType() {
        return mRepeatType;
    }

    public void setRepeatType(String repeatType) {
        mRepeatType = repeatType;
    }

    public String getRepeatAmount() {
        return mRepeatAmount;
    }

    public void setRepeatAmount(String repeatAmount) {
        mRepeatAmount = repeatAmount;
    }

    public String getRepeat() {
        return mRepeat;
    }

    public void setRepeat(String repeat) {
        mRepeat = repeat;
    }

    public String getActive() {
        return mActive;
    }

    public void setActive(String active) {
        mActive = active;
    }

    public String getPhotoFilename() {
        return "IMG_" + getID() + ".jpg";
    }
}
