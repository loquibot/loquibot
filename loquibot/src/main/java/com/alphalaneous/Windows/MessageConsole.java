package com.alphalaneous.Windows;

import com.alphalaneous.Utils.Defaults;

import java.io.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.text.*;

public class MessageConsole
{
	private final JTextComponent textComponent;
	private final Document document;
	private final boolean isAppend;

	public MessageConsole(JTextComponent textComponent) {
		this(textComponent, true);
	}

	public MessageConsole(JTextComponent textComponent, boolean isAppend) {
		this.textComponent = textComponent;
		this.document = textComponent.getDocument();
		this.isAppend = isAppend;
		textComponent.setEditable(false);
	}

	public void redirectOut(Color textColor, PrintStream printStream) {
		ConsoleOutputStream cos = new ConsoleOutputStream(textColor, printStream);
		System.setOut( new PrintStream(cos, true) );
	}

	public void redirectErr(Color textColor, PrintStream printStream) {
		ConsoleOutputStream cos = new ConsoleOutputStream(textColor, printStream);
		System.setErr( new PrintStream(cos, true) );
	}

	private static FileOutputStream fileOutputStream;
	static {
		Date now = new Date();
		SimpleDateFormat format =
				new SimpleDateFormat ("yyyy.MM.dd-HH.mm.ss.SSSS");
		try {
			if(!Files.isDirectory(Paths.get(Defaults.saveDirectory + "/loquibot"))){
				Files.createDirectory(Paths.get(Defaults.saveDirectory + "/loquibot"));
			}
			if(!Files.isDirectory(Paths.get(Defaults.saveDirectory + "/loquibot/logs/"))){
				Files.createDirectory(Paths.get(Defaults.saveDirectory + "/loquibot/logs/"));
			}
			fileOutputStream = new FileOutputStream(Defaults.saveDirectory + "/loquibot/logs/" + format.format(now) + ".txt", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class ConsoleOutputStream extends ByteArrayOutputStream {
		private final String EOL = System.getProperty("line.separator");
		private SimpleAttributeSet attributes;
		private final PrintStream printStream;
		private final StringBuffer buffer = new StringBuffer(80);
		private boolean isFirstLine;

		public ConsoleOutputStream(Color textColor, PrintStream printStream)
		{
			if (textColor != null)
			{
				attributes = new SimpleAttributeSet();
				StyleConstants.setForeground(attributes, textColor);
			}

			this.printStream = printStream;

			if (isAppend)
				isFirstLine = true;
		}

		public void flush() {
			String message = toString();

			if (message.length() == 0) return;

			if (isAppend) handleAppend(message);
			else handleInsert(message);
			reset();
		}

		private void handleAppend(String message) {

			if (document.getLength() == 0) buffer.setLength(0);

			if (EOL.equals(message)) buffer.append(message);

			else {
				buffer.append(message);
				clearBuffer();
			}

			try {
				fileOutputStream.write(message.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		private void handleInsert(String message) {
			buffer.append(message);
			if (EOL.equals(message)) clearBuffer();

		}

		private void clearBuffer() {

			if (isFirstLine && document.getLength() != 0) buffer.insert(0, "\n");

			isFirstLine = false;
			String line = buffer.toString();

			try {
				if (isAppend) {
					int offset = document.getLength();
					document.insertString(offset, line, attributes);
					textComponent.setCaretPosition( document.getLength() );
				}
				else {
					document.insertString(0, line, attributes);
					textComponent.setCaretPosition( 0 );
				}
			}
			catch (BadLocationException ignored) {}

			if (printStream != null) printStream.print(line);

			buffer.setLength(0);
		}
	}
}
