package com.mvm.mibiblio;

import com.mvm.mibiblio.models.BookModel;

import java.util.Comparator;

public class AuthorComparator implements Comparator<BookModel> {
    @Override
    public int compare(BookModel book1, BookModel book2) {
        String author1 = book1.getAuthor().toLowerCase();
        String author2 = book2.getAuthor().toLowerCase();

        String[] author1Array = author1.split(",");
        String[] author2Array = author2.split(",");

        String author1Final, author2Final;

        if(book1.getLanguage() == null) {
            author1Final = authorFinalEn(author1Array);
        } else if(book1.getLanguage().equalsIgnoreCase("en")) {
            author1Final = authorFinalEn(author1Array);
        } else {
            author1Final = authorFinalEs(author1Array);
        }

        if(book2.getLanguage() == null || book2.getLanguage().equalsIgnoreCase("en")) {
            author2Final = authorFinalEn(author2Array);
        } else {
            author2Final = authorFinalEs(author2Array);
        }
        return author1Final.compareTo(author2Final);
    }

    public static char getSortCharacter(BookModel book) {
        String authors = book.getAuthor().toUpperCase();
        String[] authorsArray = authors.split(",");

        String authorFinal;

        if(book.getLanguage() == null) {
            authorFinal = authorFinalEn(authorsArray);
        } else if(book.getLanguage().equalsIgnoreCase("en")) {
            authorFinal = authorFinalEn(authorsArray);
        } else {
            authorFinal = authorFinalEs(authorsArray);
        }

        return authorFinal.charAt(0);
    }

    private static String authorFinalEs(String[] authorArray) {
        String[] authorPart = authorArray[0].split(" ");

        //Tres nombres: devolver el penultimo (primer apellido)
        if(authorPart.length >= 3) {
            return authorPart[authorPart.length - 2];
        } else if (authorPart.length == 2) {
            // Dos nombres, devolver apellido
            return authorPart[1];
        } else {
            return authorPart[0];
        }
    }

    private static String authorFinalEn(String[] authors) {
        String[] authorPart = authors[0].split(" ");
        return authorPart[authorPart.length - 1]; // Devolver ultima palabra (apellido)
    }
}
