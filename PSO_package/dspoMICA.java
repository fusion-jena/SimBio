package slib.sml.sm.core.metrics.ic.pso;

/**
 * A particle swarm-based approach for semantic similarity computation
 * Publication: Babalou, Samira, Alsayed Algergawy, and Birgitta König-Ries. "A particle swarm-based approach for semantic similarity computation." OTM Confederated International Conferences" On the Move to Meaningful Internet Systems". Springer, Cham, 2017. 
 * @author Samira Babalou (samira[dot]babalou[at]uni_jean[dot]de)
 */
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Set;
import org.openrdf.model.URI;
import slib.utils.ex.SLIB_Exception;
import slib.graph.model.graph.G;
import slib.graph.model.impl.repo.URIFactoryMemory;
import slib.graph.model.repo.URIFactory;
import slib.sml.sm.core.engine.SM_Engine;;

public class dspoMICA {

	public ArrayList<URI> PSOfunc(URI a, URI b, Set<URI> aAncestors, Set<URI> bAncestors, Set<URI> aDescendantors,
			Set<URI> bDescendantors, G graph, SM_Engine eng, String MergedOnt)
			throws SLIB_Exception, URISyntaxException {

		ArrayList<URI> mica = new ArrayList<URI>();

		if (a.toString().equals(b.toString())) {
			URIFactory factory = URIFactoryMemory.getSingleton();
			URI rea = factory.getURI("http://www.nlm.nih.gov/test/1");
			mica.add(rea);
		} else if (MergedOnt != null && findShareP.existInDataset(a, b, MergedOnt)) {
			URIFactory factory = URIFactoryMemory.getSingleton();
			URI rea = factory.getURI("http://www.nlm.nih.gov/test/1");
			mica.add(rea);
		} else {
			PSO4 ae = new PSO4(a, b, aAncestors, bAncestors, graph, eng, MergedOnt);
			mica = ae.dspoAlgorithm();
		}
		return mica;
	}
}
