package com.mvm.mibiblio.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mvm.mibiblio.R;

import static com.mvm.mibiblio.MainActivity.REQUEST_ADD_MANUALLY;

public class Operations extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operations_activity);

        Button addBookButton = this.findViewById(R.id.addBookButton);
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Operations.this.onAddBook();
            }
        });
    }

    private void onAddBook() {
        Intent intent = new Intent(this, BookAddActivity.class);
        startActivityForResult(intent, REQUEST_ADD_MANUALLY);
    }
}
