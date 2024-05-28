package org.example;

import java.util.Scanner;

public class ParentingAgeException  extends Exception {

    public ParentingAgeException(){
        super();
    }

    public String getMessage(){
        return "Rodzic młodszy niż 15 lat lub nie żyje";
    }



}

