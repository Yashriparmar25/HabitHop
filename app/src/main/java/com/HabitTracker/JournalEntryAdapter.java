package com.HabitTracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JournalEntryAdapter extends RecyclerView.Adapter<JournalEntryAdapter.VH> {

    private final List<JournalEntry> list;

    public JournalEntryAdapter(List<JournalEntry> list) {
        this.list = list;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvDate, tvMood, tvSummary, tvGrateful, tvAffirmations, tvReflection, tvWater, tvExpandIcon;
        LinearLayout expandedLayout;

        VH(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_entry_date);
            tvMood = itemView.findViewById(R.id.tv_entry_mood);
            tvSummary = itemView.findViewById(R.id.tv_entry_summary);
            tvGrateful = itemView.findViewById(R.id.tv_grateful);
            tvAffirmations = itemView.findViewById(R.id.tv_affirmations);
            tvReflection = itemView.findViewById(R.id.tv_reflection);
            tvWater = itemView.findViewById(R.id.tv_water);
            tvExpandIcon = itemView.findViewById(R.id.tv_expand_icon);
            expandedLayout = itemView.findViewById(R.id.expanded_layout);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_journal_entry, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        JournalEntry entry = list.get(position);

        holder.tvDate.setText(entry.date + " • " + entry.time);
        holder.tvMood.setText("Mood: " + entry.mood);
        holder.tvSummary.setText("Tap to view full entry");

        holder.tvGrateful.setText("Grateful:\n1. " + safe(entry.grateful1) + "\n2. " + safe(entry.grateful2) + "\n3. " + safe(entry.grateful3));
        holder.tvAffirmations.setText("Affirmations:\n1. " + safe(entry.affirmation1) + "\n2. " + safe(entry.affirmation2) + "\n3. " + safe(entry.affirmation3));
        holder.tvReflection.setText("Reflection:\nWent well: " + safe(entry.wentWell) +
                "\nImprove: " + safe(entry.improve) +
                "\nNotes: " + safe(entry.notes) +
                "\nTomorrow: " + safe(entry.tomorrow));
        holder.tvWater.setText("Water: " + entry.waterCount + " / 8 glasses");

        boolean expanded = holder.expandedLayout.getVisibility() == View.VISIBLE;

        holder.expandedLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);
        holder.tvExpandIcon.setText(expanded ? "▲" : "▼");

        holder.itemView.setOnClickListener(v -> {
            boolean isVisible = holder.expandedLayout.getVisibility() == View.VISIBLE;
            holder.expandedLayout.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            holder.tvExpandIcon.setText(isVisible ? "▼" : "▲");
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String safe(String s) {
        return (s == null || s.trim().isEmpty()) ? "-" : s.trim();
    }
}