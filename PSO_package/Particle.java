package slib.sml.sm.core.metrics.ic.pso;

/**
 * A particle swarm-based approach for semantic similarity computation
 * Publication: Babalou, Samira, Alsayed Algergawy, and Birgitta König-Ries. "A particle swarm-based approach for semantic similarity computation." OTM Confederated International Conferences" On the Move to Meaningful Internet Systems". Springer, Cham, 2017. 
 * @author Samira Babalou (samira[dot]babalou[at]uni_jean[dot]de)
 */
import org.openrdf.model.URI;

public class Particle {
	private double value;
	private URI name;

	public URI getName() {
		return name;
	}

	public double getValue() {
		return value;
	}

	public void setParticleValue(double v) {
		value = v;
	}

	public void setParticleName(URI s) {
		name = s;
	}

}
