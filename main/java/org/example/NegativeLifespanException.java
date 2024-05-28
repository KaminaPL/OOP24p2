package org.example;

public class NegativeLifespanException extends Exception{

    public NegativeLifespanException(){
        super();
    }

    public String getMessage(){
        return "Data śmierci niepoprawna.";
    }
}
