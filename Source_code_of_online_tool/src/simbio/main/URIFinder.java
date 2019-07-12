package simbio.main;

/**
 * Fidning URI of user terms 
 * @author Samira Babalou (samira[dot]babalou[at]uni_jean[dot]de)
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.Set;

import org.openrdf.model.URI;

import au.com.bytecode.opencsv.CSVReader;
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
import slib.sml.sm.core.engine.SM_Engine;
import slib.utils.ex.SLIB_Exception;

public class URIFinder {
	public static String[] finder(String T1, String T2, String OntFlag, String Term1OntPath, String Term2OntPath)
			throws IOException, SLIB_Exception, URISyntaxException {

		String[] Terms = new String[2];

		switch (OntFlag) {

		case "MeSH":
			Terms = MeshURIFinder(T1, T2);
			break;

		case "SNOMEDCT":
			Terms = SnomedURIFinder(T1, T2);
			break;

		case "WordNet":
			Terms = WordNetURIFinder(T1, T2);
			break;

		case "MeSH-SNOMEDCT":
			Terms[0] = MeshURIFinder(T1);
			Terms[1] = SnomedURIFinder(T2);
			break;

		case "SNOMEDCT-MeSH":
			Terms[0] = SnomedURIFinder(T1);
			Terms[1] = MeshURIFinder(T2);
			break;

		case "MeSH-WordNet":
			Terms[0] = MeshURIFinder(T1);
			Terms[1] = WordNetURIFinder(T2);
			break;
		case "SNOMEDCT-WordNet":
			Terms[0] = SnomedURIFinder(T1);
			Terms[1] = WordNetURIFinder(T2);
			break;

		case "WordNet-MeSH":
			Terms[0] = T1.toLowerCase();
			Terms[1] = MeshURIFinder(T2);
			break;

		case "WordNet-SNOMEDCT":
			Terms[0] = WordNetURIFinder(T1);
			Terms[1] = SnomedURIFinder(T2);
			break;

		case "Other":
			Terms = OntURIFinder(T1, T2, Term1OntPath);
			break;

		case "Other1-Other2":
			Terms[0] = OntURIFinder(T1, Term1OntPath);
			Terms[1] = OntURIFinder(T2, Term2OntPath);
			break;

		case "MeSH-Other":
			Terms[0] = MeshURIFinder(T1);
			Terms[1] = OntURIFinder(T2, Term2OntPath);
			break;

		case "SNOMEDCT-Other":
			Terms[0] = SnomedURIFinder(T1);
			Terms[1] = OntURIFinder(T2, Term2OntPath);
			break;

		case "WordNet-Other":
			Terms[0] = WordNetURIFinder(T1);
			Terms[1] = OntURIFinder(T2, Term2OntPath);
			break;

		case "Other-MeSH":
			Terms[0] = OntURIFinder(T1, Term1OntPath);
			Terms[1] = MeshURIFinder(T2);
			break;

		case "Other-SNOMEDCT":
			Terms[0] = OntURIFinder(T1, Term1OntPath);
			Terms[1] = SnomedURIFinder(T2);
			break;

		case "Other-WordNet":
			Terms[0] = OntURIFinder(T1, Term1OntPath);
			Terms[1] = WordNetURIFinder(T2);
			break;

		}

		return Terms;
	}

	public static String[][] finderAll(String T1, String T2, String OntPath, String MeshCheck, String SnomedCheck,
			String WordnetCheck, String YourFileCheck) throws IOException, SLIB_Exception, URISyntaxException {

		String[][] Terms = new String[4][2];
		if (MeshCheck.equals("on")) {
			String[] tempMesh = MeshURIFinder(T1, T2);
			Terms[0][0] = tempMesh[0];
			Terms[0][1] = tempMesh[1];
		}
		if (SnomedCheck.equals("on")) {
			String[] tempSnomed = SnomedURIFinder(T1, T2);
			Terms[1][0] = tempSnomed[0];
			Terms[1][1] = tempSnomed[1];
		}
		if (WordnetCheck.equals("on")) {
			Terms[2] = WordNetURIFinder(T1, T2);
		}
		if (YourFileCheck.equals("on")) {
			Terms[3] = OntURIFinder(T1, T2, OntPath);
		}
		return Terms;
	}

	private static String[] MeshURIFinder(String t1, String t2) throws IOException {
		String[] terms = new String[2];
		InputStream ins = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("simbio/resources/meshMap.csv");
		BufferedReader rdr = new BufferedReader(new InputStreamReader(ins));

		CSVReader reader = null;
		try {
			reader = new CSVReader(rdr);
			String[] line;
			String tl = "";
			while ((line = reader.readNext()) != null) {
				if (line.length > 2) {
					tl = line[1] + ", " + line[2];
				} else
					tl = line[1];
				if (tl.toLowerCase().equals(t1.toLowerCase()) || line[0].toLowerCase().equals(t1.toLowerCase()))
					terms[0] = line[0];
				if (tl.toLowerCase().equals(t2.toLowerCase()) || line[0].toLowerCase().equals(t2.toLowerCase()))
					terms[1] = line[0];
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return terms;
	}

	private static String MeshURIFinder(String t1) {
		String term1 = "";
		InputStream ins = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("simbio/resources/meshMap.csv");
		BufferedReader rdr = new BufferedReader(new InputStreamReader(ins));
		CSVReader reader = null;
		try {

			reader = new CSVReader(rdr);
			String[] line;
			String tl = "";
			while ((line = reader.readNext()) != null) {
				if (line.length > 2) {
					tl = line[1] + ", " + line[2];
				} else
					tl = line[1];
				if (tl.toLowerCase().equals(t1.toLowerCase()) || line[0].toLowerCase().equals(t1.toLowerCase())) {
					term1 = line[0];
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return term1;
	}

	private static String SnomedURIFinder(String t1) throws IOException {

		String term1 = "";

		InputStream inputStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("simbio/resources/SnomedMap.txt");

		try (Scanner fileScanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
			fileScanner.nextLine(); // skip 1st line
			while (fileScanner.hasNextLine()) {
				String lin = fileScanner.nextLine();
				String tl = "";
				String[] te = lin.split(",");
				if (te.length > 2) {
					tl = te[1] + ", " + te[2];
				} else
					tl = te[1];
				if (te[0].toLowerCase().equals(t1.toLowerCase()) || tl.toLowerCase().equals(t1.toLowerCase())) {
					term1 = te[0];
					break;
				}
			}
		}
		return term1;
	}

	private static String[] SnomedURIFinder(String t1, String t2) throws FileNotFoundException {
		String[] terms = new String[2];

		InputStream inputStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("simbio/resources/SnomedMap.txt");

		try (Scanner fileScanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
			fileScanner.nextLine(); // skip 1st line
			while (fileScanner.hasNextLine()) {
				String lin = fileScanner.nextLine();
				String tl = "";
				String[] te = lin.split(",");
				if (te.length > 2) {
					tl = te[1] + ", " + te[2];
				} else
					tl = te[1];
				if (te[0].toLowerCase().equals(t1.toLowerCase()) || tl.toLowerCase().equals(t1.toLowerCase()))
					terms[0] = te[0];
				if (te[0].toLowerCase().equals(t2.toLowerCase()) || tl.toLowerCase().equals(t2.toLowerCase()))
					terms[1] = te[0];
			}
		}
		return terms;
	}

	private static String[] OntURIFinder(String t1, String t2, String t1Path) throws SLIB_Exception {
		String[] terms = new String[2];
		terms[0] = null;
		terms[1] = null;
		if (t1Path == null || t1Path == "") {
			terms[0] = null;
			terms[1] = null;
		} else {
			t1 = t1.toLowerCase();
			t2 = t2.toLowerCase();
			String ontname = "";
			int a = t1Path.indexOf("uploads");
			ontname = t1Path.substring(a + 8, t1Path.length() - 4);
			String uriOnt = "http://" + ontname.toLowerCase();
			URIFactoryMemory factory = URIFactoryMemory.getSingleton();
			URI graphURI = factory.getURI(uriOnt + "/");
			factory.loadNamespacePrefix("ONT", graphURI.toString());
			G ontGraph = new GraphMemory(graphURI);
			GDataConf dataConf = new GDataConf(GFormat.RDF_XML, t1Path);
			GAction actionRerootConf = new GAction(GActionType.REROOTING);
			GraphConf gConf = new GraphConf();
			gConf.addGDataConf(dataConf);
			gConf.addGAction(actionRerootConf);
			GraphLoaderGeneric.load(gConf, ontGraph);
			Set<URI> roots = new ValidatorDAG().getTaxonomicRoots(ontGraph);
			t1 = t1.substring(0, 1).toUpperCase() + t1.substring(1);
			t2 = t2.substring(0, 1).toUpperCase() + t2.substring(1);
			URI c1 = factory.getURI(uriOnt + "#" + t1.trim());
			URI c2 = factory.getURI(uriOnt + "#" + t2.trim());
			SM_Engine engine = new SM_Engine(ontGraph);
			if (engine.CheckIfNotClass(c1))
				terms[0] = t1;
			if (engine.CheckIfNotClass(c2))
				terms[1] = t2;
		}
		return terms;
	}

	private static String OntURIFinder(String t1, String t1Path) throws SLIB_Exception {
		String terms = null;
		if (t1Path == null || t1Path == "") {
			terms = null;
		} else {
			t1 = t1.toLowerCase();
			String ontname = "";
			int a = t1Path.indexOf("uploads");
			ontname = t1Path.substring(a + 8, t1Path.length() - 4);
			String uriOnt = "http://" + ontname.toLowerCase();
			URIFactoryMemory factory = URIFactoryMemory.getSingleton();
			URI graphURI = factory.getURI(uriOnt + "/");
			factory.loadNamespacePrefix(ontname, graphURI.toString());
			G ontGraph = new GraphMemory(graphURI);
			GDataConf dataConf = new GDataConf(GFormat.RDF_XML, t1Path);
			GAction actionRerootConf = new GAction(GActionType.REROOTING);
			GraphConf gConf = new GraphConf();
			gConf.addGDataConf(dataConf);
			gConf.addGAction(actionRerootConf);
			GraphLoaderGeneric.load(gConf, ontGraph);
			Set<URI> roots = new ValidatorDAG().getTaxonomicRoots(ontGraph);
			t1 = t1.substring(0, 1).toUpperCase() + t1.substring(1);
			URI c1 = factory.getURI(uriOnt + "#" + t1.trim());
			SM_Engine engine = new SM_Engine(ontGraph);
			if (engine.CheckIfNotClass(c1))
				terms = t1;
		}
		return terms;
	}

	private static String[] WordNetURIFinder(String t1, String t2) throws SLIB_Exception, URISyntaxException {
		String[] terms = new String[2];
		terms[0] = null;
		terms[1] = null;
		t1 = t1.toLowerCase();
		t2 = t2.toLowerCase();
		if (Coordinator.engineWordNet == null)
			Coordinator.wordnetLoader();

		Set<URI> uris_Term1 = Coordinator.indexWordnetNoun.get(t1.trim());
		Set<URI> uris_Term2 = Coordinator.indexWordnetNoun.get(t2.trim());
		if (uris_Term1 != null)
			terms[0] = t1;
		if (uris_Term2 != null)
			terms[1] = t2;

		return terms;
	}

	private static String WordNetURIFinder(String t1) throws SLIB_Exception, URISyntaxException {
		String terms = "";
		t1 = t1.toLowerCase();
		if (Coordinator.engineWordNet == null)
			Coordinator.wordnetLoader();

		Set<URI> uris_Term1 = Coordinator.indexWordnetNoun.get(t1.trim());
		if (uris_Term1 != null)
			terms = t1;

		return terms;
	}
}
