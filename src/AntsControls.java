
import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Gabriel
 * Date: 7/14/12
 * Time: 12:22 PM
 * To change this template use File | Settings | File Templates.
 * Additional contributions throughout by Ray Alfano until 7/15/12 7:48pm
 */
public class AntsControls {

    private Colony ants;
    private JPanel panel = new JPanel();
    private JButton playButton = new JButton("Go");



    private Timer steps = new Timer(0, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            step();
        }
    });

    public void step()
    {
        ants.step();
    }

    public void start()
    {
        steps.restart();
        playButton.setText("Pause");
    }

    public void pause()
    {
        steps.stop();
        playButton.setText("Go");
    }

    public AntsControls(Colony ants, int gridDimensions, int numberOfFoodSites)
    {
        this.ants = ants;

        Dimension playControlDimen = new Dimension(75,50);

        playButton.setPreferredSize(playControlDimen);
        playButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (steps.isRunning())
                    pause();
                else
                    start();
            }
        });

        final JButton stepButton = new JButton("Step");
        stepButton.setPreferredSize(playControlDimen);
        stepButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                pause();
                step();
            }
        });

        AntsControls.this.ants.setGrid(gridDimensions, gridDimensions, numberOfFoodSites);

        panel.add(playButton);
        panel.add(stepButton);
    }

    public JPanel getPanel()
    {
        return panel;
    }
}
