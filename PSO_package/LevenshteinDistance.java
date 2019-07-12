package slib.sml.sm.core.metrics.ic.pso;

/**
 * A particle swarm-based approach for semantic similarity computation
 * Publication: Babalou, Samira, Alsayed Algergawy, and Birgitta König-Ries.
 * "A particle swarm-based approach for semantic similarity computation." OTM
 * Confederated International Conferences
 * " On the Move to Meaningful Internet Systems". Springer, Cham, 2017.
 * 
 * @author Samira Babalou (samira[dot]babalou[at]uni_jean[dot]de)
 */
public class LevenshteinDistance {

	public static double similarity(String s1, String s2) {
		if (s1.length() < s2.length()) {
			String swap = s1;
			s1 = s2;
			s2 = swap;
		}
		int bigLen = s1.length();
		if (bigLen == 0) {
			return 1.0;
		}
		return (bigLen - computeEditDistance(s1, s2)) / (double) bigLen;
	}

	public static int computeEditDistance(String s1, String s2) {
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();

		int[] costs = new int[s2.length() + 1];
		for (int i = 0; i <= s1.length(); i++) {
			int lastValue = i;
			for (int j = 0; j <= s2.length(); j++) {
				if (i == 0)
					costs[j] = j;
				else {
					if (j > 0) {
						int newValue = costs[j - 1];
						if (s1.charAt(i - 1) != s2.charAt(j - 1))
							newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
						costs[j - 1] = lastValue;
						lastValue = newValue;
					}
				}
			}
			if (i > 0)
				costs[s2.length()] = lastValue;
		}
		return costs[s2.length()];
	}

	public static double getDistance(String s1, String s2) {
		double CSimilarity = similarity(s1, s2);
		return CSimilarity;
	}

}