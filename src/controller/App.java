package controller;

import view.IndexPage;

import javax.swing.*;

/**
 * This class serves as the entry point for the Hangman game application.
 */
public class App {

    /**
     * The main method that launches the Hangman game application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new IndexPage();
            }
        });
    }
}
