package slib.examples.sml.MultiOnt;
/**
 * A particle swarm-based approach for semantic similarity computation
 * Publication: Babalou, Samira, Alsayed Algergawy, and Birgitta König-Ries. "A particle swarm-based approach for semantic similarity computation." OTM Confederated International Conferences" On the Move to Meaningful Internet Systems". Springer, Cham, 2017. 
 * @author Samira Babalou (samira[dot]babalou[at]uni_jean[dot]de)
 */
 
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDFS;

import slib.graph.algo.utils.GAction;
import slib.graph.algo.utils.GActionType;
import slib.graph.algo.utils.GraphActionExecutor;
import slib.graph.algo.validator.dag.ValidatorDAG;
import slib.graph.io.conf.GDataConf;
import slib.graph.io.loader.GraphLoaderGeneric;
import slib.graph.io.loader.bio.snomedct.GraphLoaderSnomedCT_RF2;
import slib.graph.io.loader.wordnet.GraphLoader_Wordnet;
import slib.graph.io.util.GFormat;
import slib.graph.model.graph.G;
import slib.graph.model.graph.elements.E;
import slib.graph.model.graph.utils.Direction;
import slib.graph.model.impl.graph.memory.GraphMemory;
import slib.graph.model.impl.repo.URIFactoryMemory;
import slib.graph.model.repo.URIFactory;
import slib.indexer.wordnet.IndexerWordNetBasic;
import slib.sml.sm.core.engine.SM_Engine;
import slib.sml.sm.core.metrics.ic.pso.PakhamoDataset;
import slib.sml.sm.core.metrics.ic.pso.findShareP;
import slib.sml.sm.core.metrics.ic.utils.IC_Conf_Topo;
import slib.sml.sm.core.metrics.ic.utils.ICconf;
import slib.sml.sm.core.utils.SMConstants;
import slib.sml.sm.core.utils.SMconf;
import slib.utils.ex.SLIB_Ex_Critic;
import slib.utils.ex.SLIB_Exception;
import slib.utils.impl.Timer;

/**
 *
 * @author Samira Babalou (samira.babalou@uni-jena.de)
 */
public class WordNetMeSHExample {

	public static void removeMeshCycles(G meshGraph) throws SLIB_Ex_Critic {
		/* same code as stated in SML */
	}

	public static void main(String[] args) throws URISyntaxException {
		String[] DatasetTerm1 = { "renal_failure", "heart" };
		String[] DatasetTerm2 = { "kidney_failure", "myocardium" };

		boolean MeshWordnet = false;
		/* Engine from slib.sml.sm.core.engine.SM_Engine */
		SM_Engine.firstPriority = false;
		/*
		 * keep it true if you want to run Mesh+WordNet. Set it to FALSE if you
		 * desire to run WordNet+Mesh
		 */
		String MergedOnt = "MeshWordNet";
		try {
			Timer t = new Timer();
			t.start();

			// *********************** read MeSH**********************
			URIFactory factoryMesh = URIFactoryMemory.getSingleton();
			URI meshURI = factoryMesh.getURI("http://www.nlm.nih.gov/mesh/");
			G meshGraph = new GraphMemory(meshURI);
			GDataConf dataMeshXML = new GDataConf(GFormat.MESH_XML, "YourPath/desc2017.xml");
			GraphLoaderGeneric.populate(dataMeshXML, meshGraph);
			ValidatorDAG validatorDAGMesh = new ValidatorDAG();
			boolean isDAG = validatorDAGMesh.containsTaxonomicDag(meshGraph);
			WordNetMeSHExample.removeMeshCycles(meshGraph);
			isDAG = validatorDAGMesh.containsTaxonomicDag(meshGraph);

			// *********************** read WordNet**********************
			String dataloc = "YourPath/WordNet/2.1/dict/";
			URIFactory factory = URIFactoryMemory.getSingleton();
			URI guri = factory.getURI("http://graph/wordnet/");
			G wordnet = new GraphMemory(guri);
			GraphLoader_Wordnet loader = new GraphLoader_Wordnet();
			GDataConf dataNoun = new GDataConf(GFormat.WORDNET_DATA, dataloc + "data.noun");
			GDataConf dataVerb = new GDataConf(GFormat.WORDNET_DATA, dataloc + "data.verb");
			GDataConf dataAdj = new GDataConf(GFormat.WORDNET_DATA, dataloc + "data.adj");
			GDataConf dataAdv = new GDataConf(GFormat.WORDNET_DATA, dataloc + "data.adv");
			loader.populate(dataNoun, wordnet);
			loader.populate(dataVerb, wordnet);
			loader.populate(dataAdj, wordnet);
			loader.populate(dataAdv, wordnet);
			GAction addRoot = new GAction(GActionType.REROOTING);
			GraphActionExecutor.applyAction(addRoot, wordnet);
			ValidatorDAG validatorDAGWordnet = new ValidatorDAG();
			Set<URI> roots = validatorDAGWordnet.getTaxonomicRoots(wordnet);
			String data_noun = dataloc + "index.noun";
			IndexerWordNetBasic indexWordnetNoun = new IndexerWordNetBasic(factory, wordnet, data_noun);

			// ***************************************************************************************************************************************
			/* Set the IC measure that you want */
			ICconf icConf = new IC_Conf_Topo(SMConstants.FLAG_ICI_SANCHEZ_2011_b_adapted);
			SMconf measureConf = new SMconf(SMConstants.FLAG_DIST_PAIRWISE_DAG_NODE_JIANG_CONRATH_1997, icConf);

			/*
			 * Prepare your terms (retrieve from which file (Mesh/WordNet) with
			 * which prefix
			 */
			findShareP.CSVtoArray();
			URI c1 = null, c2 = null;
			for (int i = 0; i < DatasetTerm1.length; i++) {
				if (DatasetTerm1[i] != null) {
					if (MeshWordnet == true) {
						c1 = factoryMesh.getURI("http://www.nlm.nih.gov/mesh/" + DatasetTerm1[i]);
						Set<URI> uris_Term2 = indexWordnetNoun.get(DatasetTerm2[i]);
						c2 = uris_Term2.iterator().next();
					} else {
						Set<URI> uris_Term1 = indexWordnetNoun.get(DatasetTerm1[i]);
						c1 = uris_Term1.iterator().next();
						c2 = factoryMesh.getURI("http://www.nlm.nih.gov/mesh/" + DatasetTerm2[i]);
					}

					// boolean mergeFlag = true;
					SM_Engine engine = new SM_Engine(c1, c2, meshGraph, wordnet, MergedOnt);
					// compute the similarity
					double sim = engine.compare(measureConf, c1, c2, MergedOnt);

					System.out.println("--*******Sim " + c1.getLocalName() + "\t" + c2.getLocalName() + "\t" + "\t"
							+ Math.round(sim * 10000.0) / 10000.0 + "\n");

				} else {
					System.out.println();
				}
			}

			t.stop();
			t.elapsedTime();

		} catch (SLIB_Exception ex) {
			Logger.getLogger(WordNetMeSHExample.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
