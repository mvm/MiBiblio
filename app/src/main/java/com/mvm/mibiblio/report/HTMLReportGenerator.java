package com.mvm.mibiblio.report;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.mvm.mibiblio.AuthorComparator;
import com.mvm.mibiblio.Collection;
import com.mvm.mibiblio.R;
import com.mvm.mibiblio.TitleComparator;
import com.mvm.mibiblio.models.BookModel;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HTMLReportGenerator extends ReportGenerator {
    private Context context;
    private Collection collection;
    private BufferedOutputStream buf;

    public HTMLReportGenerator(Context context, OutputStream outputStream, Collection collection) {
        super(context, outputStream, collection);
        this.context = context;
        this.collection = collection;
        buf = new BufferedOutputStream(outputStream);
    }

    private void writeText(String string) throws IOException {
        buf.write(string.getBytes());
    }

    private void writeBooks(Cursor cursor) throws IOException {
        List<BookModel> contents = new LinkedList<>();

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            contents.add(BookModel.fromCursor(cursor));
            cursor.moveToNext();
        }

        SharedPreferences prefs = context.getSharedPreferences("mibiblio", MODE_PRIVATE);
        String sort_field = prefs.getString("sort_field", "title");
        if(sort_field.equalsIgnoreCase("title")) {
            Collections.sort(contents, new TitleComparator());
        } else if(sort_field.equalsIgnoreCase("author")) {
            Collections.sort(contents, new AuthorComparator());
        }

        writeText("<table>");

        writeText("<thead><tr>");
        writeText("<th></th>");
        String[] colNames = {
                context.getString(R.string.report_title),
                context.getString(R.string.report_author),
                context.getString(R.string.report_publisher),
                context.getString(R.string.report_isbn),
                context.getString(R.string.report_pages)
        };

        for(String x : colNames) {
            writeText("<th>");
            writeText(x);
            writeText("</th>");
        }

        writeText("</tr></thead>");

        writeText("<tbody>");

        for(BookModel book: contents) {
            writeText("<tr>");

            Bitmap cover = book.getCover();
            writeText("<td>");

            String coverString;
            boolean showCover = prefs.getBoolean("show_cover", false);

            if(cover != null && showCover) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                BookModel.resizeCover(cover).compress(Bitmap.CompressFormat.PNG, 100, stream);

                byte[] coverBytes = stream.toByteArray();
                coverString = Base64.encodeToString(coverBytes, Base64.DEFAULT);
                writeText("<img src=\"data:image/png;base64,");
                writeText(coverString);
                writeText("\"/>");
            }
            writeText("</td>");

            String[] columns = {book.getTitle(), book.getAuthor(),
                    book.getPublisher(), book.getIsbn(), String.valueOf(book.getPageNum())};

            for(String str: columns) {
                writeText("<td>");
                if(str != null)
                    writeText(str);
                writeText("</td>");
            }

            writeText("</tr>");
        }
        writeText("</tbody></table>");
    }

    public void save() throws IOException {
        buf.write("<html>".getBytes());
        buf.write("<body>".getBytes());

        buf.write("<h1>".getBytes());
        String colname = String.format(context.getString(R.string.report_colname), collection.getName());
        buf.write(colname.getBytes());
        buf.write("</h1>".getBytes());

        SQLiteDatabase database = collection.getDbHelper(context).getReadableDatabase();

        int bookNum = 0;
        Cursor bookNumCursor = database.rawQuery("SELECT COUNT(*) FROM books", null);
        bookNumCursor.moveToFirst();
        bookNum = bookNumCursor.getInt(0);
        bookNumCursor.close();

        writeText("<p>");
        writeText(String.format(context.getString(R.string.report_booknum), String.valueOf(bookNum)));
        writeText("</p>");

        int authorNum = 0;
        Cursor authorNumCursor = database.rawQuery("SELECT COUNT(author) FROM books", null);
        authorNumCursor.moveToFirst();
        authorNum = authorNumCursor.getInt(0);
        authorNumCursor.close();
        writeText("<p>");
        writeText(String.format(context.getString(R.string.report_authornum), String.valueOf(authorNum)));
        writeText("</p>");

        database.close();

        Cursor books = collection.getDbHelper(context).getBooksAll();
        writeBooks(books);
        books.close();

        buf.write("</body>".getBytes());
        buf.write("</html>".getBytes());
        buf.flush();
        buf.close();
    }
}
