package com.alphalaneous.GibberishDetector;

import java.util.List;

public class GibberishDetectorExtended extends GibberishDetector {

	public GibberishDetectorExtended(List<String> trainingLinesList, List<String> goodLinesList, List<String> badLinesList,String alphabet) {
		super(trainingLinesList, goodLinesList, badLinesList, alphabet);
	}
}
