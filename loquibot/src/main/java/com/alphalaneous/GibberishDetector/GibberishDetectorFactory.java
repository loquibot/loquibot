package com.alphalaneous.GibberishDetector;

import java.util.List;

/**
 * gibberish detector factory for creating an instance of GibberishDetector or another detector that extends it.
 * @author sfiszman
 *
 */
public class GibberishDetectorFactory {

	private final Class<? extends GibberishDetector> type;

	public GibberishDetectorFactory(Class<? extends GibberishDetector> type) {
		this.type = type;
	}
	/**
	 * creates a gibberish detector trained by the given lines and alphabet.
	 * @param trainingList list of lines for training
	 * @param goodList list of good valid lines
	 * @param badList list of bad gibberish lines
	 * @param alphabet String that contains all the alphabet of the language plus the white space character. for example: "abcdefghijklmnopqrstuvwxyz "
	 * @return gibberish detector 
	 */
	public GibberishDetector createGibberishDetector(List<String> trainingList, List<String> goodList, List<String> badList, String alphabet) {
		try {
			return type.getConstructor(new Class[] {List.class, List.class, List.class, String.class}).newInstance(trainingList, goodList, badList, alphabet);		
		} 
		catch (Exception e) {
			throw new IllegalArgumentException("Exception in GibberishDetectorFactory: " + (e.getCause() != null ? e.getCause().getMessage() : ""));
		}
	}
}