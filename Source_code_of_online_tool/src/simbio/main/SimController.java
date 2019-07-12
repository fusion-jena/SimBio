package simbio.main;

/**
 * Similarity Controller 
 * @author Samira Babalou (samira[dot]babalou[at]uni_jean[dot]de)
 */
import java.net.URISyntaxException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openrdf.model.URI;

import slib.graph.algo.utils.GAction;
import slib.graph.algo.utils.GActionType;
import slib.graph.algo.validator.dag.ValidatorDAG;
import slib.graph.io.conf.GDataConf;
import slib.graph.io.conf.GraphConf;
import slib.graph.io.loader.GraphLoaderGeneric;
import slib.graph.io.util.GFormat;
import slib.graph.model.graph.G;
import slib.graph.model.impl.graph.memory.GraphMemory;
import slib.graph.model.impl.repo.URIFactoryMemory;
import slib.graph.model.repo.URIFactory;
import slib.sml.sm.core.engine.SM_Engine;
import slib.sml.sm.core.metrics.ic.dspo.findShareP;
import slib.sml.sm.core.metrics.ic.utils.IC_Conf_Topo;
import slib.sml.sm.core.metrics.ic.utils.ICconf;
import slib.sml.sm.core.utils.SMConstants;
import slib.sml.sm.core.utils.SMconf;
import slib.utils.ex.SLIB_Exception;

public class SimController {

	public static String SimBioFunc(String Term1, String Term2, String OntFlag, String Term1OntPath,
			String Term2OntPath, String Simflag) throws URISyntaxException {
		String MergedOnt = null;
		double sim = 0.0;
		String res = null;
		try {
			ICconf icConf = new IC_Conf_Topo(SMConstants.FLAG_ICI_SANCHEZ_2011_b_adapted);
			SMconf measureConf = new SMconf(SMConstants.FLAG_SIM_PAIRWISE_DAG_NODE_LIN_1998, icConf);
			URIFactory factory = URIFactoryMemory.getSingleton();
			URI c1 = factory.getURI("http://www.nlm.nih.gov/thing"),
					c2 = factory.getURI("http://www.nlm.nih.gov/thing");

			icConf = new IC_Conf_Topo(SMConstants.FLAG_ICI_SANCHEZ_2011_b_adapted);

			switch (Simflag) {
			case "Lin":
				measureConf = new SMconf(SMConstants.FLAG_SIM_PAIRWISE_DAG_NODE_LIN_1998, icConf);
				break;
			case "Resnik":
				measureConf = new SMconf(SMConstants.FLAG_SIM_PAIRWISE_DAG_NODE_RESNIK_1995, icConf);
				break;
			case "J&C":
				measureConf = new SMconf(SMConstants.FLAG_DIST_PAIRWISE_DAG_NODE_JIANG_CONRATH_1997, icConf);
				break;
			}

			String[] SetTerm1 = Term1.split(",");
			String[] SetTerm2 = Term2.split(",");

			switch (OntFlag) {
			case "MeSH":
				res = "";
				MergedOnt = null;
				for (int i = 0; i < SetTerm1.length; i++) {
					sim = 0;
					c1 = factory.getURI("http://www.nlm.nih.gov/mesh/" + SetTerm1[i].trim());
					c2 = factory.getURI("http://www.nlm.nih.gov/mesh/" + SetTerm2[i].trim());
					if (Coordinator.engineMeSH == null) {
						Coordinator.meshLoader();
					}
					sim = Coordinator.engineMeSH.compare(measureConf, c1, c2, MergedOnt);
					res = String.valueOf(Math.round(sim * 10000.0) / 10000.0);
					c1 = null;
					c2 = null;
				}
				break;

			case "SNOMEDCT":
				res = "";
				MergedOnt = null;
				for (int i = 0; i < SetTerm1.length; i++) {
					sim = 0;
					if (Coordinator.engineSNOMED == null) {
						Coordinator.snomedLoader();
					}
					URI snomedctURI = factory.getURI("http://snomedct/");
					c1 = factory.getURI(snomedctURI.stringValue() + SetTerm1[i].trim());
					c2 = factory.getURI(snomedctURI.stringValue() + SetTerm2[i].trim());
					sim = Coordinator.engineSNOMED.compare(measureConf, c1, c2, MergedOnt);
					res = String.valueOf(Math.round(sim * 10000.0) / 10000.0);
					snomedctURI = null;
					c1 = null;
					c2 = null;
				}
				break;

			case "WordNet":
				res = "";
				MergedOnt = null;
				for (int i = 0; i < SetTerm1.length; i++) {
					sim = 0;
					if (Coordinator.engineWordNet == null) {
						Coordinator.wordnetLoader();
					}
					Set<URI> uris_Term1 = Coordinator.indexWordnetNoun.get(SetTerm1[i].trim());
					Set<URI> uris_Term2 = Coordinator.indexWordnetNoun.get(SetTerm2[i].trim());
					c1 = uris_Term1.iterator().next();
					c2 = uris_Term2.iterator().next();
					sim = Coordinator.engineWordNet.compare(measureConf, c1, c2, MergedOnt);
					res = String.valueOf(Math.round(sim * 10000.0) / 10000.0);
					c1 = null;
					c2 = null;
					factory = null;
				}
				break;

			case "MeSH-SNOMEDCT":
				MergedOnt = "MeshSnomedCT";
				res = "";
				SM_Engine.firstPriority = false;
				findShareP.CSVtoArray(MergedOnt);
				for (int i = 0; i < SetTerm1.length; i++) {
					sim = 0;
					if (Coordinator.engineMeSH == null)
						Coordinator.meshLoader();
					if (Coordinator.engineSNOMED == null)
						Coordinator.snomedLoader();
					// ***********call combine engine
					URIFactory factoryMesh = URIFactoryMemory.getSingleton();
					URIFactory factorySnomed = URIFactoryMemory.getSingleton();
					URI snomedctURI = factorySnomed.getURI("http://snomedct/");
					c1 = factoryMesh.getURI("http://www.nlm.nih.gov/mesh/" + SetTerm1[i].trim());
					c2 = factorySnomed.getURI(snomedctURI.stringValue() + SetTerm2[i].trim());
					SM_Engine engine = new SM_Engine(c1, c2, Coordinator.meshGraph, Coordinator.snomedGraph, MergedOnt); // MergingGraph
					sim = engine.compare(measureConf, c1, c2, MergedOnt);
					res = String.valueOf(Math.round(sim * 10000.0) / 10000.0);
					engine = null;
					c1 = null;
					c2 = null;
					factorySnomed = null;
					factoryMesh = null;
					snomedctURI = null;
				}
				break;

			case "MeSH-WordNet":
				res = "";
				MergedOnt = "MeshWordNet";
				SM_Engine.firstPriority = false;
				findShareP.CSVtoArray(MergedOnt);
				for (int i = 0; i < SetTerm1.length; i++) {
					sim = 0;
					if (Coordinator.engineMeSH == null)
						Coordinator.meshLoader();
					if (Coordinator.engineWordNet == null)
						Coordinator.wordnetLoader();
					// ***********call combine engine
					URIFactory factoryMesh = URIFactoryMemory.getSingleton();
					c1 = factoryMesh.getURI("http://www.nlm.nih.gov/mesh/" + SetTerm1[i]);
					Set<URI> uris_Term2 = Coordinator.indexWordnetNoun.get(SetTerm2[i]);
					c2 = uris_Term2.iterator().next();
					SM_Engine engine = new SM_Engine(c1, c2, Coordinator.meshGraph, Coordinator.wordnetGraph,
							MergedOnt);
					sim = engine.compare(measureConf, c1, c2, MergedOnt);
					res = String.valueOf(Math.round(sim * 10000.0) / 10000.0);
					factory = null;
					engine = null;
					c1 = null;
					c2 = null;
					factoryMesh = null;
				}
				break;

			case "SNOMEDCT-MeSH":
				MergedOnt = "MeshSnomedCT";
				SM_Engine.firstPriority = true;
				findShareP.CSVtoArray(MergedOnt);
				res = "";
				findShareP.CSVtoArray(MergedOnt);
				for (int i = 0; i < SetTerm1.length; i++) {
					sim = 0;
					if (Coordinator.engineSNOMED == null)
						Coordinator.snomedLoader();
					if (Coordinator.engineMeSH == null)
						Coordinator.meshLoader();
					// ***********call combine engine
					URIFactory factorySnomed = URIFactoryMemory.getSingleton();
					URIFactory factoryMesh = URIFactoryMemory.getSingleton();
					URI snomedctURI = factorySnomed.getURI("http://snomedct/");
					c1 = factorySnomed.getURI(snomedctURI.stringValue() + SetTerm1[i].trim());
					c2 = factoryMesh.getURI("http://www.nlm.nih.gov/mesh/" + SetTerm2[i].trim());
					SM_Engine engine = new SM_Engine(c1, c2, Coordinator.meshGraph, Coordinator.snomedGraph, MergedOnt);
					sim = engine.compare(measureConf, c1, c2, MergedOnt);
					res = String.valueOf(Math.round(sim * 10000.0) / 10000.0);
					snomedctURI = null;
					engine = null;
					c1 = null;
					c2 = null;
					factoryMesh = null;
				}
				break;

			case "SNOMEDCT-WordNet":
				res = "";
				MergedOnt = "SnomedCTWordNet";
				SM_Engine.firstPriority = false;
				findShareP.CSVtoArray(MergedOnt);
				for (int i = 0; i < SetTerm1.length; i++) {
					sim = 0;
					if (Coordinator.engineSNOMED == null)
						Coordinator.snomedLoader();
					if (Coordinator.engineWordNet == null)
						Coordinator.wordnetLoader();
					// ***********call combine engine
					URIFactory factorySnomed = URIFactoryMemory.getSingleton();
					URI snomedctURI = factorySnomed.getURI("http://snomedct/");
					c1 = factorySnomed.getURI(snomedctURI.stringValue() + SetTerm1[i]);
					Set<URI> uris_Term2 = Coordinator.indexWordnetNoun.get(SetTerm2[i]);
					c2 = uris_Term2.iterator().next();
					SM_Engine engine = new SM_Engine(c1, c2, Coordinator.snomedGraph, Coordinator.wordnetGraph,
							MergedOnt);
					sim = engine.compare(measureConf, c1, c2, MergedOnt);
					res = String.valueOf(Math.round(sim * 10000.0) / 10000.0);
					snomedctURI = null;
					engine = null;
					c1 = null;
					c2 = null;
					factory = null;
				}
				break;

			case "WordNet-MeSH":
				res = "";
				MergedOnt = "MeshWordNet";
				SM_Engine.firstPriority = false;
				findShareP.CSVtoArray(MergedOnt);
				for (int i = 0; i < SetTerm1.length; i++) {
					sim = 0;
					if (Coordinator.engineWordNet == null)
						Coordinator.wordnetLoader();
					if (Coordinator.engineMeSH == null)
						Coordinator.meshLoader();
					// ***********call combine engine
					URIFactory factoryMesh = URIFactoryMemory.getSingleton();
					Set<URI> uris_Term1 = Coordinator.indexWordnetNoun.get(SetTerm1[i]);
					c1 = uris_Term1.iterator().next();
					c2 = factoryMesh.getURI("http://www.nlm.nih.gov/mesh/" + SetTerm2[i]);
					SM_Engine engine = new SM_Engine(c1, c2, Coordinator.meshGraph, Coordinator.wordnetGraph,
							MergedOnt);
					sim = engine.compare(measureConf, c1, c2, MergedOnt);
					res = String.valueOf(Math.round(sim * 10000.0) / 10000.0);
					factory = null;
					engine = null;
					c1 = null;
					c2 = null;
					factoryMesh = null;
				}
				break;

			case "WordNet-SNOMEDCT":
				res = "";
				MergedOnt = "SnomedCTWordNet";
				SM_Engine.firstPriority = false;
				findShareP.CSVtoArray(MergedOnt);
				for (int i = 0; i < SetTerm1.length; i++) {
					sim = 0;
					if (Coordinator.engineWordNet == null)
						Coordinator.wordnetLoader();
					if (Coordinator.engineSNOMED == null)
						Coordinator.snomedLoader();
					// ***********call combine engine
					URIFactory factorySnomed = URIFactoryMemory.getSingleton();
					URI snomedctURI = factorySnomed.getURI("http://snomedct/");
					Set<URI> uris_Term1 = Coordinator.indexWordnetNoun.get(SetTerm1[i]);
					c1 = uris_Term1.iterator().next();
					c2 = factorySnomed.getURI(snomedctURI.stringValue() + SetTerm2[i]);
					SM_Engine engine = new SM_Engine(c1, c2, Coordinator.snomedGraph, Coordinator.wordnetGraph,
							MergedOnt);
					sim = engine.compare(measureConf, c1, c2, MergedOnt);
					res = String.valueOf(Math.round(sim * 10000.0) / 10000.0);
					factory = null;
					snomedctURI = null;
					engine = null;
					c1 = null;
					c2 = null;
					factorySnomed = null;
				}
				break;

			case "Other":
				res = "";
				MergedOnt = null;
				String ontname = "";
				int a = Term1OntPath.indexOf("uploads");
				ontname = Term1OntPath.substring(a + 8, Term1OntPath.length() - 4);
				String uriOnt = "http://" + ontname.toLowerCase();
				for (int i = 0; i < SetTerm1.length; i++) {
					sim = 0;
					factory = URIFactoryMemory.getSingleton();
					URI graphURI = factory.getURI(uriOnt + "/");
					factory.loadNamespacePrefix("ONT", graphURI.toString());
					G ontGraph = new GraphMemory(graphURI);
					GDataConf dataConf = new GDataConf(GFormat.RDF_XML, Term1OntPath);
					GAction actionRerootConf = new GAction(GActionType.REROOTING);
					GraphConf gConf = new GraphConf();
					gConf.addGDataConf(dataConf);
					gConf.addGAction(actionRerootConf);
					GraphLoaderGeneric.load(gConf, ontGraph);
					Set<URI> roots = new ValidatorDAG().getTaxonomicRoots(ontGraph);
					// OntPath
					c1 = factory.getURI(uriOnt + "#" + SetTerm1[i].trim());
					c2 = factory.getURI(uriOnt + "#" + SetTerm2[i].trim());
					SM_Engine engine = new SM_Engine(ontGraph);
					sim = engine.compare(measureConf, c1, c2, MergedOnt);
					res = String.valueOf(Math.round(sim * 10000.0) / 10000.0);
					engine = null;
					factory = null;
					c1 = null;
					c2 = null;
					factory = null;
					ontGraph = null;
				}
				break;

			case "Other1-Other2":
				res = "";
				MergedOnt = null;
				for (int i = 0; i < SetTerm1.length; i++) {
					sim = 0;
					// *********** Other1**********************************
					String ontname1 = "";
					int a1 = Term1OntPath.indexOf("uploads");
					ontname1 = Term1OntPath.substring(a1 + 8, Term1OntPath.length() - 4);
					String uriOnt1 = "http://" + ontname1.toLowerCase();
					URIFactory factoryont1 = URIFactoryMemory.getSingleton();
					URI graphURI1 = factoryont1.getURI(uriOnt1 + "/");
					factoryont1.loadNamespacePrefix("ONTA", graphURI1.toString());
					G ont1Graph = new GraphMemory(graphURI1);
					GDataConf dataConf1 = new GDataConf(GFormat.RDF_XML, Term1OntPath);
					GAction actionRerootConf1 = new GAction(GActionType.REROOTING);
					GraphConf gConf1 = new GraphConf();
					gConf1.addGDataConf(dataConf1);
					gConf1.addGAction(actionRerootConf1);
					GraphLoaderGeneric.load(gConf1, ont1Graph);
					Set<URI> roots1 = new ValidatorDAG().getTaxonomicRoots(ont1Graph);
					// **********Other2****************************************
					String ontname2 = "";
					int a2 = Term2OntPath.indexOf("uploads");
					ontname2 = Term2OntPath.substring(a2 + 8, Term2OntPath.length() - 4);
					String uriOnt2 = "http://" + ontname2.toLowerCase();
					URIFactory factoryont2 = URIFactoryMemory.getSingleton();
					URI graphURI2 = factoryont2.getURI(uriOnt2 + "/");
					factoryont2.loadNamespacePrefix("ONTB", graphURI2.toString());
					G ont2Graph = new GraphMemory(graphURI2);
					GDataConf dataConf2 = new GDataConf(GFormat.RDF_XML, Term2OntPath);
					GAction actionRerootConf2 = new GAction(GActionType.REROOTING);
					GraphConf gConf2 = new GraphConf();
					gConf2.addGDataConf(dataConf2);
					gConf2.addGAction(actionRerootConf2);
					GraphLoaderGeneric.load(gConf2, ont2Graph);
					Set<URI> roots2 = new ValidatorDAG().getTaxonomicRoots(ont2Graph);
					// **********combine****************************************
					c1 = factoryont1.getURI(uriOnt1 + "#" + SetTerm1[i].trim());
					c2 = factoryont2.getURI(uriOnt2 + "#" + SetTerm2[i].trim());
					SM_Engine engine = new SM_Engine(c1, c2, ont1Graph, ont2Graph, MergedOnt);
					sim = engine.compare(measureConf, c1, c2, MergedOnt);
					res = String.valueOf(Math.round(sim * 10000.0) / 10000.0);
					ont1Graph = null;
					factoryont2 = null;
					ont2Graph = null;
					graphURI1 = null;
					graphURI2 = null;
					engine = null;
					c1 = null;
					c2 = null;
					factoryont1 = null;
				}
				break;

			case "MeSH-Other":
				res = "";
				MergedOnt = null;
				for (int i = 0; i < SetTerm1.length; i++) {
					sim = 0;
					if (Coordinator.engineMeSH == null)
						Coordinator.meshLoader();
					// **********Other2****************************************
					String ontname2 = "";
					int a2 = Term2OntPath.indexOf("uploads");
					ontname2 = Term2OntPath.substring(a2 + 8, Term2OntPath.length() - 4);
					String uriOnt2 = "http://" + ontname2.toLowerCase();
					factory = URIFactoryMemory.getSingleton();
					URI graphURI2 = factory.getURI(uriOnt2 + "/");
					factory.loadNamespacePrefix("ONT", graphURI2.toString());
					G ont2Graph = new GraphMemory(graphURI2);
					GDataConf dataConf2 = new GDataConf(GFormat.RDF_XML, Term2OntPath);
					GAction actionRerootConf2 = new GAction(GActionType.REROOTING);
					GraphConf gConf2 = new GraphConf();
					gConf2.addGDataConf(dataConf2);
					gConf2.addGAction(actionRerootConf2);
					GraphLoaderGeneric.load(gConf2, ont2Graph);
					Set<URI> roots2 = new ValidatorDAG().getTaxonomicRoots(ont2Graph);
					// ***********call combine
					// engine*********************************
					URIFactory factoryMesh = URIFactoryMemory.getSingleton();
					c1 = factoryMesh.getURI("http://www.nlm.nih.gov/mesh/" + SetTerm1[i].trim());
					c2 = factory.getURI(uriOnt2 + "#" + SetTerm2[i].trim());
					SM_Engine engine = new SM_Engine(c1, c2, Coordinator.meshGraph, ont2Graph, MergedOnt);
					sim = engine.compare(measureConf, c1, c2, MergedOnt);
					res = String.valueOf(Math.round(sim * 10000.0) / 10000.0);
					graphURI2 = null;
					factory = null;
					ont2Graph = null;
					engine = null;
					c1 = null;
					c2 = null;
					factoryMesh = null;
				}
				break;

			case "SNOMEDCT-Other":
				res = "";
				MergedOnt = null;
				for (int i = 0; i < SetTerm1.length; i++) {
					sim = 0;
					if (Coordinator.engineSNOMED == null)
						Coordinator.snomedLoader();
					// **********Other2*********************************
					String ontname2 = "";
					int a2 = Term2OntPath.indexOf("uploads");
					ontname2 = Term2OntPath.substring(a2 + 8, Term2OntPath.length() - 4);
					String uriOnt2 = "http://" + ontname2.toLowerCase();
					factory = URIFactoryMemory.getSingleton();
					URI graphURI2 = factory.getURI(uriOnt2 + "/");
					factory.loadNamespacePrefix("ONT", graphURI2.toString());
					G ont2Graph = new GraphMemory(graphURI2);
					GDataConf dataConf2 = new GDataConf(GFormat.RDF_XML, Term2OntPath);
					GAction actionRerootConf2 = new GAction(GActionType.REROOTING);
					GraphConf gConf2 = new GraphConf();
					gConf2.addGDataConf(dataConf2);
					gConf2.addGAction(actionRerootConf2);
					GraphLoaderGeneric.load(gConf2, ont2Graph);
					Set<URI> roots2 = new ValidatorDAG().getTaxonomicRoots(ont2Graph);
					// ***********call combine
					// engine*********************************
					URIFactory factorySnomed = URIFactoryMemory.getSingleton();
					URI snomedctURI = factorySnomed.getURI("http://snomedct/");
					c1 = factory.getURI(snomedctURI.stringValue() + SetTerm1[i].trim());
					c2 = factory.getURI(uriOnt2 + "#" + SetTerm2[i].trim());
					SM_Engine engine = new SM_Engine(c1, c2, Coordinator.snomedGraph, ont2Graph, MergedOnt);
					sim = engine.compare(measureConf, c1, c2, MergedOnt);
					res = String.valueOf(Math.round(sim * 10000.0) / 10000.0);
					factory = null;
					graphURI2 = null;
					ont2Graph = null;
					snomedctURI = null;
					engine = null;
					c1 = null;
					c2 = null;
					factorySnomed = null;
				}
				break;

			case "WordNet-Other":
				res = "";
				MergedOnt = null;
				for (int i = 0; i < SetTerm1.length; i++) {
					sim = 0;
					if (Coordinator.engineWordNet == null)
						Coordinator.wordnetLoader();
					// **********Other2*********************************
					String ontname2 = "";
					int a2 = Term2OntPath.indexOf("uploads");
					ontname2 = Term2OntPath.substring(a2 + 8, Term2OntPath.length() - 4);
					String uriOnt2 = "http://" + ontname2.toLowerCase();
					factory = URIFactoryMemory.getSingleton();
					URI graphURI2 = factory.getURI(uriOnt2 + "/");
					factory.loadNamespacePrefix("ONT", graphURI2.toString());
					G ont2Graph = new GraphMemory(graphURI2);
					GDataConf dataConf2 = new GDataConf(GFormat.RDF_XML, Term2OntPath);
					GAction actionRerootConf2 = new GAction(GActionType.REROOTING);
					GraphConf gConf2 = new GraphConf();
					gConf2.addGDataConf(dataConf2);
					gConf2.addGAction(actionRerootConf2);
					GraphLoaderGeneric.load(gConf2, ont2Graph);
					Set<URI> roots2 = new ValidatorDAG().getTaxonomicRoots(ont2Graph);
					// ********Combine*********************************
					Set<URI> uris_Term1 = Coordinator.indexWordnetNoun.get(SetTerm2[i].trim());
					c1 = uris_Term1.iterator().next();
					c2 = factory.getURI(uriOnt2 + "#" + SetTerm2[i].trim());
					SM_Engine engine = new SM_Engine(c1, c2, Coordinator.wordnetGraph, ont2Graph, MergedOnt);
					sim = engine.compare(measureConf, c1, c2, MergedOnt);
					res = String.valueOf(Math.round(sim * 10000.0) / 10000.0);
					graphURI2 = null;
					ont2Graph = null;
					engine = null;
					c1 = null;
					c2 = null;
					factory = null;
				}
				break;

			case "Other-MeSH":
				res = "";
				MergedOnt = null;
				for (int i = 0; i < SetTerm1.length; i++) {
					sim = 0;
					if (Coordinator.engineMeSH == null)
						Coordinator.meshLoader();
					// *********** Other1*********************************
					String ontname1 = "";
					int a1 = Term1OntPath.indexOf("uploads");
					ontname1 = Term1OntPath.substring(a1 + 8, Term1OntPath.length() - 4);
					String uriOnt1 = "http://" + ontname1.toLowerCase();
					factory = URIFactoryMemory.getSingleton();
					URI graphURI1 = factory.getURI(uriOnt1 + "/");
					factory.loadNamespacePrefix("ONT", graphURI1.toString());
					G ont1Graph = new GraphMemory(graphURI1);
					GDataConf dataConf1 = new GDataConf(GFormat.RDF_XML, Term1OntPath);
					GAction actionRerootConf1 = new GAction(GActionType.REROOTING);
					GraphConf gConf1 = new GraphConf();
					gConf1.addGDataConf(dataConf1);
					gConf1.addGAction(actionRerootConf1);
					GraphLoaderGeneric.load(gConf1, ont1Graph);
					Set<URI> roots1 = new ValidatorDAG().getTaxonomicRoots(ont1Graph);
					// ************MeSH***************************************
					URIFactory factoryMesh = URIFactoryMemory.getSingleton();
					URI meshURI = factoryMesh.getURI("http://www.nlm.nih.gov/mesh/");
					// ***********call combine engine*********************
					c1 = factory.getURI(uriOnt1 + "#" + SetTerm1[i].trim());
					c2 = factoryMesh.getURI("http://www.nlm.nih.gov/mesh/" + SetTerm2[i].trim());
					SM_Engine engine = new SM_Engine(c1, c2, ont1Graph, Coordinator.meshGraph, MergedOnt);
					sim = engine.compare(measureConf, c1, c2, MergedOnt);
					res = String.valueOf(Math.round(sim * 10000.0) / 10000.0);
					factory = null;
					graphURI1 = null;
					ont1Graph = null;
					meshURI = null;
					engine = null;
					c1 = null;
					c2 = null;
					factoryMesh = null;
				}
				break;

			case "Other-SNOMEDCT":
				res = "";
				MergedOnt = null;
				for (int i = 0; i < SetTerm1.length; i++) {
					sim = 0;
					if (Coordinator.engineSNOMED == null)
						Coordinator.snomedLoader();
					// *********** Other1*********************************
					String ontname1 = "";
					int a1 = Term1OntPath.indexOf("uploads");
					ontname1 = Term1OntPath.substring(a1 + 8, Term1OntPath.length() - 4);
					String uriOnt1 = "http://" + ontname1.toLowerCase();
					factory = URIFactoryMemory.getSingleton();
					URI graphURI1 = factory.getURI(uriOnt1 + "/");
					factory.loadNamespacePrefix("ONT", graphURI1.toString());
					G ont1Graph = new GraphMemory(graphURI1);
					GDataConf dataConf1 = new GDataConf(GFormat.RDF_XML, Term1OntPath);
					GAction actionRerootConf1 = new GAction(GActionType.REROOTING);
					GraphConf gConf1 = new GraphConf();
					gConf1.addGDataConf(dataConf1);
					gConf1.addGAction(actionRerootConf1);
					GraphLoaderGeneric.load(gConf1, ont1Graph);
					Set<URI> roots1 = new ValidatorDAG().getTaxonomicRoots(ont1Graph);
					// ***********SNOMED******************************************
					URIFactory factorySnomed = URIFactoryMemory.getSingleton();
					URI snomedctURI = factorySnomed.getURI("http://snomedct/");
					// ***********call combine engine*********************
					c1 = factory.getURI(uriOnt1 + "#" + SetTerm1[i].trim());
					c2 = factory.getURI(snomedctURI.stringValue() + SetTerm2[i].trim());
					SM_Engine engine = new SM_Engine(c1, c2, ont1Graph, Coordinator.snomedGraph, MergedOnt);
					sim = engine.compare(measureConf, c1, c2, MergedOnt);
					res = String.valueOf(Math.round(sim * 10000.0) / 10000.0);
					factory = null;
					ont1Graph = null;
					graphURI1 = null;
					snomedctURI = null;
					engine = null;
					c1 = null;
					c2 = null;
					factorySnomed = null;
				}
				break;

			case "Other-WordNet":
				res = "";
				MergedOnt = null;
				for (int i = 0; i < SetTerm1.length; i++) {
					sim = 0;
					if (Coordinator.engineWordNet == null)
						Coordinator.wordnetLoader();
					// *********** Other1*********************************
					String ontname1 = "";
					int a1 = Term1OntPath.indexOf("uploads");
					ontname1 = Term1OntPath.substring(a1 + 8, Term1OntPath.length() - 4);
					String uriOnt1 = "http://" + ontname1.toLowerCase();
					factory = URIFactoryMemory.getSingleton();
					URI graphURI1 = factory.getURI(uriOnt1 + "/");
					factory.loadNamespacePrefix("ONT", graphURI1.toString());
					G ont1Graph = new GraphMemory(graphURI1);
					GDataConf dataConf1 = new GDataConf(GFormat.RDF_XML, Term1OntPath);
					GAction actionRerootConf1 = new GAction(GActionType.REROOTING);
					GraphConf gConf1 = new GraphConf();
					gConf1.addGDataConf(dataConf1);
					gConf1.addGAction(actionRerootConf1);
					GraphLoaderGeneric.load(gConf1, ont1Graph);
					Set<URI> roots1 = new ValidatorDAG().getTaxonomicRoots(ont1Graph);
					// ****************Combine*******************************
					c1 = factory.getURI(uriOnt1 + "#" + SetTerm1[i].trim());
					Set<URI> uris_Term2 = Coordinator.indexWordnetNoun.get(SetTerm2[i].trim());
					c2 = uris_Term2.iterator().next();
					SM_Engine engine = new SM_Engine(c1, c2, ont1Graph, Coordinator.wordnetGraph, MergedOnt);
					sim = engine.compare(measureConf, c1, c2, MergedOnt);
					res = String.valueOf(Math.round(sim * 10000.0) / 10000.0);
					factory = null;
					graphURI1 = null;
					ont1Graph = null;
					engine = null;
					c1 = null;
					c2 = null;
				}
				break;
			}

			SetTerm1 = null;
			SetTerm2 = null;
			icConf = null;
			measureConf = null;
			factory = null;
			icConf = null;
		} catch (SLIB_Exception ex) {
			Logger.getLogger(SimController.class.getName()).log(Level.SEVERE, null, ex);
		}

		return res;
	}

	public static String DetermineCase(String term1Source, String term2Source, String Term1OntPath,
			String Term2OntPath) {
		String state = "MeSH"; // default

		if (term1Source.equals("MeSH") && term2Source.equals("MeSH"))
			state = "MeSH";

		else if (term1Source.equals("SNOMEDCT") && term2Source.equals("SNOMEDCT"))
			state = "SNOMEDCT";

		else if (term1Source.equals("WordNet") && term2Source.equals("WordNet"))
			state = "WordNet";

		else if (term1Source.equals("MeSH") && term2Source.equals("SNOMEDCT"))
			state = "MeSH-SNOMEDCT";

		else if (term1Source.equals("MeSH") && term2Source.equals("WordNet"))
			state = "MeSH-WordNet";

		else if (term1Source.equals("SNOMEDCT") && term2Source.equals("MeSH"))
			state = "SNOMEDCT-MeSH";

		else if (term1Source.equals("SNOMEDCT") && term2Source.equals("WordNet"))
			state = "SNOMEDCT-WordNet";

		else if (term1Source.equals("WordNet") && term2Source.equals("MeSH"))
			state = "WordNet-MeSH";

		else if (term1Source.equals("WordNet") && term2Source.equals("SNOMEDCT"))
			state = "WordNet-SNOMEDCT";

		else if (term1Source.equals("YourFile") && term2Source.equals("YourFile")) {
			if (Term1OntPath.equals(Term2OntPath))
				state = "Other";
			else
				state = "Other1-Other2";
		}

		else if (term1Source.equals("MeSH") && term2Source.equals("YourFile"))
			state = "MeSH-Other";

		else if (term1Source.equals("SNOMEDCT") && term2Source.equals("YourFile"))
			state = "SNOMEDCT-Other";

		else if (term1Source.equals("WordNet") && term2Source.equals("YourFile"))
			state = "WordNet-Other";

		else if (term1Source.equals("YourFile") && term2Source.equals("MeSH"))
			state = "Other-MeSH";

		else if (term1Source.equals("YourFile") && term2Source.equals("SNOMEDCT"))
			state = "Other-SNOMEDCT";

		else if (term1Source.equals("YourFile") && term2Source.equals("WordNet"))
			state = "Other-WordNet";

		return state;
	}

}
