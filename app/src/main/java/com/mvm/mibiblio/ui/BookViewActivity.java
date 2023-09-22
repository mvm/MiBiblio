package com.mvm.mibiblio.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mvm.mibiblio.R;
import com.mvm.mibiblio.models.BookModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class BookViewActivity extends AppCompatActivity {
    private BookModel book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = this.getIntent();
        book = (BookModel) intent.getParcelableExtra("book");

        Button editButton = findViewById(R.id.buttonEdit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookViewActivity.this.edit();
            }
        });

        Button newReadingButton = findViewById(R.id.buttonReading);
        newReadingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookViewActivity.this.newReading();
            }
        });

        Button newLoanButton = findViewById(R.id.buttonLoan);
        newLoanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookViewActivity.this.newLoan();
            }
        });

        Bitmap cover = book.getCover();
        if(cover != null) {
            ImageView coverView = findViewById(R.id.coverView);
            coverView.setImageBitmap(cover);
        }

        TextView title = findViewById(R.id.textTitle);
        title.setText(book.getTitle());
        TextView author = findViewById(R.id.textAuthor);
        author.setText(book.getAuthor());
        TextView publisher = findViewById(R.id.textPublisher);
        publisher.setText(book.getPublisher());
        TextView edition = findViewById(R.id.textEdition);
        edition.setText(String.valueOf(book.getEdition()));
        TextView isbn = findViewById(R.id.textISBN);
        isbn.setText(book.getIsbn());
        TextView pages = findViewById(R.id.textPages);
        pages.setText(String.valueOf(book.getPageNum())); // cambiar a cadena de recursos con placeholder
        TextView language = findViewById(R.id.textLanguage);
        language.setText(book.getLanguage());

        SimpleDateFormat dateFormat = BookModel.DateFormat;

        TextView date = findViewById(R.id.textDate);
        if(book.getDateBought().getTime() == 0) {
            date.setText("---");
        } else {
            date.setText(dateFormat.format(book.getDateBought()));
        }

        TextView yearPublished = findViewById(R.id.textYearPublished);
        if(book.getYearPublished().getTime() == 0) {
            yearPublished.setText("---");
        } else
            yearPublished.setText(dateFormat.format(book.getYearPublished()));

        RatingBar ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setRating(book.getRating()*0.5f);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

            }
        });
        TextView comments = findViewById(R.id.textComment);
        comments.setText(book.getComments());
    }

    private void edit() {
        Intent intent = new Intent(this, BookEditActivity.class);
        intent.putExtra("book", this.book);
        this.startActivity(intent);
        this.finish();
    }

    private void newReading() {
        Intent intent = new Intent(this, ReadingActivity.class);
        intent.putExtra("book", this.book);
        this.startActivity(intent);
    }

    private void newLoan() {
        Intent intent = new Intent(this, LoanActivity.class);
        intent.putExtra("book", this.book);
        this.startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        finish();
        return true;
    }
}
