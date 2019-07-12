package slib.sml.sm.core.metrics.ic.pso;

/**
 * A particle swarm-based approach for semantic similarity computation
 * Publication: Babalou, Samira, Alsayed Algergawy, and Birgitta König-Ries. "A particle swarm-based approach for semantic similarity computation." OTM Confederated International Conferences" On the Move to Meaningful Internet Systems". Springer, Cham, 2017. 
 * @author Samira Babalou (samira[dot]babalou[at]uni_jean[dot]de)
 */
import java.util.ArrayList;

import org.openrdf.model.URI;

public class swarmEvolution {
	public swarmEvolution() {

	}

	public ArrayList<Element> swarmEvol_func(ArrayList<Element> Fi, ArrayList<Element> Vi, ArrayList<Element> Gi,
			int PiLength, int populationSize) {
		int kk = 0, ss = 0;

		// Step6: Build keep_set and safe-set
		ArrayList<Element> Fpik = new ArrayList<Element>();
		ArrayList<Element> Vpik = new ArrayList<Element>();
		ArrayList<Element> KeepSet = new ArrayList<Element>();
		ArrayList<Element> SafeSet = new ArrayList<Element>();

		if (parameters.kK >= Fi.size()) {
			kk = Fi.size();
		} else {
			kk = parameters.kK;
		}

		if (kk > populationSize) {
			kk = populationSize;
		}
		for (int j = 0; j < kk; j++) {
			Fpik.add(Fi.get(j));
			Vpik.add(Vi.get(j));
		}

		KeepSet = intersectFunc(Fpik, Vpik);

		if (parameters.ss >= Fi.size()) {
			ss = Fi.size();
		} else {
			ss = parameters.ss;
		}
		if (ss > populationSize)
			ss = populationSize;

		for (int j = 0; j < ss; j++) {
			SafeSet.add(Fi.get(j));
		}

		return unionFunc(KeepSet, SafeSet);
	}

	public ArrayList<Element> Gi_detetmin(ArrayList<Element> pi, ArrayList<Element> Gi) {
		// Step8: update G
		if (Gi.size() == 0) {
			Gi = findBest(pi); // for 1st iteration
		} else {
			ArrayList<Element> besP = findBest(pi);
			ArrayList<Element> besG = findBest(Gi);
			if (besP.get(0).getFitValue() > besG.get(0).getFitValue()) {
				Gi.add(besP.get(0));
			}
		}

		return Gi;
	}

	public ArrayList<Element> Gi_update(ArrayList<Element> g, ArrayList<Element> p) {

		for (int i = 0; i < p.size(); i++) {
			if (!existFunc(g, p.get(i).getName()))
				g.add(p.get(i));
		}
		return g;
	}

	public ArrayList<Element> Bi_detetmin(ArrayList<Element> pb) {
		ArrayList<Element> Bi = new ArrayList<>();
		Bi = findBest(pb);
		return Bi;
	}

	private ArrayList<Element> findBest(ArrayList<Element> fi) {
		double maxf = fi.get(0).getFitValue();
		ArrayList<Element> el = new ArrayList<Element>();
		el.add(fi.get(0));

		for (int i = 1; i < fi.size(); i++) {
			if (fi.get(i).getFitValue() > maxf) {
				maxf = fi.get(i).getFitValue();
				el = new ArrayList<Element>();
				el.add(fi.get(i));
			}
		}
		return el;
	}

	private ArrayList<Element> intersectFunc(ArrayList<Element> fp, ArrayList<Element> Vp) {
		ArrayList<Element> array3 = new ArrayList<Element>();

		for (int i = 0; i < fp.size(); i++) {
			for (int j = 0; j < Vp.size(); j++) {
				if (fp.get(i).getName().equals(Vp.get(j).getName())) {
					array3.add(fp.get(i));
					break;
				}
			}
		}
		return array3;
	}

	// ********************************************************************************************************************************************************************
	// ********************************************************************************************************************************************************************
	private ArrayList<Element> unionFunc(ArrayList<Element> keepSet2, ArrayList<Element> safeSet2) {
		ArrayList<Element> array3 = keepSet2;

		for (int i = 0; i < safeSet2.size(); i++) {
			if (!existFunc(array3, safeSet2.get(i).getName())) {
				array3.add(safeSet2.get(i));
			}
		}
		return array3;
	}

	private boolean existFunc(ArrayList<Element> Bi2, URI uri) {
		for (int i = 0; i < Bi2.size(); i++) {
			if (Bi2.get(i).getName().toString().equals(uri.toString()))
				return true;
		}

		return false;

	}

}
