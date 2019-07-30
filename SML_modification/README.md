# How to make the modification: 
After importing SML and PSO_package in your project, please follow below instruction:

PSO method should be called from every desire similarity method in SML. 
For instance, to calculate IC_Lin, please make the below changes in class *Sim_pairwise_DAG_node_Lin_1998* of package *slib.sml.sm.core.measures.graph.pairwise.dag.node_based*:

```
public double compare(URI a, URI b, SM_Engine eng, SMconf conf, String MergedOnt) throws SLIB_Exception {
  	Boolean MergingOntology = null;
	/* set to null since we are running one file, so do not need to merged onologies */
	ArrayList<Double> ic_LCS = null;
	ic_LCS = eng.get_PSO_IC(conf.getICconf(), a, b, eng, MergingOntology);
	double ic_a = eng.getIC(conf.getICconf(), a);
	double ic_b = eng.getIC(conf.getICconf(), b);
	double sum = 0;
	for (int i = 0; i < ic_LCS.size(); i++) {
		sum = sum + ic_LCS.get(i);
	}
	return (double) (sum / ic_LCS.size());
}
```

Indeed, to calculate the similarity of `a` and `b`, we get IC value of `a` and `b` by `ic_a` and `ic_b` from SML package. For the set of the parents of `a` and `b`, i.e. `LCS`, we run our PSO approach to (i) select a set of optimal parents for `a` and `b`. (ii) get IC of the LCS set, and do average value on them. 
Note that `eng.get_PSO_IC` is our extra function in `SMEngine` from SML. Please modify the engine of SML based on existing files in **engine** folder.

For running a single graph (MeSH or SNOMED-CT), we used the respective files in SML.
For running two resources together, we add our own function inside SMLEngine.java of SML to merged their respective graphes. Please refer to **multiOntologies** folder.
Before running them, please make the suggested changes (from **engine** folder) into SML’s engine.


