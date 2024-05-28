package org.example;


import java.util.List;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) {
        List<Person> personList = Person.fromCsv("/home/bartosz/IdeaProjects/OOP24_P/src/family.csv");
        //List<Person> personList = Person.fromBinaryFile("PersonList.dat");

        //PlantUMLRunner.generateDiagram(Person.generateTree(personList),"/home/bartosz/IdeaProjects/OOP24_P/src/","Tree.puml");

        //List<Person> secondpersonList = Person.getPersonListTo(personList, "Jan Kowalski");
        List<Person> secondpersonList = Person.getPersonListByBirthDate(personList);
        //List<Person> secondpersonList = Person.getDeceasedPersonListByLifetime(personList);

        // Person.toBinaryFile(personList, "PersonList.dat");

        PlantUMLRunner.generateDiagram(Person.generateTree(secondpersonList),"/home/bartosz/IdeaProjects/OOP24_P/src/","Tree.puml");



    }
}