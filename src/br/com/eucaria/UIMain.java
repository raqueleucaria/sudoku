package br.com.eucaria;

import br.com.eucaria.ui.custom.frame.MainFrame;
import br.com.eucaria.ui.custom.panel.MainPanel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;

public class UIMain {
    public static void main(String[] args) {
        var dimension = new Dimension(600, 600);
        JPanel mainPanel = new MainPanel(dimension);
        JFrame mainFrame = new MainFrame(dimension, mainPanel);
        mainFrame.revalidate(); //recarregar a interface
        mainFrame.repaint();
    }
}
