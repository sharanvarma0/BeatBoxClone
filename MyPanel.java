import javax.swing.*;
import javax.sound.midi.*;
import java.awt.event.*;
import java.awt.*;

public class MyPanel extends JPanel implements ControllerEventListener {
    boolean msg = false;

    public void controlChange(ShortMessage event) {
        msg = true;
        repaint();
    }

    public void paintComponent(Graphics graphics) {
        if (msg) {
            Graphics2D g2d = (Graphics2D) graphics;
            int r = (int) (Math.random() * 256);
            int b = (int) (Math.random() * 256);
            int g = (int) (Math.random() * 256);

            graphics.setColor(new Color(r, b, g));

            int ht = (int) ((Math.random() * 120) + 10);
            int width = (int) ((Math.random() * 120) + 10);
            int x = (int) ((Math.random() * 40) + 10);
            int y = (int) ((Math.random() * 40) + 10);

            graphics.fillRect(x, y, width, ht);
            msg = false;
        }
    }
}



