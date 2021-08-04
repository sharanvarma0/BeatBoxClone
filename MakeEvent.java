import javax.sound.midi.*;

public class MakeEvent {
    public static MidiEvent makeEvent(final int type, final int channel, final int instrument, final int frequency, final int tick) {
        MidiEvent localEvent = null;
        try {
            ShortMessage message = new ShortMessage();
            message.setMessage(type, channel, instrument, frequency);
            localEvent = new MidiEvent(message, tick);
        } catch (Exception localException) { }
        return localEvent;
    }
}

