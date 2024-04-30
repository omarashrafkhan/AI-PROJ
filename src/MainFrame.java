import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame  {



    public MainFrame() {
        // Set the title of the frame
        setTitle("Path Visualization");

        // Set the default close operation
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setSize(600,600);

        this.setExtendedState(MAXIMIZED_BOTH);

        // Create an instance of the PathPanel class and add it to the frame
        PathPanel pathPanel = new PathPanel();
        add(pathPanel);


    }


    public static void main(String[] args) {
        // Create an instance of the MainFrame class
        SwingUtilities.invokeLater(() -> {
            Toolkit.getDefaultToolkit().sync();
            new MainFrame().setVisible(true);

        });
    }

}
