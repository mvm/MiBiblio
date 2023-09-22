package com.mvm.mibiblio.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import com.mvm.mibiblio.MainActivity;
import com.mvm.mibiblio.R;

public class SettingsActivity extends AppCompatActivity {
    private Switch switchCover;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = this.getSharedPreferences("mibiblio", MODE_PRIVATE);

        Spinner spinnerSort = findViewById(R.id.spinnerSort);
        String sortField = preferences.getString("sort_field", "title");
        int sortSelection;

        if(sortField.equalsIgnoreCase("title")) {
            sortSelection = 0;
        } else if(sortField.equalsIgnoreCase("author")) {
            sortSelection = 1;
        } else {
            sortSelection = 0;
        }

        spinnerSort.setSelection(sortSelection);
        switchCover = findViewById(R.id.switchCover);

        switchCover.setChecked(preferences.getBoolean("show_cover", false));

        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
    }

    private void save() {
        SharedPreferences.Editor editor = preferences.edit();

        /*
        Spinner spinnerFormat = findViewById(R.id.spinnerFormat);
        String format = (String)spinnerFormat.getSelectedItem();

        if(format.equalsIgnoreCase("xml")) {
            editor.putString("export_format", "xml");
        }

        Spinner spinnerReport = findViewById(R.id.spinnerReport);
        String report = (String)spinnerReport.getSelectedItem();

        if(report.equalsIgnoreCase("html")) {
            editor.putString("report_format", "html");
        }

        Spinner spinnerSearch = findViewById(R.id.spinnerSearch);
        String search = (String)spinnerSearch.getSelectedItem();

        if(search.equalsIgnoreCase("google books")) {
            editor.putString("search_site", "googlebooks");
        }
         */

        Spinner spinnerSort = findViewById(R.id.spinnerSort);
        int campo = spinnerSort.getSelectedItemPosition();

        switch(campo) {
            case 0:
                editor.putString("sort_field", "title");
                break;
            case 1:
                editor.putString("sort_field", "author");
                break;
            default:
                editor.putString("sort_field", "title");
                break;
        }

        editor.putBoolean("show_cover", switchCover.isChecked());
        editor.apply();

        setResult(RESULT_OK);
        finish();
    }
}
