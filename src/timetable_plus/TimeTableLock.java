package timetable_plus;

import java.io.Serializable;

/**
 * This class is used to Lock the SubjectData such that it is no more accepts new
 * values for editing. This is used to protect manually entered data from being
 * overwritten by auto generation.
 *
 * @author Hariprasad
 */

public class TimeTableLock implements Serializable {
    SubjectData data = null;
    boolean lock = false;
}

