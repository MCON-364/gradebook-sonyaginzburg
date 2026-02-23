package edu.course.gradebook;

import java.util.*;

public class Gradebook {

    private final Map<String, List<Integer>> gradesByStudent = new HashMap<>();
    private final Deque<UndoAction> undoStack = new ArrayDeque<>();
    private final LinkedList<String> activityLog = new LinkedList<>();

    public Optional<List<Integer>> findStudentGrades(String name) {
        return Optional.ofNullable(gradesByStudent.get(name));
    }

    public boolean addStudent(String name) {
        if (gradesByStudent.containsKey(name)) {
            return false;
        }
        gradesByStudent.put(name, new ArrayList<>());
        activityLog.addFirst("Student added: " + name);
        return true;
    }

    public boolean addGrade(String name, int grade) {
        var grades = gradesByStudent.get(name);
        if  (grades == null) {
            return false;
        }
        grades.add(grade);
        // undo by removing last element
        undoStack.push(gb -> {var g = gb.gradesByStudent.get(name);
        if (g != null&& !g.isEmpty()) {
            g.remove(g.size() - 1);
            }
        });
        // log entry
        activityLog.addFirst("Name:" + name + " ,added Grade:" + grade);
        return true;

    }

    public boolean removeStudent(String name) {
        var removed = gradesByStudent.remove(name);
        if (removed == null) {
            return false;
        }
        //push undo action to restore student and their grades
        undoStack.push(gb -> gb.gradesByStudent.put(name, removed));
        activityLog.addFirst("Removed student: " + name);
        return true;
    }

    public Optional<Double> averageFor(String name) {
        var grades = gradesByStudent.get(name);
        if (grades == null || grades.isEmpty()) {
            return Optional.empty();
        }
        int sum = 0;
        for  (int grade : grades) {
            sum += grade;
        }
        // cast to double so dont lose decimals
        double avg = (double) sum / grades.size();
        return Optional.of(avg);
    }

    public Optional<String> letterGradeFor(String name) {
        Optional<Double> avgOptional = averageFor(name);
        if (avgOptional.isEmpty()) {
            return Optional.empty();
        }
        double avg = avgOptional.get();
        // had to use AI to figure out how to use switch expression and  dividing averages
        String letter = switch ((int)avg / 10) {
            case 10, 9 -> {yield "A";}
            case 8 -> {yield "B";}
            case 7 -> {yield "C";}
            case 6 -> {yield "D";}
            default -> {yield "F";}
        };
        return Optional.of(letter);
    }

    public Optional<Double> classAverage() {

        double total = 0;
        int count = 0;
        for (var grades : gradesByStudent.values()){
            for (int grade : grades) {
                total += grade;
                count++;
            }
        }
        if  (count == 0) {
            return Optional.empty();
        }
        return Optional.of(total / count);
    }

    public boolean undo() {
        if (undoStack.isEmpty()) {
            return false;
        }
        var action = undoStack.pop();
        action.undo(this);
        activityLog.addFirst("Undo performed");
        return true;

    }

    public List<String> recentLog(int maxItems) {
        var result = new ArrayList<String>();
        int count = 0;
        for (String activity : activityLog) {
            if (count >= maxItems) {
                break;
            }
            result.add(activity);
            count++;
        }
        return result;
    }
}
