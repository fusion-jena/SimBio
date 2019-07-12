package slib.sml.sm.core.metrics.ic.pso;

/**
 * A particle swarm-based approach for semantic similarity computation
 * Publication: Babalou, Samira, Alsayed Algergawy, and Birgitta König-Ries. "A particle swarm-based approach for semantic similarity computation." OTM Confederated International Conferences" On the Move to Meaningful Internet Systems". Springer, Cham, 2017. 
 * @author Samira Babalou (samira[dot]babalou[at]uni_jean[dot]de)
 */
import java.util.ArrayList;

public class Sorter {

	public Sorter() {

	}

	public ArrayList<Element> DesSortFit(final ArrayList<Element> fpp) {
		ArrayList<Element> fp = new ArrayList<Element>();
		fp = deepCopy(fpp);

		for (int i = 0; i < fp.size(); i++) {
			for (int j = 0; j < fp.size(); j++) {
				if (fp.get(i).getFitValue() >= fp.get(j).getFitValue()) {
					Element temp = new Element();

					temp.setName(fp.get(i).getName());
					temp.setFitValue(fp.get(i).getFitValue());
					temp.setVelocityValue(fp.get(i).getVelocityValue());
					temp.setNbSelection(fp.get(i).getNbSelection());

					fp.get(i).setName(fp.get(j).getName());
					fp.get(i).setFitValue(fp.get(j).getFitValue());
					fp.get(i).setVelocityValue(fp.get(j).getVelocityValue());
					fp.get(i).setNbSelection(fp.get(j).getNbSelection());

					fp.get(j).setName(temp.getName());
					fp.get(j).setFitValue(temp.getFitValue());
					fp.get(j).setVelocityValue(temp.getVelocityValue());
					fp.get(j).setNbSelection(temp.getNbSelection());
				}
			}
		}
		return fp;
	}

	public ArrayList<Element> DesSortVel(ArrayList<Element> fpp) {
		ArrayList<Element> fp = new ArrayList<Element>();
		fp = deepCopy(fpp);

		for (int i = 0; i < fp.size(); i++) {
			for (int j = 0; j < fp.size(); j++) {
				if (fp.get(i).getVelocityValue() >= fp.get(j).getVelocityValue()) {
					Element temp = new Element();

					temp.setName(fp.get(i).getName());
					temp.setFitValue(fp.get(i).getFitValue());
					temp.setVelocityValue(fp.get(i).getVelocityValue());
					temp.setNbSelection(fp.get(i).getNbSelection());

					fp.get(i).setName(fp.get(j).getName());
					fp.get(i).setFitValue(fp.get(j).getFitValue());
					fp.get(i).setVelocityValue(fp.get(j).getVelocityValue());
					fp.get(i).setNbSelection(fp.get(j).getNbSelection());

					fp.get(j).setName(temp.getName());
					fp.get(j).setFitValue(temp.getFitValue());
					fp.get(j).setVelocityValue(temp.getVelocityValue());
					fp.get(j).setNbSelection(temp.getNbSelection());
				}
			}
		}
		return fp;
	}

	public static ArrayList<Element> deepCopy(ArrayList<Element> arrEl) {
		ArrayList<Element> clonedList = new ArrayList<Element>(arrEl.size());
		for (Element el : arrEl) {
			clonedList.add(new Element(el));
		}
		return clonedList;
	}
}
