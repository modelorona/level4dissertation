package com.anguel.dissertation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anguel.dissertation.R;
import com.anguel.dissertation.persistence.database.logevent.LogEvent;

import java.util.List;

public class LogEventAdapter extends RecyclerView.Adapter<LogEventAdapter.ViewHolder> {
    private List<LogEvent> dataSet;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View textView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StringBuilder s = new StringBuilder();
//        s.append(new Date(dataSet.get(position).getTimestamp()).toString());
        s.append(System.lineSeparator());
        s.append(dataSet.get(position).getData().toString());
        s.append(System.lineSeparator());
        s.trimToSize();
        holder.textView.setText(s.toString());
    }

    public LogEventAdapter(List<LogEvent> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.text_view);
        }
    }
}
