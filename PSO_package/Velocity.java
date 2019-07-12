package slib.sml.sm.core.metrics.ic.pso;

/**
 * A particle swarm-based approach for semantic similarity computation
 * Publication: Babalou, Samira, Alsayed Algergawy, and Birgitta König-Ries. "A particle swarm-based approach for semantic similarity computation." OTM Confederated International Conferences" On the Move to Meaningful Internet Systems". Springer, Cham, 2017. 
 * @author Samira Babalou (samira[dot]babalou[at]uni_jean[dot]de)
 */
import java.util.ArrayList;

import org.openrdf.model.URI;

public class Velocity {
	public Velocity() {

	}

	public ArrayList<Element> updateVi(ArrayList<Element> Bii, ArrayList<Element> Gii, ArrayList<Element> Pii) {
		// Step5: update Vi
		ArrayList<Element> res = new ArrayList<Element>();

		for (int j = 0; j < Pii.size(); j++) {
			if (existFunc(Bii, Pii.get(j).getName()) == true) {
				Pii.get(j).setVelocityValue(Pii.get(j).getVelocityValue() + parameters.beta);
			}
			if (existFunc(Gii, Pii.get(j).getName()) == true) {
				Pii.get(j).setVelocityValue(Pii.get(j).getVelocityValue() + parameters.landa);
			}
		}

		for (int j = 0; j < Pii.size(); j++) {
			Pii.get(j).setVelocityValue(Pii.get(j).getVelocityValue() * Math.random() * 1);
		}

		Sorter srr = new Sorter();
		res = srr.DesSortVel(Pii);

		return res;
	}

	private boolean existFunc(ArrayList<Element> Bi2, URI uri) {
		for (int i = 0; i < Bi2.size(); i++) {
			if (Bi2.get(i).getName().toString().equals(uri.toString()))
				return true;
		}

		return false;

	}

}
