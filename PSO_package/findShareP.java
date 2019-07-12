package slib.sml.sm.core.metrics.ic.pso;

/**
 * A particle swarm-based approach for semantic similarity computation
 * Publication: Babalou, Samira, Alsayed Algergawy, and Birgitta König-Ries. "A particle swarm-based approach for semantic similarity computation." OTM Confederated International Conferences" On the Move to Meaningful Internet Systems". Springer, Cham, 2017. 
 * @author Samira Babalou (samira[dot]babalou[at]uni_jean[dot]de)
 */
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.openrdf.model.URI;

import au.com.bytecode.opencsv.CSVReader;
import slib.graph.model.graph.G;
import slib.sml.sm.core.engine.SM_Engine;
import slib.utils.ex.SLIB_Exception;

public class findShareP {
	public static int MeshSnomedNumber = 39366;
	public static int MeshWordnetNumber = 5822;
	public static int SnomedWordnetNumber = 22711;
	public static String[][] datasetMeshSnomed = new String[MeshSnomedNumber][2];
	public static String[][] datasetMeshWordNet = new String[MeshWordnetNumber][2];
	public static String[][] datasetSnomedWordNet = new String[SnomedWordnetNumber][2];
	public static boolean loadMeshSnomed = false;
	public static boolean loadMeshWordnet = false;
	public static boolean loadSnomedWordnet = false;

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static boolean existInDataset(URI p1, URI p2, String MergedOnt) {
		switch (MergedOnt) {
		case "MeshSnomedCT":
			String A = p1.toString(), B = p2.toString();
			if (p1.getNamespace().toString().equals("http://www.nlm.nih.gov/mesh/"))
				A = "http://biograph/mesh/" + p1.getLocalName();
			if (p1.getNamespace().toString().equals("http://snomedct/"))
				A = "http://biograph/snomed-ct/" + p1.getLocalName();
			if (p2.getNamespace().toString().equals("http://www.nlm.nih.gov/mesh/"))
				B = "http://biograph/mesh/" + p2.getLocalName();
			if (p2.getNamespace().toString().equals("http://snomedct/"))
				B = "http://biograph/snomed-ct/" + p2.getLocalName();
			if (A != null && B != null) {
				for (int i = 0; i < MeshSnomedNumber; i++) {
					if (datasetMeshSnomed[i][0].toString().equals(A.toString())) {
						if (datasetMeshSnomed[i][1].toString().equals(B.toString())) {
							return true;
						}
					}

					if (datasetMeshSnomed[i][0].toString().equals(B.toString())) {
						if (datasetMeshSnomed[i][1].toString().equals(A.toString())) {
							return true;
						}
					}
				}

			}
			break;
		case "MeshWordNet":
			A = p1.toString();
			B = p2.toString();

			if (A != null && B != null) {
				for (int i = 0; i < MeshWordnetNumber; i++) {
					if (datasetMeshWordNet[i][0].toString().equals(A.toString())) {
						if (datasetMeshWordNet[i][1].toString().equals(B.toString())) {
							return true;
						}
					}

					if (datasetMeshWordNet[i][0].toString().equals(B.toString())) {
						if (datasetMeshWordNet[i][1].toString().equals(A.toString())) {
							return true;
						}
					}
				}

			}
			break;

		case "SnomedCTWordNet":
			A = p1.toString();
			B = p2.toString();

			if (A != null && B != null) {
				for (int i = 0; i < SnomedWordnetNumber; i++) {
					if (datasetSnomedWordNet[i][0].toString().equals(A.toString())) {
						if (datasetSnomedWordNet[i][1].toString().equals(B.toString())) {
							return true;
						}
					}

					if (datasetSnomedWordNet[i][0].toString().equals(B.toString())) {
						if (datasetSnomedWordNet[i][1].toString().equals(A.toString())) {
							return true;
						}
					}
				}

			}
			break;
		}

		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void CSVtoArray(String MergedOnt) throws URISyntaxException {

		switch (MergedOnt) {
		case "MeshSnomedCT":
			if (loadMeshSnomed == false || datasetMeshSnomed == null) {
				String csvFile1 = "PATH/TO/Snomed_Mesh_Mapping_file.csv";
				int i = 0;
				CSVReader reader1 = null;
				try {
					reader1 = new CSVReader(new FileReader(csvFile1));
					String[] line;
					while ((line = reader1.readNext()) != null) {
						String[] te = line[0].split("\t");
						datasetMeshSnomed[i][0] = te[0];
						datasetMeshSnomed[i][1] = te[1];
						i++;
					}

					// for Hli dataset:
					datasetMeshSnomed[i][0] = "http://biograph/snomed-ct/37356005";
					datasetMeshSnomed[i][1] = "http://biograph/mesh/D012640";
					i++;
					datasetMeshSnomed[i][0] = "http://biograph/mesh/D012640";
					datasetMeshSnomed[i][1] = "http://biograph/snomed-ct/37356005";
					i++;
					datasetMeshSnomed[i][0] = "http://biograph/snomed-ct/419241000";
					datasetMeshSnomed[i][1] = "http://biograph/mesh/D000900";
					i++;
					datasetMeshSnomed[i][0] = "http://biograph/mesh/D000900";
					datasetMeshSnomed[i][1] = "http://biograph/snomed-ct/419241000";
					i++;

					loadMeshSnomed = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		case "MeshWordNet":
			if (loadMeshWordnet == false || datasetMeshWordNet == null) {
				String csvFile2 = "PATH/TO/MeshWordnetMapping.csv";
				int j = 0;
				CSVReader reader2 = null;
				try {
					reader2 = new CSVReader(new FileReader(csvFile2));
					String[] line;
					while ((line = reader2.readNext()) != null) {
						datasetMeshWordNet[j][0] = line[1];
						datasetMeshWordNet[j][1] = line[2];
						j++;
					}
					loadMeshWordnet = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		case "SnomedCTWordNet":
			if (loadSnomedWordnet == false || datasetSnomedWordNet == null) {
				String csvFile3 = "PATH/TO/Snomed_Wordnet_Mapping_file.csv";
				int k = 0;
				CSVReader reader3 = null;
				try {
					reader3 = new CSVReader(new FileReader(csvFile3));
					String[] line;
					while ((line = reader3.readNext()) != null) {
						datasetSnomedWordNet[k][0] = line[1];
						datasetSnomedWordNet[k][1] = line[2];
						k++;
					}
					loadSnomedWordnet = true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		}

	}

	public static URI[][] FindBridge(URI c1, URI c2, Set<URI> aParent, Set<URI> bParent, String MergedOnt)
			throws URISyntaxException {
		URI[][] bridge = new URI[200][2];
		switch (MergedOnt) {
		case "MeshSnomedCT":
			CSVtoArray(MergedOnt);
			Iterator<URI> iterA = aParent.iterator();
			int i = 0;
			while (iterA.hasNext()) {
				URI p1 = iterA.next();
				if (!p1.getLocalName().toString().equals(c1.getLocalName().toString())
						&& !p1.getLocalName().toString().equals(c2.getLocalName().toString())) {
					Iterator<URI> iterB = bParent.iterator();
					while (iterB.hasNext()) {
						URI p2 = iterB.next();
						if (!p2.getLocalName().toString().equals(c1.getLocalName().toString())
								&& !p2.getLocalName().toString().equals(c2.getLocalName().toString())) {
							if (existInDataset(p1, p2, MergedOnt)) {
								bridge[i][0] = p1;
								bridge[i][1] = p2;
								i++;
							}
						}
					}
				}
			}
			break;
		case "MeshWordNet":
			CSVtoArray(MergedOnt);
			iterA = aParent.iterator();
			i = 0;
			while (iterA.hasNext()) {
				URI p1 = iterA.next();
				if (!p1.getLocalName().toString().equals(c1.getLocalName().toString())
						&& !p1.getLocalName().toString().equals(c2.getLocalName().toString())) {
					Iterator<URI> iterB = bParent.iterator();
					while (iterB.hasNext()) {
						URI p2 = iterB.next();
						if (!p2.getLocalName().toString().equals(c1.getLocalName().toString())
								&& !p2.getLocalName().toString().equals(c2.getLocalName().toString())) {
							if (existInDataset(p1, p2, MergedOnt)) {
								bridge[i][0] = p1;
								bridge[i][1] = p2;
								i++;
							}
						}
					}
				}
			}
			break;
		case "SnomedCTWordNet":
			CSVtoArray(MergedOnt);
			iterA = aParent.iterator();
			i = 0;
			while (iterA.hasNext()) {
				URI p1 = iterA.next();
				if (!p1.getLocalName().toString().equals(c1.getLocalName().toString())
						&& !p1.getLocalName().toString().equals(c2.getLocalName().toString())) {
					Iterator<URI> iterB = bParent.iterator();
					while (iterB.hasNext()) {
						URI p2 = iterB.next();
						if (!p2.getLocalName().toString().equals(c1.getLocalName().toString())
								&& !p2.getLocalName().toString().equals(c2.getLocalName().toString())) {
							if (existInDataset(p1, p2, MergedOnt)) {
								bridge[i][0] = p1;
								bridge[i][1] = p2;
								i++;
							}
						}
					}
				}
			}
			break;
		}

		return bridge;
	}

	public static URI FindPairInBridge(URI samiraMICA, URI[][] brigeArray) {
		URI pair = null;
		for (int i = 0; i < brigeArray.length; i++) {
			if (brigeArray[i][0] != null && brigeArray[i][0].toString().equals(samiraMICA.toString())) {
				return brigeArray[i][1];
			}
			if (brigeArray[i][1] != null && brigeArray[i][1].toString().equals(samiraMICA.toString())) {
				return brigeArray[i][0];
			}
		}
		return pair;
	}

	public static ArrayList<URI> findShareParent2(URI c1, URI c2, Set<URI> aParent, Set<URI> bParent, G g,
			String MergedOnt) throws SLIB_Exception, URISyntaxException {
		ArrayList<URI> shareParent = new ArrayList<URI>();

		switch (MergedOnt) {
		case "MeshSnomedCT":
			if (loadMeshSnomed == false || datasetMeshSnomed == null)
				CSVtoArray(MergedOnt);
			break;
		case "MeshWordNet":
			if (loadMeshWordnet == false || datasetMeshWordNet == null)
				CSVtoArray(MergedOnt);
			break;

		case "SnomedCTWordNet":
			if (loadSnomedWordnet == false || datasetSnomedWordNet == null)
				CSVtoArray(MergedOnt);
		}
		if (MergedOnt != null) {
			Iterator<URI> iterA = aParent.iterator();
			while (iterA.hasNext()) {
				URI p1 = iterA.next();
				if (!p1.getLocalName().toString().equals(c1.getLocalName().toString())
						&& !p1.getLocalName().toString().equals(c2.getLocalName().toString())) {
					Iterator<URI> iterB = bParent.iterator();
					while (iterB.hasNext()) {
						URI p2 = iterB.next();
						if (!p2.getLocalName().toString().equals(c1.getLocalName().toString())
								&& !p2.getLocalName().toString().equals(c2.getLocalName().toString())) {
							if (existInDataset(p1, p2, MergedOnt)) {
								if (SM_Engine.firstPriority == true) {
									shareParent.add(p2);
								} else {
									shareParent.add(p1);
								}
							}
						}
					}
				}
			}
		}
		return shareParent;

	}

}