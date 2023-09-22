package com.mvm.mibiblio.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mvm.mibiblio.AuthorComparator;
import com.mvm.mibiblio.R;
import com.mvm.mibiblio.TitleComparator;
import com.mvm.mibiblio.models.BookModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class CollectionBookAdapter extends RecyclerView.Adapter<CollectionBookAdapter.ViewHolder> {
    private Cursor cursor;
    private Context context;
    private OnItemSelect listener;
    private ArrayList<BookModel> contents;
    private ArrayList<Object> objects;
    private boolean showCover;

    private final int VIEW_EMPTY = 0;
    private final int VIEW_ITEM = 1;
    private final int VIEW_SORT = 2;

    public interface OnItemSelect {
        void onSelect(BookModel model);
        void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo info, BookModel book);
    }

    class SortObject {
        private char sortCharacter;

        public SortObject(char sortCharacter) {
            this.sortCharacter = sortCharacter;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        BookModel book;
        TextView title;
        TextView author;
        ImageView cover;
        View root;
        TextView sortText;
        int viewType;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            if(viewType == VIEW_ITEM) {
                this.root = itemView.getRootView();
                this.title = itemView.findViewById(R.id.textTitle);
                this.author = itemView.findViewById(R.id.textAuthor);
                this.cover = itemView.findViewById(R.id.imageCover);
                this.root.setOnCreateContextMenuListener(this);
            } else {
                this.sortText = itemView.findViewById(R.id.sortText);
            }
        }

        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo info) {

            CollectionBookAdapter.this.listener.onCreateContextMenu(menu, v, info, this.book);
        }
    }

    public CollectionBookAdapter(Cursor cursor, Context context, OnItemSelect listener) {
        this.cursor = cursor;
        this.context = context;
        this.listener = listener;
        this.contents = new ArrayList<>();

        SharedPreferences prefs = context.getSharedPreferences("mibiblio", MODE_PRIVATE);
        this.showCover = prefs.getBoolean("show_cover", false);
        Log.d("mibiblio", "show cover = " + this.showCover);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            BookModel book = BookModel.fromCursor(cursor);
            contents.add(book);
        }

        String sort_field = prefs.getString("sort_field", "title");
        if(sort_field.equalsIgnoreCase("title")) {
            Collections.sort(this.contents, new TitleComparator());
        } else if(sort_field.equalsIgnoreCase("author")) {
            Collections.sort(this.contents, new AuthorComparator());
        }

        char sortCharacter = '\0';

        this.objects = new ArrayList<Object>();
        for(BookModel b : this.contents) {
            char newSortChar;

            if(sort_field.equalsIgnoreCase("title")) {
                newSortChar = TitleComparator.getSortCharacter(b);
            } else {
                newSortChar = AuthorComparator.getSortCharacter(b);
            }

            if(newSortChar != sortCharacter) {
                this.objects.add(new SortObject(newSortChar));
                sortCharacter = newSortChar;
            }

            this.objects.add(b);
        }
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(this.context).inflate(R.layout.book_entry_fragment, parent, false);
            return new CollectionBookAdapter.ViewHolder(v, VIEW_ITEM);
        } else if(viewType == VIEW_SORT) {
            View v = LayoutInflater.from(context).inflate(R.layout.fragment_sortholder, parent, false);
            return new CollectionBookAdapter.ViewHolder(v, VIEW_SORT);
        } else {
                View v = LayoutInflater.from(this.context).inflate(R.layout.layout_nobook, parent, false);
                return new ViewHolder(v, VIEW_EMPTY);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(this.objects.size() == 0)
            return;

        if(holder.viewType == VIEW_ITEM) {
            final BookModel book = (BookModel)objects.get(position);

            holder.title.setText(book.getTitle());
            holder.author.setText(book.getAuthor());
            Bitmap bitmapCover = book.getCover();
            if (bitmapCover != null) {
                holder.cover.setImageBitmap(bitmapCover);
            }
            holder.book = book;

            if (showCover == false || book.getCover() == null)
                holder.cover.setVisibility(View.GONE);

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CollectionBookAdapter.this.listener.onSelect(book);
                }
            });
        } else if(holder.viewType == VIEW_SORT) {
            SortObject sort = (SortObject)this.objects.get(position);
            holder.sortText.setText(String.valueOf(sort.sortCharacter));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(this.contents.size() == 0)
            return VIEW_EMPTY;
        else {
            if(this.objects.get(position) instanceof SortObject) {
                return VIEW_SORT;
            } else {
                return VIEW_ITEM;
            }
        }
    }

    @Override
    public int getItemCount() {

        if(this.objects.size() != 0) return this.objects.size();
        else {
            return 1;
        }
    }


    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        if(!this.cursor.isClosed()) this.cursor.close();
    }

}
