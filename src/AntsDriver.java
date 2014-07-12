
import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Gabriel
 * Date: 7/15/12
 * Time: 1:59 AM
 * To change this template use File | Settings | File Templates.
 * Additional contributions throughout by Ray Alfano until 7/15/12 7:46pm
 */
public class AntsDriver {

    public static void main(String [] args){

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                JFrame frame = new JFrame("Ant Network");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                Colony ants = new Colony(50, 50, 100);
                frame.add(ants);
                final AntsControls antsPanel = new AntsControls(ants, 50, 10);

                frame.add(antsPanel.getPanel(), BorderLayout.NORTH);
                frame.setSize(600, 600);
                frame.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("ant_icon.png")).getImage());
                frame.setVisible(true);
            }
        });
    }
}
