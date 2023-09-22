package com.mvm.mibiblio.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.mvm.mibiblio.MiBiblioApplication;
import com.mvm.mibiblio.R;
import com.mvm.mibiblio.models.BookModel;
import com.mvm.mibiblio.models.ReadingModel;

import java.util.Date;

public class ReadingActivity extends AppCompatActivity {
    private ReadingModel reading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        Intent intent = getIntent();
        BookModel book = intent.getParcelableExtra("book");
        if(book == null) {
            return;
        }

        this.reading = new ReadingModel();
        this.reading.setDate(new Date());
        this.reading.setBookId(book.getId());

        TextView textTitle = findViewById(R.id.textTitle);
        textTitle.setText(book.getTitle());
        TextView textAuthor = findViewById(R.id.textAuthor);
        textAuthor.setText(book.getAuthor());
        if(book.getCover() != null) {
            ImageView coverView = findViewById(R.id.coverView);
            coverView.setImageBitmap(book.getCover());
        }

        TextView date = findViewById(R.id.textDate);
        date.setText(this.reading.getDate().toString());

        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadingActivity.this.save();
            }
        });

        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReadingActivity.this.cancel();
            }
        });
    }

    public void save() {
        EditText editComments = findViewById(R.id.editComments);
        RatingBar ratingBar = findViewById(R.id.ratingBar);

        if(editComments.getText().toString().length() >= 512) {
            Snackbar.make(this.getContentScene().getSceneRoot(),
                    getString(R.string.comment_error), Snackbar.LENGTH_LONG).show();
            return;
        }

        this.reading.setComments(editComments.getText().toString());
        this.reading.setRating((int)(ratingBar.getRating()*2));

        MiBiblioApplication app = (MiBiblioApplication)this.getApplication();
        SQLiteDatabase db = app.getCurrentCollection().getDbHelper(this.getApplicationContext()).getWritableDatabase();
        this.reading.insert(db);
        this.finish();
    }

    public void cancel() {
        this.finish();
    }
}
