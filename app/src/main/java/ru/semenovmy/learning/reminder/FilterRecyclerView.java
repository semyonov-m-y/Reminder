package ru.semenovmy.learning.reminder;

import android.content.Context;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

public class FilterRecyclerView extends RecyclerViewAdapter implements Filterable {

    RecyclerViewAdapter recyclerViewAdapter;

    FilterRecyclerView(Context context, OnItemClickListener listener) {
        super(context, listener);
        generateListData(getItemCount());
        recyclerViewAdapter = new RecyclerViewAdapter(context, listener);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
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
            getmItems().clear();
            getmItems().addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
