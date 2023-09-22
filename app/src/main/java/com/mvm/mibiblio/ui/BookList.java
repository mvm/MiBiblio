package com.mvm.mibiblio.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;

import com.mvm.mibiblio.adapter.CollectionBookAdapter;
import com.mvm.mibiblio.search.CollectionBookSearch;
import com.mvm.mibiblio.search.GoogleBooksNetSearch;
import com.mvm.mibiblio.adapter.ListBookAdapter;
import com.mvm.mibiblio.MiBiblioApplication;
import com.mvm.mibiblio.models.BookModel;
import com.mvm.mibiblio.R;
import com.mvm.mibiblio.ui.booklist.BookListFragment;
import com.mvm.mibiblio.ui.operations.OperationsFragment;

import java.util.List;

public class BookList extends AppCompatActivity {
    public static final int CURSOR_LENT = 1;
    public static final int CURSOR_VIEW_LOANS = 2;
    public static final int CURSOR_SEARCH_COLLECTION = 3;
    public static final int CURSOR_SEARCH_INTERNET = 4;
    public static final int CURSOR_VIEW_READS = 5;

    private Cursor cursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();
        int option = intent.getIntExtra("options", 0);

        MiBiblioApplication app = (MiBiblioApplication)this.getApplication();
        GoogleBooksNetSearch netSearch;

        if(option == BookList.CURSOR_LENT) {
            cursor = app.getCurrentCollection().getDbHelper(this).getLentBooks();
        } else if(option == BookList.CURSOR_VIEW_LOANS) {
            cursor = app.getCurrentCollection().getDbHelper(this).getBookWithLoans();
        } else if(option == BookList.CURSOR_SEARCH_COLLECTION) {
            CollectionBookSearch bookSearch = new CollectionBookSearch(app.getCurrentCollection(),
                    (ContentValues)intent.getParcelableExtra("search_params"));
            cursor = bookSearch.search(this);
        } else if(option == BookList.CURSOR_VIEW_READS) {
            cursor = app.getCurrentCollection().getDbHelper(this).getBookWithReadings();
        }

        if(option == BookList.CURSOR_SEARCH_COLLECTION) {
            setContentView(R.layout.book_list_activity);
            BookListFragment fragment = BookListFragment.newInstance(new CollectionBookAdapter.OnItemSelect() {
                @Override
                public void onSelect(BookModel model) {
                    BookList.this.onViewBook(model);
                }

                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo info, BookModel book) {
                    return;
                }
            }, cursor);

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .commitNow();
            }
        } else if(option != BookList.CURSOR_SEARCH_INTERNET) {
            setContentView(R.layout.book_list_activity);
            BookListFragment fragment = BookListFragment.newInstance(new CollectionBookAdapter.OnItemSelect() {
                @Override
                public void onSelect(BookModel model) {
                    BookList.this.onSelect(model);
                }

                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo info, BookModel book) {
                    return;
                }
            }, cursor);

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .commitNow();
            }
        } else {
            setContentView(R.layout.layout_waiting);
            netSearch =
                    new GoogleBooksNetSearch(new GoogleBooksNetSearch.OnResultListener() {
                        @Override
                        public void onResult(List<BookModel> adapter) {
                            onNetResult(adapter);
                        }
                    });
            netSearch.execute((ContentValues)intent.getParcelableExtra("search_params"));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }

    private void onSelect(BookModel book) {
        Intent intent = new Intent();
        intent.putExtra("book", book);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void onViewBook(BookModel book) {
        Intent intent = new Intent(this, BookViewActivity.class);
        intent.putExtra("book", book);
        startActivity(intent);
        finish();
    }

    public void onNetResult(final List<BookModel> list) {
        final Activity activity = this;
        setContentView(R.layout.book_list_activity);
        ListBookAdapter adapter = new ListBookAdapter(activity, list, new ListBookAdapter.OnSelectListener() {
            @Override
            public void onSelect(BookModel book) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                dialogBuilder.setMessage(R.string.addbook_msg)
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MiBiblioApplication app = (MiBiblioApplication)getApplication();
                                SQLiteDatabase db = app.getCurrentCollection().getDbHelper(activity).getWritableDatabase();
                                book.insert(db);
                                activity.finish();
                            }
                        }).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.bookListRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);
    }

    public void onDestroy() {
        super.onDestroy();
        if(this.cursor != null && !this.cursor.isClosed()) this.cursor.close();
    }
}
