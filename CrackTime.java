package markov;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.nio.file.Files;


public class CrackTime {
	private static double twoCharRatio(File filename, String substring, String alphabet) throws IOException {
		// Read the contents of the file into a string
		String text = new String(Files.readAllBytes(filename.toPath()));
		for (int i = 32; i < 128; i++) {
			alphabet += (char) i;
		}

		// Calculate the ratio of the substring
		int numerator = countSubstringOccurrences(text, substring);
		int denominator = 0;
		for (int i = 0; i < alphabet.length(); i++) {
			String c = alphabet.substring(i, i+1);
			denominator += countSubstringOccurrences(text, c + substring.substring(1));
		}
		double ratio = denominator != 0 ? numerator / (double) denominator : 0;
		return ratio;
	}

	private static int countSubstringOccurrences(String text, String substring) {
		int lastIndex = 0;
		int count = 0;
		while (lastIndex != -1) {
			lastIndex = text.indexOf(substring, lastIndex);
			if (lastIndex != -1) {
				count++;
				lastIndex += substring.length();
			}
		}
		return count;
	}

	private static double stringStrength(File filename, String password) throws IOException {
		String text = new String(Files.readAllBytes(filename.toPath()));
		String alphabet = "";
		for (int i = 32; i < 128; i++) {
			alphabet += (char) i;
		}

		double probability = 1;
		for (int i = 0; i < password.length(); i++) {
			if (i == 0) {
				// Probability of the first character
				int firstCharOccurrences = countSubstringOccurrences(text, String.valueOf(password.charAt(i)));
				int denominator = firstCharOccurrences == 0 ? text.length() : firstCharOccurrences;
				probability *= firstCharOccurrences / (double) denominator;
			} else {
				// Ratio of the continuing substrings
				String substring = password.substring(i-1, i+1);
				double ratio = twoCharRatio(filename, substring, alphabet);
				probability *= ratio;
			}
		}

		// Calculate the "strength" of the string
		double strength = -10 * Math.log10(probability);

		return strength;
	}

	public static void main(String[] args) throws IOException {
	    File myFile = new File("src/phpbb.txt");
	    ArrayList<Double> strengthList = new ArrayList<>();
	    ArrayList<Double> crackTimeList = new ArrayList<>();

	    // Read the file and test the strength of each password
	    try (Scanner scanner = new Scanner(myFile)) {
	        int n = 184388;
	        while (scanner.hasNext()) {
	            String password = scanner.nextLine().trim();
	            double strength = stringStrength(myFile, password);
	            strengthList.add(strength);

	            // Calculate the time it would take to crack the password using a brute-force attack
	            String charset = getCharset(password);
	            double crackTime = crackTime(password.length(), charset.length(), 1e9);
	            crackTimeList.add(crackTime);

	            n--;
	            if (n % 2000 == 0) {
	                System.out.println("");
	                System.out.print(crackTime + ", ");
	            }
	            else {
	                System.out.print(crackTime + ", ");
	            }
	        }
	    }
	}

	private static String getCharset(String password) {
	    String charset = "";
	    for (int i = 0; i < password.length(); i++) {
	        char c = password.charAt(i);
	        if (charset.indexOf(c) == -1) {
	            charset += c;
	        }
	    }
	    return charset;
	}
	private static double crackTime(int passwordLength, int charSetSize, double guessesPerSecond) {
	    double guesses = Math.pow(charSetSize, passwordLength);
	    double seconds = guesses / guessesPerSecond;
	    return seconds;
	}
}
