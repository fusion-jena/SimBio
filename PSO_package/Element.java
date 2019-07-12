package slib.sml.sm.core.metrics.ic.pso;

/**
 * A particle swarm-based approach for semantic similarity computation
 * Publication: Babalou, Samira, Alsayed Algergawy, and Birgitta König-Ries. "A particle swarm-based approach for semantic similarity computation." OTM Confederated International Conferences" On the Move to Meaningful Internet Systems". Springer, Cham, 2017. 
 * @author Samira Babalou (samira[dot]babalou[at]uni_jean[dot]de)
 */
import org.openrdf.model.URI;

public class Element {
	// in our PSO elements structure, each element has name, fitness value,
	// velocity and numbe of selection.
	private URI name;
	private double fitValue;
	private double velocityValue;
	private int nbSelection;

	public Element() {
		name = null;
		fitValue = 0;
		velocityValue = 1;
		nbSelection = 0;

	}

	public Element(Element elm) {
		name = elm.name;
		fitValue = elm.fitValue;
		velocityValue = elm.velocityValue;
		nbSelection = elm.nbSelection;
	}

	public URI getName() {
		return name;
	}

	public void setName(URI s) {
		name = s;
	}

	public double getFitValue() {
		return fitValue;
	}

	public void setFitValue(double v) {
		fitValue = v;
	}

	public double getVelocityValue() {
		return velocityValue;
	}

	public void setVelocityValue(double v) {
		velocityValue = v;
	}

	public int getNbSelection() {
		return nbSelection;
	}

	public void setNbSelection(int nb) {
		nbSelection = nb;
	}

}
