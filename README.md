# SimBio: Semantic Similarity Assessment for Biomedical Terms 
SimBio is a semantic similarity assessment method by exploiting different knowledge resources to find the degree of proximity between your terms, where a numerical score quantifies this proximity as a function of the semantic evidence.  


Our similarity method is based on ''Particle Swarm Optimization (PSO) algorithm’’. Please see our [publication]( https://link.springer.com/chapter/10.1007/978-3-319-69459-7_11).

The tool is freely available at http://simbio.uni-jena.de/

This repository includes:
	* Source code of the online tool
	* PSO method implementation
	* The manual mapping between WordNet and SNOMED-CT|MeSH


We have implemented our method based on Semantic Measures Library available in http://www.semantic-measures-library.org/sml/.

# Getting Started

To run our system:
	* Download the SML 
	* Add our `PSO_package`
	* Make the modification from `SML_modification` folder


## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details