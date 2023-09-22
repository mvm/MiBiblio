package com.mvm.mibiblio.search;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.error.ANError;
import com.mvm.mibiblio.models.BookModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

public class GoogleBooksNetSearch extends AsyncTask<ContentValues, Integer, List<BookModel>> {
    private final String url = "https://www.googleapis.com/books/v1/volumes?q={query}&key={apikey}";
    private final String apiKey = "AIzaSyCSgbGTYjEVviR76FCQ4bySx0u7Kh1WLps";
    private OnResultListener listener;

    public GoogleBooksNetSearch(OnResultListener listener) {
        this.listener = listener;
    }
    @Override
    protected List<BookModel> doInBackground(ContentValues... params) {
        StringBuilder queryBuilder = new StringBuilder();

        for(String key : params[0].keySet()) {
            if(key.equalsIgnoreCase("title")) {
                queryBuilder.append("intitle:");
                queryBuilder.append(params[0].getAsString(key));
            } else if(key.equalsIgnoreCase("author")) {
                queryBuilder.append("inauthor:");
                queryBuilder.append(params[0].getAsString(key));
            } else if(key.equalsIgnoreCase("isbn")) {
                queryBuilder.append("isbn:");
                queryBuilder.append(params[0].getAsString(key));
            } else if(key.equalsIgnoreCase("publisher")) {
                queryBuilder.append("publisher:");
                queryBuilder.append(params[0].getAsString(key));
            }

            queryBuilder.append(" ");
        }

        ANRequest request = AndroidNetworking.get(this.url)
                .addPathParameter("query", queryBuilder.toString())
                .addPathParameter("apikey", this.apiKey)
                .setExecutor(Executors.newSingleThreadExecutor())
                .build();

        ANResponse<JSONObject> anResponse = request.executeForJSONObject();


        try {
            return parseBooks(anResponse.getResult());
        } catch(Exception e) {
            Log.e("mibiblio", "Error: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<BookModel> bookModels) {
        this.listener.onResult(bookModels);
    }

    public interface OnResultListener {
        void onResult(List<BookModel> adapter);
    }

    private BookModel parseBook(JSONObject object) throws JSONException {
        BookModel result = new BookModel();
        JSONObject volumeInfo = object.getJSONObject("volumeInfo");
        result.setTitle(volumeInfo.getString("title"));

        if(volumeInfo.has("authors")) {
            JSONArray arrayAuthors = volumeInfo.getJSONArray("authors");
            List<String> authors = new LinkedList<>();
            for(int i = 0; i < arrayAuthors.length(); i++) {
                authors.add(arrayAuthors.getString(i));
            }
            String authorsString = TextUtils.join(", ", authors);
            result.setAuthor(authorsString);
        }

        if(volumeInfo.has("publisher"))
            result.setPublisher(volumeInfo.getString("publisher"));
        if(volumeInfo.has("pageCount"))
            result.setPageNum(volumeInfo.getInt("pageCount"));
        if(volumeInfo.has("language"))
            result.setLanguage(volumeInfo.getString("language"));
        // leer el ISBN 13 de los resultados
        if(volumeInfo.has("industryIdentifiers")) {
            JSONArray identifiers = volumeInfo.getJSONArray("industryIdentifiers");
            for(int i = 0; i < identifiers.length(); i++) {
                JSONObject id = identifiers.getJSONObject(i);
                if(id.has("type") && id.has("identifier") &&
                        id.getString("type").equalsIgnoreCase("ISBN_13")) {
                    result.setIsbn(id.getString("identifier"));
                }
            }
        }

        if(volumeInfo.has("imageLinks")) {
            JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
            if(imageLinks.has("smallThumbnail")) {
                String coverUrl = imageLinks.getString("smallThumbnail");

                try {
                    // solo se nos permite usar HTTPS; convertir URL a HTTPS
                    URL httpUrl = new URL(coverUrl);
                    URL httpsUrl = new URL("https", httpUrl.getHost(), httpUrl.getPort(),
                            httpUrl.getFile(), null);
                    // descargar portada de libro
                    ANRequest coverRequest = AndroidNetworking.get(httpsUrl.toString())
                            .setBitmapMaxHeight(128)
                            .setBitmapMaxWidth(128)
                            .build();
                    ANResponse<Bitmap> coverResponse = coverRequest.executeForBitmap();
                    if(coverResponse.isSuccess()) {
                        result.setCover(coverResponse.getResult());
                    } else {
                        ANError error = coverResponse.getError();
                        error.printStackTrace();
                        result.setCover(null);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private List<BookModel> parseBooks(JSONObject object) throws JSONException {
        List<BookModel> list = new LinkedList<>();
        JSONArray items = object.getJSONArray("items");

        for(int i = 0; i < items.length(); i++) {
            JSONObject book = items.getJSONObject(i);
            list.add(this.parseBook(book));
        }
        return list;
    }
}
