/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.sound;

import jaddon.controller.StaticStandard;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author Paul
 */
public class ToneGenerator {
 
  /** Generates a tone.
  @param hz Base frequency (neglecting harmonic) of the tone in cycles per second
  @param msecs The number of milliseconds to play the tone.
  @param volume Volume, form 0 (mute) to 100 (max).
  @param addHarmonic Whether to add an harmonic, one octave up.
  */
  public static void generateTone(int hz,int msecs, int volume, boolean addHarmonic) {
      try {
        float frequency = 44100;
        byte[] buf;
        AudioFormat af;
        if (addHarmonic) {
            buf = new byte[2];
            af = new AudioFormat(frequency, 8, 2, true, false);
        } else {
            buf = new byte[1];
            af = new AudioFormat(frequency, 8, 1, true, false);
        }
        SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
        sdl.open(af);
        sdl.start();
        for(int i = 0; i < msecs * frequency / 1000; i++){
            final double angle = i / (frequency / hz) * 2.0 * Math.PI;
            buf[0] = (byte) (Math.sin(angle) * volume);
            if(addHarmonic) {
              buf[1] = (byte)(Math.sin(2 * angle) * volume * 0.6);
              sdl.write(buf, 0, 2);
            } else {
              sdl.write(buf, 0, 1);
            }
        }
        sdl.drain();
        sdl.stop();
        sdl.close();
    } catch (Exception ex) {
        StaticStandard.logErr("Error while generating tone: " + ex, ex);
    }
}
    
}
