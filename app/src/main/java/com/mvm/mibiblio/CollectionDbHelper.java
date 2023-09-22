package com.mvm.mibiblio;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mvm.mibiblio.models.BookModel;
import com.mvm.mibiblio.models.LoanModel;

import java.util.ArrayList;

public class CollectionDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;

    public CollectionDbHelper(Context context, String dbName) {
        super(context, dbName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS books;");
        db.execSQL("CREATE TABLE books ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title varchar(255)," +
                "author varchar(64)," +
                "cover blob," +
                "isbn varchar(32)," +
                "publisher varchar(255)," +
                "pagenum integer(16)," +
                "language varchar(255)," +
                "yearpublished integer(16)," +
                "edition integer(8)," +
                "comments varchar(512)," +
                "rating integer(8)," +
                "datebought date" +
                ");");
        db.execSQL("DROP TABLE IF EXISTS loans;");
        db.execSQL("CREATE TABLE loans ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "bookid INTEGER," +
                    "date date," +
                    "name varchar(255)," +
                    "returned boolean," +
                    "comments varchar(512)," +
                    "FOREIGN KEY(bookid) REFERENCES books(_id)" +
                ")");
        db.execSQL("DROP TABLE IF EXISTS reads;");
        db.execSQL("CREATE TABLE reads ( _id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "bookid INTEGER,"+
                "date date,"+
                "comments varchar(512)," +
                "rating integer(8), " +
                "FOREIGN KEY(bookid) REFERENCES books(_id)"+
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public Cursor getBooksAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM books ORDER BY title ASC", null);
        return c;
    }

    public Cursor getReadings(int bookId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("reads", null, "bookid = ?",
                new String[]{String.valueOf(bookId)}, null, null, null );
        return c;
    }

    public Cursor getLentBooks() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT books.* FROM books" +
                " WHERE books._id IN (SELECT bookid FROM loans WHERE returned = 0)", null);
        return c;
    }

    public Cursor getBookWithLoans() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT DISTINCT books.* FROM books " +
                "WHERE books._id IN (SELECT bookid FROM loans)" , null);
        return c;
    }

    public Cursor getBookWithReadings() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT DISTINCT books.* FROM books INNER JOIN reads " +
                "ON books._id = reads.bookid ORDER BY reads.date ASC", null);
    }

    public Cursor getLoans(BookModel book) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM loans WHERE bookid = ?",
                new String[]{String.valueOf(book.getId())});
        return c;
    }

    public void returnBook(BookModel book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("returned", true);
        db.update("loans", values, "bookid = ?",
                new String[]{String.valueOf(book.getId())});
    }
}
