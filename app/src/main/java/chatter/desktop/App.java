/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package chatter.desktop;

import java.awt.Dimension;

import javax.swing.JFrame;

import chatter.desktop.ui.Application;

public class App {

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            JFrame.setDefaultLookAndFeelDecorated(true);
            Application app = new Application();
            app.setResizable(false);
            app.setPreferredSize(new Dimension(500,600));
            app.pack();
            app.setVisible(true);
        });
    }
}
