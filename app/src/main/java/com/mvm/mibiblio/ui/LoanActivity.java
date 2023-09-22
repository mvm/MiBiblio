package com.mvm.mibiblio.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.mvm.mibiblio.CollectionDbHelper;
import com.mvm.mibiblio.MiBiblioApplication;
import com.mvm.mibiblio.R;
import com.mvm.mibiblio.models.BookModel;
import com.mvm.mibiblio.models.LoanModel;

public class LoanActivity extends AppCompatActivity {
    private BookModel book;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan);

        book = this.getIntent().getParcelableExtra("book");
        if(book == null) {
            finish();
        }

        TextView textTitle = findViewById(R.id.textTitle);
        textTitle.setText(book.getTitle());
        TextView textAuthor = findViewById(R.id.textAuthor);
        textAuthor.setText(book.getAuthor());
        if(book.getCover() != null) {
            ImageView coverView = findViewById(R.id.coverView);
            coverView.setImageBitmap(book.getCover());
        }

        Button saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoanActivity.this.save();
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

    private void save() {
        EditText editName = findViewById(R.id.editName);
        EditText editComments = findViewById(R.id.editComments);
        LinearLayout layout = findViewById(R.id.layout);

        if(editName.getText().toString().isEmpty()) {
            Snackbar.make(layout,
                    getString(R.string.borrower_error), Snackbar.LENGTH_LONG).show();
            return;
        }

        if(editComments.getText().toString().length() >= 512) {
            Snackbar.make(layout,
                    getString(R.string.comment_error), Snackbar.LENGTH_LONG).show();
            return;
        }

        MiBiblioApplication application = (MiBiblioApplication)this.getApplication();
        CollectionDbHelper dbHelper = application.getCurrentCollection().getDbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        LoanModel loan = new LoanModel();
        loan.setBookId(this.book.getId());
        loan.setName(editName.getText().toString());
        loan.setComments(editComments.getText().toString());
        loan.save(db);

        setResult(RESULT_OK);
        this.finish();
    }
}
