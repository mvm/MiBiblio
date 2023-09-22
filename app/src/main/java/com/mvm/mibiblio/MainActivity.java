package com.mvm.mibiblio;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mvm.mibiblio.adapter.CollectionBookAdapter;
import com.mvm.mibiblio.models.BookModel;
import com.mvm.mibiblio.ui.BookViewActivity;
import com.mvm.mibiblio.ui.SectionsPagerAdapter;
import com.mvm.mibiblio.ui.SettingsActivity;
import com.mvm.mibiblio.ui.booklist.BookListFragment;
import com.mvm.mibiblio.ui.dialog.CreateCollectionDialog;
import com.mvm.mibiblio.ui.dialog.OpenCollectionDialog;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;

    public static final int REQUEST_ADD_MANUALLY = 1;
    public static final int REQUEST_VIEW_READING_LIST = 2;
    public static final int REQUEST_ADD_READING_LIST = 3;
    public static final int REQUEST_ADD_LOAN_LIST = 4;
    public static final int REQUEST_RETURN = 5;
    public static final int REQUEST_VIEW_LOANS = 6;
    public static final int REQUEST_SEARCH_COLLECTION = 7;
    public static final int REQUEST_EXPORT_FILE = 8;
    public static final int REQUEST_IMPORT_FILE = 9;
    public static final int REQUEST_SEARCH_INTERNET = 10;
    public static final int REQUEST_ADD_AUTO = 11;
    public static final int REQUEST_GENERATE_REPORT = 12;
    public static final int REQUEST_ADD_AUTO2 = 13;
    public static final int REQUEST_SETTINGS = 14;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_SETTINGS) {
            Log.d("mibiblio", "recreate called");
            this.updateBookList();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.sectionsPagerAdapter =
                new SectionsPagerAdapter(this, getSupportFragmentManager(),
                        new CollectionBookAdapter.OnItemSelect() {
            @Override
            public void onSelect(BookModel model) {
                Intent intent = new Intent(MainActivity.this, BookViewActivity.class);
                intent.putExtra("book", model);
                MainActivity.this.startActivity(intent);

            }
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo info,
                                            final BookModel book) {
                MenuInflater inflater = MainActivity.this.getMenuInflater();
                inflater.inflate(R.menu.menu_book, menu);
                menu.setHeaderTitle(book.getTitle());
                menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        MainActivity.this.onDelete(book);
                        return true;
                    }
                });
            }
        });
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    public void updateBookList() {
        BookListFragment bookList = (BookListFragment)this
                .getSupportFragmentManager().getFragments().get(0);
        bookList.updateBookList();
    }

    private void createCollection() {
        CreateCollectionDialog dialog =
                new CreateCollectionDialog(new CreateCollectionDialog.CollectionDialogListener() {
            @Override
            public void onSuccess() {
                MainActivity main = MainActivity.this;
                main.updateBookList();
                viewPager.setCurrentItem(0);

                Snackbar.make(MainActivity.this.viewPager,
                        getString(R.string.collection_created), LENGTH_LONG).show();
            }
        });
        FragmentManager fragmentManager = getSupportFragmentManager();
        dialog.show(fragmentManager, "create_collection");
    }

    private void openCollection() {
        OpenCollectionDialog dialog =
                new OpenCollectionDialog(new OpenCollectionDialog.OpenCollectionDialogListener() {
                    @Override
                    public void onSuccess() {
                        MainActivity main = MainActivity.this;
                        BookListFragment bookList = (BookListFragment)main
                                .getSupportFragmentManager().getFragments().get(0);
                        bookList.updateBookList();
                        MainActivity.this.viewPager.setCurrentItem(0);
                        Snackbar.make(viewPager, getString(R.string.collection_opened), LENGTH_LONG).show();
                    }
                });
        FragmentManager fragmentManager = getSupportFragmentManager();
        dialog.show(fragmentManager, "open_collection");
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, REQUEST_SETTINGS);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.menu_create_col:
                this.createCollection();
                break;
            case R.id.menu_open_col:
                this.openCollection();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);
        return true;
    }

    public void onDelete(BookModel book) {
        Activity activity = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(R.string.delbook_msg)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MiBiblioApplication app = (MiBiblioApplication) getApplication();
                                CollectionDbHelper dbHelper = app.getCurrentCollection()
                                        .getDbHelper(activity);
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                book.delete(db);
                                db.close();

                                BookListFragment bookList = (BookListFragment)MainActivity.this
                                        .getSupportFragmentManager().getFragments().get(0);
                                bookList.updateBookList();
                            }
                        })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }
}