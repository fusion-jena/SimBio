package simbio.main;
/**
 * Comparision Controller 
 * @author Samira Babalou (samira[dot]babalou[at]uni_jean[dot]de)
 */

import java.net.URISyntaxException;
import java.util.ArrayList;
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
import slib.utils.ex.SLIB_Ex_Critic;
import slib.utils.ex.SLIB_Exception;

public class CompController {
	private static boolean PREVENT_INCOHERENCES = true;
	final public static boolean IS_SYMMETRIC = true;

	public static String[][] CompBioFunc(String[][] Terms, String OntPath, String MeshCheck, String SnomedCheck,
			String WordnetCheck, String yourfileCheck, String LinCheck, String ResnikCheck, String JCCheck)
			throws URISyntaxException {
		String MergedOnt = null;
		double sim = 0.0;
		String uriOnt = "";
		String[][] res = new String[16][3];
		try {

			ICconf icConf = new IC_Conf_Topo(SMConstants.FLAG_ICI_SANCHEZ_2011_b_adapted);
			SMconf confAll = new SMconf(SMConstants.FLAG_SIM_PAIRWISE_DAG_NODE_LIN_1998, icConf);
			URIFactory factory = URIFactoryMemory.getSingleton();
			URI Term1Mesh = factory.getURI("http://www.nlm.nih.gov/thing"),
					Term2Mesh = factory.getURI("http://www.nlm.nih.gov/thing"),
					Term1Snomed = factory.getURI("http://www.nlm.nih.gov/thing"),
					Term2Snomed = factory.getURI("http://www.nlm.nih.gov/thing"),
					Term1WordNet = factory.getURI("http://www.nlm.nih.gov/thing"),
					Term2WordNet = factory.getURI("http://www.nlm.nih.gov/thing"),
					Term1Other = factory.getURI("http://www.nlm.nih.gov/thing"),
					Term2Other = factory.getURI("http://www.nlm.nih.gov/thing");

			G ontGraph = null;

			// MeSH configuration
			if (MeshCheck.equals("on") && (Terms[0][0] != null || Terms[0][1] != null)) {
				if (Coordinator.engineMeSH == null)
					Coordinator.meshLoader();

				Term1Mesh = factory.getURI("http://www.nlm.nih.gov/mesh/" + Terms[0][0].trim());
				Term2Mesh = factory.getURI("http://www.nlm.nih.gov/mesh/" + Terms[0][1].trim());
			}
			// SNOMEDCT configuration
			if (SnomedCheck.equals("on") && (Terms[1][0] != null || Terms[1][1] != null)) {
				if (Coordinator.engineSNOMED == null)
					Coordinator.snomedLoader();
				URI snomedctURI = factory.getURI("http://snomedct/");

				if (Terms[1][0] != null)
					Term1Snomed = factory.getURI(snomedctURI.stringValue() + Terms[1][0].trim());
				if (Terms[1][1] != null)
					Term2Snomed = factory.getURI(snomedctURI.stringValue() + Terms[1][1].trim());

			}
			// WordNEt Configuration
			if (WordnetCheck.equals("on") && (Terms[2][0] != null || Terms[2][1] != null)) {
				if (Coordinator.engineWordNet == null)
					Coordinator.wordnetLoader();

				if (Terms[2][0] != null) {
					Set<URI> uris_Term1 = Coordinator.indexWordnetNoun.get(Terms[2][0].trim());
					Term1WordNet = uris_Term1.iterator().next();
				}
				if (Terms[2][1] != null) {
					Set<URI> uris_Term2 = Coordinator.indexWordnetNoun.get(Terms[2][1].trim());
					Term2WordNet = uris_Term2.iterator().next();
				}
			}
			// Other ontology Configuration
			if (yourfileCheck.equals("on") && OntPath != "" && (Terms[3][0] != null || Terms[3][1] != null)) {
				String ontname = "";
				int a = OntPath.indexOf("uploads");
				ontname = OntPath.substring(a + 8, OntPath.length() - 4);
				uriOnt = "http://" + ontname.toLowerCase();
				System.out.println("uriOnt URI    : " + uriOnt);
				sim = 0;
				factory = URIFactoryMemory.getSingleton();
				URI graphURI = factory.getURI(uriOnt + "/");
				factory.loadNamespacePrefix("ONT", graphURI.toString());
				ontGraph = new GraphMemory(graphURI);
				GDataConf dataConf = new GDataConf(GFormat.RDF_XML, OntPath);
				GAction actionRerootConf = new GAction(GActionType.REROOTING);
				GraphConf gConf = new GraphConf();
				gConf.addGDataConf(dataConf);
				gConf.addGAction(actionRerootConf);
				GraphLoaderGeneric.load(gConf, ontGraph);
				Set<URI> roots2 = new ValidatorDAG().getTaxonomicRoots(ontGraph);
				if (Terms[3][0] != null)
					Term1Other = factory.getURI(uriOnt + "#" + Terms[3][0].trim());
				if (Terms[3][1] != null)
					Term2Other = factory.getURI(uriOnt + "#" + Terms[3][1].trim());
			}

			// --------- State 0: Run MeSH
			if (MeshCheck.equals("on") && Terms[0][0] != null && Terms[0][1] != null) {
				MergedOnt = null;
				double ic_a = Coordinator.engineMeSH.getIC(confAll.getICconf(), Term1Mesh);
				double ic_b = Coordinator.engineMeSH.getIC(confAll.getICconf(), Term2Mesh);
				ArrayList<Double> ic_parent = Coordinator.engineMeSH.get_PSO_IC(confAll.getICconf(), Term1Mesh,
						Term2Mesh, Coordinator.engineMeSH, MergedOnt);
				if (LinCheck.equals("on")) {
					double simLin = CompareLin(ic_a, ic_b, ic_parent);
					res[0][0] = String.valueOf(Math.round(simLin * 10000.0) / 10000.0);
				} else {
					res[0][0] = "-";
				}
				if (ResnikCheck.equals("on")) {
					double simResnik = CompareResnik(ic_a, ic_b, ic_parent);
					res[0][1] = String.valueOf(Math.round(simResnik * 10000.0) / 10000.0);
				} else {
					res[0][1] = "-";
				}
				if (JCCheck.equals("on")) {
					double simJC = CompareJC(ic_a, ic_b, ic_parent);
					res[0][2] = String.valueOf(Math.round(simJC * 10000.0) / 10000.0);
				} else {
					res[0][2] = "-";
				}
				ic_parent = null;
			} else if (MeshCheck == "") {
				res[0][0] = "-";
				res[0][1] = "-";
				res[0][2] = "-";
			} else {
				res[0][0] = "Your terms do not find";
				res[0][1] = "Your terms do not find";
				res[0][2] = "Your terms do not find";
			}
			// --------- State 1: Run SNOMEDCT
			if (SnomedCheck.equals("on") && Terms[1][0] != null && Terms[1][1] != null) {
				MergedOnt = null;
				double ic_a = Coordinator.engineSNOMED.getIC(confAll.getICconf(), Term1Snomed);
				double ic_b = Coordinator.engineSNOMED.getIC(confAll.getICconf(), Term2Snomed);
				ArrayList<Double> ic_parent = Coordinator.engineSNOMED.get_PSO_IC(confAll.getICconf(), Term1Snomed,
						Term2Snomed, Coordinator.engineSNOMED, MergedOnt);
				if (LinCheck.equals("on")) {
					double simLin = CompareLin(ic_a, ic_b, ic_parent);
					res[1][0] = String.valueOf(Math.round(simLin * 10000.0) / 10000.0);
				} else {
					res[1][0] = "-";
				}
				if (ResnikCheck.equals("on")) {
					double simResnik = CompareResnik(ic_a, ic_b, ic_parent);
					res[1][1] = String.valueOf(Math.round(simResnik * 10000.0) / 10000.0);
				} else {
					res[1][1] = "-";
				}
				if (JCCheck.equals("on")) {
					double simJC = CompareJC(ic_a, ic_b, ic_parent);
					res[1][2] = String.valueOf(Math.round(simJC * 10000.0) / 10000.0);
				} else {
					res[1][2] = "-";
				}
				ic_parent = null;
			} else if (SnomedCheck == "") {
				res[1][0] = "-";
				res[1][1] = "-";
				res[1][2] = "-";
			} else {
				res[1][0] = "Your terms do not find";
				res[1][1] = "Your terms do not find";
				res[1][2] = "Your terms do not find";
			}

			// --------- State 2: Run WordNet
			if (WordnetCheck.equals("on") && Terms[2][0] != null && Terms[2][1] != null) {
				MergedOnt = null;
				double ic_a = Coordinator.engineWordNet.getIC(confAll.getICconf(), Term1WordNet);
				double ic_b = Coordinator.engineWordNet.getIC(confAll.getICconf(), Term2WordNet);
				ArrayList<Double> ic_parent = Coordinator.engineWordNet.get_PSO_IC(confAll.getICconf(), Term1WordNet,
						Term2WordNet, Coordinator.engineWordNet, MergedOnt);
				if (LinCheck.equals("on")) {
					double simLin = CompareLin(ic_a, ic_b, ic_parent);
					res[2][0] = String.valueOf(Math.round(simLin * 10000.0) / 10000.0);
				} else {
					res[2][0] = "-";
				}
				if (ResnikCheck.equals("on")) {
					double simResnik = CompareResnik(ic_a, ic_b, ic_parent);
					res[2][1] = String.valueOf(Math.round(simResnik * 10000.0) / 10000.0);
				} else {
					res[2][1] = "-";
				}
				if (JCCheck.equals("on")) {
					double simJC = CompareJC(ic_a, ic_b, ic_parent);
					res[2][2] = String.valueOf(Math.round(simJC * 10000.0) / 10000.0);
				} else {
					res[2][2] = "-";
				}
				ic_parent = null;
			} else if (WordnetCheck == "") {
				res[2][0] = "-";
				res[2][1] = "-";
				res[2][2] = "-";
			} else {
				res[2][0] = "Your terms do not find";
				res[2][1] = "Your terms do not find";
				res[2][2] = "Your terms do not find";
			}

			// --------- State 3: Run User Ont
			if (yourfileCheck.equals("on") && OntPath != "" && Terms[3][0] != null && Terms[3][1] != null) {
				MergedOnt = null;
				SM_Engine engineOnt = new SM_Engine(ontGraph);
				double ic_a = engineOnt.getIC(confAll.getICconf(), Term1Other);
				double ic_b = engineOnt.getIC(confAll.getICconf(), Term2Other);
				ArrayList<Double> ic_parent = engineOnt.get_PSO_IC(confAll.getICconf(), Term1Other, Term2Other,
						engineOnt, MergedOnt);
				if (LinCheck.equals("on")) {
					double simLin = CompareLin(ic_a, ic_b, ic_parent);
					res[3][0] = String.valueOf(Math.round(simLin * 10000.0) / 10000.0);
				} else {
					res[3][0] = "-";
				}
				if (ResnikCheck.equals("on")) {
					double simResnik = CompareResnik(ic_a, ic_b, ic_parent);
					res[3][1] = String.valueOf(Math.round(simResnik * 10000.0) / 10000.0);
				} else {
					res[3][1] = "-";
				}
				if (JCCheck.equals("on")) {
					double simJC = CompareJC(ic_a, ic_b, ic_parent);
					res[3][2] = String.valueOf(Math.round(simJC * 10000.0) / 10000.0);
				} else {
					res[3][2] = "-";
				}
				engineOnt = null;
				ic_parent = null;
			} else if (yourfileCheck == "") {
				res[3][0] = "-";
				res[3][1] = "-";
				res[3][2] = "-";
			} else {
				res[3][0] = "Your terms do not find";
				res[3][1] = "Your terms do not find";
				res[3][2] = "Your terms do not find";
			}
			// --------- State 4: Run MeSH SNOMED
			if (MeshCheck.equals("on") && SnomedCheck.equals("on") && Terms[0][0] != null && Terms[1][1] != null) {
				MergedOnt = "MeshSnomedCT";
				SM_Engine.firstPriority = false;
				findShareP.CSVtoArray(MergedOnt);
				SM_Engine engineMeshSnomed = new SM_Engine(Term1Mesh, Term2Snomed, Coordinator.meshGraph,
						Coordinator.snomedGraph, MergedOnt);
				double ic_a = engineMeshSnomed.getIC(confAll.getICconf(), Term1Mesh);
				double ic_b = engineMeshSnomed.getIC(confAll.getICconf(), Term2Snomed);
				ArrayList<Double> ic_parent = engineMeshSnomed.get_PSO_IC(confAll.getICconf(), Term1Mesh, Term2Snomed,
						engineMeshSnomed, MergedOnt);
				if (LinCheck.equals("on")) {
					double simLin = CompareLin(ic_a, ic_b, ic_parent);
					res[4][0] = String.valueOf(Math.round(simLin * 10000.0) / 10000.0);
				} else {
					res[4][0] = "-";
				}
				if (ResnikCheck.equals("on")) {
					double simResnik = CompareResnik(ic_a, ic_b, ic_parent);
					res[4][1] = String.valueOf(Math.round(simResnik * 10000.0) / 10000.0);
				} else {
					res[4][1] = "-";
				}
				if (JCCheck.equals("on")) {
					double simJC = CompareJC(ic_a, ic_b, ic_parent);
					res[4][2] = String.valueOf(Math.round(simJC * 10000.0) / 10000.0);
				} else {
					res[4][2] = "-";
				}
				engineMeshSnomed = null;
				ic_parent = null;
			} else if (MeshCheck == "" || SnomedCheck == "") {
				res[4][0] = "-";
				res[4][1] = "-";
				res[4][2] = "-";
			} else {
				res[4][0] = "Your terms do not find";
				res[4][1] = "Your terms do not find";
				res[4][2] = "Your terms do not find";
			}
			// --------- State 5: Run MeSH WordNet
			if (MeshCheck.equals("on") && WordnetCheck.equals("on") && Terms[0][0] != null && Terms[2][1] != null) {
				MergedOnt = "MeshWordNet";
				SM_Engine.firstPriority = false;
				findShareP.CSVtoArray(MergedOnt);
				SM_Engine engineMeshWordnet = new SM_Engine(Term1Mesh, Term2WordNet, Coordinator.meshGraph,
						Coordinator.wordnetGraph, MergedOnt);
				double ic_a = engineMeshWordnet.getIC(confAll.getICconf(), Term1Mesh);
				double ic_b = engineMeshWordnet.getIC(confAll.getICconf(), Term2WordNet);
				ArrayList<Double> ic_parent = engineMeshWordnet.get_PSO_IC(confAll.getICconf(), Term1Mesh, Term2WordNet,
						engineMeshWordnet, MergedOnt);
				if (LinCheck.equals("on")) {
					double simLin = CompareLin(ic_a, ic_b, ic_parent);
					res[5][0] = String.valueOf(Math.round(simLin * 10000.0) / 10000.0);
				} else {
					res[5][0] = "-";
				}
				if (ResnikCheck.equals("on")) {
					double simResnik = CompareResnik(ic_a, ic_b, ic_parent);
					res[5][1] = String.valueOf(Math.round(simResnik * 10000.0) / 10000.0);
				} else {
					res[5][1] = "-";
				}
				if (JCCheck.equals("on")) {
					double simJC = CompareJC(ic_a, ic_b, ic_parent);
					res[5][2] = String.valueOf(Math.round(simJC * 10000.0) / 10000.0);
				} else {
					res[5][2] = "-";
				}
				engineMeshWordnet = null;
				ic_parent = null;
			} else if (MeshCheck == "" || WordnetCheck == "") {
				res[5][0] = "-";
				res[5][1] = "-";
				res[5][2] = "-";
			} else {
				res[5][0] = "Your terms do not find";
				res[5][1] = "Your terms do not find";
				res[5][2] = "Your terms do not find";
			}
			// --------- State 6: Run Snomed MeSH
			if (SnomedCheck.equals("on") && MeshCheck.equals("on") && Terms[1][0] != null && Terms[0][1] != null) {
				MergedOnt = "MeshSnomedCT";
				SM_Engine.firstPriority = true;
				findShareP.CSVtoArray(MergedOnt);
				SM_Engine engineSnomedMesh = new SM_Engine(Term1Snomed, Term2Mesh, Coordinator.meshGraph,
						Coordinator.snomedGraph, MergedOnt);
				double ic_a = engineSnomedMesh.getIC(confAll.getICconf(), Term1Snomed);
				double ic_b = engineSnomedMesh.getIC(confAll.getICconf(), Term2Mesh);
				ArrayList<Double> ic_parent = engineSnomedMesh.get_PSO_IC(confAll.getICconf(), Term1Snomed, Term2Mesh,
						engineSnomedMesh, MergedOnt);
				if (LinCheck.equals("on")) {
					double simLin = CompareLin(ic_a, ic_b, ic_parent);
					res[6][0] = String.valueOf(Math.round(simLin * 10000.0) / 10000.0);
				} else {
					res[6][0] = "-";
				}
				if (ResnikCheck.equals("on")) {
					double simResnik = CompareResnik(ic_a, ic_b, ic_parent);
					res[6][1] = String.valueOf(Math.round(simResnik * 10000.0) / 10000.0);
				} else {
					res[6][1] = "-";
				}
				if (JCCheck.equals("on")) {
					double simJC = CompareJC(ic_a, ic_b, ic_parent);
					res[6][2] = String.valueOf(Math.round(simJC * 10000.0) / 10000.0);
				} else {
					res[6][2] = "-";
				}
				engineSnomedMesh = null;
				ic_parent = null;
			} else if (SnomedCheck == "" || MeshCheck == "") {
				res[6][0] = "-";
				res[6][1] = "-";
				res[6][2] = "-";
			} else {
				res[6][0] = "Your terms do not find";
				res[6][1] = "Your terms do not find";
				res[6][2] = "Your terms do not find";
			}
			// --------- State 7: Run Snomed WordNet
			if (SnomedCheck.equals("on") && WordnetCheck.equals("on") && Terms[1][0] != null && Terms[2][1] != null) {
				MergedOnt = "SnomedCTWordNet";
				SM_Engine.firstPriority = false;
				SM_Engine engineSnomedWordnet = new SM_Engine(Term1Snomed, Term2WordNet, Coordinator.snomedGraph,
						Coordinator.wordnetGraph, MergedOnt);
				double ic_a = engineSnomedWordnet.getIC(confAll.getICconf(), Term1Snomed);
				double ic_b = engineSnomedWordnet.getIC(confAll.getICconf(), Term2WordNet);
				ArrayList<Double> ic_parent = engineSnomedWordnet.get_PSO_IC(confAll.getICconf(), Term1Snomed,
						Term2WordNet, engineSnomedWordnet, MergedOnt);
				if (LinCheck.equals("on")) {
					double simLin = CompareLin(ic_a, ic_b, ic_parent);
					res[7][0] = String.valueOf(Math.round(simLin * 10000.0) / 10000.0);
				} else {
					res[7][0] = "-";
				}
				if (ResnikCheck.equals("on")) {
					double simResnik = CompareResnik(ic_a, ic_b, ic_parent);
					res[7][1] = String.valueOf(Math.round(simResnik * 10000.0) / 10000.0);
				} else {
					res[7][1] = "-";
				}
				if (JCCheck.equals("on")) {
					double simJC = CompareJC(ic_a, ic_b, ic_parent);
					res[7][2] = String.valueOf(Math.round(simJC * 10000.0) / 10000.0);
				} else {
					res[7][2] = "-";
				}
				engineSnomedWordnet = null;
				ic_parent = null;
			} else if (SnomedCheck == "" || WordnetCheck == "") {
				res[7][0] = "-";
				res[7][1] = "-";
				res[7][2] = "-";
			} else {
				res[7][0] = "Your terms do not find";
				res[7][1] = "Your terms do not find";
				res[7][2] = "Your terms do not find";
			}
			// --------- State 8: Run WordNet Mesh
			if (WordnetCheck.equals("on") && MeshCheck.equals("on") && Terms[2][0] != null && Terms[0][1] != null) {
				MergedOnt = "MeshWordNet";
				SM_Engine.firstPriority = false;
				SM_Engine engineWordnetMesh = new SM_Engine(Term1WordNet, Term2Mesh, Coordinator.meshGraph,
						Coordinator.wordnetGraph, MergedOnt);
				double ic_a = engineWordnetMesh.getIC(confAll.getICconf(), Term1WordNet);
				double ic_b = engineWordnetMesh.getIC(confAll.getICconf(), Term2Mesh);
				ArrayList<Double> ic_parent = engineWordnetMesh.get_PSO_IC(confAll.getICconf(), Term1WordNet, Term2Mesh,
						engineWordnetMesh, MergedOnt);
				if (LinCheck.equals("on")) {
					double simLin = CompareLin(ic_a, ic_b, ic_parent);
					res[8][0] = String.valueOf(Math.round(simLin * 10000.0) / 10000.0);
				} else {
					res[8][0] = "-";
				}
				if (ResnikCheck.equals("on")) {
					double simResnik = CompareResnik(ic_a, ic_b, ic_parent);
					res[8][1] = String.valueOf(Math.round(simResnik * 10000.0) / 10000.0);
				} else {
					res[8][1] = "-";
				}
				if (JCCheck.equals("on")) {
					double simJC = CompareJC(ic_a, ic_b, ic_parent);
					res[8][2] = String.valueOf(Math.round(simJC * 10000.0) / 10000.0);
				} else {
					res[8][2] = "-";
				}
				engineWordnetMesh = null;
				ic_parent = null;
			} else if (WordnetCheck == "" || MeshCheck == "") {
				res[8][0] = "-";
				res[8][1] = "-";
				res[8][2] = "-";
			} else {
				res[8][0] = "Your terms do not find";
				res[8][1] = "Your terms do not find";
				res[8][2] = "Your terms do not find";
			}
			// --------- State 9: Run WordNet Snomed
			if (WordnetCheck.equals("on") && SnomedCheck.equals("on") && Terms[2][0] != null && Terms[1][1] != null) {
				MergedOnt = "SnomedCTWordNet";
				SM_Engine.firstPriority = false;
				SM_Engine engineWordnetSnomed = new SM_Engine(Term1WordNet, Term2Snomed, Coordinator.wordnetGraph,
						Coordinator.snomedGraph, MergedOnt);
				double ic_a = engineWordnetSnomed.getIC(confAll.getICconf(), Term1WordNet);
				double ic_b = engineWordnetSnomed.getIC(confAll.getICconf(), Term2Snomed);
				ArrayList<Double> ic_parent = engineWordnetSnomed.get_PSO_IC(confAll.getICconf(), Term1WordNet,
						Term2Snomed, engineWordnetSnomed, MergedOnt);
				if (LinCheck.equals("on")) {
					double simLin = CompareLin(ic_a, ic_b, ic_parent);
					res[9][0] = String.valueOf(Math.round(simLin * 10000.0) / 10000.0);
				} else {
					res[9][0] = "-";
				}
				if (ResnikCheck.equals("on")) {
					double simResnik = CompareResnik(ic_a, ic_b, ic_parent);
					res[9][1] = String.valueOf(Math.round(simResnik * 10000.0) / 10000.0);
				} else {
					res[9][1] = "-";
				}
				if (JCCheck.equals("on")) {
					double simJC = CompareJC(ic_a, ic_b, ic_parent);
					res[9][2] = String.valueOf(Math.round(simJC * 10000.0) / 10000.0);
				} else {
					res[9][2] = "-";
				}
				engineWordnetSnomed = null;
				ic_parent = null;
			} else if (WordnetCheck == "" || SnomedCheck == "") {
				res[9][0] = "-";
				res[9][1] = "-";
				res[9][2] = "-";
			} else {
				res[9][0] = "Your terms do not find";
				res[9][1] = "Your terms do not find";
				res[9][2] = "Your terms do not find";
			}
			// --------- State 10: Run Mesh other
			if (MeshCheck.equals("on") && yourfileCheck.equals("on") && Terms[0][0] != null && Terms[3][1] != null) {
				MergedOnt = null;
				SM_Engine engineMeshOther = new SM_Engine(Term1Mesh, Term2Other, Coordinator.meshGraph, ontGraph,
						MergedOnt);
				double ic_a = engineMeshOther.getIC(confAll.getICconf(), Term1Mesh);
				double ic_b = engineMeshOther.getIC(confAll.getICconf(), Term2Other);
				ArrayList<Double> ic_parent = engineMeshOther.get_PSO_IC(confAll.getICconf(), Term1Mesh, Term2Other,
						engineMeshOther, MergedOnt);
				if (LinCheck.equals("on")) {
					double simLin = CompareLin(ic_a, ic_b, ic_parent);
					res[10][0] = String.valueOf(Math.round(simLin * 10000.0) / 10000.0);
				} else {
					res[10][0] = "-";
				}
				if (ResnikCheck.equals("on")) {
					double simResnik = CompareResnik(ic_a, ic_b, ic_parent);
					res[10][1] = String.valueOf(Math.round(simResnik * 10000.0) / 10000.0);
				} else {
					res[10][1] = "-";
				}
				if (JCCheck.equals("on")) {
					double simJC = CompareJC(ic_a, ic_b, ic_parent);
					res[10][2] = String.valueOf(Math.round(simJC * 10000.0) / 10000.0);
				} else {
					res[10][2] = "-";
				}
				engineMeshOther = null;
				ic_parent = null;
			} else if (MeshCheck == "" || yourfileCheck == "") {
				res[10][0] = "-";
				res[10][1] = "-";
				res[10][2] = "-";
			} else {
				res[10][0] = "Your terms do not find";
				res[10][1] = "Your terms do not find";
				res[10][2] = "Your terms do not find";
			}
			// --------- State 11: Run Snomed Other
			if (SnomedCheck.equals("on") && yourfileCheck.equals("on") && Terms[1][0] != null && Terms[3][1] != null) {
				MergedOnt = null;
				SM_Engine engineSnomedOther = new SM_Engine(Term1Snomed, Term2Other, Coordinator.snomedGraph, ontGraph,
						MergedOnt);
				double ic_a = engineSnomedOther.getIC(confAll.getICconf(), Term1Snomed);
				double ic_b = engineSnomedOther.getIC(confAll.getICconf(), Term2Other);
				ArrayList<Double> ic_parent = engineSnomedOther.get_PSO_IC(confAll.getICconf(), Term1Snomed, Term2Other,
						engineSnomedOther, MergedOnt);
				if (LinCheck.equals("on")) {
					double simLin = CompareLin(ic_a, ic_b, ic_parent);
					res[11][0] = String.valueOf(Math.round(simLin * 10000.0) / 10000.0);
				} else {
					res[11][0] = "-";
				}
				if (ResnikCheck.equals("on")) {
					double simResnik = CompareResnik(ic_a, ic_b, ic_parent);
					res[11][1] = String.valueOf(Math.round(simResnik * 10000.0) / 10000.0);
				} else {
					res[11][1] = "-";
				}
				if (JCCheck.equals("on")) {
					double simJC = CompareJC(ic_a, ic_b, ic_parent);
					res[11][2] = String.valueOf(Math.round(simJC * 10000.0) / 10000.0);
				} else {
					res[11][2] = "-";
				}
				engineSnomedOther = null;
				ic_parent = null;
			} else if (SnomedCheck == "" || yourfileCheck == "") {
				res[11][0] = "-";
				res[11][1] = "-";
				res[11][2] = "-";
			} else {
				res[11][0] = "Your terms do not find";
				res[11][1] = "Your terms do not find";
				res[11][2] = "Your terms do not find";
			}
			// --------- State 12: Run Wordnet Other
			if (WordnetCheck.equals("on") && yourfileCheck.equals("on") && Terms[2][0] != null && Terms[3][1] != null) {
				MergedOnt = null;
				SM_Engine engineWordnetOther = new SM_Engine(Term1WordNet, Term2Other, Coordinator.wordnetGraph,
						ontGraph, MergedOnt);
				double ic_a = engineWordnetOther.getIC(confAll.getICconf(), Term1WordNet);
				double ic_b = engineWordnetOther.getIC(confAll.getICconf(), Term2Other);
				ArrayList<Double> ic_parent = engineWordnetOther.get_PSO_IC(confAll.getICconf(), Term1WordNet,
						Term2Other, engineWordnetOther, MergedOnt);
				if (LinCheck.equals("on")) {
					double simLin = CompareLin(ic_a, ic_b, ic_parent);
					res[12][0] = String.valueOf(Math.round(simLin * 10000.0) / 10000.0);
				} else {
					res[12][0] = "-";
				}
				if (ResnikCheck.equals("on")) {
					double simResnik = CompareResnik(ic_a, ic_b, ic_parent);
					res[12][1] = String.valueOf(Math.round(simResnik * 10000.0) / 10000.0);
				} else {
					res[12][1] = "-";
				}
				if (JCCheck.equals("on")) {
					double simJC = CompareJC(ic_a, ic_b, ic_parent);
					res[12][2] = String.valueOf(Math.round(simJC * 10000.0) / 10000.0);
				} else {
					res[12][2] = "-";
				}
				engineWordnetOther = null;
				ic_parent = null;
			} else if (WordnetCheck == "" || yourfileCheck == "") {
				res[12][0] = "-";
				res[12][1] = "-";
				res[12][2] = "-";
			} else {
				res[12][0] = "Your terms do not find";
				res[12][1] = "Your terms do not find";
				res[12][2] = "Your terms do not find";
			}
			// --------- State 13: Run Other Mesh
			if (yourfileCheck.equals("on") && MeshCheck.equals("on") && Terms[3][0] != null && Terms[0][1] != null) {
				MergedOnt = null;
				SM_Engine engineMeshOther = new SM_Engine(Term1Other, Term2Mesh, ontGraph, Coordinator.meshGraph,
						MergedOnt);
				double ic_a = engineMeshOther.getIC(confAll.getICconf(), Term1Other);
				double ic_b = engineMeshOther.getIC(confAll.getICconf(), Term2Mesh);
				ArrayList<Double> ic_parent = engineMeshOther.get_PSO_IC(confAll.getICconf(), Term1Other, Term2Mesh,
						engineMeshOther, MergedOnt);
				if (LinCheck.equals("on")) {
					double simLin = CompareLin(ic_a, ic_b, ic_parent);
					res[13][0] = String.valueOf(Math.round(simLin * 10000.0) / 10000.0);
				} else {
					res[13][0] = "-";
				}
				if (ResnikCheck.equals("on")) {
					double simResnik = CompareResnik(ic_a, ic_b, ic_parent);
					res[13][1] = String.valueOf(Math.round(simResnik * 10000.0) / 10000.0);
				} else {
					res[13][1] = "-";
				}
				if (JCCheck.equals("on")) {
					double simJC = CompareJC(ic_a, ic_b, ic_parent);
					res[13][2] = String.valueOf(Math.round(simJC * 10000.0) / 10000.0);
				} else {
					res[13][2] = "-";
				}
				engineMeshOther = null;
				ic_parent = null;
			} else if (yourfileCheck == "" || MeshCheck == "") {
				res[13][0] = "-";
				res[13][1] = "-";
				res[13][2] = "-";
			} else {
				res[13][0] = "Your terms do not find";
				res[13][1] = "Your terms do not find";
				res[13][2] = "Your terms do not find";
			}
			// --------- State 14: Run Other Snomed
			if (yourfileCheck.equals("on") && SnomedCheck.equals("on") && Terms[3][0] != null && Terms[1][1] != null) {
				MergedOnt = null;
				SM_Engine engineOtherSnomed = new SM_Engine(Term1Other, Term2Snomed, ontGraph, Coordinator.snomedGraph,
						MergedOnt);
				double ic_a = engineOtherSnomed.getIC(confAll.getICconf(), Term1Other);
				double ic_b = engineOtherSnomed.getIC(confAll.getICconf(), Term2Snomed);
				ArrayList<Double> ic_parent = engineOtherSnomed.get_PSO_IC(confAll.getICconf(), Term1Other, Term2Snomed,
						engineOtherSnomed, MergedOnt);
				if (LinCheck.equals("on")) {
					double simLin = CompareLin(ic_a, ic_b, ic_parent);
					res[14][0] = String.valueOf(Math.round(simLin * 10000.0) / 10000.0);
				} else {
					res[14][0] = "-";
				}
				if (ResnikCheck.equals("on")) {
					double simResnik = CompareResnik(ic_a, ic_b, ic_parent);
					res[14][1] = String.valueOf(Math.round(simResnik * 10000.0) / 10000.0);
				} else {
					res[14][1] = "-";
				}
				if (JCCheck.equals("on")) {
					double simJC = CompareJC(ic_a, ic_b, ic_parent);
					res[14][2] = String.valueOf(Math.round(simJC * 10000.0) / 10000.0);
				} else {
					res[14][2] = "-";
				}
				engineOtherSnomed = null;
				ic_parent = null;
			} else if (yourfileCheck == "" || SnomedCheck == "") {
				res[14][0] = "-";
				res[14][1] = "-";
				res[14][2] = "-";
			} else {
				res[14][0] = "Your terms do not find";
				res[14][1] = "Your terms do not find";
				res[14][2] = "Your terms do not find";
			}
			// --------- State 15: Run Other WordNet
			if (yourfileCheck.equals("on") && WordnetCheck.equals("on") && Terms[3][0] != null && Terms[2][1] != null) {
				MergedOnt = null;
				SM_Engine engineOtherWordnet = new SM_Engine(Term1Other, Term2WordNet, ontGraph,
						Coordinator.wordnetGraph, MergedOnt);
				double ic_a = engineOtherWordnet.getIC(confAll.getICconf(), Term1Other);
				double ic_b = engineOtherWordnet.getIC(confAll.getICconf(), Term2WordNet);
				ArrayList<Double> ic_parent = engineOtherWordnet.get_PSO_IC(confAll.getICconf(), Term1Other,
						Term2WordNet, engineOtherWordnet, MergedOnt);
				if (LinCheck.equals("on")) {
					double simLin = CompareLin(ic_a, ic_b, ic_parent);
					res[15][0] = String.valueOf(Math.round(simLin * 10000.0) / 10000.0);
				} else {
					res[15][0] = "-";
				}
				if (ResnikCheck.equals("on")) {
					double simResnik = CompareResnik(ic_a, ic_b, ic_parent);
					res[15][1] = String.valueOf(Math.round(simResnik * 10000.0) / 10000.0);
				} else {
					res[15][1] = "-";
				}
				if (JCCheck.equals("on")) {
					double simJC = CompareJC(ic_a, ic_b, ic_parent);
					res[15][2] = String.valueOf(Math.round(simJC * 10000.0) / 10000.0);
				} else {
					res[15][2] = "-";
				}
				engineOtherWordnet = null;
				ic_parent = null;
			} else if (yourfileCheck == "" || WordnetCheck == "") {
				res[15][0] = "-";
				res[15][1] = "-";
				res[15][2] = "-";
			} else {
				res[15][0] = "Your terms do not find";
				res[15][1] = "Your terms do not find";
				res[15][2] = "Your terms do not find";
			}

			uriOnt = null;
			icConf = null;
			confAll = null;
			factory = null;
			Term1Mesh = null;
			Term2Mesh = null;
			Term1Snomed = null;
			Term2Snomed = null;
			Term1WordNet = null;
			Term2WordNet = null;
			Term1Other = null;
			Term2Other = null;
			ontGraph = null;
		} catch (SLIB_Exception ex) {
			Logger.getLogger(CompController.class.getName()).log(Level.SEVERE, null, ex);
		}

		return res;
	}

	public static double CompareLin(double ic_a, double ic_b, ArrayList<Double> ic_MICA) throws SLIB_Exception {
		double re = 0.0;

		if (ic_MICA == null) {
			re = 0;
		} else if (ic_MICA.get(0) == 1) {
			re = 1;
		} else {

			double sum = 0, s = 0;
			ArrayList<Double> res = new ArrayList<>();
			for (int i = 0; i < ic_MICA.size(); i++) {
				s = simLin(ic_a, ic_b, ic_MICA.get(i));
				sum = sum + s;
				res.add(s);
			}

			// average value
			re = (double) (sum / ic_MICA.size());
		}
		return re;
	}

	public static double simLin(double ic_a, double ic_b, double ic_mica) throws SLIB_Ex_Critic {

		double lin = 0.;

		double den = ic_a + ic_b;

		PREVENT_INCOHERENCES = false;
		if (PREVENT_INCOHERENCES
				&& ((ic_mica > ic_a && ic_mica - ic_a > 0.00001) || (ic_mica > ic_b && ic_mica - ic_b > 0.00001))) {
			throw new SLIB_Ex_Critic("Cannot compute Lin considering ic MICA > ic C1 or ic c2, ic MICA set to "
					+ ic_mica + " ic c1 " + ic_a + " ic c2 " + ic_b);
		}

		if (den != 0) {
			lin = (2. * ic_mica) / den;
		}
		return lin;
	}

	public static double CompareResnik(double ic_a, double ic_b, ArrayList<Double> ic_MICA) throws SLIB_Exception {
		double re = 0.0;

		if (ic_MICA == null) {
			// if our optimal set parents are null, means there is no similarity
			// between a and b, so , similarity will be setted to 0
			re = 0;
		} else if (ic_MICA.get(0) == 1) {
			re = 1;
		} else {

			double sum = 0;
			for (int i = 0; i < ic_MICA.size(); i++) {
				sum = sum + ic_MICA.get(i);
			}
			re = (double) (sum / ic_MICA.size());
		}
		return re;
	}

	public static double CompareJC(double ic_a, double ic_b, ArrayList<Double> ic_MICA) throws SLIB_Exception {

		double re = 0.0;

		if (ic_MICA == null) {
			re = 0;
		} else if (ic_MICA.get(0) == 1) {
			re = 1;
		} else {
			double sum = 0;
			for (int i = 0; i < ic_MICA.size(); i++) {
				sum = sum + simJC(ic_a, ic_b, ic_MICA.get(i));
			}

			re = (double) (sum / ic_MICA.size());
		}
		return re;

	}

	public static double simJC(double ic_a, double ic_b, double ic_MICA) {
		double jc = 1. - (ic_a + ic_b - 2. * ic_MICA) / 2.;
		return jc;
	}
}
