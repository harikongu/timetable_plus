package timetable_plus;

import java.util.LinkedList;
import java.util.Random;

/**
 * TimeTableEngine is the core part of automated timetable scheduling system.
 * This class contains methods which will automatically fill the timetable
 * for all teachers and subjects. Genetic algorithm(Selection and Mutation)
 * is used in this timetable generation program.
 * <p>
 * Genetic Algorithm:
 * ------------------
 * https://www.doc.ic.ac.uk/~nd/surprise_96/journal/vol1/hmw/article1.html
 * <p>
 * This timetable generation program will work even if the timetable is
 * partially filled manually.
 *
 * @author Hariprasad
 */

public class TimeTableEngine {
    private int product;// This is no. of periods*no.days
    private int noOfDays;
    private int noOfPeriods;
    private int noOfSubjects;

    // contains no. of hours per week for all subjects
    private int[] subjectHoursPerWeek = new int[50];
    private int[] initialExtra = new int[50];// Contains unallocated subjects

    private int[][] initialValues = new int[7][50];// For 2D arrays [days] X [subject index]
    private int[][] initialValuesCopy = new int[7][50];
    private int[] initialTotal = new int[50];// Sum of initial reserved subjects
    private int outputTimetable[][] = new int[7][50];

    void setTimeTableSize(int days, int periods) {
        this.noOfDays = days;
        this.noOfPeriods = periods;
        this.product = days * periods;
    }

    void setNoSubject(int noOfSubjects) {
        this.noOfSubjects = noOfSubjects;
    }

    // Sets the no. of hours per week for each subject
    void setSubjectHoursPerWeek(int index, int noOfHours) {
        this.subjectHoursPerWeek[index] = noOfHours;
    }

    // Row are days; columns are subjects
    void setManuallyAllotedSubject(int day, int subjectIndex, int value) {
        this.initialValues[day][subjectIndex] = value;
        this.initialValuesCopy[day][subjectIndex] = value;
    }

    void findManuallyAllotedSubject() {
        for (int day = 0; day < noOfDays; day++) {
            for (int subject = 0; subject < noOfSubjects; subject++) {
                this.initialTotal[subject] = this.initialTotal[subject] + this.initialValues[day][subject];
            }
        }
    }

    void findUnallocatedSubjects() {
        for (int subject = 0; subject < noOfSubjects; subject++) {
            this.initialExtra[subject] = this.subjectHoursPerWeek[subject] - this.initialTotal[subject];
            System.out.println(this.initialExtra[subject]);
        }
    }

    /*
     * This function will generate timetable automatically.
     * Genetic algorithm(Selection and Mutation) is used in this function.
     * Mutation will produce different combination of timetable. From this,
     * fittest timetable will be selected for every row.
     */
    void generateTimeTable() {
        LinkedList<Object> dataList1 = new LinkedList<Object>();

        for (int subject = 0; subject < noOfSubjects; subject++) {
            if (this.initialExtra[subject] > 0)
                dataList1.add(subject);
        }

        while (dataList1.size() != 0) {
            Random r = new Random();
            int rand = r.nextInt(dataList1.size());// Random value from 0 to noOfDays
            int subjectIndex = Integer.parseInt(String.valueOf(dataList1.get(rand)));

            System.out.print(subjectIndex + ",");
            int collect = 0;

            while (this.initialExtra[subjectIndex] > 0) {
                LinkedList<Object> dataList = new LinkedList<Object>();
                LinkedList<Object> tempList = new LinkedList<Object>();
                for (int day = 0; day < noOfDays; day++) {
                    if (this.initialValues[day][subjectIndex] == collect)
                        tempList.add(day);
                }

                while (tempList.size() != 0) {
                    int randVal = r.nextInt(tempList.size());
                    dataList.add(tempList.remove(randVal));
                }

                // Bubble Short sort
                for (int idx = dataList.size() - 1; idx > 0; idx--) {
                    for (int num = 0; num < idx; num++) {
                        int day1 = Integer.parseInt(String.valueOf(dataList.get(num)));
                        int day2 = Integer.parseInt(String.valueOf(dataList.get(num + 1)));

                        int sum1 = 0;
                        for (int subject = 0; subject < noOfSubjects; subject++) {
                            sum1 = sum1 + this.initialValues[day1][subject];
                        }

                        int sum2 = 0;
                        for (int subject = 0; subject < noOfSubjects; subject++) {
                            sum2 = sum2 + this.initialValues[day2][subject];
                        }

                        if (sum1 > sum2) {
                            Object obj = dataList.get(num);
                            dataList.set(num, dataList.get(num + 1));
                            dataList.set(num + 1, obj);
                        }
                    }
                }

                for (int idx = 0; idx < dataList.size(); idx++) {
                    int day = Integer.parseInt(String.valueOf(dataList.get(idx)));

                    int sum = 0;
                    for (int subject = 0; subject < noOfSubjects; subject++) {
                        sum = sum + this.initialValues[day][subject];
                    }

                    if (sum >= noOfPeriods) {
                    } else {
                        this.initialValues[day][subjectIndex]++;
                        this.initialExtra[subjectIndex]--;
                    }

                    if (this.initialExtra[subjectIndex] == 0) {
                        break;
                    }
                }
                collect++;
            }
            dataList1.remove(rand);
        }
    }

    void forTestingOutput() {
        System.out.println();
        for (int day = 0; day < noOfDays; day++) {
            for (int subject = 0; subject < noOfSubjects; subject++) {
                System.out.print(this.initialValues[day][subject] + ",");
            }
            System.out.println();
        }
    }

    /*
     * This function will return programmatically generated timetable.
     */
    int[][] getOutput() {
        System.out.println();
        for (int day = 0; day < noOfDays; day++) {
            for (int subject = 0; subject < noOfSubjects; subject++) {
                outputTimetable[day][subject] = this.initialValues[day][subject] - this.initialValuesCopy[day][subject];
                System.out.print(outputTimetable[day][subject] + ",");
            }
            System.out.println();
        }
        return outputTimetable;
    }
}

