package timetable_plus;

import java.awt.*;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * This class will store the details of a subject(like name of the subject,
 * teacher who handles the subject). Objects of SubjectData will be stored in
 * InMemoryStore.
 *
 * @author Hariprasad
 */

public class SubjectData implements Serializable {
    String subjectName = "";
    String className = "";
    LinkedList<String> teacherList = null;
    Color subjectColor = Color.white;
}

