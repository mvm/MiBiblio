package com.mvm.mibiblio.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class BookModel implements Parcelable {
    public static SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Date getYearPublished() {
        return yearPublished;
    }

    public void setYearPublished(Date yearPublished) {
        this.yearPublished = yearPublished;
    }

    public int getEdition() {
        return edition;
    }

    public void setEdition(int edition) {
        this.edition = edition;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Date getDateBought() {
        return dateBought;
    }

    public void setDateBought(Date dateBought) {
        this.dateBought = dateBought;
    }

    private String title;
    private String author;
    private Bitmap cover;
    private String isbn;
    private String publisher;
    private int pageNum;
    private String language;
    private Date yearPublished;
    private int edition;
    private String comments;
    private int rating;
    private Date dateBought;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public BookModel createFromParcel(Parcel source) {
            return new BookModel(source);
        }

        @Override
        public BookModel[] newArray(int size) {
            return new BookModel[size];
        }
    };

    private BookModel(Parcel source) {
        this.id = source.readInt();
        this.title = source.readString();
        this.author = source.readString();
        this.isbn = source.readString();
        this.publisher = source.readString();
        this.pageNum = source.readInt();
        this.language = source.readString();
        this.edition = source.readInt();
        this.comments = source.readString();
        this.rating = source.readInt();
        this.dateBought = new Date(source.readLong());
        this.cover = Bitmap.CREATOR.createFromParcel(source);
        this.yearPublished = new Date(source.readLong());
    }

    public BookModel(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.cover = null;
        this.isbn = "";
        this.publisher = "";
        this.pageNum = 0;
        this.language = "";
        this.edition = 0;
        this.comments = "";
        this.rating = 0;
        this.dateBought = new Date();
        this.yearPublished = new Date();
    }

    public BookModel() {
        // nothing
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.title);
        dest.writeString(this.author);

        dest.writeString(this.isbn);
        dest.writeString(this.publisher);
        dest.writeInt(this.pageNum);
        dest.writeString(this.language);
        dest.writeInt(this.edition);
        dest.writeString(this.comments);
        dest.writeInt(this.rating);

        if(this.dateBought != null)
            dest.writeLong(this.dateBought.getTime());
        else
            dest.writeLong(0);

        if(this.cover == null)
            this.cover = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888);
        this.cover.writeToParcel(dest, 0);

        if(this.yearPublished != null)
            dest.writeLong(yearPublished.getTime());
        else
            dest.writeLong((new Date()).getTime());
    }

    private ContentValues toValues() {
        ContentValues values = new ContentValues();
        values.put("title", this.title);
        values.put("author", this.author);
        values.put("isbn", this.isbn);
        values.put("publisher", this.publisher);
        values.put("pagenum", this.pageNum);
        values.put("language", this.language);
        values.put("edition", this.edition);
        values.put("comments", this.comments);
        values.put("rating", this.rating);
        if(this.cover != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            this.cover.compress(Bitmap.CompressFormat.PNG, 100, bos);
            values.put("cover", bos.toByteArray());
        }
        if(this.dateBought != null)
            values.put("datebought", this.dateBought.getTime());
        if(this.yearPublished != null)
            values.put("yearpublished", this.yearPublished.getTime());
        return values;
    }

    public static BookModel fromCursor(Cursor cursor) {
        BookModel book = new BookModel();
        book.setId(cursor.getInt(cursor.getColumnIndex("_id")));
        book.setTitle(cursor.getString(cursor.getColumnIndex("title")));
        book.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
        book.setPublisher(cursor.getString(cursor.getColumnIndex("publisher")));
        book.setEdition(cursor.getInt(cursor.getColumnIndex("edition")));
        book.setPageNum(cursor.getInt(cursor.getColumnIndex("pagenum")));
        book.setIsbn(cursor.getString(cursor.getColumnIndex("isbn")));
        book.setComments(cursor.getString(cursor.getColumnIndex("comments")));
        book.setRating(cursor.getInt(cursor.getColumnIndex("rating")));
        book.setLanguage(cursor.getString(cursor.getColumnIndex("language")));

        byte[] coverBytes = cursor.getBlob(cursor.getColumnIndex("cover"));
        if(coverBytes != null)
            book.setCover(BitmapFactory.decodeByteArray(coverBytes, 0, coverBytes.length));

        long publishedTime = cursor.getLong(cursor.getColumnIndex("yearpublished"));
        book.setYearPublished(new Date(publishedTime));
        long boughtTime = cursor.getLong(cursor.getColumnIndex("datebought"));
        book.setDateBought(new Date(boughtTime));

        return book;
    }

    public void update(SQLiteDatabase db) {
        db.update("books", this.toValues(), "_id = ?",
                new String[]{String.valueOf(this.id)});
    }

    public void insert(SQLiteDatabase db) {
        db.insert("books", null, this.toValues());
    }

    public void delete(SQLiteDatabase db) {
        db.delete("books", "_id = ?", new String[]{String.valueOf(this.getId())});
    }

    public static boolean verifyISBN(String isbn) {
        return Pattern.matches("(\\d{13}|\\d{10})", isbn);
    }

    public static Bitmap resizeCover(Bitmap cover) {
        float aspectRatio = cover.getWidth() / (float)cover.getHeight();
        int width = Math.min(Math.round(128 * aspectRatio), 96);
        Bitmap bitmapFinal = Bitmap.createScaledBitmap(cover, width, 128, true);
        return bitmapFinal;
    }
}
