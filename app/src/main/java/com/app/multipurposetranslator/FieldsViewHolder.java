package com.app.multipurposetranslator;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FieldsViewHolder extends RecyclerView.ViewHolder {
    View view;

    public FieldsViewHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;

    }

    public void setSavedWords(String words) {
        TextView textView = view.findViewById(R.id.wordText);
        textView.setText(words);
    }
}
