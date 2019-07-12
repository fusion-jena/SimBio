package slib.sml.sm.core.metrics.ic.pso;

/**
 * A particle swarm-based approach for semantic similarity computation
 * Publication: Babalou, Samira, Alsayed Algergawy, and Birgitta König-Ries. "A particle swarm-based approach for semantic similarity computation." OTM Confederated International Conferences" On the Move to Meaningful Internet Systems". Springer, Cham, 2017. 
 * @author Samira Babalou (samira[dot]babalou[at]uni_jean[dot]de)
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.openrdf.model.URI;

import slib.sml.sm.core.engine.SM_Engine;
import slib.sml.sm.core.metrics.ic.utils.IC_Conf_Topo;
import slib.sml.sm.core.metrics.ic.utils.ICconf;
import slib.sml.sm.core.utils.SMConstants;
import slib.sml.sm.core.utils.SMconf;
import slib.utils.ex.SLIB_Exception;

public class Fitness {

	public Fitness() {

	}

	// ********************************************************************************************************************************************************************
	// ********************************************************************************************************************************************************************
	public ArrayList<Element> fitnessVector(ArrayList<Element> P, SM_Engine eng) throws SLIB_Exception {
		ArrayList<Element> res = new ArrayList<Element>();
		Sorter sr = new Sorter();
		res = sr.DesSortFit(P);
		return res;

	}

	// ********************************************************************************************************************************************************************
	// ********************************************************************************************************************************************************************
	public static double FitFunc(URI a, URI b, Set<URI> aParents, Set<URI> bParents, ArrayList<URI> SP, SM_Engine eng,
			URI candidParent) throws SLIB_Exception {
		double f = 0.0;

		Set<URI> allParentA = aParents;
		Set<URI> allParentB = bParents;

		double sz = (double) eng.getAllDescendantsInc().size();
		double sc = (double) eng.getDescendantsInc(candidParent).size();

		if (existFunc(allParentA, b) || existFunc(allParentB, a)) {
			Iterator<URI> firstParentA = eng.getParents(a).iterator();
			Iterator<URI> firstParentB = eng.getParents(b).iterator();
			while (firstParentA.hasNext()) {
				URI s = firstParentA.next();
				if (s.toString().equals(candidParent.toString()) && existFunc(SP, s)) {
					f = 1 / (sc / sz);

				}
			}
			while (firstParentB.hasNext()) {
				URI s = firstParentB.next();
				if (s.toString().equals(candidParent.toString()) && existFunc(SP, s)) {
					f = 1 / (sc / sz);
				}
			}
			return Math.round(f * 10000.0) / 10000.0;
		}
		if (SP.size() == 1) {
			Set<URI> chi = eng.getDescendantsInc(SP.get(0));
			Iterator<URI> iterChi = chi.iterator();
			while (iterChi.hasNext()) {
				URI c = iterChi.next();
				if (c.toString().equals(candidParent.toString()) && !c.toString().equals(SP.get(0).toString()))
					f = f + ((double) eng.getDescendantsInc(c).size() / sz);
			}
			return Math.round(f * 10000.0) / 10000.0;
		}
		if (SP.size() > 1) {
			for (int i = 0; i < SP.size(); i++) {
				if (SP.get(i).toString().equals(candidParent.toString())) {
					f = (double) eng.getDescendantsInc(SP.get(i)).size() / sz;
					break;
				}
			}
		}
		if (f == 0) {
			f = sc / sz;
		}

		f = Math.round(f * 10000.0) / 10000.0;

		return f;
	}

	private static double distanceFunc(URI a, URI b, URI c, SM_Engine eng) {
		double dis_a_c = 0.0, dis_b_c = 0.0, f = 0.0;
		double dis_c = (double) eng.getDescendantsInc(c).size();
		dis_a_c = dis_c - (double) eng.getDescendantsInc(a).size();
		dis_b_c = dis_c - (double) eng.getDescendantsInc(b).size();
		f = (double) 1 / (dis_a_c + dis_b_c);
		return f;
	}

	private static double icFunc(SM_Engine eng, URI c) throws SLIB_Exception {
		ICconf icConf = new IC_Conf_Topo(SMConstants.FLAG_ICI_SANCHEZ_2011_b_adapted);
		SMconf conf = new SMconf(SMConstants.FLAG_SIM_PAIRWISE_DAG_NODE_LIN_1998, icConf);
		double ic = eng.getIC(conf.getICconf(), c);
		return ic;
	}

	public static double FitTaxonomicPosition(SM_Engine eng, URI candidParent, URI a, URI b) throws SLIB_Exception { 
		double f = 0, f1 = 0, f2 = 0;

		Set<URI> pChild = eng.getDescendantsInc(candidParent);
		Set<URI> aChild = eng.getDescendantsInc(a);
		Set<URI> bChild = eng.getDescendantsInc(b);
		double t1 = intersectFuncNb(pChild, aChild);
		double t2 = unionFuncNb(pChild, aChild);
		double t3 = intersectFuncNb(pChild, bChild);
		double t4 = unionFuncNb(pChild, bChild);

		if (t1 != 0 && t2 != 0)
			f1 = Math.log((t1 / t2));
		if (t3 != 0 && t4 != 0)
			f2 = Math.log((t3 / t4));
		f = (f1 + f2) / 2;

		return f;
	}

	// *******************************************************************************************************************************************************************
	// ********************************************************************************************************************************************************************
	public static double intersectFuncNb(Set<URI> A, Set<URI> B) {
		double c = 0;
		Iterator<URI> itA = A.iterator();
		while (itA.hasNext()) {
			URI a = itA.next();
			Iterator<URI> itB = B.iterator();
			while (itB.hasNext()) {
				if (itB.next().toString().equals(a.toString())) {
					c++;
					break;
				}
			}
		}

		return c;
	}

	// *******************************************************************************************************************************************************************
	// ********************************************************************************************************************************************************************
	public static double unionFuncNb(Set<URI> A, Set<URI> B) {
		double c = 0;
		ArrayList<URI> temp = new ArrayList<>();
		Iterator<URI> itA = A.iterator();
		while (itA.hasNext()) {
			URI a = itA.next();
			temp.add(a);
		}
		Iterator<URI> itB = B.iterator();
		while (itB.hasNext()) {
			URI b = itB.next();
			if (!existFunc(temp, b)) {
				temp.add(b);
			}
		}
		c = (double) temp.size();
		return c;
	}

	public ArrayList<Element> FitFuncCacheBuilt(URI a, URI b, Set<URI> aParents, Set<URI> bParents, ArrayList<URI> SP,
			ArrayList<Element> population, SM_Engine eng) throws SLIB_Exception {

		for (int i = 0; i < population.size(); i++) {
			population.get(i).setFitValue(FitFunc(a, b, aParents, bParents, SP, eng, population.get(i).getName()));
		}

		population = NormFitValue(population);
		return population;
	}

	private ArrayList<Element> NormFitValue(ArrayList<Element> pop) {
		double maxf = findMax(pop);
		double minf = findMin(pop);

		for (int i = 0; i < pop.size(); i++) {
			pop.get(i).setFitValue((pop.get(i).getFitValue() - minf) / (maxf - minf));
		}

		return pop;
	}

	private double findMin(ArrayList<Element> fi2) {
		double minf = fi2.get(0).getFitValue();
		for (int i = 0; i < fi2.size(); i++) {
			if (fi2.get(i).getFitValue() < minf) {
				minf = fi2.get(i).getFitValue();
			}
		}
		return minf;
	}

	private double findMax(ArrayList<Element> fi2) {
		double maxf = fi2.get(0).getFitValue();
		for (int i = 0; i < fi2.size(); i++) {
			if (fi2.get(i).getFitValue() > maxf) {
				maxf = fi2.get(i).getFitValue();
			}
		}
		return maxf;
	}

	// ********************************************************************************************************************************************************************
	// ********************************************************************************************************************************************************************

	public static boolean existFunc(Set<URI> arr, URI uri) {
		Iterator<URI> ia = arr.iterator();
		while (ia.hasNext()) {
			if (ia.next().toString().equals(uri.toString()))
				return true;
		}

		return false;

	}

	public static boolean existFunc(ArrayList<URI> Bi2, URI uri) {
		for (int i = 0; i < Bi2.size(); i++) {
			if (Bi2.get(i).toString().equals(uri.toString()))
				return true;
		}

		return false;

	}

}
