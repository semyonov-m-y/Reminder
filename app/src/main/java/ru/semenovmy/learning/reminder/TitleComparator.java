package ru.semenovmy.learning.reminder;

import java.util.Comparator;

/**
 * Класс для сравнения заголовков, чтобы элементы списка были отсортированы в возрастающем порядке
 *
 * @author Maxim Semenov on 2019-11-15
 */
class TitleComparator implements Comparator {

    @Override
    public int compare(Object o1, Object o2) {
        String a = ((TitleSorter) o1).getTitle();
        String b = ((TitleSorter) o2).getTitle();
        return a.compareTo(b);
    }
}
