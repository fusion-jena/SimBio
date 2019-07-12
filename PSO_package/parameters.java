package slib.sml.sm.core.metrics.ic.pso;

/**
 * A particle swarm-based approach for semantic similarity computation
 * Publication: Babalou, Samira, Alsayed Algergawy, and Birgitta König-Ries. "A particle swarm-based approach for semantic similarity computation." OTM Confederated International Conferences" On the Move to Meaningful Internet Systems". Springer, Cham, 2017. 
 * @author Samira Babalou (samira[dot]babalou[at]uni_jean[dot]de)
 */
import java.util.ArrayList;

import org.openrdf.model.URI;

public class parameters {

	public static double beta = 1;
	public static double landa = 0.6;
	public static int kK = 3; // number of safeSet selection
	public static int ss = 2; // number of Gi elements selection
	public static double sigma = 0.7;
	public static double fThreshold = 0.5;
	public static int iter = 100;
	public static int h = 10; // number of history
	public static int deltaStep = 3;
	public static double Winc = 0.03;
	public static double Wdec = 0.5;
	public static ArrayList<URI> ExtraSP = null;
}
