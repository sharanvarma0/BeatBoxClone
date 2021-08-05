import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.sound.midi.*;
import java.util.ArrayList;
import java.io.*;

public class BeatBox {
    private JPanel mainPanel;
    private ArrayList<JCheckBox> checkBoxList;
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;
    private JFrame frame;

    private String[] instrumentNames = {"Bass Drum", "Closed Hi Hat", "Open Hi Hat", "Acoustic Snare", "Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo", "Maracas", "Whistle", "Low Conga", "CowBell", "VibraSlap",
                                        "Low-Mid Tom", "High Agogo", "Open Hi Conga"};
    
    private int[] instruments = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};

    public static void main(String[] args) {
        BeatBox gui = new BeatBox();
        gui.buildGUI();
    }

    public void buildGUI() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        checkBoxList = new ArrayList<JCheckBox>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS);

        JButton start = new JButton("Start");
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);

        JButton serialize = new JButton("Serialize");
        serialize.addActionListener(new MySerializeListener());
        buttonBox.add(serialize);

        JButton restore = new JButton("Restore");
        restore.addActionListener(new MyRestoreListener());
        buttonBox.add(restore);

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (int i = 0; i < 16; i++) {
            nameBox.add(new Label(instrumentNames[i]));
        }

        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        frame.getContentPane().add(background);

        GridLayout grid = new GridLayout(16, 16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER, mainPanel);

        for (int i = 0; i < 256; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkBoxList.add(c);
            mainPanel.add(c);
        }

        setUpMidi();

        frame.setBounds(50, 50, 300, 300);
        frame.pack();
        frame.setVisible(true);
    }

    public void setUpMidi() {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();

            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void buildTrackAndStart() {
        int[] tracklist = null;
        sequence.deleteTrack(track);
        track = sequence.createTrack();

        for (int i = 0; i < 16; i++) {
            tracklist = new int[16];

            int key = instruments[i];

            for (int j = 0; j < 16; j++) {
                JCheckBox jc = checkBoxList.get(j + 16 * i);
                if (jc.isSelected()) {
                    tracklist[j] = key;
                } else {
                    tracklist[j] = 0;
                }
            }

            makeTracks(tracklist);
            track.add(MakeEvent.makeEvent(176, 1, 127, 0, 16));
        }

        track.add(MakeEvent.makeEvent(192, 9, 1, 0, 15));
        try {
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public class MyStartListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            buildTrackAndStart();
        }
    }

    public class MyStopListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            sequencer.stop();
        }
    }

    public class MyUpTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            float tempofactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempofactor * 1.03));
        }
    }

    public class MyDownTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            float tempofactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempofactor * 0.97));
        }
    }

    public class MySerializeListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            boolean[] checkboxState = new boolean[256];
            for (int i = 0; i < 256; i++) {
                JCheckBox tmpCheckbox = (JCheckBox) checkBoxList.get(i);
                if (tmpCheckbox.isSelected()) {
                    checkboxState[i] = true;
                }
            }

            try {
                FileOutputStream filestream = new FileOutputStream(new File("checkbox.ser"));
                ObjectOutputStream os = new ObjectOutputStream(filestream);
                os.writeObject(checkboxState);
                os.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public class MyRestoreListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            boolean[] restoredCheckboxStatus = null;

            try {
                FileInputStream inputstream = new FileInputStream(new File("checkbox.ser"));
                ObjectInputStream os = new ObjectInputStream(inputstream);
                restoredCheckboxStatus = (boolean[]) os.readObject();
                for (int i = 0; i < 256; i++) {
                    if (restoredCheckboxStatus[i]) {
                        JCheckBox checkbox = checkBoxList.get(i);
                        checkbox.setSelected(true);
                    }
                }
                os.close();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
                        
    public void makeTracks(int[] list) {
        for (int i = 0; i < 16; i++) {
            int key = list[i];
            
            if (key != 0) {
                track.add(MakeEvent.makeEvent(144, 9, key, 100, i));
                track.add(MakeEvent.makeEvent(128, 9, key, 100, i+1));
            }
        }
    }

}









