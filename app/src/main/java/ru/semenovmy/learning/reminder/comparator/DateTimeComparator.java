package ru.semenovmy.learning.reminder.comparator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Locale;

import ru.semenovmy.learning.reminder.sorter.DateTimeSorter;

/**
 * Класс для сравнения даты и времени, чтобы элементы сортировались в убывающем порядке
 *
 * @author Maxim Semenov on 2019-11-15
 */
public class DateTimeComparator implements Comparator {

    private final DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy hh:mm", Locale.US);

    public int compare(Object firstObject, Object secondObject) {
        String object1 = ((DateTimeSorter) firstObject).getDateTime();
        String object2 = ((DateTimeSorter) secondObject).getDateTime();

        try {
            return dateFormat.parse(object2).compareTo(dateFormat.parse(object1));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
