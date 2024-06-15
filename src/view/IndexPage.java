package view;

import controller.ExitButtonAction;
import controller.PlayButtonAction;
import model.Game;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class IndexPage extends JFrame {

    Font f = new Font("serif", Font.BOLD, 20);
    public static JComboBox<String> categoryComboBox = new JComboBox<>();
    private JTextField enterName = new JTextField();
    static boolean con = true;

    public IndexPage() throws HeadlessException {
        super("Welcome");
        setSize(800, 480);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        if (con) loadCategories();
        con = false;

        //dodan outer panel koji stavlja marginu oko svih komponenti
        JPanel marginBox = new JPanel();
        marginBox.setLayout(new BorderLayout());
        marginBox.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 70));
        setContentPane(marginBox);

        marginBox.add(initTopPanel(), BorderLayout.NORTH);
        marginBox.add(initLeftPanel(), BorderLayout.WEST);
        marginBox.add(initRightPanel(), BorderLayout.EAST);
        marginBox.add(initBotPanel(), BorderLayout.SOUTH);

        setLocationRelativeTo(null);

        JPanel panel = (JPanel) initBotPanel();
        add(panel, BorderLayout.SOUTH);
    }

    private Component initTopPanel() {
        JPanel northPanel = new JPanel();
        JLabel label = new JLabel("Welcome to Hangman");
        label.setFont(f);
        northPanel.add(label, BorderLayout.CENTER);
        return northPanel;
    }

    private Component initBotPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        JButton b1 = new JButton("Play");
        JButton b2 = new JButton("Exit");
        panel.add(b1);

        PlayButtonAction playButtonAction = new PlayButtonAction(enterName, categoryComboBox);
        playButtonAction.setPlayButtonActionListener(event -> {
            new Game(event.getPlayerName());
            dispose();
        });
        b1.addActionListener(playButtonAction);

        ExitButtonAction exitButtonAction = new ExitButtonAction();
        exitButtonAction.setExitButtonActionListener(event -> System.exit(0));
        b2.addActionListener(exitButtonAction);

        panel.add(b2);
        panel.add(Box.createVerticalStrut(120));
        return panel;
    }

    private Component initLeftPanel() {
        JLabel label1 = new JLabel("GAME RULES");
        label1.setFont(f);
        JLabel label2 = new JLabel("Guess the hidden word or phrase by entering letters.");
        JLabel label3 = new JLabel("Be careful, each wrong letter builds the gallows.");
        JLabel nameLabel = new JLabel("Enter your name:");

        JPanel westPanel = new JPanel();
        westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
        westPanel.add(Box.createVerticalStrut(100));
        westPanel.add(label1);
        westPanel.add(Box.createVerticalStrut(10));
        westPanel.add(label2);
        westPanel.add(label3);
        westPanel.add(Box.createVerticalStrut(40));
        enterName.setMaximumSize(new Dimension(400, 25));
        westPanel.add(nameLabel);
        westPanel.add(Box.createVerticalStrut(10));
        westPanel.add(enterName);
        return westPanel;
    }

    private Component initRightPanel() {
        JLabel label1 = new JLabel("CHOOSE A CATEGORY");
        label1.setFont(f);
        label1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
        eastPanel.add(Box.createVerticalStrut(100));
        categoryComboBox.setMaximumSize(new Dimension(400, 25));
        eastPanel.add(label1);
        eastPanel.add(Box.createVerticalStrut(10));
        eastPanel.add(categoryComboBox);
        return eastPanel;
    }

    private void loadCategories() {
        try {
            File file = new File("data/words");
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();

                if (line.startsWith("[") && line.endsWith("]")) {
                    String category = line.substring(1, line.length() - 1);
                    categoryComboBox.addItem(category);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}