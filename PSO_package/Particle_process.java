package slib.sml.sm.core.metrics.ic.pso;

/**
 * A particle swarm-based approach for semantic similarity computation
 * Publication: Babalou, Samira, Alsayed Algergawy, and Birgitta König-Ries. "A particle swarm-based approach for semantic similarity computation." OTM Confederated International Conferences" On the Move to Meaningful Internet Systems". Springer, Cham, 2017. 
 * @author Samira Babalou (samira[dot]babalou[at]uni_jean[dot]de)
 */
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.openrdf.model.URI;

import slib.graph.model.graph.G;
import slib.sml.sm.core.engine.SM_Engine;
import slib.utils.ex.SLIB_Exception;

public class Particle_process {

	public Particle_process() {

	}

	public ArrayList<Element> initialPopulation(URI a, URI b, Set<URI> aParents, Set<URI> bParents, G g,
			String MergedOntFlag) throws SLIB_Exception {

		ArrayList<Element> population = new ArrayList<Element>();
		ArrayList<URI> temp = new ArrayList<URI>();

		URI p;
		Iterator<URI> iap = aParents.iterator();
		while (iap.hasNext()) {
			p = iap.next();
			if (p.toString().equals("http://www.w3.org/2002/07/owl#Thing")
					|| p.toString().equals("http://snomedct/138875005")
					|| p.toString().equals("http://combinedGraph/NewRoot")) {
			} else if (!p.equals(a) && !p.equals(b)) {
				Element e = new Element();
				e.setName(p);
				e.setVelocityValue(1);
				e.setFitValue(0);
				e.setNbSelection(0);

				population.add(e);
				temp.add(p);
			}
		}

		Iterator<URI> ibp = bParents.iterator();
		while (ibp.hasNext()) {
			p = ibp.next();
			if (p.toString().equals("http://www.w3.org/2002/07/owl#Thing")
					|| p.toString().equals("http://snomedct/138875005")
					|| p.toString().equals("http://combinedGraph/NewRoot")) {
			} else if (!existFunc(temp, p) && !p.equals(b) && !p.equals(a)) {
				Element e = new Element();
				e.setName(p);
				e.setVelocityValue(1);
				e.setFitValue(0);
				e.setNbSelection(0);

				population.add(e);
				temp.add(p);

			}
		}

		temp = null;
		return population;
	}

	public ArrayList<URI> FindSP(URI a, URI b, Set<URI> aParents, Set<URI> bParents, G g, String MergedOntFlag)
			throws SLIB_Exception, URISyntaxException {
		ArrayList<URI> SP = new ArrayList<URI>();

		Iterator<URI> iap = aParents.iterator();
		while (iap.hasNext()) {
			URI aa = iap.next();
			Iterator<URI> ibp = bParents.iterator();
			while (ibp.hasNext()) {
				URI bb = ibp.next();
				if (aa.toString().equals(bb.toString())) {
					if (!aa.toString().equals("http://www.w3.org/2002/07/owl#Thing")
							&& !aa.toString().equals("http://snomedct/138875005")
							&& !aa.toString().equals("http://combinedGraph/NewRoot")) {

						SP.add(aa);
						break;
					}
				}
			}
		}

		ArrayList<URI> MappingShareParent = new ArrayList<URI>();
		if (MergedOntFlag != null) {
			MappingShareParent = findShareP.findShareParent2(a, b, aParents, bParents, g, MergedOntFlag);
		}

		SP.addAll(MappingShareParent);

		return SP;
	}

	public ArrayList<Element> selectParticle(ArrayList<Element> population, int PiLength) {

		ArrayList<Element> Pi = new ArrayList<Element>();

		if (population.size() <= Pi.size())
			PiLength = population.size();

		int i = 0;

		while (i < population.size() && Pi.size() < PiLength) {
			if (population.get(i).getNbSelection() == 0) {
				population.get(i).setNbSelection(1);
				Pi.add(population.get(i));
				i++;
			}
		}

		if (Pi.size() < PiLength)
			PiLength = Pi.size();
		return Pi;
	}

	public ArrayList<Element> selectParticle_iter2(ArrayList<Element> population, ArrayList<Element> Pi, int PiLength,
			SM_Engine eng) throws SLIB_Exception {

		boolean Fl = false;
		int i = 0;

		if (population.size() <= Pi.size())
			PiLength = population.size();

		if (Pi.size() < PiLength) {
			int n = 0;
			while (!Fl) {
				n++;
				if (n > parameters.iter)
					Fl = true;
				Iterator<Element> iop = population.iterator();
				while (iop.hasNext() && i < population.size() && Pi.size() < PiLength) {
					Element e = iop.next();
					if (e.getNbSelection() < n) {
						if (!existFuncPi(Pi, e.getName())) {
							Fl = true;
							e.setNbSelection(e.getNbSelection() + 1);
							Pi.add(e);
							i++;
						}
					}

				}
			}

		} else if (Pi.size() > PiLength) {
			while (PiLength < Pi.size()) {
				while (Pi.size() > PiLength) {
					Element wo = findWorst(Pi);
					ArrayList<Element> temp = new ArrayList<Element>();
					for (int k = 0; k < Pi.size(); k++) {
						if (!Pi.get(k).getName().toString().equals(wo.getName().toString()))
							temp.add(Pi.get(k));
					}
					Pi = temp;
				}
				Element wo = findWorst(Pi);
				int n = 0;
				while (!Fl) {
					n++;
					if (n > parameters.iter)
						Fl = true;
					Iterator<Element> iop = population.iterator();
					while (iop.hasNext() && i < population.size() && Pi.size() <= PiLength) {
						Element e = iop.next();
						if (e.getNbSelection() < n) {
							if (!existFuncPi(Pi, e.getName())) {
								if (e.getFitValue() > wo.getFitValue()) {
									ArrayList<Element> temp = new ArrayList<Element>();
									for (int k = 0; k < Pi.size(); k++) {
										if (!Pi.get(k).getName().toString().equals(wo.getName().toString()))
											temp.add(Pi.get(k));
									}
									Pi = temp;
									Fl = true;
									e.setNbSelection(e.getNbSelection() + 1);
									Pi.add(e);
									i++;
								}
							}
						}
					}
				}
			}
		} else {
			Element wo = findWorst(Pi);
			int n = 0;
			while (!Fl) {
				n++;
				if (n > parameters.iter)
					Fl = true;
				Iterator<Element> iop = population.iterator();
				while (iop.hasNext() && i < population.size() && Pi.size() <= PiLength) {
					Element e = iop.next();
					if (e.getNbSelection() < n) {
						if (!existFuncPi(Pi, e.getName())) {
							if (e.getFitValue() > wo.getFitValue()) {
								ArrayList<Element> temp = new ArrayList<Element>();
								for (int k = 0; k < Pi.size(); k++) {
									if (!Pi.get(k).getName().toString().equals(wo.getName().toString()))
										temp.add(Pi.get(k));
								}
								Pi = temp;
								Fl = true;
								e.setNbSelection(e.getNbSelection() + 1);
								Pi.add(e);
								i++;
							}
						}
					}
				}
			}
		}

		if (Pi.size() < PiLength)
			PiLength = Pi.size();

		return Pi;
	}

	private Element findWorst(ArrayList<Element> fi) {
		double minf = fi.get(0).getFitValue();
		Element el = new Element();
		el = fi.get(0);

		for (int i = 1; i < fi.size(); i++) {
			if (fi.get(i).getFitValue() < minf) {
				minf = fi.get(i).getFitValue();
				el = new Element();
				el = fi.get(i);
			}
		}
		return el;
	}

	public int AssignLenght(ArrayList<Element> population) {
		int k = 1;
		int min = 1, max = population.size() / 2;
		if (max > 0) {
			Random rand = new Random();
			k = rand.nextInt((max - min) + 1) + min;
		}
		return k;

	}

	public int updateLenght(ArrayList<Element> population, ArrayList<Element> pi, ArrayList<Element> Gi, int PiLength) {

		double Fpi = 0.0;
		for (int i = 0; i < pi.size(); i++) {
			Fpi = Fpi + pi.get(i).getFitValue();
		}
		Fpi = Fpi / (double) pi.size();

		if (Gi.size() > PiLength) {
			if (Fpi < parameters.fThreshold) {
				PiLength = (int) (PiLength + parameters.Winc * (PiLength + Gi.size())) + 1;
			} else {
				PiLength = (int) (PiLength + parameters.Winc * (PiLength + Gi.size())) - 1;
			}
		} else {
			if (Fpi < parameters.fThreshold) {
				PiLength = (int) (PiLength - parameters.Wdec * (PiLength - Gi.size())) + 1;
			} else {
				PiLength = (int) (PiLength - parameters.Wdec * (PiLength - Gi.size())) - 1;
			}

		}

		if (PiLength < 1)
			PiLength = 1;

		if (PiLength > population.size())
			PiLength = population.size();

		return PiLength;
	}

	public int updateLenght2(ArrayList<Element> population, ArrayList<Element> pi, ArrayList<Element> Gi,
			int PiLength) {

		double threshold = 0.5;
		double Fpi = 0.0;
		for (int i = 0; i < pi.size(); i++) {
			Fpi = Fpi + pi.get(i).getFitValue();
		}
		Fpi = Fpi / (double) pi.size();

		double Winc = 0.7, Wdec = 0.3;
		if (Gi.size() > PiLength) {// never
			if (Fpi < threshold) {
				PiLength = (int) (PiLength + Winc * (PiLength + Gi.size())) + parameters.deltaStep;
			} else {
				PiLength = (int) (PiLength + Winc * (PiLength + Gi.size())) - parameters.deltaStep;
			}
		} else {
			if (Fpi < threshold) {
				PiLength = (int) (PiLength - Wdec * (PiLength - Gi.size())) + parameters.deltaStep;
			} else {
				PiLength = (int) (PiLength - Wdec * (PiLength - Gi.size())) - parameters.deltaStep;
			}

		}

		if (PiLength < 1)
			PiLength = 1;

		if (PiLength > population.size())
			PiLength = population.size();

		return PiLength;
	}

	public static boolean existFunc(ArrayList<URI> Bi2, URI uri) {
		for (int i = 0; i < Bi2.size(); i++) {
			if (Bi2.get(i).toString().equals(uri.toString()))
				return true;
		}

		return false;

	}

	private boolean existFuncPi(ArrayList<Element> Bi2, URI uri) {
		for (int i = 0; i < Bi2.size(); i++) {
			if (Bi2.get(i).getName().toString().equals(uri.toString()))
				return true;
		}

		return false;

	}
}
