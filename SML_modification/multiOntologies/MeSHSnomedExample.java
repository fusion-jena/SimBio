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
import slib.graph.algo.validator.dag.ValidatorDAG;
import slib.graph.io.conf.GDataConf;
import slib.graph.io.loader.GraphLoaderGeneric;
import slib.graph.io.loader.bio.snomedct.GraphLoaderSnomedCT_RF2;
import slib.graph.io.util.GFormat;
import slib.graph.model.graph.G;
import slib.graph.model.graph.elements.E;
import slib.graph.model.graph.utils.Direction;
import slib.graph.model.impl.graph.memory.GraphMemory;
import slib.graph.model.impl.repo.URIFactoryMemory;
import slib.graph.model.repo.URIFactory;
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
public class MeSHSnomedExample {

	public static void removeMeshCycles(G meshGraph) throws SLIB_Ex_Critic {
		/* same code as stated in SML */

	}

	public static void main(String[] args) throws URISyntaxException {
		String[] DatasetTerm1 = { "D051437", "D006321" };
		String[] DatasetTerm2 = { "D009206", "D007238" };
		
		/* Engine from slib.sml.sm.core.engine.SM_Engine */
		SM_Engine.firstPriority = true; /*
										 * keep it true if you want to run
										 * Mesh+Snomed. Set it to FALSE if you
										 * desire to run Snomed+Mesh
										 */
		String MergedOnt = "MeshSnomedCT";

		try {
			Timer t = new Timer();
			t.start();

			// *********************** read MeSH**********************
			URIFactory factoryMesh = URIFactoryMemory.getSingleton();
			URI meshURI = factoryMesh.getURI("http://www.nlm.nih.gov/mesh/");
			G meshGraph = new GraphMemory(meshURI);
			GDataConf dataMeshXML = new GDataConf(GFormat.MESH_XML, "YourPath/desc2017.xml");
			/*
			 * the DTD must be located in the same directory
			 */
			GraphLoaderGeneric.populate(dataMeshXML, meshGraph);
			ValidatorDAG validatorDAG = new ValidatorDAG();
			boolean isDAG = validatorDAG.containsTaxonomicDag(meshGraph);
			MeSHSnomedExample.removeMeshCycles(meshGraph);
			isDAG = validatorDAG.containsTaxonomicDag(meshGraph);

			// *********************** read SnomedCT**********************
			String DATA_DIR = "YourPath/SNOMED";
			/*
			 * this is the directory in which the downloaded snomed version has
			 * been extracted
			 */
			String SNOMEDCT_VERSION = "20170131"; 
			String SNOMEDCT_DIR = DATA_DIR + "/SnomedCT_Release_INT_" + SNOMEDCT_VERSION;
			String SNOMEDCT_CONCEPT = DATA_DIR + "/sct2_Concept_Full_INT_" + SNOMEDCT_VERSION + ".txt";
			String SNOMEDCT_RELATIONSHIPS = DATA_DIR + "/sct2_Relationship_Full_INT_" + SNOMEDCT_VERSION + ".txt";
			URIFactory factorySnomed = URIFactoryMemory.getSingleton();
			URI snomedctURI = factorySnomed.getURI("http://snomedct/");
			G snomedGraph = new GraphMemory(snomedctURI);
			GDataConf conf = new GDataConf(GFormat.SNOMED_CT_RF2);
			conf.addParameter(GraphLoaderSnomedCT_RF2.ARG_CONCEPT_FILE, SNOMEDCT_CONCEPT);
			conf.addParameter(GraphLoaderSnomedCT_RF2.ARG_RELATIONSHIP_FILE, SNOMEDCT_RELATIONSHIPS);
			GraphLoaderGeneric.populate(conf, snomedGraph);
			// ***************************************************************************************************************************************
			/* Set the IC measure that you want */
			ICconf icConf = new IC_Conf_Topo(SMConstants.FLAG_ICI_SANCHEZ_2011_b_adapted);
			SMconf measureConf = new SMconf(SMConstants.FLAG_SIM_PAIRWISE_DAG_NODE_LIN_1998, icConf);

			/*
			 * Prepare your terms (retrieve from which file (Mesh/Snomed) with
			 * which prefix
			 */
			URI c1 = null, c2 = null;
			if (findShareP.loadDataset == false)
				findShareP.CSVtoArray();
			for (int i = 0; i < DatasetTerm1.length; i++) {
				if (SM_Engine.firstPriority == true) { // .MeshSnomed == true){
					c1 = factoryMesh.getURI("http://www.nlm.nih.gov/mesh/" + DatasetTerm1[i]);
					c2 = factorySnomed.getURI(snomedctURI.stringValue() + DatasetTerm2[i]);
					SM_Engine.firstPriority = true; // for SimBio Poratl
				} else {
					c1 = factorySnomed.getURI(snomedctURI.stringValue() + DatasetTerm1[i]);
					c2 = factoryMesh.getURI("http://www.nlm.nih.gov/mesh/" + DatasetTerm2[i]);
					SM_Engine.firstPriority = false; // For SimBio Portal
				}

				/* Run the engine for two graphs (meshGraph and snomedGraph) */
				boolean runTwoGraph = true; /* means run two graphs together */
				SM_Engine engine = new SM_Engine(c1, c2, meshGraph, snomedGraph, runTwoGraph);

				/* compute the similarity */
				double sim = engine.compare(measureConf, c1, c2, runTwoGraph);
				/*
				 * true means it runs MeshSnomed together
				 */
				System.out.println("--*******Sim " + c1.getLocalName() + "\t" + c2.getLocalName() + "\t" + "\t"
						+ Math.round(sim * 10000.0) / 10000.0 + "\n");
			}

			t.stop();
			t.elapsedTime();

		} catch (SLIB_Exception ex) {
			Logger.getLogger(MeSHSnomedExample.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
