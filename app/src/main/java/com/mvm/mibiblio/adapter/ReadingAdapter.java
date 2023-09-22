package com.mvm.mibiblio.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mvm.mibiblio.R;
import com.mvm.mibiblio.ui.ReadingFragment.OnListFragmentInteractionListener;
import com.mvm.mibiblio.models.ReadingModel;

public class ReadingAdapter extends RecyclerView.Adapter<ReadingAdapter.ViewHolder> {

    private final Cursor cursor;
    private final OnListFragmentInteractionListener mListener;

    public ReadingAdapter(Cursor cursor, OnListFragmentInteractionListener listener) {
        this.cursor = cursor;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_reading, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        this.cursor.moveToPosition(position);

        holder.item = ReadingModel.fromCursor(cursor);
        if(holder.item.getDate() != null)
            holder.mDateView.setText(holder.item.getDate().toString());

        if(!holder.item.getComments().isEmpty())
            holder.mCommentsView.setText(holder.item.getComments());
        else
            holder.mCommentsView.setText("---");

        holder.rating.setRating(holder.item.getRating()*0.5f);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mDateView;
        public final TextView mCommentsView;
        public final RatingBar rating;
        public ReadingModel item;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDateView = (TextView) view.findViewById(R.id.textDate);
            mCommentsView = (TextView) view.findViewById(R.id.textComments);
            rating = view.findViewById(R.id.ratingBar);
            item = null;
        }

    }
}
