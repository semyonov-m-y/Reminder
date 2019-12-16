package ru.semenovmy.learning.reminder;

import android.content.Context;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import ru.semenovmy.learning.reminder.data.model.ReminderItem;

/**
 * Класс для фильтрации Recycler View
 *
 * @author Maxim Semenov on 2019-11-15
 */
class FilterRecyclerView extends RecyclerViewAdapter implements Filterable {

    FilterRecyclerView(Context context, OnItemClickListener listener) {
        super(context, listener);
        getListData(getItemCount());
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ReminderItem> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(getReminderItemsFull());
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ReminderItem item : getReminderItemsFull()) {
                    if (item.mTitle.toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            getItems().clear();
            getItems().addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
