package com.example.firebaseexample;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    // Member Variables
    TextView textTitle, textContent;
    ItemClickListener itemClickListener;

    // Constructors
    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        // Initialize Variables
        textTitle = itemView.findViewById(R.id.text_title);
        textContent = itemView.findViewById(R.id.text_content);
        itemView.setOnClickListener(this);
    }

    // Setter
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition());
    }
}
