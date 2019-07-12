package simbio.main;
/**
 * Coordinator 
 * @author Samira Babalou (samira[dot]babalou[at]uni_jean[dot]de)
 */

import java.net.URISyntaxException;

import org.openrdf.model.URI;

import slib.graph.algo.utils.GAction;
import slib.graph.algo.utils.GActionType;
import slib.graph.algo.utils.GraphActionExecutor;
import slib.graph.io.conf.GDataConf;
import slib.graph.io.loader.GraphLoaderGeneric;
import slib.graph.io.loader.bio.snomedct.GraphLoaderSnomedCT_RF2;
import slib.graph.io.loader.wordnet.GraphLoader_Wordnet;
import slib.graph.io.util.GFormat;
import slib.graph.model.graph.G;
import slib.graph.model.impl.graph.memory.GraphMemory;
import slib.graph.model.impl.repo.URIFactoryMemory;
import slib.graph.model.repo.URIFactory;
import slib.indexer.wordnet.IndexerWordNetBasic;
import slib.sml.sm.core.engine.SM_Engine;
import slib.utils.ex.SLIB_Exception;

public class Coordinator {

	public static SM_Engine engineMeSH = null;
	public static SM_Engine engineSNOMED = null;
	public static SM_Engine engineWordNet = null;
	public static IndexerWordNetBasic indexWordnetNoun = null;
	public static G meshGraph = null;
	public static G snomedGraph = null;
	public static G wordnetGraph = null;

	public static void meshLoader() throws URISyntaxException, SLIB_Exception {
		URIFactory factory = URIFactoryMemory.getSingleton();
		URI meshURI = factory.getURI("http://www.nlm.nih.gov/mesh/");
		meshGraph = new GraphMemory(meshURI);
		String meshAddress = Thread.currentThread().getContextClassLoader().getResource("simbio/resources/desc2017.xml")
				.toURI().getPath();
		meshAddress = meshAddress.substring(1);
		GDataConf dataMeshXML = new GDataConf(GFormat.MESH_XML, meshAddress);
		GraphLoaderGeneric.populate(dataMeshXML, meshGraph);
		engineMeSH = new SM_Engine(meshGraph);
	}

	public static void snomedLoader() throws URISyntaxException, SLIB_Exception {
		URIFactory factory = URIFactoryMemory.getSingleton();
		String DATA_DIR = Thread.currentThread().getContextClassLoader().getResource("simbio/resources/SNOMED").toURI()
				.getPath();
		DATA_DIR = DATA_DIR.substring(1);
		String SNOMEDCT_VERSION = "20170131";
		String SNOMEDCT_CONCEPT = DATA_DIR + "sct2_Concept_Full_INT_" + SNOMEDCT_VERSION + ".txt";
		String SNOMEDCT_RELATIONSHIPS = DATA_DIR + "sct2_Relationship_Full_INT_" + SNOMEDCT_VERSION + ".txt";
		URI snomedctURI = factory.getURI("http://snomedct/");
		snomedGraph = new GraphMemory(snomedctURI);
		GDataConf conf = new GDataConf(GFormat.SNOMED_CT_RF2);
		conf.addParameter(GraphLoaderSnomedCT_RF2.ARG_CONCEPT_FILE, SNOMEDCT_CONCEPT);
		conf.addParameter(GraphLoaderSnomedCT_RF2.ARG_RELATIONSHIP_FILE, SNOMEDCT_RELATIONSHIPS);
		GraphLoaderGeneric.populate(conf, snomedGraph);
		engineSNOMED = new SM_Engine(snomedGraph);
	}

	public static void wordnetLoader() throws URISyntaxException, SLIB_Exception {
		URIFactory factory = URIFactoryMemory.getSingleton();
		String dataloc = Thread.currentThread().getContextClassLoader().getResource("simbio/resources/dict").toURI()
				.getPath();
		factory = URIFactoryMemory.getSingleton();
		URI guri = factory.getURI("http://graph/wordnet/");
		wordnetGraph = new GraphMemory(guri);
		GraphLoader_Wordnet loader = new GraphLoader_Wordnet();
		GDataConf dataNoun = new GDataConf(GFormat.WORDNET_DATA, dataloc + "data.noun");
		GDataConf dataVerb = new GDataConf(GFormat.WORDNET_DATA, dataloc + "data.verb");
		GDataConf dataAdj = new GDataConf(GFormat.WORDNET_DATA, dataloc + "data.adj");
		GDataConf dataAdv = new GDataConf(GFormat.WORDNET_DATA, dataloc + "data.adv");
		loader.populate(dataNoun, wordnetGraph);
		loader.populate(dataVerb, wordnetGraph);
		loader.populate(dataAdj, wordnetGraph);
		loader.populate(dataAdv, wordnetGraph);
		GAction addRoot = new GAction(GActionType.REROOTING);
		GraphActionExecutor.applyAction(addRoot, wordnetGraph);
		String data_noun = dataloc + "index.noun";
		indexWordnetNoun = new IndexerWordNetBasic(factory, wordnetGraph, data_noun);
		engineWordNet = new SM_Engine(wordnetGraph);
	}
}
