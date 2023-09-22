package com.mvm.mibiblio.io;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Base64;

import com.mvm.mibiblio.Collection;
import com.mvm.mibiblio.models.BookModel;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class XMLExporter extends Exporter {
    private Context context;
    private OutputStream os;
    private Collection col;

    public XMLExporter(Context context, OutputStream outputStream, Collection collection) {
        super(context, outputStream, collection);
        this.context = context;
        this.os = outputStream;
        this.col = collection;
    }

    private void writeBook(XmlSerializer serializer, BookModel book) throws XmlPullParserException, IOException {
        String[] fields = {
          "title", "author", "publisher", "edition", "isbn", "pages", "rating", "comments", "id",
                "cover"
        };

        Bitmap cover = book.getCover();
        String coverString = "";

        if(cover != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            cover.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] coverBytes = stream.toByteArray();
            coverString = Base64.encodeToString(coverBytes, Base64.DEFAULT);
        }

        String[] values = {
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                String.valueOf(book.getEdition()),
                book.getIsbn(),
                String.valueOf(book.getPageNum()),
                String.valueOf(book.getRating()),
                book.getComments(),
                String.valueOf(book.getId()),
                coverString
        };

        for(int i = 0; i < fields.length; i++) {
            serializer.startTag(null, fields[i]);
            if(values[i] != null)
                serializer.text(values[i]);
            serializer.endTag(null, fields[i]);
        }
    }

    public void save() throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlSerializer serializer = factory.newSerializer();
        serializer.setOutput(this.os, "utf-8");
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        serializer.startDocument("utf-8", false);
        serializer.startTag(null, "tellico");
        serializer.attribute(null, "xmlns", "http://periapsis.org/tellico/");
        serializer.attribute(null, "syntaxVersion", "11");

        serializer.startTag(null, "collection");
        serializer.attribute(null, "title", "Collection");
        serializer.attribute(null, "type", "2");

        Cursor cursor = this.col.getDbHelper(this.context).getBooksAll();

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            BookModel book = BookModel.fromCursor(cursor);

            serializer.startTag(null, "entry");
            serializer.attribute(null, "id", String.valueOf(book.getId()));

            this.writeBook(serializer, book);

            serializer.endTag(null, "entry");
        }

        serializer.endTag(null, "collection");
        serializer.endTag(null, "tellico");
        serializer.endDocument();
    }
}
