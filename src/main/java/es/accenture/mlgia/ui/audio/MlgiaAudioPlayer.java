package es.accenture.mlgia.ui.audio;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.vaadin.addon.audio.server.AudioPlayer;
import org.vaadin.addon.audio.server.Encoder;
import org.vaadin.addon.audio.server.Stream;
import org.vaadin.addon.audio.server.encoders.WaveEncoder;
import org.vaadin.addon.audio.server.util.ULawUtil;
import org.vaadin.addon.audio.server.util.WaveUtil;
import org.vaadin.addon.audio.shared.PCMFormat;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MlgiaAudioPlayer {

	private AudioPlayer player;
	
	public void play(byte[] bytesAudio) {
		log.debug("MlgiaAudioPlayer play()");
	
		ByteBuffer fileBytes = ByteBuffer.wrap(bytesAudio);
		Stream stream = createWaveStream(fileBytes, new WaveEncoder());
		
		player = new AudioPlayer(stream);
		final double volume = 80d / 100d;
		player.setVolume(volume);
		player.play();
	}
	
	public void play(Stream stream) {
		log.debug("MlgiaAudioPlayer play()");
		
		player = new AudioPlayer(stream);
		final double volume = 80d / 100d;
		player.setVolume(volume);	
	}
	
	public void play(String filename) {
		log.debug("MlgiaAudioPlayer play()");
		
		ByteBuffer fileBytes = decodeToPcm(filename, "");
		Stream stream = createWaveStream(fileBytes, new WaveEncoder());
		
		player = new AudioPlayer(stream);
		final double volume = 80d / 100d;
		player.setVolume(volume);
		player.play();
	}
	
	/**
	 * Returns a ByteBuffer filled with PCM data. If the original audio file is using
	 * a different encoding, this method attempts to decode it into PCM signed data.
	 * @param fname 	filename
	 * @param dir		directory in which the file exists
	 * @return ByteBuffer containing byte[] of PCM data
	 */
	private static ByteBuffer decodeToPcm(String fname, String dir) {
		ByteBuffer buffer = null;
		try {
			// load audio file
			Path path = Paths.get(dir + fname);
			System.out.println(path.toAbsolutePath());
			byte[] bytes = Files.readAllBytes(path);
			// create input stream with audio file bytes
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new ByteArrayInputStream(bytes));
			AudioFormat.Encoding encoding = audioInputStream.getFormat().getEncoding();
			// handle current encoding
			if (encoding.equals(AudioFormat.Encoding.ULAW)) {
				buffer = ULawUtil.decodeULawToPcm(audioInputStream);
			} else {
				// for now assume it is PCM data and load it straight into byte buffer
				buffer = ByteBuffer.wrap(bytes);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer;
	}

	private static Stream createWaveStream(ByteBuffer waveFile, Encoder outputEncoder) {
		int startOffset = WaveUtil.getDataStartOffset(waveFile);
		int dataLength = WaveUtil.getDataLength(waveFile);
		int chunkLength = 5000;
		PCMFormat dataFormat = WaveUtil.getDataFormat(waveFile);
		//System.out.println(dataFormat.toString());
		//System.out.println("arrayLength: " + waveFile.array().length
//				+ "\n\rstartOffset: " + startOffset
//				+ "\n\rdataLength: " + dataLength
//				+ "\r\nsampleRate: " + dataFormat.getSampleRate());
		ByteBuffer dataBuffer = ByteBuffer.wrap(waveFile.array(),startOffset,dataLength);
		Stream stream = new Stream(dataBuffer, dataFormat, outputEncoder, chunkLength);
		return stream;
	}
}
