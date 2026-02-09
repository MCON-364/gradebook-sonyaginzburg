package edu.course.gradebook;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class AutoGraderGradebookTest {

    private Gradebook gradebook;

    @BeforeEach
    void setUp() {
        gradebook = new Gradebook();
    }

    // ========== addStudent Tests ==========

    @Test
    void addStudent_createsEmptyGradeList() {
        assertTrue(gradebook.addStudent("Alice"));
        var grades = gradebook.findStudentGrades("Alice");
        assertTrue(grades.isPresent());
        assertTrue(grades.get().isEmpty());
    }

    @Test
    void addStudent_returnsFalseForDuplicate() {
        assertTrue(gradebook.addStudent("Alice"));
        assertFalse(gradebook.addStudent("Alice"), "Should return false when adding duplicate student");
    }

    @Test
    void addStudent_allowsMultipleStudents() {
        assertTrue(gradebook.addStudent("Alice"));
        assertTrue(gradebook.addStudent("Bob"));
        assertTrue(gradebook.addStudent("Charlie"));

        assertTrue(gradebook.findStudentGrades("Alice").isPresent());
        assertTrue(gradebook.findStudentGrades("Bob").isPresent());
        assertTrue(gradebook.findStudentGrades("Charlie").isPresent());
    }

    // ========== addGrade Tests ==========

    @Test
    void addGrade_addsGradeToExistingStudent() {
        gradebook.addStudent("Alice");
        assertTrue(gradebook.addGrade("Alice", 90));

        var grades = gradebook.findStudentGrades("Alice");
        assertTrue(grades.isPresent());
        assertEquals(1, grades.get().size());
        assertEquals(90, grades.get().get(0));
    }

    @Test
    void addGrade_returnsFalseForNonexistentStudent() {
        assertFalse(gradebook.addGrade("Bob", 85), "Should return false for student that doesn't exist");
    }

    @Test
    void addGrade_allowsMultipleGrades() {
        gradebook.addStudent("Alice");
        assertTrue(gradebook.addGrade("Alice", 90));
        assertTrue(gradebook.addGrade("Alice", 85));
        assertTrue(gradebook.addGrade("Alice", 95));

        var grades = gradebook.findStudentGrades("Alice");
        assertTrue(grades.isPresent());
        assertEquals(3, grades.get().size());
    }

    @Test
    void addGrade_acceptsBoundaryValues() {
        gradebook.addStudent("Alice");
        assertTrue(gradebook.addGrade("Alice", 0));
        assertTrue(gradebook.addGrade("Alice", 100));

        var grades = gradebook.findStudentGrades("Alice");
        assertEquals(2, grades.get().size());
    }

    // ========== removeStudent Tests ==========

    @Test
    void removeStudent_removesExistingStudent() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 90);

        assertTrue(gradebook.removeStudent("Alice"));
        assertTrue(gradebook.findStudentGrades("Alice").isEmpty());
    }

    @Test
    void removeStudent_returnsFalseForNonexistentStudent() {
        assertFalse(gradebook.removeStudent("Bob"), "Should return false when removing nonexistent student");
    }

    @Test
    void removeStudent_doesNotAffectOtherStudents() {
        gradebook.addStudent("Alice");
        gradebook.addStudent("Bob");
        gradebook.addGrade("Alice", 90);
        gradebook.addGrade("Bob", 85);

        assertTrue(gradebook.removeStudent("Alice"));

        assertTrue(gradebook.findStudentGrades("Alice").isEmpty());
        assertTrue(gradebook.findStudentGrades("Bob").isPresent());
        assertEquals(85, gradebook.findStudentGrades("Bob").get().get(0));
    }

    // ========== averageFor Tests ==========

    @Test
    void averageFor_returnsEmptyForNonexistentStudent() {
        assertTrue(gradebook.averageFor("Bob").isEmpty());
    }

    @Test
    void averageFor_returnsEmptyForStudentWithNoGrades() {
        gradebook.addStudent("Alice");
        assertTrue(gradebook.averageFor("Alice").isEmpty());
    }

    @Test
    void averageFor_calculatesCorrectAverageForOneGrade() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 90);

        var avg = gradebook.averageFor("Alice");
        assertTrue(avg.isPresent());
        assertEquals(90.0, avg.get(), 0.01);
    }

    @Test
    void averageFor_calculatesCorrectAverageForMultipleGrades() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 90);
        gradebook.addGrade("Alice", 80);
        gradebook.addGrade("Alice", 70);

        var avg = gradebook.averageFor("Alice");
        assertTrue(avg.isPresent());
        assertEquals(80.0, avg.get(), 0.01);
    }

    @Test
    void averageFor_handlesDecimalAverages() {
        gradebook.addStudent("Bob");
        gradebook.addGrade("Bob", 85);
        gradebook.addGrade("Bob", 90);
        gradebook.addGrade("Bob", 88);

        var avg = gradebook.averageFor("Bob");
        assertTrue(avg.isPresent());
        assertEquals(87.666667, avg.get(), 0.01);
    }

    // ========== letterGradeFor Tests ==========

    @Test
    void letterGradeFor_returnsEmptyForNonexistentStudent() {
        assertTrue(gradebook.letterGradeFor("Bob").isEmpty());
    }

    @Test
    void letterGradeFor_returnsEmptyForStudentWithNoGrades() {
        gradebook.addStudent("Alice");
        assertTrue(gradebook.letterGradeFor("Alice").isEmpty());
    }

    @Test
    void letterGradeFor_returnsA_for90to100() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 90);
        gradebook.addGrade("Alice", 100);

        var letter = gradebook.letterGradeFor("Alice");
        assertTrue(letter.isPresent());
        assertEquals("A", letter.get());
    }

    @Test
    void letterGradeFor_returnsB_for80to89() {
        gradebook.addStudent("Bob");
        gradebook.addGrade("Bob", 80);
        gradebook.addGrade("Bob", 89);

        var letter = gradebook.letterGradeFor("Bob");
        assertTrue(letter.isPresent());
        assertEquals("B", letter.get());
    }

    @Test
    void letterGradeFor_returnsC_for70to79() {
        gradebook.addStudent("Charlie");
        gradebook.addGrade("Charlie", 70);
        gradebook.addGrade("Charlie", 79);

        var letter = gradebook.letterGradeFor("Charlie");
        assertTrue(letter.isPresent());
        assertEquals("C", letter.get());
    }

    @Test
    void letterGradeFor_returnsD_for60to69() {
        gradebook.addStudent("David");
        gradebook.addGrade("David", 60);
        gradebook.addGrade("David", 69);

        var letter = gradebook.letterGradeFor("David");
        assertTrue(letter.isPresent());
        assertEquals("D", letter.get());
    }

    @Test
    void letterGradeFor_returnsF_forBelow60() {
        gradebook.addStudent("Eve");
        gradebook.addGrade("Eve", 50);
        gradebook.addGrade("Eve", 59);

        var letter = gradebook.letterGradeFor("Eve");
        assertTrue(letter.isPresent());
        assertEquals("F", letter.get());
    }

    @Test
    void letterGradeFor_handlesBoundaryAt90() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 89);
        gradebook.addGrade("Alice", 90);
        assertEquals("B", gradebook.letterGradeFor("Alice").get(), "89.5 average should be B");

        gradebook.addStudent("Bob");
        gradebook.addGrade("Bob", 90);
        assertEquals("A", gradebook.letterGradeFor("Bob").get(), "90 average should be A");
    }

    // ========== classAverage Tests ==========

    @Test
    void classAverage_returnsEmptyForNoStudents() {
        assertTrue(gradebook.classAverage().isEmpty());
    }

    @Test
    void classAverage_returnsEmptyForStudentsWithNoGrades() {
        gradebook.addStudent("Alice");
        gradebook.addStudent("Bob");
        assertTrue(gradebook.classAverage().isEmpty());
    }

    @Test
    void classAverage_calculatesAverageForOneStudent() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 90);
        gradebook.addGrade("Alice", 80);

        var avg = gradebook.classAverage();
        assertTrue(avg.isPresent());
        assertEquals(85.0, avg.get(), 0.01);
    }

    @Test
    void classAverage_calculatesAverageAcrossMultipleStudents() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 90);
        gradebook.addGrade("Alice", 80);

        gradebook.addStudent("Bob");
        gradebook.addGrade("Bob", 70);
        gradebook.addGrade("Bob", 60);

        var avg = gradebook.classAverage();
        assertTrue(avg.isPresent());
        assertEquals(75.0, avg.get(), 0.01); // (90+80+70+60)/4 = 75
    }

    @Test
    void classAverage_ignoresStudentsWithNoGrades() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 90);

        gradebook.addStudent("Bob"); // No grades

        var avg = gradebook.classAverage();
        assertTrue(avg.isPresent());
        assertEquals(90.0, avg.get(), 0.01);
    }

    @Test
    void classAverage_handlesUnevenGradeCounts() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 100);

        gradebook.addStudent("Bob");
        gradebook.addGrade("Bob", 80);
        gradebook.addGrade("Bob", 90);
        gradebook.addGrade("Bob", 70);

        var avg = gradebook.classAverage();
        assertTrue(avg.isPresent());
        assertEquals(85.0, avg.get(), 0.01); // (100+80+90+70)/4 = 85
    }

    // ========== undo Tests ==========

    @Test
    void undo_returnsFalseWhenNothingToUndo() {
        assertFalse(gradebook.undo());
    }

    @Test
    void undo_revertsAddGrade() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 90);

        var gradesBefore = gradebook.findStudentGrades("Alice");
        assertEquals(1, gradesBefore.get().size());

        assertTrue(gradebook.undo());

        var gradesAfter = gradebook.findStudentGrades("Alice");
        assertTrue(gradesAfter.isPresent());
        assertEquals(0, gradesAfter.get().size());
    }

    @Test
    void undo_revertsMultipleAddGrades() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 90);
        gradebook.addGrade("Alice", 85);
        gradebook.addGrade("Alice", 95);

        assertTrue(gradebook.undo()); // Remove 95
        assertEquals(2, gradebook.findStudentGrades("Alice").get().size());

        assertTrue(gradebook.undo()); // Remove 85
        assertEquals(1, gradebook.findStudentGrades("Alice").get().size());
        assertEquals(90, gradebook.findStudentGrades("Alice").get().get(0));
    }

    @Test
    void undo_revertsRemoveStudent() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 90);
        gradebook.addGrade("Alice", 85);

        gradebook.removeStudent("Alice");
        assertTrue(gradebook.findStudentGrades("Alice").isEmpty());

        assertTrue(gradebook.undo());

        var grades = gradebook.findStudentGrades("Alice");
        assertTrue(grades.isPresent());
        assertEquals(2, grades.get().size());
        assertEquals(90, grades.get().get(0));
        assertEquals(85, grades.get().get(1));
    }

    @Test
    void undo_handlesMultipleOperations() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 90);
        gradebook.addGrade("Alice", 85);
        gradebook.removeStudent("Alice");

        // Undo remove
        assertTrue(gradebook.undo());
        assertTrue(gradebook.findStudentGrades("Alice").isPresent());

        // Undo second grade
        assertTrue(gradebook.undo());
        assertEquals(1, gradebook.findStudentGrades("Alice").get().size());

        // Undo first grade
        assertTrue(gradebook.undo());
        assertEquals(0, gradebook.findStudentGrades("Alice").get().size());

        // Nothing left to undo
        assertFalse(gradebook.undo());
    }

    @Test
    void undo_doesNotUndoAddStudent() {
        gradebook.addStudent("Alice");
        gradebook.addStudent("Bob");

        // addStudent should not be undoable
        assertFalse(gradebook.undo());

        // Students should still exist
        assertTrue(gradebook.findStudentGrades("Alice").isPresent());
        assertTrue(gradebook.findStudentGrades("Bob").isPresent());
    }

    // ========== recentLog Tests ==========

    @Test
    void recentLog_returnsEmptyListInitially() {
        var log = gradebook.recentLog(10);
        assertNotNull(log);
        assertTrue(log.isEmpty());
    }

    @Test
    void recentLog_recordsAddStudent() {
        gradebook.addStudent("Alice");

        var log = gradebook.recentLog(10);
        assertEquals(1, log.size());
        assertTrue(log.get(0).contains("Alice"));
        assertTrue(log.get(0).toLowerCase().contains("add") || log.get(0).toLowerCase().contains("student"));
    }

    @Test
    void recentLog_recordsAddGrade() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 90);

        var log = gradebook.recentLog(10);
        assertTrue(log.size() >= 2);

        var gradeEntry = log.stream()
            .filter(entry -> entry.contains("90") && entry.contains("Alice"))
            .findFirst();
        assertTrue(gradeEntry.isPresent());
    }

    @Test
    void recentLog_recordsRemoveStudent() {
        gradebook.addStudent("Bob");
        gradebook.removeStudent("Bob");

        var log = gradebook.recentLog(10);
        assertTrue(log.size() >= 2);

        var removeEntry = log.stream()
            .filter(entry -> entry.contains("Bob") &&
                    (entry.toLowerCase().contains("remove") || entry.toLowerCase().contains("deleted")))
            .findFirst();
        assertTrue(removeEntry.isPresent());
    }

    @Test
    void recentLog_respectsMaxItemsLimit() {
        gradebook.addStudent("Alice");
        for (int i = 0; i < 20; i++) {
            gradebook.addGrade("Alice", 90);
        }

        var log = gradebook.recentLog(5);
        assertEquals(5, log.size());
    }

    @Test
    void recentLog_returnsAllEntriesWhenMaxItemsExceedsLogSize() {
        gradebook.addStudent("Alice");
        gradebook.addStudent("Bob");
        gradebook.addGrade("Alice", 90);

        var log = gradebook.recentLog(100);
        assertEquals(3, log.size());
    }

    @Test
    void recentLog_recordsUndo() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 90);
        gradebook.undo();

        var log = gradebook.recentLog(10);
        assertTrue(log.size() >= 3);

        var undoEntry = log.stream()
            .filter(entry -> entry.toLowerCase().contains("undo"))
            .findFirst();
        assertTrue(undoEntry.isPresent());
    }

    // ========== Integration Tests ==========

    @Test
    void integration_fullWorkflow() {
        // Add students
        assertTrue(gradebook.addStudent("Alice"));
        assertTrue(gradebook.addStudent("Bob"));
        assertTrue(gradebook.addStudent("Charlie"));

        // Add grades
        assertTrue(gradebook.addGrade("Alice", 95));
        assertTrue(gradebook.addGrade("Alice", 90));
        assertTrue(gradebook.addGrade("Bob", 85));
        assertTrue(gradebook.addGrade("Bob", 80));
        assertTrue(gradebook.addGrade("Charlie", 70));

        // Check averages
        assertEquals(92.5, gradebook.averageFor("Alice").get(), 0.01);
        assertEquals(82.5, gradebook.averageFor("Bob").get(), 0.01);
        assertEquals(70.0, gradebook.averageFor("Charlie").get(), 0.01);

        // Check letter grades
        assertEquals("A", gradebook.letterGradeFor("Alice").get());
        assertEquals("B", gradebook.letterGradeFor("Bob").get());
        assertEquals("C", gradebook.letterGradeFor("Charlie").get());

        // Check class average
        assertEquals(84.0, gradebook.classAverage().get(), 0.01); // (95+90+85+80+70)/5

        // Test undo
        assertTrue(gradebook.undo()); // Undo Charlie's grade
        assertEquals(87.5, gradebook.classAverage().get(), 0.01); // (95+90+85+80)/4

        // Remove student
        assertTrue(gradebook.removeStudent("Bob"));
        assertEquals(92.5, gradebook.classAverage().get(), 0.01); // (95+90)/2

        // Undo removal
        assertTrue(gradebook.undo());
        assertTrue(gradebook.findStudentGrades("Bob").isPresent());

        // Check log
        var log = gradebook.recentLog(20);
        assertFalse(log.isEmpty());
    }

    @Test
    void findStudentGrades_returnsEmptyForNonexistentStudent() {
        assertTrue(gradebook.findStudentGrades("Nonexistent").isEmpty());
    }

    @Test
    void findStudentGrades_returnsGradesForExistingStudent() {
        gradebook.addStudent("Alice");
        var grades = gradebook.findStudentGrades("Alice");
        assertTrue(grades.isPresent());
        assertTrue(grades.get().isEmpty());

        gradebook.addGrade("Alice", 90);
        grades = gradebook.findStudentGrades("Alice");
        assertTrue(grades.isPresent());
        assertEquals(1, grades.get().size());
    }
}
