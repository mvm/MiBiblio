package com.mvm.mibiblio.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;
import com.mvm.mibiblio.Collection;
import com.mvm.mibiblio.MiBiblioApplication;
import com.mvm.mibiblio.R;

import java.util.Arrays;

public class CreateCollectionDialog extends DialogFragment {
    private CollectionDialogListener listener;

    public interface CollectionDialogListener {
        void onSuccess();
    }

    public CreateCollectionDialog(CollectionDialogListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_create_col, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        Button buttonCancel = this.getView().findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateCollectionDialog.this.dismiss();
            }
        });

        Button buttonCreate = this.getView().findViewById(R.id.buttonCreate);
        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CreateCollectionDialog.this.create()) {
                    CreateCollectionDialog.this.listener.onSuccess();
                    CreateCollectionDialog.this.dismiss();
                }
            }
        });
    }

    private boolean create() {
        EditText editFileName = this.getView().findViewById(R.id.editCollectionName);
        String[] databases = this.getContext().databaseList();
        String filename = editFileName.getText().toString();

        Arrays.sort(databases);
        if(Arrays.binarySearch(databases, filename) >= 0) {
            Snackbar.make(this.getView(), getString(R.string.create_col_error),
                    Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(filename.isEmpty()) {
            return false;
        }

        MiBiblioApplication app =
                (MiBiblioApplication)this.getActivity().getApplication();
        Collection newCollection = new Collection(filename);
        app.setCurrentCollection(newCollection);
        return true;
    }
}
