package com.mvm.mibiblio.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.google.android.material.snackbar.Snackbar;
import com.mvm.mibiblio.MiBiblioApplication;
import com.mvm.mibiblio.R;
import com.mvm.mibiblio.models.BookModel;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Pattern;

import static android.content.Intent.ACTION_OPEN_DOCUMENT;
import static android.content.Intent.CATEGORY_OPENABLE;

public class BookEditActivity extends AppCompatActivity {
    private final int REQUEST_SELECT_COVER = 1;

    private EditText editTitle;
    private EditText editAuthor;
    private EditText editEdition;
    private EditText editPublisher;
    private EditText editISBN;
    private EditText editPages;
    private EditText editComments;
    private EditText editDateBought;
    private EditText editLanguage;
    private EditText editYearPublished;
    private RatingBar ratingBar;
    private SimpleDateFormat dateFormat;

    private BookModel book;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_SELECT_COVER && resultCode == RESULT_OK) {
            if(data == null || data.getData() == null) return;
            try {
                InputStream is = this.getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                Bitmap bitmapFinal = BookModel.resizeCover(bitmap);
                this.book.setCover(bitmapFinal);
                updateCover();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_book_add);

        Intent intent = this.getIntent();
        this.book = intent.getParcelableExtra("book");
        if(this.book == null) {
            this.setResult(RESULT_CANCELED);
            this.finish();
        }

        editTitle = findViewById(R.id.editTitle);
        editTitle.setText(this.book.getTitle());
        editAuthor = findViewById(R.id.editAuthor);
        editAuthor.setText(this.book.getAuthor());
        editEdition = findViewById(R.id.editEdition);
        editEdition.setText(String.valueOf(this.book.getEdition()));
        editPublisher = findViewById(R.id.editPublisher);
        editPublisher.setText(this.book.getPublisher());
        editPages = findViewById(R.id.editPages);
        editPages.setText(String.valueOf(this.book.getPageNum()));
        editISBN = findViewById(R.id.editISBN);
        editISBN.setText(this.book.getIsbn());
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setRating(book.getRating()*0.5f);
        editComments = findViewById(R.id.editComments);
        editComments.setText(this.book.getComments());
        editLanguage = findViewById(R.id.editLanguage);
        editLanguage.setText(book.getLanguage());

        dateFormat = BookModel.DateFormat;

        editDateBought = findViewById(R.id.editDateBought);
        if(book.getDateBought().getTime() != 0)
            editDateBought.setText(dateFormat.format(book.getDateBought()));

        editYearPublished = findViewById(R.id.editYearPublished);
        if(book.getYearPublished().getTime() != 0)
            editYearPublished.setText(dateFormat.format(book.getYearPublished()));


        updateCover();

        Button buttonSetCover = findViewById(R.id.buttonSetCover);
        buttonSetCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectCover();
            }
        });

        Button saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        Button cancelButton = findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void onSelectCover() {
        Intent intent = new Intent(ACTION_OPEN_DOCUMENT);
        intent.addCategory(CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_SELECT_COVER);
    }

    private void updateCover() {
        Bitmap coverBitmap = this.book.getCover();
        if(coverBitmap != null) {
            ImageView coverView = findViewById(R.id.coverView);
            coverView.setImageBitmap(coverBitmap);
        }
    }

    private void save() {
        LinearLayout layout = findViewById(R.id.layout);
        if(editTitle.getText().toString().isEmpty()) {
            Snackbar.make(layout,
                    getString(R.string.title_empty_error), Snackbar.LENGTH_LONG).show();
            return;
        }

        if(editAuthor.getText().toString().isEmpty()) {
            Snackbar.make(layout,
                    getString(R.string.author_empty_error), Snackbar.LENGTH_LONG).show();
            return;
        }

        String isbn = editISBN.getText().toString();
        if(!isbn.isEmpty() && !BookModel.verifyISBN(isbn)) {
            Snackbar.make(layout,
                    getString(R.string.isbn_incorrect_error), Snackbar.LENGTH_LONG).show();
            return;
        }

        String dateBought = editDateBought.getText().toString();
        if(!dateBought.isEmpty() && !Pattern.matches("\\d{4}", dateBought)) {
            Snackbar.make(layout,
                    getString(R.string.date_bought_error), Snackbar.LENGTH_LONG).show();
            return;
        }

        String yearPublished = editYearPublished.getText().toString();
        if(!yearPublished.isEmpty() && !Pattern.matches("\\d{4}", yearPublished)) {
            Snackbar.make(layout,
                    getString(R.string.date_pub_error), Snackbar.LENGTH_LONG).show();
            return;
        }

        if(editComments.getText().length() >= 512) {
            Snackbar.make(layout,
                    getString(R.string.comment_error), Snackbar.LENGTH_LONG).show();
            return;
        }

        this.book.setTitle(this.editTitle.getText().toString());
        this.book.setAuthor(this.editAuthor.getText().toString());
        try {
            this.book.setEdition(Integer.parseInt(this.editEdition.getText().toString()));
        } catch(NumberFormatException e) {
            this.book.setEdition(1);
        }
        this.book.setPublisher(this.editPublisher.getText().toString());
        this.book.setIsbn(this.editISBN.getText().toString());
        this.book.setRating((int)(this.ratingBar.getRating()*2));
        this.book.setComments(this.editComments.getText().toString());

        ImageView coverView = findViewById(R.id.coverView);
        Bitmap bitmap = null;
        BitmapDrawable drawable = (BitmapDrawable)coverView.getDrawable();
        if(drawable != null) {
            bitmap = drawable.getBitmap();
            Bitmap bitmapFinal = BookModel.resizeCover(bitmap);
            this.book.setCover(bitmapFinal);
        }

        book.setLanguage(editLanguage.getText().toString());

        try {
            book.setDateBought(dateFormat.parse(editDateBought.getText().toString()));
        } catch(ParseException e) {
            Log.e("mibiblio", "error parsing datebought: " + e.getLocalizedMessage());
        }

        try {
            book.setYearPublished(dateFormat.parse(editYearPublished.getText().toString()));
        } catch(ParseException e) {
            Log.e("mibiblio", "error parsing yearpublished: " + e.getLocalizedMessage());
        }

        MiBiblioApplication app = (MiBiblioApplication)this.getApplication();
        SQLiteDatabase db = app.getCurrentCollection().getDbHelper(this).getWritableDatabase();
        this.book.update(db);

        this.setResult(RESULT_OK);
        this.finish();
    }
}
