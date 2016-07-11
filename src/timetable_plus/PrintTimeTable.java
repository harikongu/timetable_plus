package timetable_plus;

import javax.swing.*;
import java.awt.*;

/**
 * This class prints the timetable displaying on screen.
 * Graphics functions are used in this class.
 *
 * @author Hariprasad
 */

public class PrintTimeTable extends JPanel {

    private InMemoryStore memory;
    private int selectedX = 0, selectedY = 0;

    private int titleValue = 0;
    private int class_teacher = 1;
    private int teacherTitleValue = 0;
    private ImageIcon TimeTable;
    private ImageIcon mouseMove;
    private ImageIcon mouseClick;
    private ImageIcon extra1;
    private ImageIcon extra2;
    private ImageIcon lock;

    PrintTimeTable(InMemoryStore mem) {
        memory = mem;

        TimeTable = new ImageIcon("images/timetable_grid.png");
        mouseMove = new ImageIcon("images/green_rect.png");
        mouseClick = new ImageIcon("images/blue_rect.png");

        extra1 = new ImageIcon("images/extra_cell1.png");
        extra2 = new ImageIcon("images/extra_cell2.png");

        lock = new ImageIcon("images/lock.png");

        setSize(701, 381);
    }

    @Override
    public void paint(Graphics g) {
        // super.paint(g);
        Image test = createImage(701, 381);
        Graphics gc = test.getGraphics();

        int midX = 0, midY = 0;
        if (memory.noOfPeriods != 13) {
            midX = 227 - 22 - (memory.noOfPeriods * 35) / 2;
        }
        if (memory.noOfDays != 7) {
            midY = 105 - 15 - (memory.noOfDays * 30) / 2;
        }

        gc.setColor(Color.white);
        gc.fillRect(0, 0, 500, 241);
        gc.setColor(Color.black);
        gc.setFont(new Font("Tahoma", java.awt.Font.PLAIN, 11));

        if (class_teacher == 1) {
            TimeTableLock[][] temp = memory.getTimeTable(titleValue);
            for (int day = 0; day < memory.noOfDays; day++) {
                for (int period = 0; period < memory.noOfPeriods; period++) {
                    if (temp[day][period].data == null) {
                        gc.setColor(new Color(236, 233, 216));
                    } else if (temp[day][period].data != null && temp[day][period].data.teacherList.get(0).equals("Without teacher")) {
                        gc.setColor(Color.white);
                        gc.fillRect(period * 35 + 43, day * 30 + 30, 35, 30);
                        gc.setColor(Color.black);
                        gc.drawString(temp[day][period].data.subjectName, period * 35 + 55, day * 30 + 58);
                        if (temp[day][period].lock == true) {
                            lock.paintIcon(this, gc, period * 35 + 50 + 40 - 4, day * 30 + 30 + 4);
                        }
                    } else if (temp[day][period].data != null && !temp[day][period].data.teacherList.get(0).equals("Without teacher")) {

                        gc.setColor(Color.black);
                        gc.drawString(temp[day][period].data.subjectName, period * 35 + 55, day * 30 + 58);
                        if (temp[day][period].lock == true) {
                            lock.paintIcon(this, gc, period * 35 + 50 + 40 - 4, day * 30 + 30 + 4);
                        }
                    }
                }
            }
        } else {

            for (int day = 0; day < memory.noOfDays; day++) {
                for (int period = 0; period < memory.noOfPeriods; period++) {
                    for (int classIdx = 0; classIdx < memory.classModel.size(); classIdx++) {
                        TimeTableLock[][] temp = memory.getTimeTable(classIdx);

                        if (temp[day][period].data != null) {
                            if (temp[day][period].data.teacherList.contains(memory.teacherModel.get(teacherTitleValue))) {
                                gc.setColor(temp[day][period].data.subjectColor);
                                gc.fillRect(period * 50 + 50, day * 50 + 30, 50, 50);
                                gc.setColor(Color.black);
                                gc.drawString(temp[day][period].data.subjectName, period * 50 + 55, day * 50 + 58);
                                gc.drawString("(" + temp[day][period].data.className + ")", period * 50 + 55, day * 50 + 58 + 10);
                                if (temp[day][period].lock == true) {
                                    lock.paintIcon(this, gc, period * 50 + 50 + 40 - 4, day * 50 + 30 + 4);
                                }
                            }
                        }
                    }
                }
            }
        }

        TimeTable.paintIcon(this, gc, midX + 0, midY + 0);

        gc.setColor(Color.white);
        int height = (7 - memory.noOfDays) * 30;
        int width = (13 - memory.noOfPeriods) * 35;
        gc.fillRect(midX + 0, midY + 30 + 210 - height + 1, 455 + 44 + 1, height);// horizantal
        gc.fillRect(midX + 44 + 455 - width + 1, midY + 0, width, 210 + 30 + 1);// vertical

        g.drawImage(test, 0, 0, this);
    }
}

