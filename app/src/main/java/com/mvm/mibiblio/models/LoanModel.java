package com.mvm.mibiblio.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

public class LoanModel {
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBookId() {
        return bookId;
    }

    public void setBookId(long bookId) {
        this.bookId = bookId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public LoanModel() {}

    private long bookId;
    private Date date;
    private String name;
    private String comments;
    private boolean returned;

    public void save(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("name", this.name);
        values.put("bookid", this.bookId);
        values.put("comments", this.comments);
        values.put("date", new Date().getTime());
        values.put("returned", false);

        db.insert("loans", null, values);
    }

    public static LoanModel fromCursor(Cursor cursor) {
        LoanModel loan = new LoanModel();
        loan.setId(cursor.getInt(cursor.getColumnIndex("_id")));
        loan.setBookId(cursor.getInt(cursor.getColumnIndex("bookid")));
        long date = cursor.getLong(cursor.getColumnIndex("date"));
        loan.setDate(new Date(date));
        loan.setName(cursor.getString(cursor.getColumnIndex("name")));
        loan.setReturned(cursor.getInt(cursor.getColumnIndex("returned")) > 0);
        loan.setComments(cursor.getString(cursor.getColumnIndex("comments")));
        return loan;
    }
}
