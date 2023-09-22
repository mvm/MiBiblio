package com.mvm.mibiblio.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import com.mvm.mibiblio.MiBiblioApplication;
import com.mvm.mibiblio.R;
import com.mvm.mibiblio.adapter.ReadingAdapter;
import com.mvm.mibiblio.models.BookModel;
import com.mvm.mibiblio.models.ReadingModel;

public class ReadViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();
        BookModel book = (BookModel) intent.getParcelableExtra("book");

        MiBiblioApplication app = (MiBiblioApplication)this.getApplication();
        Cursor c = app.getCurrentCollection().getDbHelper(this.getApplicationContext()).getReadings(book.getId());

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        recyclerView.setAdapter(new ReadingAdapter(c, new ReadingFragment.OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(ReadingModel item) {

            }
        }));
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }
}
