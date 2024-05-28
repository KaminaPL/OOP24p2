package org.example;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class Person implements Serializable{

    public  String name;
    public LocalDate birthDate;
    public LocalDate deathDate;
    public List<Person> parents;

    public Person(){}
    public Person(Person person){
        this.name = person.name;
        this.birthDate = person.birthDate;
        this.deathDate = person.deathDate;
        this.parents = new ArrayList<>();
    }
    public Person(String name, LocalDate birthDate, LocalDate deathDate){
        this.name = name;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
        this.parents = new ArrayList<>();
    }

    public String toUML(Function<String,String> postProcess, Predicate<String> condition){
        StringWriter sw = new StringWriter();
        sw.append("actor \"" + name + "\"\n");

        /* if(condition.test(name)) {
            sw.append(postProcess.apply(sw.toString()));
        } */

        if(parents.size() == 2){
            if(parents.get(0).name == null || parents.get(1).name != null) {
                sw.append("\n \""+name+"\" -->  \""+parents.get(1).name+"\": child of\n");
            } else if(parents.get(1).name == null || parents.get(0).name != null) {
                sw.append("\n \""+name+"\" -->  \""+parents.get(0).name+"\": child of\n");
            } else {
                sw.append("\n \""+name+"\" -->  \""+parents.get(0).name+"\": child of\n \\n \\\"\"+name+\"\\\" -->  \\\"\"+parents.get(1).name+\"\\\": child of\\n");
            }
        } else if(parents.size() == 1){
            sw.append("\n \""+name+"\" -->  \""+parents.get(0).name+"\": child of\n");
        }

        return sw.toString();
    }
    public static String generateTree(List<Person> personList){
        Function<String, String> makeYellow = x -> " #yellow\n";
        Predicate<String> correctName = s -> s.equals("Ewa Kowalska");
        StringWriter sw = new StringWriter();
        for(int i=0;i<personList.size();i++){
            sw.append(personList.get(i).toUML(makeYellow,correctName));
        }
        return sw.toString();
    }

    public void addParent(Person parent){
        parents.add(parent);
    }
    public static Person fromCsvLine(String line) {
        String[] parts = line.split(",");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate birthDate = LocalDate.parse(parts[1], formatter);
        LocalDate deathDate = parts[2].isEmpty() ? null : LocalDate.parse(parts[2], formatter);

        if(deathDate != null && birthDate != null && deathDate.isBefore(birthDate)){
            try{
                throw new NegativeLifespanException();
            }catch(NegativeLifespanException e){
                System.err.println("Error: "+e.getMessage());
                return null;
            }
        }

        Person person = new Person(parts[0], birthDate, deathDate);
       if(parts.length > 3 && parts[3] != null) { person.addParent(new Person(parts[3], null, null));}
        if(parts.length > 4 && parts[4] != null) { person.addParent(new Person(parts[4], null, null));}
        return person;
    }
    public static List<Person> fromCsv(String path) {
        List<Person> personList = new ArrayList<>();
        Map<String, Person> personMap = new HashMap<>();
        Scanner sc = new Scanner(System.in);
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = new String();
            br.readLine();
            while ((line = br.readLine()) != null) {
                personList.add(Person.fromCsvLine(line));
                personMap.put(Person.fromCsvLine(line).name, Person.fromCsvLine(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < personList.size(); i++) {
            for (int j = 0; j < personList.size(); j++) {
                if (i != j && personList.get(i).name == personList.get(j).name) {
                    try {
                        throw new AmbiguousPersonException();
                    } catch (AmbiguousPersonException e) {
                        System.err.println(e.getMessage());
                        return null;
                    }
                }
            }
        }

        for (int i = 0; i < personList.size(); i++) {
            if (personList.get(i).parents.size() >= 2) {
                for (int k = 1; k >= 0; k--) {
                    if (personMap.containsKey(personList.get(i).parents.get(k).name)) {
                        try {
                            if (personList.get(i).birthDate.compareTo(personMap.get(personList.get(i).parents.get(k).name).birthDate) > 15) {
                                if (personMap.get(personList.get(i).parents.get(k).name).deathDate == null || personMap.get(personList.get(i).parents.get(k).name).deathDate.isAfter(personList.get(i).birthDate)) {
                                    personList.get(i).addParent(new Person(personMap.get(personList.get(i).parents.get(k).name)));
                                    personList.get(i).parents.remove(k);
                                } else {
                                    throw new ParentingAgeException();
                                }
                            } else {
                                throw new ParentingAgeException();
                            }
                        } catch (ParentingAgeException e) {
                            System.err.println(e.getMessage() + "\nCzy dodać osobę?");
                            String answer = sc.nextLine();
                            if (answer.equals("Y")) {
                                personList.get(i).addParent(new Person(personMap.get(personList.get(i).parents.get(k).name)));
                                personList.get(i).parents.remove(k);
                            }else {
                                personList.get(i).parents.remove(k);
                            }
                        }
                    }
                }
            } else if (personList.get(i).parents.size() == 1) {
                try {
                    if (personList.get(i).birthDate.compareTo(personMap.get(personList.get(i).parents.get(0).name).birthDate) > 15) {
                        if (personMap.get(personList.get(i).parents.get(0).name).deathDate == null || personMap.get(personList.get(i).parents.get(0).name).deathDate.isAfter(personList.get(i).birthDate)) {
                            personList.get(i).addParent(new Person(personMap.get(personList.get(i).parents.get(0).name)));
                            personList.get(i).parents.remove(0);
                        } else {
                            throw new ParentingAgeException();
                        }
                    } else {
                        throw new ParentingAgeException();
                    }
                } catch (ParentingAgeException e) {
                    System.err.println(e.getMessage() + "\nCzy dodać osobę?");
                    String answer = sc.nextLine();
                    if (answer.equals("Y")) {
                        personList.get(i).addParent(new Person(personMap.get(personList.get(i).parents.get(0).name)));
                        personList.get(i).parents.remove(0);
                    }else {
                        personList.get(i).parents.remove(0);
                    }
                }
            }
        }
        return personList;
    }
    public static List<Person> getPersonListTo(List<Person> personList, String substring){
        List<Person> newPersonList = new ArrayList<>();
        for(int i=0;i< personList.size();i++){
            if(personList.get(i).name.equals(substring)){
                break;
            }
            newPersonList.add(personList.get(i));
        }
        return newPersonList;
    }
    public static List<Person> getPersonListByBirthDate(List<Person> personList){
        List<Person> newPersonList = new ArrayList<>();
        Person [] array = new Person[personList.size()];
        Person tmp;
        for(int i=0;i< personList.size();i++){
            array[i] = personList.get(i);
        }
        for(int i=0;i<(personList.size())-1;i++){
            for(int j=0;j<(personList.size()-1);j++){
                if(array[j].birthDate.isAfter(array[j+1].birthDate)){
                    tmp = array[j+1];
                    array[j+1] = array[j];
                    array[j] = tmp;
                }
            }
        }
        for(Person o : array){
            newPersonList.add(o);
        }
        return newPersonList;
    }
    public static List<Person> getDeceasedPersonListByLifetime(List<Person> personList){
        List<Person> newPersonList = new ArrayList<>();
        Person [] array = new Person[personList.size()];
        Person tmp;
        for(int i=0;i< personList.size();i++){
            array[i] = personList.get(i);
        }
        for(int i=0;i<(personList.size())-1;i++){
            for(int j=0;j<(personList.size()-1);j++){
                if(array[j].deathDate != null && array[j+1].deathDate != null) {
                    if (array[j].deathDate.compareTo(array[j].birthDate) < array[j+1].deathDate.compareTo(array[j+1].birthDate)) {
                        tmp = array[j + 1];
                        array[j + 1] = array[j];
                        array[j] = tmp;

                    }
                }
            }
        }
        for(Person o : array){
            if(o.deathDate != null) {
                newPersonList.add(o);
            }
        }
        return newPersonList;
    }
    public static Person getPersonByLifetime(List<Person> personList){
        Person [] array = new Person[personList.size()];
        Person tmp;
        for(int i=0;i< personList.size();i++){
            array[i] = personList.get(i);
        }
        for(int i=0;i<(personList.size())-1;i++){
            for(int j=0;j<(personList.size()-1);j++){
                if(array[j].birthDate.isAfter(array[j+1].birthDate)){
                    tmp = array[j+1];
                    array[j+1] = array[j];
                    array[j] = tmp;
                }
            }
        }
       for(int i=0;i< array.length;i++){
           if(array[i].deathDate == null){
               return array[i];
           }
       }
        return null;
    }
    public static void toBinaryFile(List<Person> people, String filename){
        try{
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(people);
        }catch (IOException e){
            throw new RuntimeException();
        }
    }
    public static List<Person> fromBinaryFile(String filename){
        try{
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (List<Person>) ois.readObject();
        }catch (IOException | ClassNotFoundException e){
            throw new RuntimeException();
        }
    }

}
