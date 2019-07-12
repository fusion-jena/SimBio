package slib.sml.sm.core.metrics.ic.pso;

/**
 * A particle swarm-based approach for semantic similarity computation
 * Publication: Babalou, Samira, Alsayed Algergawy, and Birgitta König-Ries. "A particle swarm-based approach for semantic similarity computation." OTM Confederated International Conferences" On the Move to Meaningful Internet Systems". Springer, Cham, 2017. 
 * @author Samira Babalou (samira[dot]babalou[at]uni_jean[dot]de)
 */
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.URI;

import slib.graph.model.graph.G;
import slib.sml.sm.core.engine.SM_Engine;
import slib.utils.ex.SLIB_Exception;

public class PSO4 {

	private URI a;
	private URI b;
	private Set<URI> aParents;
	private Set<URI> bParents;
	private G graph;
	private String MergedOntFlag;
	private SM_Engine eng;
	private ArrayList<String> history;

	public PSO4() {
		a = null;
		b = null;
		aParents = new HashSet<URI>();
		bParents = new HashSet<URI>();
		graph = null;
		MergedOntFlag = null;
		eng = null;
		history = new ArrayList<String>();
	}

	public PSO4(URI c1, URI c2, Set<URI> aAncestors, Set<URI> bAncestors, G g, SM_Engine SMeng, String MOntFlag)
			throws SLIB_Exception {
		this.a = c1;
		this.b = c2;
		this.aParents = aAncestors;
		this.bParents = bAncestors;
		this.graph = g;
		this.eng = SMeng;
		this.MergedOntFlag = MOntFlag;

	}

	public ArrayList<URI> dspoAlgorithm() throws SLIB_Exception, URISyntaxException {

		history = new ArrayList<String>();
		ArrayList<URI> result = new ArrayList<URI>();
		Particle_process pp = new Particle_process();
		Fitness ff = new Fitness();
		Velocity vv = new Velocity();
		swarmEvolution se = new swarmEvolution();
		ArrayList<Element> Gi = new ArrayList<Element>();
		ArrayList<URI> ShareParents = new ArrayList<URI>();

		ArrayList<Element> population = pp.initialPopulation(a, b, aParents, bParents, graph, MergedOntFlag);
		if (population.size() == 0)
			return null;

		if (MergedOntFlag != null) {
			ShareParents = findShareP.findShareParent2(a, b, aParents, bParents, graph, MergedOntFlag);

			for (int s = 0; s < parameters.ExtraSP.size(); s++) {
				if (!existFunc(ShareParents, parameters.ExtraSP.get(s))) {
					ShareParents.add(parameters.ExtraSP.get(s));
				}
			}

		} else {
			ShareParents = pp.FindSP(a, b, aParents, bParents, graph, MergedOntFlag);
		}

		if (ShareParents.size() == 1) {
			result.add(ShareParents.get(0));
		} else {
			population = ff.FitFuncCacheBuilt(a, b, aParents, bParents, ShareParents, population, eng);

			int PiLength = pp.AssignLenght(population);

			// First run
			ArrayList<Element> Pi = pp.selectParticle(population, PiLength);
			ArrayList<Element> Fi = ff.fitnessVector(Pi, eng);
			ArrayList<Element> Bi = se.Bi_detetmin(Pi);
			Gi = Sorter.deepCopy(Bi);
			ArrayList<Element> Vi = vv.updateVi(Bi, Gi, Pi);
			Fi = ff.fitnessVector(Pi, eng);
			Pi = se.swarmEvol_func(Fi, Vi, Gi, PiLength, population.size());

			Gi = Sorter.deepCopy(Pi);

			// Other iterations
			boolean changFlag = false;
			while (!changFlag) {

				PiLength = pp.updateLenght2(population, Pi, Gi, PiLength);
				Pi = pp.selectParticle_iter2(population, Pi, PiLength, eng);
				Fi = ff.fitnessVector(Pi, eng);
				Bi = se.Bi_detetmin(Pi);
				Vi = vv.updateVi(Bi, Gi, Pi);
				Fi = ff.fitnessVector(Pi, eng);
				Pi = se.swarmEvol_func(Fi, Vi, Gi, PiLength, population.size());
				Gi = Sorter.deepCopy(Pi);
				changFlag = stopCheck(Gi);
			}
			if (Gi.get(0).getFitValue() == 0) {
				System.out.println(" ******Gi is null here *********");
				return null;
			} else {
				for (int gl = 0; gl < Gi.size(); gl++) {
					result.add(Gi.get(gl).getName());
				}
			}
			Pi = null;
			Fi = null;
			Bi = null;
			Vi = null;
		}

		pp = null;
		ff = null;
		vv = null;
		se = null;
		Gi = null;
		ShareParents = null;
		parameters.ExtraSP = null;
		population = null;

		return result;
	}

	private ArrayList<Element> FindBestFit(ArrayList<Element> fi) {
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

	private int FindIndex(ArrayList<Element> pop, URI uri) {
		for (int i = 0; i < pop.size(); i++) {
			if (pop.get(i).getName().equals(uri))
				return i;
		}
		return -1;
	}

	private int fixedLength() {
		int L = 1;
		parameters.kK = L;
		parameters.ss = L;
		return L;
	}

	///////////////////////////////////////////////////////////////////////////
	private boolean stopCheck(ArrayList<Element> gi) {
		boolean r = false;
		if (history == null || history.size() < parameters.h) {
			history.add(GiToString(gi));
		} else {
			history.remove(0);
			history.add(GiToString(gi));
		}
		r = checkNoChange(history);

		return r;
	}

	private boolean checkNoChange(ArrayList<String> his) {
		boolean ch = false;
		boolean stopCheck = false;
		int i = 0;
		while (stopCheck == false && i < his.size()) {
			String[] tem = his.get(i).split("\\,");
			for (int j = i + 1; j < his.size(); j++) {
				String[] tem2 = his.get(j).split("\\,");
				ch = compareArrays(tem, tem2);

				if (!ch)
					stopCheck = true;
				// break;
			}
			i++;
		}

		if (ch == true && history.size() >= parameters.h) {
			ch = true;
		} else {
			ch = false;
		}
		return ch;
	}

	private boolean compareArrays(String[] arry1, String[] arry2) {

		boolean f = true;
		for (int i = 1; i < arry1.length; i++) {
			String a = arry1[i];
			f = Arrays.asList(arry2).contains(a);
			if (f == false)
				return false;
		}

		return f;

	}

	private String GiToString(ArrayList<Element> gi) {
		String s = new String();
		for (int i = 0; i < gi.size(); i++) {
			s = s + "," + gi.get(i).getName();
		}
		return s;
	}

	private static boolean existFunc(ArrayList<URI> Bi2, URI uri) {
		for (int i = 0; i < Bi2.size(); i++) {
			if (Bi2.get(i).toString().equals(uri.toString()))
				return true;
		}

		return false;

	}
}
