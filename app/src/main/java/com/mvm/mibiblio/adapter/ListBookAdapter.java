package com.mvm.mibiblio.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mvm.mibiblio.R;
import com.mvm.mibiblio.models.BookModel;

import java.util.LinkedList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

public class ListBookAdapter extends RecyclerView.Adapter<ListBookAdapter.ViewHolder> {
    private List<BookModel> contents;
    private OnSelectListener listener;
    private Context context;
    private boolean showCover;

    private final int VIEW_EMPTY = 0;
    private final int VIEW_ITEM = 1;

    class ViewHolder extends RecyclerView.ViewHolder {

        BookModel book;
        TextView title;
        TextView author;
        ImageView cover;
        View root;

        public ViewHolder(View itemView) {
            super(itemView);
            this.root = itemView.getRootView();
            this.title = itemView.findViewById(R.id.textTitle);
            this.author = itemView.findViewById(R.id.textAuthor);
            this.cover = itemView.findViewById(R.id.imageCover);
        }
    }

    public interface OnSelectListener {
        void onSelect(BookModel book);
    }

    public ListBookAdapter(Context context, List<BookModel> contents, OnSelectListener listener) {
        this.context = context;

        if(contents != null)
            this.contents = contents;
        else
            this.contents = new LinkedList<>();

        SharedPreferences prefs = context.getSharedPreferences("mibiblio", MODE_PRIVATE);
        this.showCover = prefs.getBoolean("show_cover", false);

        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(context).inflate(R.layout.book_entry_fragment, parent, false);
            return new ViewHolder(v);
        } else {
            View v = LayoutInflater.from(context).inflate(R.layout.layout_nobook, parent, false);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if(this.contents.size() == 0) {
            return;
        }
        final BookModel book = this.contents.get(position);

        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        holder.book = book;
        Bitmap bitmapCover = book.getCover();

        if(bitmapCover != null && showCover) {
            holder.cover.setImageBitmap(bitmapCover);
        } else {
            holder.cover.setVisibility(GONE);
        }

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelect(holder.book);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if(this.contents.size() == 0) {
            return VIEW_EMPTY;
        } else
            return VIEW_ITEM;
    }

    @Override
    public int getItemCount() {
        if(contents.size() != 0)
            return contents.size();
        else
            return 1;
    }
}
