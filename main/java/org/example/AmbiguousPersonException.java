package org.example;

public class AmbiguousPersonException extends Exception {

    public AmbiguousPersonException(){
        super();
    }

    public String getMessage(){
        return "Osoba o tym imieniu i nazwisku już istnieje.";
    }
}
