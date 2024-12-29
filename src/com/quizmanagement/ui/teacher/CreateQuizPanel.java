package com.quizmanagement.ui.teacher;

import com.github.lgooddatepicker.components.DatePickerSettings;
import com.quizmanagement.db.QuizDAO;
import com.quizmanagement.db.UserDAO;
import com.quizmanagement.objs.*;
import com.quizmanagement.ui.teacher.questions.CreateMCQ;
import com.quizmanagement.util.Utils;

import javax.swing.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CreateQuizPanel extends javax.swing.JPanel {
    private int currentQuestionIndex = 0;
    private final List<JPanel> questionPanels = new ArrayList<>();
    private final List<Question> questions = new ArrayList<>();
    private Quiz currentQuiz = new Quiz();
    private int totalQuestionsLimit;
    private List<ClassObj> classes = new ArrayList<>();
    private List<Quiz> quizzes = new ArrayList<>();

    private boolean loadingQuiz = false;
    private boolean isModified = false;
    private int lastSelectedQuizIndex = -1;
    private boolean isTempQuizActive = false;

    private final Utils utils = new Utils();
    private final String username;
    private final UserDAO userDAO = new UserDAO();
    private User user;
    private final QuizDAO quizDAO = new QuizDAO();

    public CreateQuizPanel(String username) {
        this.username = username;
        initComponents();
        initCustom();
        stylize();
        addTotalQuestionsListener();
        addQuestionTypeListener();
        addQuizTitleListener();
        loadClasses();
    }

    private void stylize() {
        DatePickerSettings settings = new DatePickerSettings();
        settings.setColor(DatePickerSettings.DateArea.TextFieldBackgroundValidDate,
                UIManager.getColor("TextField.background"));
    }

    private void initCustom() {
        totalQuestions.setModel(new SpinnerNumberModel(1, 1, 100, 1));
        timeLimit.setModel(new SpinnerNumberModel(1, 1, 100, 1));
        qPoints.setModel(new SpinnerNumberModel(1, 1, 100, 1));

        totalQuestions.setValue(1);
        timeLimit.setValue(30);
        qPoints.setValue(1);

        this.user = userDAO.getUserByUsername(username);
        if (this.user == null) {
            JOptionPane.showMessageDialog(this, "No user found with username: " + username);
            return;
        }

        validateAndSetTotalQuestions();
        if (totalQuestionsLimit <= 0) totalQuestionsLimit = 1;

        currentQuiz.setTimeLimit((int) timeLimit.getValue());

        CreateMCQ firstMCQ = new CreateMCQ();
        questionPanels.add(firstMCQ);
        questions.add(null);
        qPanel.add("Question 1", firstMCQ);
        updateNavigationButtons();

        selectedClass.addActionListener(e -> onClassSelected());
        selectedQuiz.addActionListener(e -> onQuizSelected());
        lastSelectedQuizIndex = -1;
    }

    private void addQuizTitleListener() {
        Utils.addSimpleDocumentListener(quizTitle, this::onQuizTitleChanged);
    }
    
    private void onQuizTitleChanged() {
        if (!loadingQuiz) {
            isModified = true;
            updateQuizItemInCombo();
        }
    }

    private void updateQuizItemInCombo() {
        int index = selectedQuiz.getSelectedIndex();
        if (index < 0) return;
        String displayedTitle = getDisplayedQuizTitle();
        loadingQuiz = true;
        try {
            selectedQuiz.insertItemAt(displayedTitle, index);
            selectedQuiz.removeItemAt(index + 1);
            selectedQuiz.setSelectedIndex(index);
        } finally {
            loadingQuiz = false;
        }
    }

    private String getDisplayedQuizTitle() {
        String baseTitle = quizTitle.getText().trim();
        if (baseTitle.isEmpty()) baseTitle = "Untitled";

        boolean unsaved = (currentQuiz.getQuizId() == 0) || isModified;
        String suffix = unsaved ? " (Unsaved)" : "";

        String oldVal = (String)selectedQuiz.getSelectedItem();
        if (oldVal != null && oldVal.contains(":")) {
            String numPart = oldVal.split(":")[0].trim();
            return numPart + ": " + baseTitle + suffix;
        } else {
            int newQuizNumber = quizzes.size() + 1;
            return newQuizNumber + ": " + baseTitle + suffix;
        }
    }

    private void addTotalQuestionsListener() {
        ((SpinnerNumberModel) totalQuestions.getModel()).addChangeListener(e -> handleTotalQuestionsChange());
    }

    private void addQuestionTypeListener() {
        selectedQType.addActionListener(e -> {
            if (currentQuestionIndex < 0 || currentQuestionIndex >= questionPanels.size()) return;
            questions.set(currentQuestionIndex, null);
            isModified = true;
            
            JPanel newPanel = new CreateMCQ();
            questionPanels.set(currentQuestionIndex, newPanel);
            qPanel.removeAll();
            qPanel.addTab("Question " + (currentQuestionIndex + 1), newPanel);
            clearErrorState(newPanel);
            revalidate();
            repaint();
        });
    }

    private void loadClasses() {
        classes = userDAO.getClassesForTeacher(user.getUserId());
        if (classes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No classes assigned to this teacher.");
            return;
        }
        selectedClass.removeAllItems();
        for (ClassObj c : classes) {
            selectedClass.addItem(c.getClassId() + ": " + c.getClassName());
        }

        if (selectedClass.getItemCount() > 0) {
            selectedClass.setSelectedIndex(0);
            onClassSelected();
        }
    }

    private void onClassSelected() {
        if (!confirmIfModified()) {
            revertToLastSelectedQuiz();
            return;
        }

        if (selectedClass.getSelectedIndex() < 0) return;
        String val = (String) selectedClass.getSelectedItem();
        if (val == null || val.isEmpty()) return;

        int classId = Integer.parseInt(val.split(":")[0].trim());
        loadQuizzesForClass(classId);
    }

    private void loadQuizzesForClass(int classId) {
        quizzes = quizDAO.getQuizzesForClass(classId, user.getUserId());
        selectedQuiz.removeAllItems();

        int count = 1;
        for (Quiz q : quizzes) {
            selectedQuiz.addItem(count + ": " + q.getTitle());
            count++;
        }

        selectedQuiz.addItem("Create New..");
        selectedQuiz.setSelectedIndex(0);
        lastSelectedQuizIndex = 0;
        isTempQuizActive = false;
        onQuizSelected();
    }

    private void onQuizSelected() {
        if (loadingQuiz) return;

        int newIndex = selectedQuiz.getSelectedIndex();
        if (newIndex == -1) return;

        String val = (String) selectedQuiz.getSelectedItem();
        if (val == null) return;

        if (isModified && newIndex != lastSelectedQuizIndex) {
            if (!confirmIfModified()) {
                revertToLastSelectedQuiz();
                return;
            } else {
                if (isTempQuizActive) {
                    removeTempQuizFromCombo();
                    isTempQuizActive = false;
                    isModified = false;
                }
            }
        }

        loadingQuiz = true;
        try {
            if (val.equals("Create New..")) {
                if (isTempQuizActive) {
                    removeTempQuizFromCombo();
                    isTempQuizActive = false;
                    isModified = false;
                }
                createTempQuizLine();
            } else {
                String[] parts = val.split(":");
                if (parts.length < 2) {
                    createTempQuizLine();
                    return;
                }

                int quizNum;
                try {
                    quizNum = Integer.parseInt(parts[0].trim());
                } catch (NumberFormatException e) {
                    createTempQuizLine();
                    return;
                }

                int quizIndex = quizNum - 1;
                if (quizIndex < 0 || quizIndex >= quizzes.size()) {
                    createTempQuizLine();
                    return;
                }

                loadQuiz(quizzes.get(quizIndex));
                isTempQuizActive = false;
            }
            lastSelectedQuizIndex = selectedQuiz.getSelectedIndex();
        } finally {
            loadingQuiz = false;
        }
    }

    private void createTempQuizLine() {
        int createNewIndex = selectedQuiz.getItemCount() - 1;
        int newQuizNumber = quizzes.size() + 1;
        String line = newQuizNumber + ": Untitled (Unsaved)";

        selectedQuiz.insertItemAt(line, createNewIndex);
        selectedQuiz.setSelectedIndex(createNewIndex);
        isTempQuizActive = true;
        isModified = true;
        createNewTempQuiz();
    }

    private void createNewTempQuiz() {
        questions.clear();
        questionPanels.clear();
        currentQuestionIndex = 0;
        totalQuestions.setValue(10);
        quizTitle.setText("Untitled");
        quizDescription.setText("");
        timeLimit.setValue(30);
        qPoints.setValue(1);
        datePicker.clear();
        errMessage.setText(" ");
        totalQuestionsLimit = 10;
        currentQuiz = new Quiz();
        currentQuiz.setCreatedBy(user.getUserId());
        currentQuiz.setTitle("Untitled");
        currentQuiz.setQuizId(0);

        CreateMCQ firstMCQ = new CreateMCQ();
        questionPanels.add(firstMCQ);
        questions.add(null);
        qPanel.removeAll();
        qPanel.addTab("Question 1", firstMCQ);
        updateNavigationButtons();
        lastSelectedQuizIndex = selectedQuiz.getSelectedIndex();
    }

    private void removeTempQuizFromCombo() {
        int count = selectedQuiz.getItemCount();
        for (int i = 0; i < count; i++) {
            String item = (String) selectedQuiz.getItemAt(i);
            if (isTempQuizLine(item)) {
                selectedQuiz.removeItemAt(i);
                break;
            }
        }
        
        if (selectedQuiz.getItemCount() == 0) {
            selectedQuiz.addItem("Create New..");
        } else {
            String lastItem = (String)selectedQuiz.getItemAt(selectedQuiz.getItemCount()-1);
            if (!lastItem.equals("Create New..")) {
                selectedQuiz.addItem("Create New..");
            }
        }
    }

    private boolean isTempQuizLine(String val) {
        return val != null && val.contains("(Unsaved)");
    }

    private boolean confirmIfModified() {
        if (isModified) {
            int result = JOptionPane.showConfirmDialog(this, 
                "You have unsaved changes. Switch without saving?", 
                "Unsaved Changes", 
                JOptionPane.YES_NO_OPTION);
            return result == JOptionPane.YES_OPTION;
        }
        return true;
    }

    private void revertToLastSelectedQuiz() {
        loadingQuiz = true;
        try {
            if (lastSelectedQuizIndex >= 0 && lastSelectedQuizIndex < selectedQuiz.getItemCount()) {
                selectedQuiz.setSelectedIndex(lastSelectedQuizIndex);
            } else if (selectedQuiz.getItemCount() > 0) {
                selectedQuiz.setSelectedIndex(0);
            }
        } finally {
            loadingQuiz = false;
        }
    }

    private void loadQuiz(Quiz qData) {
        currentQuiz = quizDAO.getQuizById(qData.getQuizId());
        quizTitle.setText(currentQuiz.getTitle());
        quizDescription.setText(currentQuiz.getDescription() != null ? currentQuiz.getDescription() : "");
        timeLimit.setValue(currentQuiz.getTimeLimit() > 0 ? currentQuiz.getTimeLimit() : 30);
        if (currentQuiz.getDeadline() != null) {
            datePicker.setDate(currentQuiz.getDeadline().toLocalDateTime().toLocalDate());
        } else {
            datePicker.clear();
        }

        questions.clear();
        questionPanels.clear();
        if (currentQuiz.getQuestions() != null && !currentQuiz.getQuestions().isEmpty()) {
            questions.addAll(currentQuiz.getQuestions());
            for (Question q : questions) {
                if (q instanceof MCQQuestion) {
                    JPanel panel = createPanelFromQuestion((MCQQuestion)q);
                    questionPanels.add(panel);
                }
            }
        }
        
        if (questionPanels.isEmpty()) {
            CreateMCQ firstMCQ = new CreateMCQ();
            questionPanels.add(firstMCQ);
            questions.add(null);
        }

        currentQuestionIndex = 0;
        qPanel.removeAll();
        qPanel.addTab("Question 1", questionPanels.get(0));
        totalQuestionsLimit = questionPanels.size();
        totalQuestions.setValue(totalQuestionsLimit);

        updateNavigationButtons();
        loadQuestionDataForCurrentIndex();

        currentQuiz.setQuizId(qData.getQuizId());
        isModified = false;
        isTempQuizActive = false;
        updateQuizItemInCombo();
    }
    private void handleTotalQuestionsChange() {
        int newLimit = (int) totalQuestions.getValue();
        int filledQuestions = getFilledQuestionsCount();
        if (newLimit < filledQuestions) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Delete extra questions?",
                    "Confirm Reduction",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                deleteExtraQuestions(newLimit);
                isModified = true;
            } else {
                SwingUtilities.invokeLater(() -> totalQuestions.setValue(totalQuestionsLimit));
                return;
            }
        }
        totalQuestionsLimit = newLimit;
        updateNavigationButtons();
    }

    private int getFilledQuestionsCount() {
        return currentQuestionIndex + 1;
    }

    private void deleteExtraQuestions(int newLimit) {
        while (questionPanels.size() > newLimit) {
            questionPanels.remove(questionPanels.size() - 1);
            questions.remove(questions.size() - 1);
        }
        currentQuestionIndex = Math.min(currentQuestionIndex, newLimit - 1);
        qPanel.removeAll();
        if (currentQuestionIndex >= 0) {
            qPanel.addTab("Question " + (currentQuestionIndex + 1), questionPanels.get(currentQuestionIndex));
        }
        revalidate();
        repaint();
    }

    private int validateAndSetTotalQuestions() {
        int val = (int) totalQuestions.getValue();
        if (val > 0) {
            totalQuestionsLimit = val;
            return val;
        }
        return -1;
    }

    private void updateNavigationButtons() {
        backButton.setEnabled(currentQuestionIndex > 0);
        if (currentQuestionIndex == totalQuestionsLimit - 1) {
            nextButton.setText("Finish");
        } else {
            nextButton.setText("Next >");
            nextButton.setEnabled(currentQuestionIndex < totalQuestionsLimit);
        }
    }

    private boolean collectCurrentQuestionData() {
        JPanel panel = questionPanels.get(currentQuestionIndex);
        clearErrorState(panel);

        int points = (int) qPoints.getValue();
        if (points <= 0) points = 1;

        if (panel instanceof CreateMCQ) {
            CreateMCQ mcqPanel = (CreateMCQ) panel;
            String text = mcqPanel.getQuestionText();
            if (text.isEmpty()) {
                mcqPanel.showError("Question text cannot be empty.", mcqPanel.getQuestionTextField(), true);
                return false;
            }

            ArrayList<String> options = mcqPanel.getOptions();
            int correctIndex = mcqPanel.getCorrectOptionIndex();
            if (options.isEmpty() || options.stream().anyMatch(String::isEmpty)) {
                mcqPanel.showError("All MCQ options must be filled.", mcqPanel.getOptionAField(), true);
                return false;
            }

            if (correctIndex == -1) {
                mcqPanel.showError("Please select a correct option.", null, true);
                return false;
            }

            MCQQuestion mcq = new MCQQuestion(text, options, correctIndex);
            mcq.setPoints(points);
            questions.set(currentQuestionIndex, mcq);
        }

        isModified = true;
        return true;
    }

    private void loadQuestionDataForCurrentIndex() {
        Question q = questions.isEmpty() ? null : questions.get(currentQuestionIndex);
        if (q != null && q instanceof MCQQuestion) {
            MCQQuestion mcq = (MCQQuestion) q;
            qPoints.setValue(mcq.getPoints());
            selectedQType.setSelectedItem("MCQ");
            
            CreateMCQ mcqPanel = new CreateMCQ();
            mcqPanel.setQuestionText(mcq.getText());
            mcqPanel.setOptions(mcq.getOptions());
            mcqPanel.setCorrectOptionIndex(mcq.getCorrectIndex());
            
            questionPanels.set(currentQuestionIndex, mcqPanel);
            qPanel.removeAll();
            qPanel.addTab("Question " + (currentQuestionIndex + 1), mcqPanel);
            clearErrorState(mcqPanel);
        } else {
            CreateMCQ newPanel = new CreateMCQ();
            questionPanels.set(currentQuestionIndex, newPanel);
            qPanel.removeAll();
            qPanel.addTab("Question " + (currentQuestionIndex + 1), newPanel);
            clearErrorState(newPanel);
            qPoints.setValue(1);
        }
    }

    private JPanel createPanelFromQuestion(MCQQuestion mcq) {
        CreateMCQ mcqPanel = new CreateMCQ();
        mcqPanel.setQuestionText(mcq.getText());
        mcqPanel.setOptions(mcq.getOptions());
        mcqPanel.setCorrectOptionIndex(mcq.getCorrectIndex());
        return mcqPanel;
    }

    private void saveQuizToDatabase() {
        if (this.user == null) {
            utils.setErrorMessage("No user set.", quizTitle, errMessage, true);
            return;
        }

        String titleText = quizTitle.getText().trim();
        String descriptionText = quizDescription.getText().trim();
        int limit = (int) timeLimit.getValue();
        currentQuiz.setTimeLimit(limit);
        currentQuiz.setCreatedBy(user.getUserId());
        currentQuiz.setTitle(titleText);
        currentQuiz.setDescription(descriptionText);

        if (titleText.isEmpty()) {
            utils.setErrorMessage("Quiz title cannot be empty.", quizTitle, errMessage, true);
            return;
        }

        LocalDate selectedDate = datePicker.getDate();
        Timestamp deadline = null;
        if (selectedDate != null) {
            LocalDate now = LocalDate.now();
            if (selectedDate.isBefore(now)) {
                utils.setErrorMessage("Deadline cannot be in the past.", null, errMessage, true);
                return;
            }
            LocalDateTime ldt = selectedDate.atStartOfDay();
            deadline = Timestamp.valueOf(ldt);
        }

        int classId = getSelectedClassId();
        try {
            if (currentQuiz.getQuizId() == 0) {
                int quizId = quizDAO.insertQuiz(titleText, descriptionText, currentQuiz.getTimeLimit(), currentQuiz.getCreatedBy(), deadline, classId);
                currentQuiz.setQuizId(quizId);
                insertAllQuestions(quizId);
                JOptionPane.showMessageDialog(this, "Quiz saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                quizDAO.updateQuiz(currentQuiz.getQuizId(), titleText, descriptionText, currentQuiz.getTimeLimit(), deadline);
                quizDAO.deleteQuestionsForQuiz(currentQuiz.getQuizId());
                insertAllQuestions(currentQuiz.getQuizId());
                JOptionPane.showMessageDialog(this, "Quiz updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            isModified = false;
            isTempQuizActive = false;
            resetForm();
            loadQuizzesForClass(classId);

        } catch (Exception e) {
            utils.setErrorMessage("Error saving quiz: " + e.getMessage(), null, errMessage, true);
            e.printStackTrace();
        }
    }

    private void insertAllQuestions(int quizId) throws SQLException {
        for (Question q : questions) {
            if (q instanceof MCQQuestion) {
                MCQQuestion mcq = (MCQQuestion) q;
                int questionId = quizDAO.insertQuestion(quizId, "MCQ", mcq.getText(), mcq.getPoints());
                mcq.setQuestionId(questionId);
                quizDAO.insertMCQOptions(questionId, mcq.getOptions(), mcq.getCorrectIndex());
            }
        }
    }

    private int getSelectedClassId() {
        String val = (String) selectedClass.getSelectedItem();
        if (val == null || val.isEmpty()) return -1;
        return Integer.parseInt(val.split(":")[0].trim());
    }

    private void resetForm() {
        questions.clear();
        questionPanels.clear();
        currentQuestionIndex = 0;
        totalQuestions.setValue(10);
        quizTitle.setText("");
        quizDescription.setText("");
        timeLimit.setValue(30);
        qPoints.setValue(1);
        datePicker.clear();
        errMessage.setText(" ");

        CreateMCQ firstMCQ = new CreateMCQ();
        questionPanels.add(firstMCQ);
        questions.add(null);
        qPanel.removeAll();
        qPanel.addTab("Question 1", firstMCQ);
        totalQuestionsLimit = 10;
        updateNavigationButtons();
        currentQuiz = new Quiz();
        currentQuiz.setCreatedBy(user.getUserId());
        isModified = false;
        isTempQuizActive = false;
    }

    private void clearErrorState(JPanel panel) {
        if (panel instanceof CreateMCQ) {
            ((CreateMCQ) panel).clearErrorState();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        selectedClass = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        nextButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        qPanel = new javax.swing.JTabbedPane();
        jSeparator2 = new javax.swing.JSeparator();
        selectedQType = new javax.swing.JComboBox<>();
        selectedQuiz = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        quizDescription = new javax.swing.JTextArea();
        quizTitle = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        datePicker = new com.github.lgooddatepicker.components.DatePicker();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        totalQuestions = new javax.swing.JSpinner();
        timeLimit = new javax.swing.JSpinner();
        qPoints = new javax.swing.JSpinner();
        errMessage = new javax.swing.JLabel();

        jLabel1.setText("Total Questions:");
        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        selectedClass.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Class 1", "Class 2", "Class 3", "Class 4" }));
        selectedClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectedClassActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        nextButton.setText("Next >");
        nextButton.setBackground(new java.awt.Color(43, 99, 223));
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        backButton.setText("< Back");
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        qPanel.setPreferredSize(new java.awt.Dimension(653, 490));

        selectedQType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "MCQ" }));

        selectedQuiz.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Create New..." }));

        jLabel2.setText("Type:");
        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel3.setText("Question Settings");
        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel4.setText("Quiz Settings");
        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        quizDescription.setColumns(20);
        quizDescription.setRows(5);
        jScrollPane1.setViewportView(quizDescription);

        jLabel5.setText("Title:");
        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel6.setText("Quiz Description");

        datePicker.setForeground(new java.awt.Color(30, 30, 30));

        jLabel7.setText("Deadline:");
        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel8.setText("Time Limit:");

        jLabel9.setText("Mins");

        jLabel10.setText("Points:");
        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        totalQuestions.setValue(10);

        errMessage.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator3)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(errMessage))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(selectedClass, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel6)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addGroup(layout.createSequentialGroup()
                                                            .addComponent(jLabel7)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(datePicker, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                            .addComponent(jLabel1)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(totalQuestions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                            .addComponent(jLabel5)
                                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(quizTitle)))
                                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGroup(layout.createSequentialGroup()
                                                        .addComponent(jLabel8)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(timeLimit, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jLabel9))))
                                            .addComponent(selectedQuiz, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jLabel3))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(qPoints, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(selectedQType, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(backButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(nextButton))
                    .addComponent(qPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE))
                .addGap(78, 78, 78))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSeparator1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(errMessage))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectedClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectedQuiz, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(totalQuestions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(quizTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(23, 23, 23)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(datePicker, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(timeLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(selectedQType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(qPoints, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(qPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 490, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nextButton)
                    .addComponent(backButton))
                .addGap(33, 33, 33))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void selectedClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectedClassActionPerformed

    }//GEN-LAST:event_selectedClassActionPerformed

    // Event Handlers
    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        if (!collectCurrentQuestionData()) return;

        if (nextButton.getText().equals("Finish")) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to finish and save the quiz?",
                "Confirm Finish", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                saveQuizToDatabase();
            }
        } else {
            if (currentQuestionIndex == questionPanels.size() - 1) {
                if (questionPanels.size() < totalQuestionsLimit) {
                    CreateMCQ newPanel = new CreateMCQ();
                    questionPanels.add(newPanel);
                    questions.add(null);
                    currentQuestionIndex = questionPanels.size() - 1;
                    qPanel.removeAll();
                    qPanel.addTab("Question " + (currentQuestionIndex + 1), newPanel);
                    clearErrorState(newPanel);
                    isModified = true;
                    qPoints.setValue(1);
                            
                } else {
                    JOptionPane.showMessageDialog(this,
                            "You have reached the total number of questions. Increase total questions if you want more.",
                            "Limit Reached", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                currentQuestionIndex++;
                qPanel.removeAll();
                qPanel.addTab("Question " + (currentQuestionIndex + 1), questionPanels.get(currentQuestionIndex));
                loadQuestionDataForCurrentIndex();
            }
            updateNavigationButtons();
        }
    }//GEN-LAST:event_nextButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
  if (!collectCurrentQuestionData()) return;
        currentQuestionIndex--;
        qPanel.removeAll();
        qPanel.addTab("Question " + (currentQuestionIndex + 1), questionPanels.get(currentQuestionIndex));
        loadQuestionDataForCurrentIndex();
        updateNavigationButtons();
    }//GEN-LAST:event_backButtonActionPerformed
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private com.github.lgooddatepicker.components.DatePicker datePicker;
    private javax.swing.JLabel errMessage;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JButton nextButton;
    private javax.swing.JTabbedPane qPanel;
    private javax.swing.JSpinner qPoints;
    private javax.swing.JTextArea quizDescription;
    private javax.swing.JTextField quizTitle;
    private javax.swing.JComboBox<String> selectedClass;
    private javax.swing.JComboBox<String> selectedQType;
    private javax.swing.JComboBox<String> selectedQuiz;
    private javax.swing.JSpinner timeLimit;
    private javax.swing.JSpinner totalQuestions;
    // End of variables declaration//GEN-END:variables
}
