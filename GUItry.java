import javax.swing.*;
import com.toedter.calendar.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class GUItry extends JFrame {
    private ArrayList<String> section = new ArrayList<>();
    private JComboBox<String> dropDown = new JComboBox<>();
    private JPanel tasksPanel;
    private JPanel sectionsPanel;
    private JFrame doneTasksFrame;
    private HashMap<String, Integer> sectionTaskCount = new HashMap<>();
    private JTextField taskField;
    private JDateChooser calendar;

    private static final String SECTION_FILE = "sections.txt";
    private static final String TASK_FILE = "tasks.txt";
    private static final String DONE_TASK_FILE = "done_tasks.txt";

    private void updateDropDown() {
        dropDown.removeAllItems();
        for (String item : section) {
            dropDown.addItem(item);
        }
        saveSectionsToFile();
    }

    public void addSection(String newSection) {
        if (!section.contains(newSection)) {
            section.add(newSection);
            sectionTaskCount.put(newSection, 0);
            updateDropDown();
            addSectionLabel(newSection);
        }
    }

    private void saveSectionsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SECTION_FILE))) {
            for (String sec : section) {
                writer.println(sec);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSectionsFromFile() {
        try (Scanner scanner = new Scanner(new File(SECTION_FILE))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!section.contains(line)) {
                    section.add(line);
                    sectionTaskCount.put(line, 0);
                    addSectionLabel(line);
                }
            }
            updateDropDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addSectionLabel(String sectionName) {
        JLabel sectionLabel = new JLabel(sectionName);
        sectionLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Change font to Arial, bold, size 20
        sectionLabel.setForeground(Color.WHITE);
        sectionsPanel.add(sectionLabel);
        sectionsPanel.revalidate();
        sectionsPanel.repaint();
    }

    private void updateSectionLabels() {
        for (String sec : section) {
            int taskCount = sectionTaskCount.getOrDefault(sec, 0);
            for (Component comp : sectionsPanel.getComponents()) {
                if (comp instanceof JLabel) {
                    JLabel label = (JLabel) comp;
                    if (label.getText().startsWith(sec)) {
                        label.setText(sec + ": " + taskCount + " tasks");
                        break;
                    }
                }
            }
        }
    }

    private void addTaskCheckbox(String taskDescription, boolean isSelected) {
        JCheckBox taskCheckbox = new JCheckBox(taskDescription, isSelected);
        customizeTaskCheckbox(taskCheckbox);
        tasksPanel.add(taskCheckbox);
        refreshPanel(tasksPanel);

        updateSectionTaskCount(taskDescription, true); // Increment count when task is added

        taskCheckbox.addActionListener(e -> {
            if (taskCheckbox.isSelected()) {
                moveTaskToDoneTasks(taskCheckbox.getText());
                tasksPanel.remove(taskCheckbox);
                refreshPanel(tasksPanel);
            }
            updateSectionTaskCount(taskDescription, false); // Decrement count when task is marked as done
            saveTasksToFile();
        });
        updateSectionLabels();
    }
    
    private void moveTaskToDoneTasks(String taskDescription) {
        if (doneTasksFrame == null) {
            doneTasksFrame = new JFrame("Done Tasks");
            doneTasksFrame.setSize(300, 400);
            doneTasksFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            doneTasksFrame.setLayout(new BoxLayout(doneTasksFrame.getContentPane(), BoxLayout.Y_AXIS));
        }
        JCheckBox doneTaskCheckbox = new JCheckBox(taskDescription, true);
        doneTaskCheckbox.setFont(new Font("Arial", Font.PLAIN, 15)); // Change font to Arial, size 15
        doneTaskCheckbox.setForeground(Color.WHITE);
        doneTaskCheckbox.setBackground(Color.BLACK);
        doneTaskCheckbox.setEnabled(false);
        doneTasksFrame.add(doneTaskCheckbox);
        doneTasksFrame.revalidate();
        doneTasksFrame.repaint();

        // Update task count when moving task to done tasks
        updateSectionTaskCount(taskDescription, false);
        saveDoneTasksToFile(taskDescription);
        updateSectionLabels();
    }


    private void loadDoneTasksFromFile() {
        try (Scanner scanner = new Scanner(new File(DONE_TASK_FILE))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (doneTasksFrame == null) {
                    doneTasksFrame = new JFrame("Done Tasks");
                    doneTasksFrame.setSize(300, 400);
                    doneTasksFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    doneTasksFrame.setLayout(new BoxLayout(doneTasksFrame.getContentPane(), BoxLayout.Y_AXIS));
                }
                addDoneTask(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAllTasks() {
        loadTasksFromFile();
        loadDoneTasksFromFile();
    }


    private void customizeTaskCheckbox(JCheckBox taskCheckbox) {
        taskCheckbox.setFont(new Font("Arial", Font.PLAIN, 15)); // Change font to Arial, size 15
        taskCheckbox.setForeground(Color.BLACK);
        taskCheckbox.setBackground(Color.LIGHT_GRAY);
    }

    private void refreshPanel(JPanel panel) {
        panel.revalidate();
        panel.repaint();
    }

    private void updateSectionTaskCount(String taskDescription, boolean isAddingTask) {
        String sectionName = extractSectionName(taskDescription);
        int count = sectionTaskCount.getOrDefault(sectionName, 0);
        if (isAddingTask) {
            sectionTaskCount.put(sectionName, count + 1);
        } else {
            if (count > 0) {
                sectionTaskCount.put(sectionName, count - 1);
            }
        }
    }



    private String extractSectionName(String taskDescription) {
        int startIndex = taskDescription.indexOf('(');
        int endIndex = taskDescription.indexOf(',');
        return (startIndex != -1 && endIndex != -1)
                ? taskDescription.substring(startIndex + 1, endIndex).trim()
                : "";
    }

    private String getSectionFromTaskDescription(String taskDescription) {
        int startIndex = taskDescription.indexOf('(');
        int endIndex = taskDescription.indexOf(',');
        if (startIndex != -1 && endIndex != -1) {
            return taskDescription.substring(startIndex + 1, endIndex).trim();
        }
        return "";
    }

    private void saveTasksToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TASK_FILE))) {
            Component[] components = tasksPanel.getComponents();
            for (Component component : components) {
                if (component instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) component;
                    writer.println(checkBox.getText() + ";;" + checkBox.isSelected());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTasksFromFile() {
        try (Scanner scanner = new Scanner(new File(TASK_FILE))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(";;");
                if (parts.length == 2) {
                    String taskDescription = parts[0];
                    boolean isSelected = Boolean.parseBoolean(parts[1]);
                    addTaskCheckbox(taskDescription, isSelected);
                }
            }
            updateSectionLabels();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveDoneTasksToFile(String doneTask) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DONE_TASK_FILE, true))) {
            writer.println(doneTask);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void saveAllTasks() {
        saveTasksToFile();
        saveDoneTasksToFileIndividually(); // Call the new method to save done tasks individually
    }

    private void saveDoneTasksToFileIndividually() {
        Component[] components = doneTasksFrame.getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JCheckBox) {
                JCheckBox doneTaskCheckbox = (JCheckBox) component;
                saveDoneTasksToFile(doneTaskCheckbox.getText());
            }
        }
    }


    public GUItry() {
        setTitle("Task Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        int sideWidth = screenWidth / 4;
        int centerWidth = sideWidth * 3;

        setSize(screenWidth, screenHeight / 2);

        taskField = new JTextField();
        calendar = new JDateChooser();

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.BLACK);
        leftPanel.setPreferredSize(new Dimension(sideWidth, screenHeight));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.BLACK);
        centerPanel.setPreferredSize(new Dimension(centerWidth, screenHeight));

        Font fontHeaders = new Font("Arial", Font.BOLD, 30);
        Font fontText = new Font("Arial", Font.PLAIN, 15);

        JLabel sections = new JLabel("Sections");
        sections.setPreferredSize(new Dimension(sideWidth, 50));
        sections.setForeground(Color.WHITE);
        sections.setHorizontalAlignment(SwingConstants.CENTER);
        sections.setFont(fontHeaders);
        leftPanel.add(sections, BorderLayout.NORTH);

        sectionsPanel = new JPanel();
        sectionsPanel.setLayout(new BoxLayout(sectionsPanel, BoxLayout.Y_AXIS));
        sectionsPanel.setBackground(Color.BLACK);
        JScrollPane sectionsScrollPane = new JScrollPane(sectionsPanel);
        leftPanel.add(sectionsScrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        formPanel.setBackground(Color.BLACK);

        JFrame firstFrame = new JFrame("Add Sections");
        firstFrame.setSize(300, 200);
        firstFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        firstFrame.setLayout(new BorderLayout());
        firstFrame.setLocationRelativeTo(null);

        JPanel firstFramePanel = new JPanel();
        firstFramePanel.setLayout(new BoxLayout(firstFramePanel, BoxLayout.Y_AXIS));
        firstFramePanel.setBackground(Color.GRAY);

        JTextField sectionField = new JTextField();
        sectionField.setMaximumSize(new Dimension(Integer.MAX_VALUE, sectionField.getPreferredSize().height));
        firstFramePanel.add(sectionField);

        JButton addSectionButton = new JButton("Add Section");
        firstFramePanel.add(addSectionButton);
        firstFrame.add(firstFramePanel, BorderLayout.CENTER);

        JButton openFirstFrameButton = new JButton("Add sections");
        openFirstFrameButton.setFont(fontText);
        formPanel.add(openFirstFrameButton);

        openFirstFrameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                firstFrame.setVisible(true);
            }
        });

        addSectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newSection = sectionField.getText().trim();
                if (!newSection.isEmpty()) {
                    addSection(newSection);
                    sectionField.setText("");
                }
            }
        });

        JTextField taskField = new JTextField();
        taskField.setPreferredSize(new Dimension(500, 30));
        formPanel.add(taskField);

        dropDown.setPreferredSize(new Dimension(200, 30));
        formPanel.add(dropDown);

        calendar.setPreferredSize(new Dimension(150, 30));
        formPanel.add(calendar);

        JButton submitButton = new JButton("Submit");
        submitButton.setFont(fontText);
        formPanel.add(submitButton);

        centerPanel.add(formPanel, BorderLayout.CENTER);

        tasksPanel = new JPanel();
        tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));
        tasksPanel.setBackground(Color.BLACK);
        JScrollPane tasksScrollPane = new JScrollPane(tasksPanel);
        centerPanel.add(tasksScrollPane, BorderLayout.LINE_END);

        JButton viewDoneTasksButton = new JButton("Tasks Done");
        viewDoneTasksButton.setFont(fontText);
        formPanel.add(viewDoneTasksButton);

        viewDoneTasksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (doneTasksFrame == null) {
                    doneTasksFrame = new JFrame("Done Tasks");
                    doneTasksFrame.setSize(300, 400);
                    doneTasksFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    doneTasksFrame.setLayout(new BoxLayout(doneTasksFrame.getContentPane(), BoxLayout.Y_AXIS));
                }
                doneTasksFrame.setVisible(true);
            }
        });

        submitButton.addActionListener(e -> {
            String taskDescription = taskField.getText().trim();
            String selectedSection = (String) dropDown.getSelectedItem();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
            String selectedDate = dateFormat.format(calendar.getDate());

            if (isValidInput(taskDescription, selectedSection, selectedDate)) {
                String fullTaskDescription = String.format("%s (%s, %s)", taskDescription, selectedSection, selectedDate);
                addTaskCheckbox(fullTaskDescription, false);

                resetFormInputs();
            }
        });



        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);

        loadSectionsFromFile();
        loadTasksFromFile();
        loadAllTasks();
        loadDoneTasksFromFile();

        setVisible(true);

        // Save all tasks (including done tasks) when the program exits
        Runtime.getRuntime().addShutdownHook(new Thread(this::saveAllTasks));
    }

    private boolean isValidInput(String taskDescription, String selectedSection, String selectedDate) {
        return !taskDescription.isEmpty() && selectedSection != null && !selectedDate.isEmpty();
    }

    private void resetFormInputs() {
        taskField.setText("");
        dropDown.setSelectedIndex(0);
        calendar.setDate(null);
    }

    private void addDoneTask(String taskDescription) {
        JCheckBox doneTaskCheckbox = new JCheckBox(taskDescription, true);
        doneTaskCheckbox.setFont(new Font("Arial", Font.PLAIN, 15)); // Change font to Arial, size 15
        doneTaskCheckbox.setForeground(Color.WHITE);
        doneTaskCheckbox.setBackground(Color.BLACK);
        doneTaskCheckbox.setEnabled(false);
        doneTasksFrame.add(doneTaskCheckbox);
        doneTasksFrame.revalidate();
        doneTasksFrame.repaint();
    }


    public static void main(String[] args) {
        new GUItry();
    }
}
