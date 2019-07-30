package slib.sml.sm.core.engine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import slib.graph.algo.accessor.GraphAccessor;
import slib.graph.algo.accessor.InstanceAccessor;
import slib.graph.algo.accessor.InstanceAccessorTax;
import slib.graph.algo.extraction.rvf.AncestorEngine;
import slib.graph.algo.extraction.rvf.DescendantEngine;
import slib.graph.algo.extraction.rvf.RVF_DAG;
import slib.graph.algo.extraction.rvf.RVF_TAX;
import slib.graph.algo.metric.DepthAnalyserAG;
import slib.graph.algo.reduction.dag.GraphReduction_Transitive;
import slib.graph.algo.shortest_path.Dijkstra;
import slib.graph.algo.traversal.classical.DFS;
import slib.graph.algo.utils.GraphActionExecutor;
import slib.graph.algo.validator.dag.ValidatorDAG;
import slib.graph.model.graph.G;
import slib.graph.model.graph.elements.E;
import slib.graph.model.graph.utils.Direction;
import slib.graph.model.graph.utils.WalkConstraint;
import slib.graph.model.graph.weight.GWS;
import slib.graph.model.impl.graph.elements.Edge;
import slib.graph.model.impl.graph.memory.GraphMemory;
import slib.graph.model.impl.graph.weight.GWS_impl;
import slib.graph.model.impl.repo.URIFactoryMemory;
import slib.graph.model.repo.URIFactory;
import slib.graph.utils.WalkConstraintGeneric;
import slib.graph.utils.WalkConstraintUtils;
import slib.sml.sm.core.measures.Sim_Groupwise_Direct;
import slib.sml.sm.core.measures.Sim_Groupwise_Indirect;
import slib.sml.sm.core.measures.Sim_Pairwise;
import slib.sml.sm.core.measures.graph.pairwise.dag.edge_based.utils.SimDagEdgeUtils;
import slib.sml.sm.core.metrics.ic.annot.ICcorpus;
import slib.sml.sm.core.metrics.ic.pso.PSO;
import slib.sml.sm.core.metrics.ic.pso.dspoMICA;
import slib.sml.sm.core.metrics.ic.pso.findShareP;
import slib.sml.sm.core.metrics.ic.pso.parameters;
import slib.sml.sm.core.metrics.ic.topo.ICtopo;
import slib.sml.sm.core.metrics.ic.utils.IC_Conf_Corpus;
import slib.sml.sm.core.metrics.ic.utils.IC_Conf_Topo;
import slib.sml.sm.core.metrics.ic.utils.ICconf;
import slib.sml.sm.core.metrics.ic.utils.IcUtils;
import slib.sml.sm.core.metrics.vector.VectorWeight_Chabalier_2007;
import slib.sml.sm.core.utils.LCAFinder;
import slib.sml.sm.core.utils.LCAFinderImpl;
import slib.sml.sm.core.utils.SMconf;
import slib.sml.sm.core.utils.SMutils;
import slib.utils.ex.SLIB_Ex_Critic;
import slib.utils.ex.SLIB_Exception;
import slib.utils.impl.MatrixDouble;
import slib.utils.impl.SetUtils;

/**
 * Add these function on the main SM_Engine file of SML
 *
 * @author Samira Babalou (saimra.babalou@uni-jena.de)
 */
public class SM_Engine {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	final G graph;
	RVF_DAG topNodeAccessor;
	RVF_DAG bottomNodeAccessor;
	LCAFinder lcaFinder;
	Set<URI> classes;
	Set<URI> classesLeaves;
	InstanceAccessor instanceAccessor;
	URI root = null;
	SMProxResultStorage cache;
	boolean cachePairwiseResults = false;
	public static boolean firstPriority = false;
	Map<URI, Double> vectorWeights = null;
	final Map<SMconf, Sim_Pairwise> pairwiseMeasures = new ConcurrentHashMap();
	final Map<SMconf, Sim_Groupwise_Indirect> groupwiseAddOnMeasures = new ConcurrentHashMap();
	final Map<SMconf, Sim_Groupwise_Direct> groupwiseStandaloneMeasures = new ConcurrentHashMap();

	// Run Mesh and SNOMED together
	public SM_Engine(G gMesh, G gSnomed) throws SLIB_Ex_Critic {
		// For MeSH
		this.graph = gMesh;
		topNodeAccessor = new AncestorEngine(gMesh);
		bottomNodeAccessor = new DescendantEngine(gMesh);
		classes = GraphAccessor.getClasses(gMesh);
		int siz = classes.size();
		instanceAccessor = new InstanceAccessorTax(gMesh);

		// For SNOMED
		RVF_DAG topNodeAccessorTEMP = new AncestorEngine(gSnomed);
		DescendantEngine bottomNodeAccessorTEMP = new DescendantEngine(gSnomed);
		Set<URI> classesTEMP = GraphAccessor.getClasses(gSnomed);
		InstanceAccessorTax instanceAccessorTEMP = new InstanceAccessorTax(gSnomed);

		// join
		// 1-Join classes:
		classes.addAll(classesTEMP);
		siz = classes.size();

		// 2- join topNodeAccessor
		Map<String, RVF_DAG> paramAC = new HashMap<>();
		paramAC.put(gMesh.getURI().toString(), topNodeAccessor);
		paramAC.put(gSnomed.getURI().toString(), topNodeAccessorTEMP);
		topNodeAccessor = new CombinedAC(paramAC, gMesh);

		// 3-join bottomNodeAccessor
		Map<String, RVF_DAG> paramDC = new HashMap<>();
		paramDC.put(gMesh.getURI().toString(), bottomNodeAccessor);
		paramDC.put(gSnomed.getURI().toString(), bottomNodeAccessorTEMP);
		bottomNodeAccessor = new CombinedDC(paramDC, gMesh);

		// Run
		initEngine();
	}

	// Run Mesh and SNOMED together
	public SM_Engine(URI c1, URI c2, G gMesh, G gSnomed, String MergedOnt) throws SLIB_Ex_Critic, URISyntaxException {
		G g = createNewGraph(gMesh, gSnomed);
		this.graph = g;

		topNodeAccessor = new AncestorEngine(graph);
		bottomNodeAccessor = new DescendantEngine(graph);
		classes = GraphAccessor.getClasses(graph);
		instanceAccessor = new InstanceAccessorTax(graph);

		initEngine();

		boolean changes = mergeByBridge(c1, c2, getAncestorsInc(c1), getAncestorsInc(c2), MergedOnt);
		if (changes == true) {
			initEngine();
			topNodeAccessor = new AncestorEngine(this.graph);
			bottomNodeAccessor = new DescendantEngine(this.graph);
			classes = GraphAccessor.getClasses(this.graph);
		}

	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public G createNewGraph(G gMesh, G gSnomed) throws SLIB_Ex_Critic {
		URI predicate = URIFactoryMemory.getSingleton().getURI("http://www.w3.org/2000/01/rdf-schema#subClassOf");

		// Create new graph
		URIFactory factory = URIFactoryMemory.getSingleton();
		URI CombinedGraphURI = factory.getURI("http://combinedGraph/");
		G CombinedGraph = new GraphMemory(CombinedGraphURI);

		// add nodes+edges of gMesh and gSnomed to new graph
		Set<URI> VMesh = gMesh.getV();
		Set<URI> VSnomed = gSnomed.getV();
		Set<E> EMesh = gMesh.getE();
		Set<E> ESnomed = gSnomed.getE();

		CombinedGraph.addV(VMesh);
		CombinedGraph.addV(VSnomed);
		CombinedGraph.addE(EMesh);
		CombinedGraph.addE(ESnomed);

		Iterator<URI> roots1 = new ValidatorDAG().getTaxonomicRoots(gMesh).iterator();
		Iterator<URI> roots2 = new ValidatorDAG().getTaxonomicRoots(gSnomed).iterator();

		URI NewRoot = URIFactoryMemory.getSingleton().getURI("http://combinedGraph/NewRoot");
		CombinedGraph.addV(NewRoot);

		// Connecting root of two graph to the new root of combined graph
		while (roots1.hasNext()) {
			URI r = roots1.next();
			CombinedGraph.addE(r, predicate, NewRoot);
			// CombinedGraph.addE(NewRoot, predicate, r);
		}

		while (roots2.hasNext()) {
			URI r = roots2.next();
			CombinedGraph.addE(r, predicate, NewRoot);
			// CombinedGraph.addE(NewRoot, predicate, r);
		}

		return CombinedGraph;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public boolean mergeByBridge(URI c1, URI c2, Set<URI> aParents, Set<URI> bParents, String MergedOnt)
			throws SLIB_Ex_Critic, URISyntaxException {
		// This function merge ontology by exsiting bridges

		findShareP.CSVtoArray(MergedOnt);

		PSO.terminologicallShareParent = new ArrayList<URI>();
		parameters.ExtraSP = new ArrayList<URI>();

		boolean changes = false;
		URI predicate = URIFactoryMemory.getSingleton().getURI("http://www.w3.org/2000/01/rdf-schema#subClassOf");

		URI[][] bridge = findShareP.FindBridge(c1, c2, aParents, bParents, MergedOnt);

		for (int i = 0; i < bridge.length; i++) {
			URI nodeB1 = bridge[i][0];
			URI nodeB2 = bridge[i][1];

			if (nodeB1 != null && nodeB2 != null) {
				// System.out.println("A brdige for " + c1.getLocalName() + "
				// and " + c2.getLocalName() + " is " + nodeB1.getLocalName() +
				// " and " + nodeB2.getLocalName());
				if (firstPriority == true) {
					Iterator<E> iterInEdgeB = this.graph.getE(nodeB1, Direction.IN).iterator();
					while (iterInEdgeB.hasNext()) {
						E e = iterInEdgeB.next();
						this.graph.addE(e.getSource(), predicate, nodeB2);
						changes = true;
					}
					Iterator<E> iterOutEdgeB = this.graph.getE(nodeB1, Direction.OUT).iterator();
					while (iterOutEdgeB.hasNext()) {
						E e = iterOutEdgeB.next();
						this.graph.addE(nodeB2, predicate, e.getTarget());
						changes = true;
					}
					// System.out.println("I merged: "+ nodeB1.getLocalName() +
					// " into " + nodeB2.getLocalName());
					// classes = GraphAccessor.getClasses(this.graph);
					if (this.graph.containsVertex(nodeB1))
						this.graph.removeV(nodeB1);
					if (classes.contains(nodeB1))
						classes.remove(nodeB1);
					// System.out.println(nodeB1 +" is died");

					// nodeB1 and nodeB2 are share, so here we delete them, in
					// the next steps, we could not find it again, so now we add
					// it to share parents
					parameters.ExtraSP.add(nodeB2);
					PSO.terminologicallShareParent.add(nodeB2);
					// we delete node B1, so here we should add nodeB2 (because
					// nodeB1 does not exist)
					// System.out.println("I am adding a bridge as share parent
					// in the merge graph process: " + nodeB2 );
				} else {
					Iterator<E> iterInEdgeB = this.graph.getE(nodeB2, Direction.IN).iterator();
					while (iterInEdgeB.hasNext()) {
						E e = iterInEdgeB.next();
						this.graph.addE(e.getSource(), predicate, nodeB1);
						changes = true;
					}
					Iterator<E> iterOutEdgeB = this.graph.getE(nodeB2, Direction.OUT).iterator();
					while (iterOutEdgeB.hasNext()) {
						E e = iterOutEdgeB.next();
						this.graph.addE(nodeB1, predicate, e.getTarget());
						changes = true;
					}
					// System.out.println("I merged: "+ nodeB2.getLocalName() +
					// " into " + nodeB1.getLocalName());
					// classes = GraphAccessor.getClasses(this.graph);
					// System.out.println("classes'size: " + classes.size() + "
					// graph's node "+ graph.getNumberVertices());
					if (this.graph.containsVertex(nodeB2))
						this.graph.removeV(nodeB2);
					if (classes.contains(nodeB2))
						classes.remove(nodeB2);

					parameters.ExtraSP.add(nodeB1);
					PSO.terminologicallShareParent.add(nodeB1);
					// we delete node B2, so here we should add nodeB1 (because
					// nodeB1 does not exist)
					// System.out.println("I am adding a bridge as share parent
					// in the merge graph process: " + nodeB1 );
				}
			}
		}
		return changes;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Rewriting getIC_MICA function of main SML
	public ArrayList<Double> get_PSO_IC(ICconf icConf, URI a, URI b, SM_Engine eng, String MergedOnt)
			throws SLIB_Exception, URISyntaxException {
		throwErrorIfNotClass(a);
		throwErrorIfNotClass(b);

		if (cache.metrics_results.get(icConf) == null) {
			computeIC(icConf);
		}

		dspoMICA dd = new dspoMICA();
		ArrayList<URI> PSO_LCS = dd.PSOfunc(a, b, getAncestorsInc(a), getAncestorsInc(b), getDescendantsInc(a),
				getDescendantsInc(b), graph, eng, MergedOnt);
		ArrayList<Double> IC_PSO = new ArrayList<Double>();

		if (PSO_LCS == null) {
			return null;
		} else if (PSO_LCS.get(0).getLocalName().equals("1")) {
			IC_PSO.add(1.0);
			return IC_PSO;
		} else {

			for (int i = 0; i < PSO_LCS.size(); i++) {
				IC_PSO.add(cache.metrics_results.get(icConf).get(PSO_LCS.get(i)));
			}

			return IC_PSO;
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public Set<URI> getAncestors(URI v) throws SLIB_Ex_Critic {
		Set<URI> b = getAncestorsInc(v);

		Map<URI, Set<URI>> allAncestors = cache.ancestorsInc;

		Set<URI> a = allAncestors.get(v);
		return a;// b
	}

	/**
	 * Compute the pairwise semantic measures score considering the two vertices
	 * and the semantic measure configuration.
	 *
	 * @param pairwiseConf
	 *            the pairwise semantic measure configuration
	 * @param a
	 *            the first vertex/class/concept
	 * @param b
	 *            the second vertex/class/concept
	 * @return the pairwise semantic measure score
	 *
	 * @throws SLIB_Ex_Critic
	 */
	public double compare(SMconf pairwiseConf, URI a, URI b, String MergedOnt) throws SLIB_Ex_Critic {
		// Rewrite this function to run two graph
		throwErrorIfNotClass(a);
		throwErrorIfNotClass(b);

		double sim = -Double.MAX_VALUE;

		try {

			if (cachePairwiseResults && cache.pairwise_results.containsKey(pairwiseConf)
					&& cache.pairwise_results.get(pairwiseConf).containsKey(a)
					&& cache.pairwise_results.get(pairwiseConf).get(a).containsKey(b)) {

				sim = cache.pairwise_results.get(pairwiseConf).get(a).get(b);
			} else {

				Sim_Pairwise pMeasure;

				synchronized (pairwiseMeasures) {

					if (pairwiseMeasures.containsKey(pairwiseConf)) {
						pMeasure = pairwiseMeasures.get(pairwiseConf);
					} else {

						Class<?> cl;
						cl = Class.forName(pairwiseConf.getClassName());
						Constructor<?> co = cl.getConstructor();

						pMeasure = (Sim_Pairwise) co.newInstance();
						pairwiseMeasures.put(pairwiseConf, pMeasure);
					}
				}

				sim = pMeasure.compare(a, b, this, pairwiseConf, MergedOnt);

				if (Double.isNaN(sim) || Double.isInfinite(sim)) {
					SMutils.throwArithmeticCriticalException(pairwiseConf, a, b, sim);
				}

				// Caching
				if (cachePairwiseResults) {

					if (cache.pairwise_results.get(pairwiseConf) == null) {

						ConcurrentHashMap<URI, Map<URI, Double>> pairwise_result = new ConcurrentHashMap();

						cache.pairwise_results.put(pairwiseConf, pairwise_result);
					}

					if (cache.pairwise_results.get(pairwiseConf).get(a) == null) {
						cache.pairwise_results.get(pairwiseConf).put(a, new HashMap<URI, Double>());
					}

					cache.pairwise_results.get(pairwiseConf).get(a).put(b, sim);
				}
			}
		} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
				| NoSuchMethodException | SecurityException | InvocationTargetException | SLIB_Exception e) {
			throw new SLIB_Ex_Critic(e);
		}
		return sim;
	}
}
