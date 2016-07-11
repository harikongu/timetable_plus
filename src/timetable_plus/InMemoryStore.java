package timetable_plus;

import javax.swing.*;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * This class stores all the contents of the timetable in memory.
 * Objects of this class is stored to disk using java serialization for
 * persistence. Since InMemoryStore objects contains simple data structures
 * internally, it is easy to store and retrieve data.
 *
 * @author Hariprasad
 */

public class InMemoryStore implements Serializable {

    static final int MAX_HOURS_PER_TEACHER = 91;
    String timeTableName;
    String acadamicYear;
    int noOfDays = 7;
    int noOfPeriods = 13;
    DefaultListModel<Object> teacherModel;
    DefaultListModel<Object> classModel;
    LinkedList<DefaultListModel> subjectModel;
    LinkedList<Object> hourTableLinkedList;
    LinkedList<LinkedList> teacherTableLinkedList;
    LinkedList<LinkedList> subject;
    LinkedList<Object> timeTable;
    boolean hourValueChanged = false;

    public InMemoryStore() {
        timeTableName = new String();
        acadamicYear = new String();

        teacherModel = new DefaultListModel<Object>();
        classModel = new DefaultListModel<Object>();

        subjectModel = new LinkedList<DefaultListModel>();
        hourTableLinkedList = new LinkedList<Object>();
        teacherTableLinkedList = new LinkedList<LinkedList>();

        subject = new LinkedList<LinkedList>();
        timeTable = new LinkedList<Object>();
    }

    void addClass() {
        subjectModel.addLast(new DefaultListModel<Object>());
        hourTableLinkedList.addLast(new LinkedList<Object>());
        teacherTableLinkedList.addLast(new LinkedList<Object>());

        subject.addLast(new LinkedList<Object>());

        TimeTableLock[][] temp = new TimeTableLock[this.noOfDays][this.noOfPeriods];
        for (int day = 0; day < noOfDays; day++)
            for (int period = 0; period < noOfPeriods; period++)
                temp[day][period] = new TimeTableLock();

        timeTable.addLast(temp);
    }

    void removeClass(int idx) {
        subjectModel.remove(idx);
        hourTableLinkedList.remove(idx);
        teacherTableLinkedList.remove(idx);

        subject.remove(idx);
        timeTable.remove(idx);
    }

    void clearClass() {
        subjectModel.clear();
        hourTableLinkedList.clear();
        teacherTableLinkedList.clear();

        subject.clear();
        timeTable.clear();
    }

    void copyTo(int from, int to) {
        int subjectCount = getSubjectModel(from).size();
        getSubjectModel(to).clear();

        for (int subject = 0; subject < subjectCount; subject++) {
            getSubjectModel(to).addElement(getSubjectModel(from).get(subject));
        }

        hourTableLinkedList.set(to, getHourTable(from).clone());

        LinkedList<LinkedList> teacherTableLinkedList1 = getTeacherTable(to);
        LinkedList subjectData = getSubject(to);

        teacherTableLinkedList1.clear();
        subjectData.clear();

        for (int subject = 0; subject < subjectCount; subject++) {
            LinkedList<String> teacher = new LinkedList<String>();
            teacher.add("Without teacher");
            teacherTableLinkedList1.add(teacher);

            SubjectData tempData = new SubjectData();
            tempData.subjectName = (String) getSubjectModel(to).get(subject);
            tempData.className = (String) classModel.get(to);
            tempData.teacherList = teacher;

            SubjectData dataFrom = (SubjectData) getSubject(from).get(subject);
            tempData.subjectColor = dataFrom.subjectColor;
            subjectData.add(tempData);
        }

        TimeTableLock[][] temp = new TimeTableLock[this.noOfDays][this.noOfPeriods];
        for (int day = 0; day < noOfDays; day++) {
            for (int period = 0; period < noOfPeriods; period++) {
                temp[day][period] = new TimeTableLock();
            }
        }

        timeTable.set(to, temp);
    }

    DefaultListModel getSubjectModel(int no) {
        return subjectModel.get(no);
    }

    LinkedList getHourTable(int no) {
        return (LinkedList) hourTableLinkedList.get(no);
    }

    LinkedList getTeacherTable(int no) {
        return teacherTableLinkedList.get(no);
    }

    LinkedList getSubject(int no) {
        return subject.get(no);
    }

    TimeTableLock[][] getTimeTable(int no) {
        return (TimeTableLock[][]) timeTable.get(no);
    }
}

