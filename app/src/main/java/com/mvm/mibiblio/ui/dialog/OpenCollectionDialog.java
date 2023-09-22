package com.mvm.mibiblio.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.mvm.mibiblio.Collection;
import com.mvm.mibiblio.MiBiblioApplication;
import com.mvm.mibiblio.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class OpenCollectionDialog extends DialogFragment {
    private Spinner spinner;

    @Override
    public void onStart() {
        super.onStart();

        spinner = this.getView().findViewById(R.id.spinner);
        String[] databases = filterDatabases(getContext().databaseList());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(),
                R.layout.support_simple_spinner_dropdown_item, databases);
        spinner.setAdapter(adapter);

        Button buttonOpen = this.getView().findViewById(R.id.buttonOpen);
        buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dbName = (String)OpenCollectionDialog.this.spinner.getSelectedItem();
                MiBiblioApplication app = (MiBiblioApplication)OpenCollectionDialog.this.getActivity().getApplication();
                Collection collection = new Collection(dbName);
                app.setCurrentCollection(collection);
                OpenCollectionDialog.this.listener.onSuccess();
                OpenCollectionDialog.this.dismiss();
            }
        });

        Button buttonCancel = this.getView().findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCollectionDialog.this.dismiss();
            }
        });
    }

    public interface OpenCollectionDialogListener {
        void onSuccess();
    }

    private OpenCollectionDialogListener listener;

    public OpenCollectionDialog(OpenCollectionDialogListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_open_col, container, false);
    }

    private String[] filterDatabases(String[] databases) {
        Pattern pattern = Pattern.compile("-(.*)$");
        List<String> result = new ArrayList<>();
        for(String db: databases) {
            if(!pattern.matcher(db).find()) result.add(db);
        }
        return result.toArray(new String[]{});
    }


}
