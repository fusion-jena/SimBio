# SimBio
Semantic Similarity Assessment for Biomedical Terms 


SimBio is a semantic similarity assessment by exploiting different knowledge resources to find the degree of proximity between your terms, where a numerical score quantifies this proximity as a function of the semantic evidence.  Please visit http://simbio.uni-jena.de/

The implemented similarity approach in SimBio is based on ''Particle Swarm Optimization (PSO) method’’. Please see our [publication]( https://link.springer.com/chapter/10.1007/978-3-319-69459-7_11). 

It has been implemented on top of SML library as one package (slib.sml.sm.core.metrics.ic.pso) . We use the implementation of Semantic Measures Library available in http://www.semantic-measures-library.org/sml/.

In this repository, the code of online tool, the code of PSO package, and the manual mapping between WordNet and SNOMED-CT|MeSH is available. 
It requires that SNOMED-CT, MeSH, and WordNet source files from their respective resources will be downloaded and embed into the code. 
