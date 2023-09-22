package com.mvm.mibiblio.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.mvm.mibiblio.R;
import com.mvm.mibiblio.models.BookModel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Pattern;

import static android.content.Intent.ACTION_OPEN_DOCUMENT;
import static android.content.Intent.CATEGORY_OPENABLE;

public class BookAddActivity extends AppCompatActivity {
    private final int REQUEST_SELECT_COVER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_add);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookAddActivity.this.showConfirm();
            }
        });

        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookAddActivity.this.setResult(RESULT_CANCELED);
                BookAddActivity.this.finish();
            }
        });

        Button buttonSetCover = findViewById(R.id.buttonSetCover);
        buttonSetCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectCover();
            }
        });
    }

    private void onSelectCover() {
        Intent intent = new Intent(ACTION_OPEN_DOCUMENT);
        intent.addCategory(CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_SELECT_COVER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_SELECT_COVER && resultCode == RESULT_OK) {
            if(data == null || data.getData() == null) return;
            try {
                InputStream is = this.getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                Bitmap bitmapFinal = BookModel.resizeCover(bitmap);
                updateCover(bitmapFinal);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateCover(Bitmap bitmap) {
        if(bitmap != null) {
            ImageView coverView = findViewById(R.id.coverView);
            coverView.setImageBitmap(bitmap);
        }
    }

    private void showConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.addbook_msg))
                .setPositiveButton(getString(R.string.dialog_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                save();
                            }
                        })
                .setNegativeButton(getString(R.string.dialog_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
        builder.create().show();
    }

    private void save() {
        View layout = findViewById(R.id.coordinator);
        EditText editTitle = findViewById(R.id.editTitle);
        EditText editAuthor = findViewById(R.id.editAuthor);
        EditText editEdition = findViewById(R.id.editEdition);
        EditText editPublisher = findViewById(R.id.editPublisher);
        EditText editDate = findViewById(R.id.editDateBought);
        EditText editLanguage = findViewById(R.id.editLanguage);
        EditText editYearPublished = findViewById(R.id.editYearPublished);
        EditText editPages = findViewById(R.id.editPages);
        EditText editISBN = findViewById(R.id.editISBN);
        EditText editComments = findViewById(R.id.editComments);
        ImageView coverView = findViewById(R.id.coverView);
        RatingBar ratingBar = findViewById(R.id.ratingBar);

        String title = editTitle.getText().toString();
        String author = editAuthor.getText().toString();

        if(title.isEmpty()) {
            Snackbar.make(layout, getString(R.string.title_empty_error), Snackbar.LENGTH_LONG).show();
            return;
        }

        if(author.isEmpty()) {
            Snackbar.make(layout, getString(R.string.author_empty_error), Snackbar.LENGTH_LONG).show();
            return;
        }

        String isbn = editISBN.getText().toString();
        if(!isbn.isEmpty() && !BookModel.verifyISBN(isbn)) {
            Snackbar.make(layout, getString(R.string.isbn_incorrect_error), Snackbar.LENGTH_LONG).show();
            return;
        }

        String dateBought = editDate.getText().toString();
        if(!dateBought.isEmpty() && !Pattern.matches("\\d{4}", dateBought)) {
            Snackbar.make(layout, getString(R.string.date_bought_error), Snackbar.LENGTH_LONG).show();
            return;
        }

        String yearPublished = editYearPublished.getText().toString();
        if(!yearPublished.isEmpty() && !Pattern.matches("\\d{4}", yearPublished)) {
            Snackbar.make(layout, getString(R.string.date_pub_error), Snackbar.LENGTH_LONG).show();
            return;
        }

        if(editComments.getText().length() >= 512) {
            Snackbar.make(layout, getString(R.string.comment_error), Snackbar.LENGTH_LONG).show();
            return;
        }

        BookModel book = new BookModel(0, title, author);
        try {
            book.setEdition(Integer.parseInt(editEdition.getText().toString()));
        } catch(NumberFormatException e) {
            book.setEdition(0);
        }
        book.setPublisher(editPublisher.getText().toString());
        //book.setDateBought(...)
        try {
            book.setPageNum(Integer.parseInt(editPages.getText().toString()));
        } catch(NumberFormatException e) {
            book.setPageNum(0);
        }
        book.setIsbn(editISBN.getText().toString());
        book.setComments(editComments.getText().toString());
        BitmapDrawable drawable = (BitmapDrawable)coverView.getDrawable();
        if(drawable != null && drawable.getBitmap() != null) {
            Bitmap bitmap = drawable.getBitmap();
            Bitmap bitmapFinal = BookModel.resizeCover(bitmap);
            book.setCover(bitmapFinal);
        }

        book.setLanguage(editLanguage.getText().toString());
        book.setRating((int)(ratingBar.getRating()*2));

        SimpleDateFormat format = BookModel.DateFormat;

        try {
            book.setDateBought(format.parse(editDate.getText().toString()));
        } catch(ParseException e) {
            Log.e("mibiblio", "error parsing datebought: " + e.getLocalizedMessage());
        }

        try {
            book.setYearPublished(format.parse(editYearPublished.getText().toString()));
        } catch(ParseException e) {
            Log.e("mibiblio", "error parsing yearpublished: " + e.getLocalizedMessage());
        }


        Intent result = new Intent();
        result.putExtra("book", book);
        this.setResult(RESULT_OK, result);
        this.finish();
    }

}
