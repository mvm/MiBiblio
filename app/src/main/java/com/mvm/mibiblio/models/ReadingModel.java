package com.mvm.mibiblio.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

public class ReadingModel {
    private long id;
    private long bookId;
    private Date date;
    private String comments;
    private int rating;

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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getRating() { return rating; }

    public void setRating(int rating) { this.rating = rating; }

    public void insert(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("date", this.date.getTime());
        values.put("comments", this.comments);
        values.put("bookid", this.bookId);
        values.put("rating", this.rating);

        db.insert("reads", null, values);
    }
    
    public static ReadingModel fromCursor(Cursor cursor) {
        ReadingModel read = new ReadingModel();
        read.setId(cursor.getInt(cursor.getColumnIndex("_id")));
        read.setBookId(cursor.getInt(cursor.getColumnIndex("bookid")));
        long date = cursor.getLong(cursor.getColumnIndex("date"));
        read.setDate(new Date(date));
        read.setComments(cursor.getString(cursor.getColumnIndex("comments")));
        read.setRating(cursor.getInt(cursor.getColumnIndex("rating")));
        return read;
    }
}
