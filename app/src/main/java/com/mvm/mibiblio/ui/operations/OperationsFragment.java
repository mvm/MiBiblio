package com.mvm.mibiblio.ui.operations;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.mvm.mibiblio.io.Exporter;
import com.mvm.mibiblio.io.ExporterFactory;
import com.mvm.mibiblio.io.Importer;
import com.mvm.mibiblio.io.ImporterFactory;
import com.mvm.mibiblio.report.ReportGenerator;
import com.mvm.mibiblio.report.ReportGeneratorFactory;
import com.mvm.mibiblio.ui.BarcodeReadActivity;
import com.mvm.mibiblio.ui.BookAddActivity;
import com.mvm.mibiblio.ui.BookSearchActivity;
import com.mvm.mibiblio.Collection;
import com.mvm.mibiblio.CollectionDbHelper;
import com.mvm.mibiblio.report.HTMLReportGenerator;
import com.mvm.mibiblio.ui.LoanActivity;
import com.mvm.mibiblio.ui.LoanViewActivity;
import com.mvm.mibiblio.MainActivity;
import com.mvm.mibiblio.MiBiblioApplication;
import com.mvm.mibiblio.R;
import com.mvm.mibiblio.ui.ReadViewActivity;
import com.mvm.mibiblio.ui.ReadingActivity;
import com.mvm.mibiblio.ui.SettingsActivity;
import com.mvm.mibiblio.io.XMLExporter;
import com.mvm.mibiblio.io.XMLImporter;
import com.mvm.mibiblio.models.BookModel;
import com.mvm.mibiblio.ui.BookList;

import java.io.InputStream;
import java.io.OutputStream;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.ACTION_CREATE_DOCUMENT;
import static android.content.Intent.ACTION_OPEN_DOCUMENT;
import static android.content.Intent.CATEGORY_OPENABLE;
import static com.google.android.material.snackbar.Snackbar.LENGTH_LONG;
import static com.google.android.material.snackbar.Snackbar.LENGTH_SHORT;
import static com.mvm.mibiblio.MainActivity.REQUEST_ADD_AUTO;
import static com.mvm.mibiblio.MainActivity.REQUEST_ADD_MANUALLY;

public class OperationsFragment extends Fragment {
    private View root;

    public static OperationsFragment newInstance() {
        return new OperationsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.root = inflater.inflate(R.layout.operations_fragment, container, false);
        return this.root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ImageButton searchCollectionButton = this.root.findViewById(R.id.searchLocalButton);
        TextView searchCollectionText = this.root.findViewById(R.id.textSearchLocal);
        View.OnClickListener searchCollectionListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OperationsFragment.this.onSearchCollection();
            }
        };

        searchCollectionButton.setOnClickListener(searchCollectionListener);
        searchCollectionText.setOnClickListener(searchCollectionListener);

        ImageButton addAutoButton = this.root.findViewById(R.id.scanBookButton);
        TextView addAutoText = this.root.findViewById(R.id.textScanBook);
        View.OnClickListener addAutoListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddAuto();
            }
        };
        addAutoButton.setOnClickListener(addAutoListener);
        addAutoText.setOnClickListener(addAutoListener);

        ImageButton searchInternetButton = this.root.findViewById(R.id.searchInternetButton);
        TextView searchInternetText = this.root.findViewById(R.id.textSearchInternet);
        View.OnClickListener searchInternetListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchInternet();
            }
        };
        searchInternetButton.setOnClickListener(searchInternetListener);
        searchInternetText.setOnClickListener(searchInternetListener);

        ImageButton addBookButton = this.root.findViewById(R.id.addBookButton);
        TextView addBookText = this.root.findViewById(R.id.textAddBook);
        View.OnClickListener addBookListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OperationsFragment.this.onAddBook();
            }
        };
        addBookButton.setOnClickListener(addBookListener);
        addBookText.setOnClickListener(addBookListener);

        ImageButton newReadingButton = this.root.findViewById(R.id.newReadingButton);
        TextView newReadingText = this.root.findViewById(R.id.textNewRead);
        View.OnClickListener newReadingListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OperationsFragment.this.onNewReading();
            }
        };
        newReadingButton.setOnClickListener(newReadingListener);
        newReadingText.setOnClickListener(newReadingListener);

        ImageButton viewReadingButton = this.root.findViewById(R.id.viewReadingButton);
        TextView textViewRead = this.root.findViewById(R.id.textViewRead);
        View.OnClickListener viewReadListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OperationsFragment.this.onViewReading();
            }
        };
        viewReadingButton.setOnClickListener(viewReadListener);
        textViewRead.setOnClickListener(viewReadListener);

        ImageButton loanButton = this.root.findViewById(R.id.newLoanButton);
        TextView textNewLoan = this.root.findViewById(R.id.textNewLoan);
        View.OnClickListener newLoanListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OperationsFragment.this.onNewLoan();
            }
        };
        loanButton.setOnClickListener(newLoanListener);
        textNewLoan.setOnClickListener(newLoanListener);

        ImageButton viewLoanButton = this.root.findViewById(R.id.viewLoanButton);
        TextView textViewLoan = this.root.findViewById(R.id.textViewLoan);
        View.OnClickListener viewLoanListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OperationsFragment.this.onViewLoans();
            }
        };
        viewLoanButton.setOnClickListener(viewLoanListener);
        textViewLoan.setOnClickListener(viewLoanListener);
        

        ImageButton returnButton = this.root.findViewById(R.id.newReturnButton);
        TextView textReturn = this.root.findViewById(R.id.textNewReturn);
        View.OnClickListener returnListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OperationsFragment.this.onReturnBook();
            }
        };
        returnButton.setOnClickListener(returnListener);
        textReturn.setOnClickListener(returnListener);

        ImageButton expimpButton = this.root.findViewById(R.id.exportImportButton);
        TextView textExpImp = this.root.findViewById(R.id.textExportImport);
        View.OnClickListener expImpListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OperationsFragment.this.onExportImport();
            }
        };
        expimpButton.setOnClickListener(expImpListener);
        textExpImp.setOnClickListener(expImpListener);

        ImageButton reportButton = this.root.findViewById(R.id.reportButton);
        TextView textReport = this.root.findViewById(R.id.textReport);
        View.OnClickListener reportListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReport();
            }
        };
        reportButton.setOnClickListener(reportListener);
        textReport.setOnClickListener(reportListener);

        ImageButton settingsButton = this.root.findViewById(R.id.settingsButton);
        TextView textSettings = this.root.findViewById(R.id.textSettings);
        View.OnClickListener settingsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSettings();
            }
        };
        settingsButton.setOnClickListener(settingsListener);
        textSettings.setOnClickListener(settingsListener);
    }

    private void onReport() {
        Intent intent = new Intent(ACTION_CREATE_DOCUMENT);
        intent.addCategory(CATEGORY_OPENABLE);
        intent.setType("text/html");
        startActivityForResult(intent, MainActivity.REQUEST_GENERATE_REPORT);
    }

    private void onSearchCollection() {
        Intent intent = new Intent(this.getActivity(), BookSearchActivity.class);
        this.startActivityForResult(intent, MainActivity.REQUEST_SEARCH_COLLECTION);
    }

    private void onSearchInternet() {
        Intent intent = new Intent(this.getActivity(), BookSearchActivity.class);
        startActivityForResult(intent, MainActivity.REQUEST_SEARCH_INTERNET);
    }

    private void onNewReading() {
        Intent intent = new Intent(this.getActivity(), BookList.class);
        this.startActivityForResult(intent, MainActivity.REQUEST_ADD_READING_LIST);
    }

    private void onViewReading() {
        Intent intent = new Intent(this.getActivity(), BookList.class);
        intent.putExtra("options", BookList.CURSOR_VIEW_READS);
        this.startActivityForResult(intent, MainActivity.REQUEST_VIEW_READING_LIST);
    }

    private void onNewLoan() {
        Intent intent = new Intent(this.getActivity(), BookList.class);
        this.startActivityForResult(intent, MainActivity.REQUEST_ADD_LOAN_LIST);
    }

    private void onAddBook() {
        Intent intent = new Intent(this.getActivity(), BookAddActivity.class);
        startActivityForResult(intent, REQUEST_ADD_MANUALLY);
    }

    private void onAddAuto() {
        Intent intent = new Intent(this.getActivity(), BarcodeReadActivity.class);
        startActivityForResult(intent, REQUEST_ADD_AUTO);
    }

    private void onReturnBook() {
        Intent intent = new Intent(this.getActivity(), BookList.class);
        intent.putExtra("options", BookList.CURSOR_LENT);
        startActivityForResult(intent, MainActivity.REQUEST_RETURN);
    }

    private void onViewLoans() {
        Intent intent = new Intent(this.getActivity(), BookList.class);
        intent.putExtra("options", BookList.CURSOR_VIEW_LOANS);
        startActivityForResult(intent, MainActivity.REQUEST_VIEW_LOANS);
    }

    private void onExportImport() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_expimp_title)
            .setMessage(R.string.dialog_expimp_msg)
            .setPositiveButton(R.string.dialog_expimp_export, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    OperationsFragment.this.onExportFile();
                }
            })
                .setNeutralButton(R.string.dialog_expimp_import, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OperationsFragment.this.onImportFile();
                    }
                })
            .setNegativeButton(R.string.dialog_expimp_cancel, null);
        builder.create().show();
    }

    private void onExportFile() {
        Intent intent = new Intent(ACTION_CREATE_DOCUMENT);
        intent.addCategory(CATEGORY_OPENABLE);
        intent.setType("application/xml");
        startActivityForResult(intent, MainActivity.REQUEST_EXPORT_FILE);
    }

    private void onImportFile() {
        Intent intent = new Intent(ACTION_OPEN_DOCUMENT);
        intent.addCategory(CATEGORY_OPENABLE);
        intent.setType("application/xml");
        startActivityForResult(intent, MainActivity.REQUEST_IMPORT_FILE);
    }

    private void onSettings() {
        Intent intent = new Intent(this.getActivity(), SettingsActivity.class);
        startActivity(intent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        MiBiblioApplication application = (MiBiblioApplication) this.getActivity().getApplication();
        CollectionDbHelper dbHelper = application.getCurrentCollection().getDbHelper(this.getActivity());

        if(requestCode == REQUEST_ADD_MANUALLY && resultCode == RESULT_OK) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            BookModel book = (BookModel) data.getExtras().getParcelable("book");

            if(book == null) {
                Snackbar.make(this.root, "Book returned is null", LENGTH_SHORT).show();
                return;
            }

            book.insert(db);
        } else if(requestCode == MainActivity.REQUEST_ADD_READING_LIST && resultCode == RESULT_OK) {
            Intent intent = new Intent(this.getActivity(), ReadingActivity.class);
            intent.putExtra("book", (BookModel)data.getParcelableExtra("book"));
            this.startActivity(intent);
        } else if(requestCode == MainActivity.REQUEST_VIEW_READING_LIST && resultCode == RESULT_OK) {
            Intent intent = new Intent(this.getActivity(), ReadViewActivity.class);
            intent.putExtra("book", (BookModel)data.getParcelableExtra("book"));
            this.startActivity(intent);
        } else if(requestCode == MainActivity.REQUEST_ADD_LOAN_LIST && resultCode == RESULT_OK) {
            Intent intent = new Intent(this.getActivity(), LoanActivity.class);
            intent.putExtra("book", (BookModel)data.getParcelableExtra("book"));
            this.startActivity(intent);
        } else if(requestCode == MainActivity.REQUEST_VIEW_LOANS && resultCode == RESULT_OK) {
            Intent intent = new Intent(this.getActivity(), LoanViewActivity.class);
            intent.putExtra("book", (BookModel)data.getParcelableExtra("book"));
            this.startActivity(intent);
        } else if(requestCode == MainActivity.REQUEST_RETURN && resultCode == RESULT_OK) {
            BookModel book = data.getParcelableExtra("book");

            if(book == null) {
                return;
            }

            dbHelper.returnBook(book);
            Snackbar.make(this.root, getString(R.string.returnbook_msg), LENGTH_SHORT).show();
        } else if(requestCode == MainActivity.REQUEST_SEARCH_COLLECTION && resultCode == RESULT_OK) {
            Intent intent = new Intent(this.getActivity(), BookList.class);
            intent.putExtra("options", BookList.CURSOR_SEARCH_COLLECTION);
            intent.putExtra("search_params", (ContentValues)data.getParcelableExtra("search_params"));
            this.startActivity(intent);
        } else if(requestCode == MainActivity.REQUEST_EXPORT_FILE && resultCode == RESULT_OK) {
            try {
                MiBiblioApplication app = (MiBiblioApplication)this.getActivity().getApplication();
                Collection collection = app.getCurrentCollection();
                SharedPreferences prefs = this.getActivity().getSharedPreferences("mibiblio", Context.MODE_PRIVATE);

                if(data.getData() == null) {
                    throw new Exception("no file name specified");
                }

                OutputStream outputStream = this.getActivity().getContentResolver().openOutputStream(data.getData(), "rwt");
                Exporter exporter = ExporterFactory.newInstance(prefs.getString("export_format", "xml"),
                        getContext(), outputStream, collection);

                if(exporter != null) {
                    exporter.save();
                    outputStream.flush();
                    outputStream.close();
                } else {
                    throw new Exception("unknown export format " + prefs.getString("export_format",
                            "default-not-set"));
                }

            } catch(Exception e) {
                Snackbar.make(this.root, getString(R.string.export_failed_msg) + e.getMessage(), LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else if(requestCode == MainActivity.REQUEST_IMPORT_FILE && resultCode == RESULT_OK) {
            try {
                MiBiblioApplication app = (MiBiblioApplication)this.getActivity().getApplication();
                Collection collection = app.getCurrentCollection();
                SharedPreferences prefs = this.getActivity().getSharedPreferences("mibiblio", Context.MODE_PRIVATE);

                if(data.getData() == null)
                    throw new Exception("no file name specified");

                InputStream inputStream = this.getActivity().getContentResolver().openInputStream(data.getData());
                Importer importer = ImporterFactory.newInstance(prefs.getString("export_format", "xml"),
                        this.getActivity(), inputStream, collection);
                if(importer != null) {
                    importer.read();
                    inputStream.close();
                } else {
                    throw new Exception("unknown import format");
                }
            } catch(Exception e) {
                Snackbar.make(this.root, getString(R.string.import_failed_msg) + e.getMessage(), LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else if(requestCode == MainActivity.REQUEST_SEARCH_INTERNET && resultCode == RESULT_OK) {
            Intent intent = new Intent(getActivity(), BookList.class);
            intent.putExtra("options", BookList.CURSOR_SEARCH_INTERNET);
            intent.putExtra("search_params", (ContentValues)data.getParcelableExtra("search_params"));
            startActivity(intent);
        } else if(requestCode == MainActivity.REQUEST_ADD_AUTO && resultCode == RESULT_OK) {
            String isbn = data.getStringExtra("barcode");
            ContentValues values = new ContentValues();
            values.put("isbn", isbn);
            Intent intent = new Intent(getActivity(), BookList.class);
            intent.putExtra("options", BookList.CURSOR_SEARCH_INTERNET);
            intent.putExtra("search_params", values);
            startActivityForResult(intent, MainActivity.REQUEST_ADD_AUTO2);
        } else if(requestCode == MainActivity.REQUEST_GENERATE_REPORT && resultCode == RESULT_OK) {
            try {
                MiBiblioApplication app = (MiBiblioApplication)this.getActivity().getApplication();
                Collection collection = app.getCurrentCollection();
                SharedPreferences prefs = this.getActivity().getSharedPreferences("mibiblio", Context.MODE_PRIVATE);

                if(data.getData() == null) {
                    throw new Exception("no file name specified");
                }

                OutputStream outputStream = this.getActivity().getContentResolver().openOutputStream(data.getData(), "rwt");
                ReportGenerator reportGenerator = ReportGeneratorFactory
                        .newInstance(prefs.getString("report_format", "html"),
                                this.getContext(), outputStream, collection);
                if(reportGenerator != null)
                    reportGenerator.save();
                outputStream.close();

                AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity())
                        .setMessage(R.string.genreport_msg)
                        .setPositiveButton(R.string.dialog_ok, null);
                builder.create().show();

            } catch(Exception e) {
                Snackbar.make(this.root, getString(R.string.report_failed_msg) + e.getMessage(), LENGTH_SHORT).show();
            }
        }
    }

}
