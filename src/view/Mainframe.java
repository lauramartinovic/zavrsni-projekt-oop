package view;

import controller.ExitButtonAction;
import controller.PlayButtonAction;


import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Mainframe extends JFrame implements MainframeInterface {
    private Font latoFont;
    private static JComboBox<String> categoryComboBox = new JComboBox<>();
    private JTextField enterName = new JTextField();

    public Mainframe() {
        super("Welcome");
        initialize();
    }

    private void initialize() {
        try {
            latoFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/Lato-Regular.ttf")).deriveFont(20f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(latoFont);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            latoFont = new Font("SansSerif", Font.PLAIN, 20);
        }

        setSize(800, 480);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        loadCategories();

        JPanel marginBox = new JPanel();
        marginBox.setLayout(new BorderLayout());
        marginBox.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 70));
        setContentPane(marginBox);

        marginBox.add(initTopPanel(), BorderLayout.NORTH);
        marginBox.add(initLeftPanel(), BorderLayout.WEST);
        marginBox.add(initRightPanel(), BorderLayout.EAST);
        marginBox.add(initBotPanel(), BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private Component initTopPanel() {
        JPanel northPanel = new JPanel();
        JLabel label = new JLabel("Welcome to Hangman");
        label.setFont(latoFont);
        northPanel.add(label, BorderLayout.CENTER);
        return northPanel;
    }

    private Component initBotPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        JButton b1 = new JButton("Play");
        JButton b2 = new JButton("Exit");

        b1.setFont(latoFont.deriveFont(Font.PLAIN, 15));
        b2.setFont(latoFont.deriveFont(Font.PLAIN, 15));

        panel.add(b1);

        PlayButtonAction playButtonAction = new PlayButtonAction(enterName, categoryComboBox);
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
        label1.setFont(latoFont);
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
        nameLabel.setFont(latoFont.deriveFont(Font.PLAIN, 15));
        enterName.setFont(latoFont.deriveFont(Font.PLAIN, 15));
        westPanel.add(nameLabel);
        westPanel.add(Box.createVerticalStrut(10));
        westPanel.add(enterName);
        return westPanel;
    }

    private Component initRightPanel() {
        JLabel label1 = new JLabel("CHOOSE A CATEGORY");
        label1.setFont(latoFont);
        label1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel eastPanel = new JPanel();
        eastPanel.setLayout(new BoxLayout(eastPanel, BoxLayout.Y_AXIS));
        eastPanel.add(Box.createVerticalStrut(100));
        categoryComboBox.setMaximumSize(new Dimension(400, 25));
        categoryComboBox.setFont(latoFont.deriveFont(Font.PLAIN, 15));
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
        categoryComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel renderer = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                renderer.setFont(latoFont.deriveFont(Font.PLAIN, 15));
                return renderer;
            }
        });
    }

    @Override
    public String getPlayerName() {
        return enterName.getText();
    }

    @Override
    public String getSelectedCategory() {
        return (String) categoryComboBox.getSelectedItem();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
    }
}
