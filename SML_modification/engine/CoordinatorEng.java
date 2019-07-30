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

import slib.graph.algo.accessor.GraphAccessor;
import slib.graph.algo.accessor.InstanceAccessor;
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

public class CoordinatorEng  {
	public static G MergedGraph;
	public static boolean  mergedload ;
	public static RVF_DAG topNodeAccessor ;
	public static RVF_DAG bottomNodeAccessor ;
	public static Set<URI> classes ;
	public static InstanceAccessor instanceAccessor ;
}
