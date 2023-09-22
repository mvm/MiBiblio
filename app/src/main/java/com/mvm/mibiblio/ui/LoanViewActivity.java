package com.mvm.mibiblio.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mvm.mibiblio.MiBiblioApplication;
import com.mvm.mibiblio.R;
import com.mvm.mibiblio.adapter.LoanAdapter;
import com.mvm.mibiblio.models.BookModel;

public class LoanViewActivity extends AppCompatActivity {
    private BookModel book;
    private Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        this.book = intent.getParcelableExtra("book");

        MiBiblioApplication app = (MiBiblioApplication)getApplication();
        this.cursor = app.getCurrentCollection().getDbHelper(this).getLoans(book);

        RecyclerView list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new LoanAdapter(this.cursor));
    }

    public void onDestroy() {
        super.onDestroy();
        this.cursor.close();
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }
}
