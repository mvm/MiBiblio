package com.mvm.mibiblio.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mvm.mibiblio.R;
import com.mvm.mibiblio.models.LoanModel;

public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.ViewHolder> {

    private final Cursor cursor;

    public LoanAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_loan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        this.cursor.moveToPosition(position);
        holder.item = LoanModel.fromCursor(this.cursor);
        holder.textDate.setText(holder.item.getDate().toString());
        holder.textName.setText(holder.item.getName());
        holder.textComments.setText(holder.item.getComments());
    }

    @Override
    public int getItemCount() {
        return this.cursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public TextView textName;
        public TextView textComments;
        public TextView textDate;

        public LoanModel item;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            textDate = view.findViewById(R.id.textDate);
            textName = view.findViewById(R.id.fieldName);
            textComments = view.findViewById(R.id.textComments);
        }
    }
}
