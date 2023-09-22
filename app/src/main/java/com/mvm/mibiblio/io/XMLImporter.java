package com.mvm.mibiblio.io;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.mvm.mibiblio.Collection;
import com.mvm.mibiblio.models.BookModel;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

public class XMLImporter extends Importer {
    private Context context;
    private InputStream is;
    private Collection col;

    public XMLImporter(Context context, InputStream is, Collection col) {
        super(context, is, col);
        this.context = context;
        this.is = is;
        this.col = col;
    }

    public void read() throws XmlPullParserException, IOException {
        SQLiteDatabase db = this.col.getDbHelper(this.context).getWritableDatabase();
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(this.is, "utf-8");

        int eventType = parser.getEventType();
        String tagName = parser.getName();
        BookModel book = new BookModel();
        String currentText = new String();

        while(eventType != XmlPullParser.END_DOCUMENT) {
            tagName = parser.getName();
            switch(eventType) {
                case XmlPullParser.START_TAG:
                    if(currentText == null) currentText = "";
                    if(tagName.equalsIgnoreCase("entry")) {
                        book = new BookModel();
                    }
                    break;
                case XmlPullParser.TEXT:
                    currentText = parser.getText();
                    break;
                case XmlPullParser.END_TAG:
                    if(book == null) break;
                    if(currentText == null) currentText = "";
                    if (tagName.equalsIgnoreCase("entry")) {
                        book.insert(db);
                    } else if(tagName.equalsIgnoreCase("title")) {
                        book.setTitle(currentText);
                    } else if(tagName.equalsIgnoreCase("author")) {
                        book.setAuthor(currentText);
                    } else if(tagName.equalsIgnoreCase("isbn")) {
                        book.setIsbn(currentText);
                    } else if(tagName.equalsIgnoreCase("comments")) {
                        book.setComments(currentText);
                    } else if(tagName.equalsIgnoreCase("pages")) {
                        book.setPageNum(Integer.parseInt(currentText));
                    } else if(tagName.equalsIgnoreCase("edition")) {
                        book.setEdition(Integer.parseInt(currentText));
                    } else if(tagName.equalsIgnoreCase("publisher")) {
                        book.setPublisher(currentText);
                    } else if(tagName.equalsIgnoreCase("cover")) {
                        if(!currentText.isEmpty()) {
                            byte[] coverBytes = Base64.decode(currentText, Base64.DEFAULT);
                            Bitmap coverBitmap = BitmapFactory.decodeByteArray(coverBytes, 0, coverBytes.length);
                            if(coverBitmap != null) {
                                book.setCover(coverBitmap);
                            }
                        }
                    }
                    break;
            }

            eventType = parser.next();
        }
    }
}
