package game;

import java.net.URISyntaxException;
import java.net.URL;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundUtil {
	private String[] fileNames = {"lowShipBeep.wav", "highShipBeep.wav", "explosion.wav"};
	private Clip lowShipBeep;
	private Clip highShipBeep;
	private Clip explosion;
	
	public SoundUtil() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try {
			for (int i = 0; i < fileNames.length; i++) {
				String name = fileNames[i];
				URL url = classLoader.getResource("resources/" + name);
				File audioFile = new File(url.toURI());
				AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
				AudioFormat format = audioStream.getFormat();

				DataLine.Info info = new DataLine.Info(Clip.class, format);

				if (i == 0) {
					lowShipBeep = (Clip) AudioSystem.getLine(info);
					lowShipBeep.open(audioStream);
				} else if (i == 1) {
					highShipBeep = (Clip) AudioSystem.getLine(info);
					highShipBeep.open(audioStream);
				} else if (i == 2) {
					explosion = (Clip) AudioSystem.getLine(info);
					explosion.open(audioStream);
				}
			}
			
		} catch (IOException | URISyntaxException |
				UnsupportedAudioFileException | LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void playLowBeep() {
		lowShipBeep.stop();
		lowShipBeep.setFramePosition(0);
		lowShipBeep.start();
	}
	
	public void playHighBeep() {
		highShipBeep.stop();
		highShipBeep.setFramePosition(0);
		highShipBeep.start();
	}
	
	public void playExplosion() {
		explosion.stop();
		explosion.setFramePosition(0);
		explosion.start();
	}
}
