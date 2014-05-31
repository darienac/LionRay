// LionRay: wav to DFPWM converter
// by Gamax92

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LionRay extends JFrame
{
	private static float sampleRate = 32768;
	
	public static void main(String[] args) throws Exception
	{
		new LionRay();
	}
	
	public static void convert(String inputFilename, String outputFilename) throws UnsupportedAudioFileException, IOException {
		File inputFile = new File(inputFilename);
		AudioFormat convertFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, 8, 1, 1, sampleRate, false);
		AudioInputStream unconverted = AudioSystem.getAudioInputStream(inputFile);
		AudioInputStream in = AudioSystem.getAudioInputStream(convertFormat, unconverted);
		
		BufferedOutputStream outFile = new BufferedOutputStream(new FileOutputStream(outputFilename));
		
		byte[] readBuffer = new byte[1024];
		byte[] outBuffer = new byte[1024/8];
		DFPWM converter = new DFPWM();
		
		int read;
		while ((read = in.read(readBuffer)) > 0)
		{
			converter.compress(outBuffer, readBuffer, 0, 0, read/8);
		    outFile.write(outBuffer, 0, read/8);
		}
		outFile.close();
	}

	private JLabel labelInputFile, labelOutputFile;
	public static JTextField textInputFile, textOutputFile;
	private JButton buttonBrowseInput, buttonBrowseOutput, buttonConvert;

	private LionRay()
	{
		labelInputFile = new JLabel("Input File: ", SwingConstants.LEFT);
		labelOutputFile = new JLabel("Output File: ", SwingConstants.LEFT);
		
		textInputFile = new JTextField();
		textOutputFile = new JTextField();
		
		buttonBrowseInput = new JButton("Browse");
		buttonBrowseOutput = new JButton("Browse");
		buttonBrowseInput.addActionListener(new inputBrowseListener()); 
		buttonBrowseOutput.addActionListener(new outputBrowseListener()); 
		
		buttonConvert = new JButton("Convert");
		buttonConvert.addActionListener(new convertListener()); 
		
		Container pane = getContentPane();
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1,1,1,1);
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		pane.add(labelInputFile, c);
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.5;
		pane.add(textInputFile, c);
		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 0;
		pane.add(buttonBrowseInput, c);
		c.gridx = 0;
		c.gridy = 1;
		pane.add(labelOutputFile, c);
		c.gridx = 1;
		c.gridy = 1;
		pane.add(textOutputFile, c);
		c.gridx = 2;
		c.gridy = 1;
		pane.add(buttonBrowseOutput,c);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 3;
		pane.add(buttonConvert, c);
		
		setTitle("LionRay Wav Converter");
		setSize(400,107);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
}

class inputBrowseListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser(new File("."));
		fileChooser.setDialogTitle("Select file to convert");
		fileChooser.setFileFilter(new FileNameExtensionFilter(
			"WAV audio files", "wav"));
		if(fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
			return;
		LionRay.textInputFile.setText(fileChooser.getSelectedFile().getAbsolutePath());
	}
}

class outputBrowseListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser(new File("."));
		fileChooser.setDialogTitle("Select output file");
		fileChooser.setFileFilter(new FileNameExtensionFilter(
			"DFPWM audio files", "dfpwm"));
		if(fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION)
			return;
		LionRay.textOutputFile.setText(fileChooser.getSelectedFile().getAbsolutePath());
	}
}

class convertListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		if (LionRay.textInputFile.getText().trim().equals(""))
			JOptionPane.showMessageDialog(null, "No file specified for input");
		else if (!new File(LionRay.textInputFile.getText()).exists())
			JOptionPane.showMessageDialog(null, "Input file does not exists");
		else if (new File(LionRay.textInputFile.getText()).isDirectory())
			JOptionPane.showMessageDialog(null, "Input file is a directory");
		else if (LionRay.textOutputFile.getText().trim().equals(""))
			JOptionPane.showMessageDialog(null, "No file specified for output");
		else if (new File(LionRay.textOutputFile.getText()).isDirectory())
			JOptionPane.showMessageDialog(null, "Output file is a directory");
		else {
			try {
				LionRay.convert(LionRay.textInputFile.getText(), LionRay.textOutputFile.getText());
			} catch (UnsupportedAudioFileException e1) {
				JOptionPane.showMessageDialog(null, "Audio format unsupported");
				return;
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(null, "IOException occured, see stdout");
				return;
			}
			JOptionPane.showMessageDialog(null, "Conversion complete");
		}
	}
}
