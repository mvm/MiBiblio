package com.mvm.mibiblio.ui.booklist;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mvm.mibiblio.adapter.CollectionBookAdapter;
import com.mvm.mibiblio.CollectionDbHelper;
import com.mvm.mibiblio.MiBiblioApplication;
import com.mvm.mibiblio.R;

public class BookListFragment extends Fragment {

    private View root;
    private CollectionBookAdapter.OnItemSelect listener;
    private RecyclerView bookList;
    private Cursor cursor;

    public static BookListFragment newInstance(CollectionBookAdapter.OnItemSelect listener,
                                               Cursor cursor) {
        return new BookListFragment(listener, cursor);
    }

    private BookListFragment(CollectionBookAdapter.OnItemSelect listener, Cursor cursor) {
        this.listener = listener;
        this.cursor = cursor;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.book_list_fragment, container, false);
        this.root = v;
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        CollectionBookAdapter adapter;

        super.onActivityCreated(savedInstanceState);

        this.bookList = this.root.findViewById(R.id.bookListRecycler);

        bookList.setLayoutManager(new LinearLayoutManager(this.getContext()));

        if(this.cursor == null) {
            MiBiblioApplication application = (MiBiblioApplication) this.getActivity().getApplication();
            CollectionDbHelper dbHelper = application.getCurrentCollection().getDbHelper(this.getContext());
            Cursor c = dbHelper.getBooksAll();
            adapter = new CollectionBookAdapter(c, this.getContext(), this.listener);

            // delete book
            registerForContextMenu(this.bookList);
        } else {
            adapter = new CollectionBookAdapter(this.cursor, this.getContext(), this.listener);
        }

        bookList.setAdapter(adapter);
    }

    public void onResume() {
        super.onResume();
        this.updateBookList();
    }


    public void updateBookList() {
            RecyclerView bookList = this.root.findViewById(R.id.bookListRecycler);
            CollectionBookAdapter adapter;
            if(this.cursor == null) {
                bookList.setAdapter(null);

                MiBiblioApplication application = (MiBiblioApplication) this.getActivity().getApplication();
                CollectionDbHelper dbHelper = application.getCurrentCollection().getDbHelper(this.getContext());
                Cursor cursor = dbHelper.getBooksAll();
                adapter = new CollectionBookAdapter(cursor, this.getContext(), this.listener);
                bookList.setAdapter(adapter);
            }
    }
}
