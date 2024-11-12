import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class DataStreamApp extends JFrame {
    private JTextArea originalTextArea;
    private JTextArea filteredTextArea;
    private JTextField searchField;
    private JButton loadButton;
    private JButton searchButton;
    private JButton quitButton;
    private Path currentFilePath;

    public DataStreamApp() {
        setTitle("Data Stream Processor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeComponents();
        layoutComponents();
        setupListeners();
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        originalTextArea = new JTextArea();
        originalTextArea.setEditable(false);
        filteredTextArea = new JTextArea();
        filteredTextArea.setEditable(false);

        searchField = new JTextField(20);
        loadButton = new JButton("Load File");
        searchButton = new JButton("Search");
        searchButton.setEnabled(false);
        quitButton = new JButton("Quit");
    }

    private void layoutComponents() {
        // Top panel for controls
        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Search String:"));
        controlPanel.add(searchField);
        controlPanel.add(loadButton);
        controlPanel.add(searchButton);
        controlPanel.add(quitButton);

        // Center panel for text areas
        JPanel textPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        textPanel.add(new JScrollPane(originalTextArea));
        textPanel.add(new JScrollPane(filteredTextArea));

        // Main layout
        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);

        // Add borders with titles
        ((JScrollPane)textPanel.getComponent(0)).setBorder(
                BorderFactory.createTitledBorder("Original File"));
        ((JScrollPane)textPanel.getComponent(1)).setBorder(
                BorderFactory.createTitledBorder("Filtered Results"));
    }

    private void setupListeners() {
        loadButton.addActionListener(e -> loadFile());
        searchButton.addActionListener(e -> searchFile());
        quitButton.addActionListener(e -> System.exit(0));
    }

    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                currentFilePath = fileChooser.getSelectedFile().toPath();
                List<String> lines = Files.readAllLines(currentFilePath);
                originalTextArea.setText(String.join("\n", lines));
                searchButton.setEnabled(true);
                filteredTextArea.setText(""); // Clear previous results
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error reading file: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchFile() {
        String searchString = searchField.getText().trim();
        if (searchString.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a search string",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Use Stream API to filter the lines
            List<String> filteredLines = Files.lines(currentFilePath)
                    .filter(line -> line.contains(searchString))
                    .collect(Collectors.toList());

            // Display filtered results
            filteredTextArea.setText(String.join("\n", filteredLines));

            if (filteredLines.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No matches found for: " + searchString,
                        "Search Results",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error processing file: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DataStreamApp().setVisible(true);
        });
    }
}