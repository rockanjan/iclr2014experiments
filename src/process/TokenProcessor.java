package process;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenProcessor {
	static String NUM = "<num>";
	public static String getSmoothedWord(String queryWord) {
		//String word = queryWord.toLowerCase();
		String word = queryWord;
		
		
		//try to match date
		Pattern p0 = Pattern.compile("[0-9]{4}"); //possible dates
		
		Pattern p1 = Pattern.compile("^-{0,1}[0-9]+\\.*[0-9]*"); //eg -9, 100, 100.001 etc
		Pattern p2 = Pattern.compile("^-{0,1}[0-9]*\\.*[0-9]+"); //eg. -.5, .5
		Pattern p3 = Pattern.compile("^-{0,1}[0-9]{1,3}[,[0-9]{3}]*\\.*[0-9]*"); //matches 100,000
		
		Pattern p4 = Pattern.compile("[0-9]+\\\\/[0-9]+"); // four \ needed, java converts it to \\
		Pattern p5 = Pattern.compile("[0-9]+:[0-9]+"); //ratios and time
		Pattern p6 = Pattern.compile("([0-9]+-)+[0-9]+"); // 1-2-3, 1-2-3-4 etc
		Pattern p7 = Pattern.compile("([0-9]+/)+[0-9]+"); // 1/2/3, 1/2/3/4 etc
		
		Matcher m0 = p0.matcher(word);
		Matcher m1 = p1.matcher(word);
		Matcher m2 = p2.matcher(word);
		Matcher m3 = p3.matcher(word);
		Matcher m4 = p4.matcher(word);
		Matcher m5 = p5.matcher(word);
		Matcher m6 = p6.matcher(word);
		Matcher m7 = p7.matcher(word);
		
		if(m0.matches()) {
			word = "_" + word.replaceAll("[0-9]", "d") + "_";
		} else if(m4.matches()) { 
			word = "_" + word.replaceAll("[0-9]+", NUM); //fractions
		} else if(m5.matches()) {
			word = "_" + word.replaceAll("[0-9]", "d") + "_"; //time and ratio
		} else if(m6.matches() || m7.matches()) {
			word = "_" + word.replaceAll("[0-9]", "d") + "_";
		} else if(m1.matches() || m2.matches() || m3.matches()) {
			word = NUM;
		}
		
		word = word.replaceAll("" +
				"([0-9]+)|" + 
				"([0-9]+\\\\/[0-9]+)|" +
				"(([0-9]+-)+[0-9]+)|" +
				"([0-9]+:[0-9]+)|" +
				"(^-{0,1}[0-9]{1,3}[,[0-9]{3}]*\\.*[0-9]*)|" +
				"(^-{0,1}[0-9]*\\.*[0-9]+)|" +
				"(^-{0,1}[0-9]+\\.*[0-9]*)+"
				, NUM); //for something like 10-years-old, 2-for-3 etc
		
		return word;
	}
	
	public static String getAlphaNumericType(String smoothedWord) {
		String type = "_MIXED_";
		if(smoothedWord.equals(NUM)) {
			type = "_NUM_";
		} else if(smoothedWord.contains(NUM) || smoothedWord.matches(".*[0-9]+.*")) {
			String numReplaced = smoothedWord.replaceAll("[0-9]+", "");
			numReplaced = numReplaced.replaceAll(NUM,"");
			for(int i=0; i<numReplaced.length(); i++) {
				char c = numReplaced.charAt(i);
				if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
					type = "_ALPHANUM_";
				}
			}
		} else {
			boolean containsAlpha = false;
			boolean containsAllAlpha = true;
			for(int i=0; i<smoothedWord.length(); i++) {
				char c = smoothedWord.charAt(i);
				if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
					containsAlpha = true;
				} else {
					containsAllAlpha = false;
				}
			}
			if(containsAllAlpha) {
				type = "_ALPHA_";
			}
			if(! containsAlpha) {
				type = "_SYM_";
			}
		}
		return type;	
	}
	
	public static String getCapitalType(String word) {
		String type = "_NA_";
		if(word.equals(word.toLowerCase())) {
			type = "_LOWER_";
		}
		if(hasAllCaps(word)) {
			type = "_ALLCAP_";
		} else if(hasTitleCase(word)) {
			type = "_TITLE_";
		} else if(hasCaps(word)) {
			type = "_HASCAP_";
		}
		return type;
	}
	
	public static boolean hasCaps(String word) {
		boolean result = false;
		for(int i=0; i<word.length(); i++) {
			char c = word.charAt(i);
			if(c >= 'A' && c <= 'Z') {
				result = true;
				break;
			}
		}
		return result;
	}
	
	
	public static boolean hasAllCaps(String word) {
		boolean result = true;
		for(int i=0; i<word.length(); i++) {
			char c = word.charAt(i);
			if(c < 'A' || c > 'Z') {
				result = false;
				break;
			}
		}
		return result;
	}
	
	public static boolean hasFirstCapFollowedByaToz(String word) {
		Pattern p = Pattern.compile("^[A-Z][a-z].*");
		Matcher m = p.matcher(word);
		return m.matches();
	}
	
	//different than hasInitialCap. only the initial has to be capital
	public static boolean hasTitleCase(String word) {
		String wordWithoutInitial = word.substring(1);
		if(hasInitialCap(word) && wordWithoutInitial.equals(wordWithoutInitial.toLowerCase())) {
			return true;
		}
		return false;
	}
	
	public static boolean hasInitialCap(String word) {
		char c = word.charAt(0); 
		if(c >= 'A' && c <= 'Z') {
			return true;
		}
		return false;
	}
	
	public static String[] prefixes(String word) {
		String prefix3 = "_NA_";
		String prefix4 = "_NA_";
		if(word.length() >= 3) {
			prefix3 = word.substring(0, 3);
		}
		if(word.length() >=4) {
			prefix4 = word.substring(0,4);
		}
		String[] prefixes = {prefix3, prefix4};
		return prefixes;
	}
	
	public static String[] suffixes(String word) {
		String suffix1= "_NA_";
		String suffix2 = "_NA_";
		String suffix3 = "_NA_";
		String suffix4 = "_NA_";
		int wordLength = word.length();
		suffix1 = word.substring(wordLength-1);
		if(wordLength >= 2) {
			suffix2 = word.substring(wordLength-2);
		}
		if(wordLength >= 3) {
			suffix3 = word.substring(wordLength-3);
		}
		if(wordLength >= 4) {
			suffix4 = word.substring(wordLength-4);
		}
		String[] suffixes = {suffix1, suffix2, suffix3, suffix4};
		return suffixes;
	}
	
	public static String suffixesOrthographic(String word) {
		//WARNING: ORDER MATTERS! mainly for -s and -ies
		String suffix= "_NA_";
		int wordLength = word.length();
		if(wordLength > 3 && word.endsWith("ing")) {
			suffix = "-ing";
		}
		if(wordLength > 3 && word.endsWith("ogy")) {
			suffix = "-ogy";
		}
		if(wordLength > 2 && word.endsWith("ed")) {
			suffix = "-ed";
		}
		if(wordLength > 1 && word.endsWith("s")) {
			suffix = "-s";
		}
		if(wordLength > 2 && word.endsWith("ly")) {
			suffix = "-ly";
		}
		if(wordLength > 3 && word.endsWith("ion")) {
			suffix = "-ion";
		}
		if(wordLength > 4 && word.endsWith("tion")) {
			suffix = "-tion";
		}
		if(wordLength > 3 && word.endsWith("ity")) {
			suffix = "-ity";
		}
		if(wordLength > 3 && word.endsWith("ies")) {
			suffix = "-ies";
		}
		if(wordLength > 3 && word.endsWith("ous")) {
			suffix = "-ous";
		}
		
		//Anjan added
		/*
		if(wordLength > 2 && word.endsWith("al")) {
			suffix = "-al";
		}
		if(wordLength > 3 && word.endsWith("ary")) {
			suffix = "-ary";
		}
		if(wordLength > 2 && word.endsWith("en")) {
			suffix = "-en";
		}
		if(wordLength > 2 && word.endsWith("ic")) {
			suffix = "-ic";
		}
		if(wordLength > 3 && word.endsWith("ant")) {
			suffix = "-ant";
		}
		if(wordLength > 3 && word.endsWith("ble")) {
			suffix = "-ble";
		}
		*/
		return suffix;
	}
	
	public static String prefixesOrthographic(String word) {
		String prefix= "_NA_";
		int wordLength = word.length();
		if(wordLength > 2 && word.startsWith("un")) {
			prefix = "un-";
		}
		if(wordLength > 3 && word.startsWith("non")) {
			prefix = "non-";
		}
		return prefix;		
	}
	
	public static void main(String[] args) {
		System.out.println(TokenProcessor.getSmoothedWord("1980"));
		System.out.println(TokenProcessor.getSmoothedWord("198012"));
		System.out.println(TokenProcessor.getSmoothedWord("198,012"));
		System.out.println(TokenProcessor.getSmoothedWord("19:30"));
		System.out.println(TokenProcessor.getSmoothedWord("12-10-2012"));
		System.out.println(TokenProcessor.getSmoothedWord("12/10/2012"));
		System.out.println(TokenProcessor.getSmoothedWord("19.02"));
		System.out.println(TokenProcessor.getSmoothedWord("2-for-3"));
		System.out.println(TokenProcessor.hasAllCaps("EBay"));
		System.out.println(TokenProcessor.hasAllCaps("EBAY"));
		
		System.out.println(TokenProcessor.hasInitialCap("eBay"));
		System.out.println(TokenProcessor.hasInitialCap("EBay"));
		
		String word = "how";
		String[] prefixes = TokenProcessor.prefixes(word);
		System.out.println("3 prefix : " + prefixes[0]);
		System.out.println("4 prefix : " + prefixes[1]);	
		
		String[] suffixes = TokenProcessor.suffixes(word);
		System.out.println("1 suffix : " + suffixes[0]);
		System.out.println("2 suffix : " + suffixes[1]);
		System.out.println("3 suffix : " + suffixes[2]);
		System.out.println("4 suffix : " + suffixes[3]);		
		
	}
}
