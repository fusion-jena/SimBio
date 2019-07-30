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
import slib.graph.algo.extraction.rvf.DescendantEngine;
import slib.graph.algo.extraction.rvf.RVF_DAG;
import slib.graph.model.graph.G;
import slib.graph.model.graph.elements.E;
import slib.graph.model.graph.utils.Direction;
import slib.graph.model.graph.utils.WalkConstraint;
import slib.graph.utils.WalkConstraintGeneric;
import slib.graph.utils.WalkConstraintUtils;
import slib.utils.ex.SLIB_Ex_Critic;
import slib.utils.impl.SetUtils;

public class CombinedDC extends DescendantEngine {

	private Map<String, RVF_DAG> map;

	public CombinedDC( Map<String, RVF_DAG> map, G g ) {
		super(null);
		this.map = map;
	}
    public CombinedDC(G g, WalkConstraint wc) {
        super(g);
    }

    public Set<URI> getDescendantsExc(URI v) {
      	String ns = v.getNamespace();
      	DescendantEngine ae = (DescendantEngine)this.map.get( ns );
        return ae.getDescendantsExc(v); 
    }

    public Set<URI> getDescendantsInc(URI v) {       
        String ns = v.getNamespace();
        DescendantEngine ae = (DescendantEngine)this.map.get( ns );
        return ae.getDescendantsInc(v); 
    }

    public Map<URI, Set<URI>> getAllDescendantsExc() throws SLIB_Ex_Critic {
       	Map<URI, Set<URI>> result = new HashMap<>();
    	for( RVF_DAG iter : this.map.values() ) {
    		result.putAll( ((DescendantEngine)iter).getAllDescendantsExc() );
    	}
        return result; 
    }
    
    public Map<URI, Set<URI>> getAllDescendantsInc() throws SLIB_Ex_Critic {
    	Map<URI, Set<URI>> result = new HashMap<>();
    	for( RVF_DAG iter : this.map.values() ) {
    		result.putAll( ((DescendantEngine)iter).getAllDescendantsInc() );
    	}
        return result; 
    }

  //***********************************************************************************************************Function from RVF-DAG
    public Map<URI, Set<URI>> getAllRV() throws SLIB_Ex_Critic {
    	Map<URI, Set<URI>> result = new HashMap<>();
    	for( RVF_DAG iter : this.map.values() ) {
    		result.putAll( iter.getAllRV() );
    	}
        return result; 
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
    	}
        return result;  
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
     		DescendantEngine ae = (DescendantEngine)this.map.get( ns );
     		result.putAll( ae.propagateNbOccurences( inputs.get(ns) ) ); 
     	}
        return result; 
    }
    
    public WalkConstraint getWalkConstraint( URI uri ) {
    	String ns = uri.getNamespace();
    	DescendantEngine de = (DescendantEngine)this.map.get( ns );
    	if( de == null ) {
    		de = (DescendantEngine)this.map.get( "http://www.nlm.nih.gov/mesh/" );
    	}
    	return de.getWalkConstraint();
    }
}
