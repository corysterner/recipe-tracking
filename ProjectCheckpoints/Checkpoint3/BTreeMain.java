import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main Application.
 */
public class BTreeMain {

    public static void main(String[] args) {

        long maxRecordId = 0;

        //Read the input file -- input.txt
        Scanner scan = null;
        try {
            scan = new Scanner(new File("input.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            e.printStackTrace();
            return;
        }

        /*
          Read the minimum degree of B+Tree first
          */

        int degree = scan.nextInt();

        BTree bTree = new BTree(degree);

        /* Reading the database student.csv into B+Tree Node*/
        List<Student> studentsDB = getStudents();

        for (Student s : studentsDB) {
            bTree.insert(s);
            if(s.recordId > maxRecordId){
                maxRecordId = s.recordId;
            }
        }

        /* Start reading the operations now from input file*/
        try {
            while (scan.hasNextLine()) {
                Scanner s2 = new Scanner(scan.nextLine());

                while (s2.hasNext()) {

                    String operation = s2.next();

                    switch (operation) {
                        case "insert": {

                            // Build student values from input file
                            long studentId = Long.parseLong(s2.next());
                            String studentName = s2.next() + " " + s2.next();
                            String major = s2.next();
                            String level = s2.next();
                            int age = Integer.parseInt(s2.next());

                            long recordID = maxRecordId++; //Increment max record ID to get next one

                            // Check whether that student record already exists
                            long existingRecordId = -1;
                            existingRecordId = bTree.search(studentId);

                            if(existingRecordId != -1){
                                // Don't insert if student already exists
                                System.out.println("Student Id already exists.");
                            } else {
                                // Build student and insert into tree
                                Student s = new Student(studentId, age, studentName, major, level, recordID);
                                bTree.insert(s);

                                // Append new student to file
                                FileWriter fw = new FileWriter("Student.csv", true);
                                BufferedWriter bw = new BufferedWriter(fw);
                                bw.write(s.csvString());
                                bw.newLine();
                                bw.close();
                            }

                            break;
                        }
                        case "delete": {
                            long studentId = Long.parseLong(s2.next());
                            boolean result = bTree.delete(studentId);
                            if (result)
                                System.out.println("Student deleted successfully.");
                            else
                                System.out.println("Student deletion failed.");

                            break;
                        }
                        case "search": {
                            long studentId = Long.parseLong(s2.next());
                            long recordID = bTree.search(studentId);
                            if (recordID != -1)
                                System.out.println("Student exists in the database at " + recordID);
                            else
                                System.out.println("Student does not exist.");
                            break;
                        }
                        case "print": {
                            List<Long> listOfRecordID;
                            listOfRecordID = bTree.print();
                            System.out.println("List of recordIDs in B+Tree " + listOfRecordID.toString());
                        }
                        default:
                            System.out.println("Wrong Operation");
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the list of students from Student.csv
     * @return - list of student objects
     */
    private static List<Student> getStudents() {

        /*
         * Extract the students information from "Students.csv"
         * return the list<Students>
         */

        //Create scanner
        Scanner scan = null;
        try {
            scan = new Scanner(new File("Student.csv"));
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }

        //Create student list
        List<Student> studentList = new ArrayList<>();

        String line;

        while (scan.hasNextLine()) {
            line = scan.nextLine();
            String[] studentValues = line.split(",");
            studentList.add(new Student(
                    Long.parseLong(studentValues[0]), // StudentId
                    Integer.parseInt(studentValues[4]), // Age
                    studentValues[1], // Name
                    studentValues[2], // Major
                    studentValues[3], // Level
                    Long.parseLong(studentValues[5]))); // RecordId
        }

        return studentList;
    }
}
