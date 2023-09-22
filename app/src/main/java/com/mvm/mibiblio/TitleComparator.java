package com.mvm.mibiblio;

import com.mvm.mibiblio.models.BookModel;

import java.util.Comparator;

public class TitleComparator implements Comparator<BookModel> {
    @Override
    public int compare(BookModel book1, BookModel book2) {
        String title1 = book1.getTitle().toLowerCase();
        String title2 = book2.getTitle().toLowerCase();

        title1 = cleanModifiers(title1);
        title2 = cleanModifiers(title2);

        if(title1.startsWith("the "))
            title1 = title1.replaceFirst("the ", "");
        if(title2.startsWith("the "))
            title2 = title2.replaceFirst("the ", "");

        return title1.compareTo(title2);
    }

    public static String cleanModifiers(String s) {
        char table[][] = {
                {'Á', 'A'}, {'á','a'}, {'É', 'E'}, {'é', 'e'}, {'Í', 'I'},
                {'í', 'i'}, {'Ó', 'O'}, {'ó', 'o'}, {'Ú', 'U'}, {'ú', 'u'}
        };
        String result = s;

        for (char[] c: table) {
            result = result.replace(c[0], c[1]);
        }
        return result;
    }

    public static char getSortCharacter(BookModel book) {
        String title = book.getTitle().toUpperCase();
        title = cleanModifiers(title);

        if(title.startsWith("THE ")) {
            title = title.replaceFirst("THE ", "");
        }

        return title.charAt(0);
    }
}
