package com.mvm.mibiblio.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.mvm.mibiblio.R;

import java.util.regex.Pattern;

public class BookSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);

        Button buttonSearch = findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookSearchActivity.this.search();
            }
        });

        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookSearchActivity.this.setResult(RESULT_CANCELED);
                BookSearchActivity.this.finish();
            }
        });
    }

    private void search() {
        ContentValues values = new ContentValues();
        Intent returnValues = new Intent();

        EditText editTitle = findViewById(R.id.editTitle);
        if(editTitle.getText().length() > 0)
            values.put("title", editTitle.getText().toString());
        EditText editAuthor = findViewById(R.id.editAuthor);
        if(editAuthor.getText().length() > 0)
            values.put("author", editAuthor.getText().toString());
        EditText editEdition = findViewById(R.id.editEdition);
        if(editEdition.getText().length() > 0)
            values.put("edition", editEdition.getText().toString());
        EditText editPublisher = findViewById(R.id.editPublisher);
        if(editPublisher.getText().length() > 0)
            values.put("publisher", editPublisher.getText().toString());
        EditText editISBN = findViewById(R.id.editISBN);
        if(editISBN.getText().length() > 0)
            values.put("isbn", editISBN.getText().toString());

        if(editTitle.getText().toString().isEmpty() &&
            editAuthor.getText().toString().isEmpty() &&
            editEdition.getText().toString().isEmpty() &&
            editPublisher.getText().toString().isEmpty() &&
            editISBN.getText().toString().isEmpty()) {
            Snackbar.make(findViewById(R.id.layout),
                    getString(R.string.search_error), Snackbar.LENGTH_LONG).show();
            return;
        }

        if(!editISBN.getText().toString().isEmpty() &&
                !Pattern.matches("(\\d{10}|\\d{13})", editISBN.getText().toString())) {
            Snackbar.make(findViewById(R.id.layout),
                    getString(R.string.isbn_incorrect_error), Snackbar.LENGTH_LONG).show();
            return;
        }

        returnValues.putExtra("search_params", values);
        this.setResult(RESULT_OK, returnValues);
        this.finish();
    }
}
