package slib.sml.sm.core.engine;
/**
 * A particle swarm-based approach for semantic similarity computation
 * Publication: Babalou, Samira, Alsayed Algergawy, and Birgitta König-Ries. "A particle swarm-based approach for semantic similarity computation." OTM Confederated International Conferences" On the Move to Meaningful Internet Systems". Springer, Cham, 2017. 
 * @author Samira Babalou (samira[dot]babalou[at]uni_jean[dot]de)
 */
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.URI;

import slib.graph.algo.extraction.rvf.AncestorEngine;
import slib.graph.algo.extraction.rvf.RVF_DAG;
import slib.graph.model.graph.G;
import slib.graph.model.graph.elements.E;
import slib.graph.model.graph.utils.Direction;
import slib.graph.model.graph.utils.WalkConstraint;
import slib.graph.utils.WalkConstraintGeneric;
import slib.graph.utils.WalkConstraintUtils;
import slib.utils.ex.SLIB_Ex_Critic;
import slib.utils.impl.SetUtils;

public class CombinedAC extends RVF_DAG {

	private Map<String, RVF_DAG> map;

	public CombinedAC( Map<String, RVF_DAG> map, G g ) {
		super(null,null);
		this.map = map;
	}
    public CombinedAC(G g, WalkConstraint wc) {
        super(g, wc);
    }

    public Set<URI> getNeighbors(URI v) {
    	String ns = v.getNamespace();
    	AncestorEngine ae = (AncestorEngine)this.map.get( ns );//sirko
        return ae.getNeighbors(v);
    }
    
    public Set<URI> getAncestorsExc(URI v) {
    	String ns = v.getNamespace();
    	AncestorEngine ae = (AncestorEngine)this.map.get( ns );//sirko
        return ae.getAncestorsExc(v); //return getRV(v);
    }
    
    public Map<URI, Set<URI>> getAllAncestorsExc() throws SLIB_Ex_Critic {
    	Map<URI, Set<URI>> result = new HashMap<>();
    	for( RVF_DAG iter : this.map.values() ) {
    		result.putAll( ((AncestorEngine)iter).getAllAncestorsExc() );
    	}
        return result; //return getAllRV();
    }
    
    public Map<URI, Set<URI>> getAllAncestorsInc() throws SLIB_Ex_Critic {

    	Map<URI, Set<URI>> result = new HashMap<>();
    	for( RVF_DAG iter : this.map.values() ) {//sirko
    		result.putAll( ((AncestorEngine)iter).getAllAncestorsInc() );
    	}
        return result; //   return getAllRV();
    }

    /**
     * Compute the set of inclusive ancestors of a class. The focused vertex
     * will be included in the set of ancestors.
     *
     * @param v the vertex of interest
     * @return the set composed of the ancestors of the concept + the concept.
     */
    public Set<URI> getAncestorsInc(URI v) {
        String ns = v.getNamespace();
        AncestorEngine ae = (AncestorEngine)this.map.get( ns );
        return ae.getAncestorsInc(v);
    }
    
    
//***********************************************************************************************************Function from RVF-DAG
    public Map<URI, Set<URI>> getAllRV() throws SLIB_Ex_Critic {
    	Map<URI, Set<URI>> result = new HashMap<>();
    	for( RVF_DAG iter : this.map.values() ) {
    		result.putAll( iter.getAllRV() );
//    		result.putAll( allVertices ); //samira
    	}
        return result; //return allVertices; 
    }

    /**
     * Compute the set of reachable vertices for each vertices contained in the
     * graph according to the specified constraint associated to the instance in
     * use. Inclusive process, i.e. the process do consider that vertex v is
     * contained in the set of reachable vertices from v.
     *
     * Optimized through a topological ordering
     *
     * @return an Map key V value the set of vertices reachable from the key
     * @throws SLIB_Ex_Critic
     */
    public Map<URI, Set<URI>> getAllRVInc() throws SLIB_Ex_Critic {
    	Map<URI, Set<URI>> result = new HashMap<>();
    	for( RVF_DAG iter : this.map.values() ) {
    		result.putAll( iter.getAllRVInc() );
//    		result.putAll(allRVEx);
    	}
        return result; //return allRVEx; 
    }

    /**
     * Return the set of terminal vertices (leaves) reachable. Only the nodes
     * which are involved in a relationships which is accepted in the global
     * configuration will be evaluated. Self-loop are not considered. Therefore
     * if p is an accepted predicate if a node i is only involved in a
     * relationship i p i, it will be considered has a leave.
     *
     * It is important to stress that only nodes involved in the considered
     * relationships will be considered. Therefore if the RVF is set to
     * rdfs:subClassOf out all nodes which are not associated to an
     * rdfs:subClassOf relationship will not be processed (even if they are
     * associated to rdf:type relationships). This is important for instance if
     * you use such a method to find all the leaves which are subsumed by a
     * specific class. In this case if the DAF is not rooted you can have
     * isolated class (which does not have ancestors or descendants) which are
     * not considered in the results provided by this method.
     *
     * @return the leaves for each vertices
     */
    public Map<URI, Set<URI>> getTerminalVertices() {
        Map<URI, Set<URI>> result = new HashMap<>();
    	for( RVF_DAG iter : this.map.values() ) {
    		result.putAll( iter.getTerminalVertices() ); 
    	}
        return result ; //return allReachableLeaves;
    }

    /**
     *
     * @return @throws SLIB_Ex_Critic
     */
    public Map<URI, Integer> computeNbPathLeadingToAllVertices() throws SLIB_Ex_Critic {
        Map<URI, Integer> result = new HashMap<>();
    	for( RVF_DAG iter : this.map.values() ) {
    		result.putAll( iter.computeNbPathLeadingToAllVertices() ); 
    	}
        return result; //return propagateNbOccurences(allVertices);
    }

    /**
     * Method used to compute the number of occurrences associated to each
     * vertex after the propagation of the given number of occurences. The
     * occurrence number are propagated considering a walk defined by the
     * relationships loaded. A number of occurrences is associated to each
     * vertices of the graph through the given inputs. The occurrences number
     * are then summed considering walks starting from the terminal vertices.
     *
     * @param nbOccurrence ResultStack of type Double representing the number of
     * occurrences of each vertices
     * @return ResultStack of type Double representing the number occurrences
     * propagated of each vertices
     * @throws SLIB_Ex_Critic
     */
    public Map<URI, Integer> propagateNbOccurences(Map<URI, Integer> nbOccurrence) throws SLIB_Ex_Critic {
    	
    	// split input by namespace
    	Map<String, Map<URI, Integer>> inputs = new HashMap<>();
    	for( URI key : nbOccurrence.keySet() ) {
    		
    		// get namespace
    		String ns = key.getNamespace();
    		
    		// make sure there is an entry
    		if( !inputs.containsKey(ns) ){
    			inputs.put( ns, new HashMap<URI,Integer>() );
    		}
    		
    		// get the entry
    		Map<URI,Integer> entry = inputs.get( ns );
    		
    		// add to entry
    		entry.put( key, nbOccurrence.get(key) );
    		
    	}
    	
        Map<URI, Integer> result = new HashMap<>();
     	for( String ns : inputs.keySet() ) {
     		// get matching class
     		AncestorEngine ae = (AncestorEngine)this.map.get( ns );
     		result.putAll( ae.propagateNbOccurences( inputs.get(ns) ) ); 
     	}
        return result; //return nbOcc_prop;
    }

//***************************************************************************************************From WalkConstraintUtils
//    public static WalkConstraint getInverse(WalkConstraint wc, boolean includeBOTHpredicate) {
//
//        Map<URI, Direction> oppositeAcceptedWalks = new HashMap<URI, Direction>();
//
//        for (URI e : wc.getAcceptedWalks_DIR_IN()) {
//            oppositeAcceptedWalks.put(e, Direction.OUT);
//        }
//
//        for (URI e : wc.getAcceptedWalks_DIR_OUT()) {
//            oppositeAcceptedWalks.put(e, Direction.IN);
//        }
//
//        if (includeBOTHpredicate) {
//            for (URI e : wc.getAcceptedWalks_DIR_BOTH()) {
//                oppositeAcceptedWalks.put(e, Direction.BOTH);
//            }
//        }
//        Map<URI, WalkConstraint> result = new HashMap<>(); //WalkConstraintGeneric();
//     	for( RVF_DAG iter : map.values() ) {
//     		result.put((URI)wc, new WalkConstraintGeneric(oppositeAcceptedWalks) ); 
//     	}
//         return result.get(wc); //return new WalkConstraintGeneric(oppositeAcceptedWalks);
//    }
//
//    public static WalkConstraint copy(WalkConstraint walkConstraint) {
//
//        WalkConstraintGeneric newwc = new WalkConstraintGeneric();
//        newwc.addAcceptedTraversal(walkConstraint.getAcceptedWalks_DIR_IN(), Direction.IN);
//        newwc.addAcceptedTraversal(walkConstraint.getAcceptedWalks_DIR_OUT(), Direction.OUT);
//        newwc.addAcceptedTraversal(walkConstraint.getAcceptedWalks_DIR_BOTH(), Direction.BOTH);
//        
//        Map<URI, WalkConstraint> result = new HashMap<>();
//     	for( RVF_DAG iter : map.values() ) {
//     		result.put((URI)walkConstraint , newwc ); 
//     	}
//         return result.get(newwc); // return newwc;
//
//    }


}
