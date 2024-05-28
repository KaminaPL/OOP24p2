package org.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PlantUMLRunner {

    public static String path;

    public static void generateDiagram(String text, String dir, String fileName){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(dir+fileName))){
            bw.append("@startuml\n")
                    .append(text)
                    .append("\n@enduml");
        }catch(IOException  e){
            throw new RuntimeException();
        }
    }
}
