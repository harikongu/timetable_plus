package timetable_plus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * This class is to display the contents of timetable on screen.
 * Contents stored in memory store will be displayed.
 * Graphics functions are used in this class.
 *
 * @author Hariprasad
 */

public class DrawTimeTable extends JPanel {
    private InMemoryStore tableData;

    private int selectedX = 0;
    private int selectedY = 0;

    private int titleValue = 0;
    private int classTeacher = 1;
    private int teacherTitleValue = 0;

    private boolean row = false;
    private boolean column = false;

    private int rowNo = 0;
    private int columnNo = 0;
    private ImageIcon background1;
    private ImageIcon background2;
    private ImageIcon TimeTable;
    private ImageIcon mouseMove;
    private ImageIcon mouseClick;
    private ImageIcon extra1;
    private ImageIcon extra2;
    private ImageIcon lock;

    private int startX = 0;
    private int startY = 0;
    private int endX = 0;
    private int endY = 0;
    private int dragX = 0;
    private int dragY = 0;
    private boolean isMouseDrag = false;

    private SubjectData startData;
    private SubjectData endData;
    private SubjectData tempData;

    // moving data from source to destination(-->)
    private boolean sourceToDestination = false;
    private boolean destnationToSource = false;
    private boolean clashFree[][];
    private int moveRecX = 0;
    private int moveRecY = 0;
    private boolean allow = true;

    DrawTimeTable(InMemoryStore mem) {
        tableData = mem;
        background1 = new ImageIcon("images/background_ash.png");
        background2 = new ImageIcon("images/background_white.png");

        TimeTable = new ImageIcon("images/timetable_grid2.png");
        mouseMove = new ImageIcon("images/green_rect.png");
        mouseClick = new ImageIcon("images/blue_rect.png");

        extra1 = new ImageIcon("images/extra_cell1.png");
        extra2 = new ImageIcon("images/extra_cell2.png");
        lock = new ImageIcon("images/lock.png");

        setSize(701, 381);
        this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                drawMouseMoved(evt);
            }
        });
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                drawMouseClicked(evt);
            }
        });
        this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                if (isMouseDrag == true) {
                    drawMouseDragged(evt);
                }
            }
        });
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                drawMousePressed(evt);
            }
        });
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                drawMouseReleased(evt);
            }
        });

        clashFree = new boolean[7][13];
        for (int day = 0; day < 7; day++) {
            for (int subject = 0; subject < 13; subject++) {
                clashFree[day][subject] = false;
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        // super.paint(g);
        Image img = createImage(701, 381);
        Graphics gc = img.getGraphics();

        background1.paintIcon(this, gc, 0, 0);
        background2.paintIcon(this, gc, 50, 30);

        if (row == true) {
            extra1.paintIcon(this, gc, 0, rowNo * 50 + 30);
        }
        if (column == true) {
            extra2.paintIcon(this, gc, columnNo * 50 + 50, 0);
        }

        gc.setFont(new Font("Tahoma", java.awt.Font.PLAIN, 11));

        if (classTeacher == 1) {
            TimeTableLock[][] table = tableData.getTimeTable(titleValue);

            for (int day = 0; day < tableData.noOfDays; day++) {
                for (int period = 0; period < tableData.noOfPeriods; period++) {
                    if (table[day][period].data == null) {
                        gc.setColor(new Color(236, 233, 216));
                        gc.fillRect(period * 50 + 50, day * 50 + 30, 50, 50);
                    } else if (table[day][period].data != null && table[day][period].data.teacherList.get(0).equals("Without teacher")) {
                        gc.setColor(Color.white);
                        gc.fillRect(period * 50 + 50, day * 50 + 30, 50, 50);
                        gc.setColor(Color.black);
                        gc.drawString(table[day][period].data.subjectName, period * 50 + 55, day * 50 + 58);
                        if (table[day][period].lock == true) {
                            lock.paintIcon(this, gc, period * 50 + 50 + 40 - 4, day * 50 + 30 + 4);
                        }
                    } else if (table[day][period].data != null && !table[day][period].data.teacherList.get(0).equals("Without teacher")) {
                        gc.setColor(table[day][period].data.subjectColor);
                        gc.fillRect(period * 50 + 50, day * 50 + 30, 50, 50);
                        gc.setColor(Color.black);
                        gc.drawString(table[day][period].data.subjectName, period * 50 + 55, day * 50 + 58);
                        if (table[day][period].lock == true) {
                            lock.paintIcon(this, gc, period * 50 + 50 + 40 - 4, day * 50 + 30 + 4);
                        }
                    }
                }
            }
        } else {
            for (int day = 0; day < tableData.noOfDays; day++) {
                for (int period = 0; period < tableData.noOfPeriods; period++) {
                    boolean match = false;
                    for (int classIdx = 0; classIdx < tableData.classModel.size(); classIdx++) {
                        TimeTableLock[][] table = tableData.getTimeTable(classIdx);

                        if (table[day][period].data != null) {
                            if (table[day][period].data.teacherList.contains(tableData.teacherModel.get(teacherTitleValue))) {
                                gc.setColor(table[day][period].data.subjectColor);
                                gc.fillRect(period * 50 + 50, day * 50 + 30, 50, 50);
                                gc.setColor(Color.black);
                                gc.drawString(table[day][period].data.subjectName, period * 50 + 55, day * 50 + 58);
                                gc.drawString("(" + table[day][period].data.className + ")", period * 50 + 55, day * 50 + 58 + 10);
                                if (table[day][period].lock == true) {
                                    lock.paintIcon(this, gc, period * 50 + 50 + 40 - 4, day * 50 + 30 + 4);
                                }

                                match = true;
                                break;
                            }
                        }
                    }

                    if (match == false) {
                        gc.setColor(new Color(236, 233, 216));
                        gc.fillRect(period * 50 + 50, day * 50 + 30, 50, 50);
                    }
                }
            }
        }

        TimeTable.paintIcon(this, gc, 0, 0);

        if (allow == true) {
            mouseMove.paintIcon(this, gc, moveRecX * 50 + 50, moveRecY * 50 + 30);
        }
        mouseClick.paintIcon(this, gc, selectedX * 50 + 50, selectedY * 50 + 30);

        gc.setColor(Color.white);

        // To draw draging subject
        if (isMouseDrag == true) {
            gc.setColor(startData.subjectColor);
            gc.fillRect(dragX, dragY, 50, 50);
            gc.setColor(Color.black);
            gc.drawString(startData.subjectName, dragX + 5, dragY + 28);
            if (classTeacher != 1) {
                gc.drawString("(" + startData.className + ")", dragX + 5, dragY + 28 + 10);
            }

            gc.drawRect(dragX, dragY, 50, 50);

            // This is to draw Class level clash free cells and for teacher view also
            for (int day = 0; day < tableData.noOfDays; day++) {
                for (int period = 0; period < tableData.noOfPeriods; period++) {
                    if (clashFree[day][period] == true) {
                        mouseMove.paintIcon(this, gc, period * 50 + 50, day * 50 + 30);
                    }
                }
            }
        }

        if (isMouseDrag == true) {
            if (destnationToSource == true) {
                gc.setColor(Color.green);
            } else {
                gc.setColor(Color.red);
            }
            gc.fillRect(startX * 50 + 50, startY * 50 + 30, 15, 15);// drag
            // source
            if (sourceToDestination == true) {
                gc.setColor(Color.green);
            } else {
                gc.setColor(Color.red);
            }
            gc.fillRect(dragX, dragY, 15, 15);// target
        }
        g.drawImage(img, 0, 0, this);
    }

    private void drawMouseMoved(java.awt.event.MouseEvent evt) {
        int moveX = evt.getX();
        int moveY = evt.getY();

        if (moveX < 50 || moveY < 30 || moveX >= tableData.noOfPeriods * 50 + 50 || moveY >= tableData.noOfDays * 50 + 30) {
            allow = false;
        } else {
            allow = true;
        }

        if (moveX >= 50 && moveY >= 30) {
            moveX = moveX - 50;
            int x = 0;
            for (x = 0; moveX >= 0; x++) {
                moveX = moveX - 50;
            }

            moveY = moveY - 30;
            int y = 0;
            for (y = 0; moveY >= 0; y++) {
                moveY = moveY - 50;
            }

            if (x > tableData.noOfPeriods) {
                moveRecX = x - 2;
            } else {
                moveRecX = x - 1;
            }

            if (y > tableData.noOfDays) {
                moveRecY = y - 2;
            } else {
                moveRecY = y - 1;
            }
        }
        repaint();
    }

    private void drawMouseClicked(java.awt.event.MouseEvent evt) {
        int clickX = evt.getX();
        int clickY = evt.getY();

        if (clickX >= 50 && clickY >= 30) {
            clickX = clickX - 50;
            int x = 0;
            for (x = 0; clickX >= 0; x++) {
                clickX = clickX - 50;
            }

            clickY = clickY - 30;
            int y = 0;
            for (y = 0; clickY >= 0; y++) {
                clickY = clickY - 50;
            }

            if (x > tableData.noOfPeriods) {
                selectedX = x - 2;
            } else {
                selectedX = x - 1;
            }

            if (y > tableData.noOfDays) {
                selectedY = y - 2;
            } else {
                selectedY = y - 1;
            }
        }

        if (evt.getButton() == MouseEvent.BUTTON3) {
            if (clickX < 50 && clickY >= 30) {
                clickY = clickY - 30;
                int y = 0;

                for (y = 0; clickY >= 0; y++) {
                    clickY = clickY - 50;
                }

                if (y > tableData.noOfDays) {
                    rowNo = y - 2;
                } else {
                    rowNo = y - 1;
                }
                row = true;
            } else {
                row = false;
            }

            clickX = evt.getX();
            clickY = evt.getY();

            if (clickY < 30 && clickX >= 50) {
                clickX = clickX - 50;
                int j = 0;

                for (j = 0; clickX >= 0; j++) {
                    clickX = clickX - 50;
                }

                if (j > tableData.noOfPeriods) {
                    columnNo = j - 2;
                } else {
                    columnNo = j - 1;
                }

                column = true;

            } else {
                column = false;
            }

        }

        repaint();
    }

    private void drawMousePressed(java.awt.event.MouseEvent evt) {// start of
        // drag
        if (evt.getButton() == MouseEvent.BUTTON1) {
            isMouseDrag = false;

            int pressX = evt.getX();
            int pressY = evt.getY();

            // This is to get the starting point of mouse drag
            if (pressX >= 50 && pressY >= 30) {
                pressX = pressX - 50;
                int x = 0;
                for (x = 0; pressX >= 0; x++) {
                    pressX = pressX - 50;
                }

                pressY = pressY - 30;
                int y = 0;
                for (y = 0; pressY >= 0; y++) {
                    pressY = pressY - 50;
                }

                if (x > tableData.noOfPeriods) {
                    startX = x - 2;
                } else {
                    startX = x - 1;
                }

                if (y > tableData.noOfDays) {
                    startY = y - 2;
                } else {
                    startY = y - 1;
                }
            }

            System.out.println(startX + "xy" + startY);

            /**
             * This is to find the start data and find whether the condition is
             * satisfied for dragging subject(Source subject should not be
             * locked and source is not null).
             */
            boolean start_lock = false;
            if (classTeacher == 1) {
                TimeTableLock[][] temp = tableData.getTimeTable(titleValue);
                startData = temp[startY][startX].data;
                start_lock = temp[startY][startX].lock;
            } else {
                TimeTableLock[][] temp;
                for (int k = 0; k < tableData.classModel.size(); k++) {
                    temp = tableData.getTimeTable(k);

                    if (temp[startY][startX].data != null) {
                        if (temp[startY][startX].data.teacherList.contains(tableData.teacherModel.get(teacherTitleValue))) {

                            startData = temp[startY][startX].data;
                            start_lock = temp[startY][startX].lock;
                            break;
                        }
                    }
                }
            }
            if (startData == null) {
                isMouseDrag = false;
            } else if (start_lock == false) {
                isMouseDrag = true;
            }

            if (isMouseDrag == true) {
                clashFree();// under testing
            }
        }
    }

    private void drawMouseReleased(java.awt.event.MouseEvent evt) {// end of drag
        if (evt.getButton() == MouseEvent.BUTTON1) {
            isMouseDrag = false;

            int releaseX = evt.getX();
            int releaseY = evt.getY();

            // This is to get the end point of drag
            if (releaseX >= 50 && releaseY >= 30) {
                releaseX = releaseX - 50;
                int x = 0;
                for (x = 0; releaseX >= 0; x++) {
                    releaseX = releaseX - 50;
                }

                releaseY = releaseY - 30;
                int y = 0;
                for (y = 0; releaseY >= 0; y++) {
                    releaseY = releaseY - 50;
                }

                if (x > tableData.noOfPeriods) {
                    endX = x - 2;
                } else {
                    endX = x - 1;
                }

                if (y > tableData.noOfDays) {
                    endY = y - 2;
                } else {
                    endY = y - 1;
                }
            }
            System.out.println(endX + "XY" + endY);
        }
    }

    private void drawMouseDragged(java.awt.event.MouseEvent evt) {
        dragX = evt.getX();
        dragY = evt.getY();

        int tempX = dragX;
        int tempY = dragY;

        if (tempX < 50 || tempY < 30 || tempX >= tableData.noOfPeriods * 50 + 50 || tempY >= tableData.noOfDays * 50 + 30) {
            allow = false;
        } else {
            allow = true;
        }

        if (tempX >= 50 && tempY >= 30) {
            tempX = tempX - 50;
            int x = 0;
            for (x = 0; tempX >= 0; x++) {
                tempX = tempX - 50;
            }

            tempY = tempY - 30;
            int y = 0;
            for (y = 0; tempY >= 0; y++) {
                tempY = tempY - 50;
            }

            if (x > tableData.noOfPeriods) {
                moveRecX = x - 2;
            } else {
                moveRecX = x - 1;
            }

            if (y > tableData.noOfDays) {
                moveRecY = y - 2;
            } else {
                moveRecY = y - 1;
            }
        }

        // if drag started! else no need
        if (isMouseDrag == true) {
            boolean tempLock = false;
            if (classTeacher == 1) {
                TimeTableLock[][] temp = tableData.getTimeTable(titleValue);
                tempData = temp[moveRecY][moveRecX].data;
                tempLock = temp[moveRecY][moveRecX].lock;
            } else {
                for (int classIdx = 0; classIdx < tableData.classModel.size(); classIdx++) {
                    TimeTableLock[][] temp = tableData.getTimeTable(classIdx);

                    if (temp[moveRecY][moveRecX].data != null) {
                        if (temp[moveRecY][moveRecX].data.teacherList.contains(tableData.teacherModel.get(teacherTitleValue))) {

                            tempData = temp[moveRecY][moveRecX].data;
                            tempLock = temp[moveRecY][moveRecX].lock;
                            break;
                        }
                    }
                }
            }
        }

        if (classTeacher == 1) {

            // Move up
            TimeTableLock[][] temp = tableData.getTimeTable(titleValue);
            startData = temp[startY][startX].data;
            tempData = temp[moveRecY][moveRecX].data;
            boolean tempLock = temp[moveRecY][moveRecX].lock;

            boolean clash = false;
            sourceToDestination = false;

            SubjectData subjectData = startData;

            if (subjectData.teacherList.get(0) == "Without teacher") {
                clash = false;
            } else {
                for (int classIdx = 0; classIdx < tableData.classModel.size(); classIdx++) {
                    temp = tableData.getTimeTable(classIdx);

                    if (classIdx != titleValue)
                        for (int teacher = 0; teacher < subjectData.teacherList
                                .size(); teacher++) {
                            if (temp[moveRecY][moveRecX].data != null) {
                                if (temp[moveRecY][moveRecX].data.teacherList.contains(subjectData.teacherList.get(teacher))) {
                                    clash = true;
                                    break;
                                }
                            }
                        }
                    if (clash == true)
                        break;
                }
            }
            if (clash == true) {
                sourceToDestination = false;
            } else if (tempLock == false) {// Destination should not be locked
                sourceToDestination = true;
            }

            // Move down
            clash = false;
            destnationToSource = false;

            subjectData = tempData;

            if (subjectData != null) {
                if (subjectData.teacherList.get(0) == "Without teacher") {
                    clash = false;
                } else {
                    for (int classIdx = 0; classIdx < tableData.classModel.size(); classIdx++) {
                        temp = tableData.getTimeTable(classIdx);

                        if (classIdx != titleValue)
                            for (int teacher = 0; teacher < subjectData.teacherList.size(); teacher++) {
                                if (temp[startY][startX].data != null) {
                                    if (temp[startY][startX].data.teacherList.contains(subjectData.teacherList.get(teacher))) {
                                        clash = true;
                                        break;
                                    }
                                }
                            }
                        if (clash == true) {
                            break;
                        }
                    }
                }
            }
            if (clash == true) {
                destnationToSource = false;
            } else if (tempLock == false) {// DESTINATION SHOULD NOT BE LOCKED
                destnationToSource = true;
            }
        } else {
            // to find the start drag subject
            for (int classIdx = 0; classIdx < tableData.classModel.size(); classIdx++) {
                TimeTableLock[][] temp = tableData.getTimeTable(classIdx);

                if (temp[startY][startX].data != null) {
                    if (temp[startY][startX].data.teacherList.contains(tableData.teacherModel.get(teacherTitleValue))) {
                        startData = temp[startY][startX].data;
                        break;
                    }
                }
            }

            // to find the temp destination(drag) subject
            TimeTableLock temp_table = null;
            for (int classIdx = 0; classIdx < tableData.classModel.size(); classIdx++) {
                TimeTableLock[][] table = tableData.getTimeTable(classIdx);

                if (table[moveRecY][moveRecX].data != null) {
                    if (table[moveRecY][moveRecX].data.teacherList.contains(tableData.teacherModel.get(teacherTitleValue))) {
                        tempData = table[moveRecY][moveRecX].data;
                        temp_table = table[moveRecY][moveRecX];
                        break;
                    }
                }

            }

            // copy subject from start to temp (swap subjects)
            boolean clash = false;
            sourceToDestination = false;
            TimeTableLock[][] temp;
            boolean temp_lock = temp_table.lock;

            SubjectData subjectData = startData;

            if (subjectData.teacherList.get(0) == "Without teacher") {
                clash = false;
            } else {
                for (int classIdx = 0; classIdx < tableData.classModel.size(); classIdx++) {
                    temp = tableData.getTimeTable(classIdx);

                    if (classIdx != tableData.classModel
                            .indexOf(subjectData.className))
                        for (int j = 0; j < subjectData.teacherList.size(); j++) {
                            if (temp[moveRecY][moveRecX].data != null) {
                                if (temp[moveRecY][moveRecX].data.teacherList.contains(subjectData.teacherList.get(j))) {
                                    clash = true;
                                    break;
                                }
                            }
                        }
                    if (clash == true) {
                        break;
                    }
                }
            }
            if (clash == true) {
                sourceToDestination = false;
            } else if (temp_lock == false) {// Destination should not be locked
                sourceToDestination = true;
            }

            // copy subject from temp to start (swap subjects)
            clash = false;
            destnationToSource = false;
            subjectData = tempData;

            if (subjectData.teacherList.get(0) == "Without teacher") {
                clash = false;
            } else {
                for (int classIdx = 0; classIdx < tableData.classModel.size(); classIdx++) {
                    temp = tableData.getTimeTable(classIdx);

                    if (classIdx != tableData.classModel.indexOf(subjectData.className))
                        for (int j = 0; j < subjectData.teacherList.size(); j++) {
                            if (temp[startY][startX].data != null) {
                                if (temp[startY][startX].data.teacherList.contains(subjectData.teacherList.get(j))) {
                                    clash = true;
                                    break;
                                }
                            }
                        }
                    if (clash == true) {
                        break;
                    }
                }
            }
            if (clash == true) {
                destnationToSource = false;
            } else if (temp_lock == false) {// Destination should not be locked
                destnationToSource = true;
            }
        }
        repaint();
    }

    private void clashFree() {
        if (classTeacher == 1) {
            for (int day = 0; day < tableData.noOfDays; day++) {
                for (int period = 0; period < tableData.noOfPeriods; period++) {
                    TimeTableLock[][] table = null;
                    boolean clash = false;
                    SubjectData subjectData = startData;

                    if (subjectData.teacherList.get(0) == "Without teacher") {
                        clash = false;
                    } else {
                        for (int classIdx = 0; classIdx < tableData.classModel.size(); classIdx++) {
                            table = tableData.getTimeTable(classIdx);

                            if (classIdx != titleValue) {
                                for (int teacher = 0; teacher < subjectData.teacherList.size(); teacher++) {
                                    if (table[day][period].data != null) {
                                        if (table[day][period].data.teacherList.contains(subjectData.teacherList.get(teacher))) {
                                            clash = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (clash == true) {
                                break;
                            }
                        }
                    }

                    if (clash != true) {
                        clashFree[day][period] = true;
                    } else {
                        clashFree[day][period] = false;
                    }
                }
            }
        } else {
            // To highlight the clash free places
            for (int day = 0; day < tableData.noOfDays; day++) {
                for (int period = 0; period < tableData.noOfPeriods; period++) {
                    boolean clash = false;
                    TimeTableLock[][] temp;

                    SubjectData subjectData = startData;

                    if (subjectData.teacherList.get(0) == "Without teacher") {
                        clash = false;
                    } else {
                        for (int classIdx = 0; classIdx < tableData.classModel.size(); classIdx++) {
                            temp = tableData.getTimeTable(classIdx);

                            if (classIdx != tableData.classModel.indexOf(startData.className))
                                for (int subject = 0; subject < subjectData.teacherList.size(); subject++) {
                                    if (temp[day][period].data != null)
                                        if (temp[day][period].data.teacherList.contains(subjectData.teacherList.get(subject))) {
                                            clash = true;
                                            break;
                                        }
                                }
                            if (clash == true) {
                                break;
                            }
                        }
                    }

                    if (clash != true) {
                        clashFree[day][period] = true;
                    } else {
                        clashFree[day][period] = false;
                    }
                }
            }
        }
    }
}

