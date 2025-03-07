import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

public class TypingSpeedTester {
    private JFrame frame;
    private JTextArea textArea;
    private JButton startButton, finishButton;
    private JLabel timerLabel, wpmLabel, accuracyLabel;
    private JComboBox<String> sampleTextComboBox;
    private String[] sampleTexts = {
        "The quick brown fox jumps over the lazy dog",
        "A journey of a thousand miles begins with a single step",
        "To be or not to be, that is the question"
    };
    private Timer timer;
    private int timeLeft = 60;
    private boolean testStarted = false;

    public TypingSpeedTester() {
        frame = new JFrame("Typing Speed Tester");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(3, 1));
        
        sampleTextComboBox = new JComboBox<>(sampleTexts);
        topPanel.add(sampleTextComboBox);

        JLabel promptLabel = new JLabel("Type this:");
        topPanel.add(promptLabel);

        textArea = new JTextArea();
        textArea.setEnabled(false);
        topPanel.add(textArea);

        frame.add(topPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 5));
        
        timerLabel = new JLabel("Time: 60s");
        wpmLabel = new JLabel("WPM: 0");
        accuracyLabel = new JLabel("Accuracy: 0%");

        startButton = new JButton("Start Test");
        startButton.addActionListener(new StartButtonListener());

        finishButton = new JButton("Finish Test");
        finishButton.setEnabled(false);
        finishButton.addActionListener(new FinishButtonListener());

        bottomPanel.add(timerLabel);
        bottomPanel.add(wpmLabel);
        bottomPanel.add(accuracyLabel);
        bottomPanel.add(startButton);
        bottomPanel.add(finishButton);

        frame.add(bottomPanel, BorderLayout.SOUTH);
        
        frame.setVisible(true);
    }

    private class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!testStarted) {
                textArea.setEnabled(true);
                textArea.setText("");
                textArea.requestFocus();
                testStarted = true;
                startButton.setEnabled(false);
                finishButton.setEnabled(true);
                startTimer();
            }
        }
    }

    private class FinishButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (testStarted) {
                timer.cancel();
                endTest();
            }
        }
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeLeft--;
                timerLabel.setText("Time: " + timeLeft + "s");
                if (timeLeft == 0) {
                    timer.cancel();
                    endTest();
                }
            }
        }, 1000, 1000);
    }

    private void endTest() {
        textArea.setEnabled(false);
        startButton.setEnabled(true);
        finishButton.setEnabled(false);
        testStarted = false;

        String typedText = textArea.getText().trim();
        String sampleText = (String) sampleTextComboBox.getSelectedItem();
        String[] typedWords = typedText.split(" ");
        String[] sampleWords = sampleText.split(" ");

        int correctWords = 0;
        for (int i = 0; i < Math.min(typedWords.length, sampleWords.length); i++) {
            if (typedWords[i].equals(sampleWords[i])) {
                correctWords++;
            }
        }

        int wpm = correctWords;
        double accuracy = (typedWords.length == 0) ? 0 : ((double) correctWords / typedWords.length) * 100;

        wpmLabel.setText("WPM: " + wpm);
        accuracyLabel.setText("Accuracy: " + String.format("%.2f", accuracy) + "%");

        JOptionPane.showMessageDialog(frame, "Test Finished!\nWPM: " + wpm + "\nAccuracy: " + String.format("%.2f", accuracy) + "%");

        saveResults(wpm, accuracy);
    }

    private void saveResults(int wpm, double accuracy) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("results.txt", true))) {
            writer.write("WPM: " + wpm + ", Accuracy: " + String.format("%.2f", accuracy) + "%\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TypingSpeedTester::new);
    }
}
