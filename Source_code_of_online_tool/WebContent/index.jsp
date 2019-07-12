<%@include file="/Head.jsp"%>

<body>

	<div id="main">
		<div id="header">
			<div id="logo">
				<div id="logo_text">
					<h1>
						<a href="index.jsp">Sim<span class="logo_colour">Bio</span></a>
					</h1>
				</div>
			</div>
			<div id="menubar">
				<ul id="menu">
					<li class="selected"><a href="index.jsp">Home</a></li>
					<li><a href="${pageContext.request.contextPath}/sim.jsp">Similarity</a></li>
					<li><a
						href="${pageContext.request.contextPath}/comparison.jsp">Comparison</a></li>
					<li><a href="${pageContext.request.contextPath}/Contact.jsp">Contact
							Us</a></li>
				</ul>
			</div>
		</div>
		<div id="content_header"></div>
		<div id="site_content">
			<div id="content"
				style="padding: 40px 30px 30px 30px; line-height: 2; text-align: justify;">
				<font color="#8db53b" size="3"><b>SimBio </b></font> is a semantic
				similarity assessment by exploiting different knowledge resources to
				find the degree of proximity between your terms, where a numerical
				score quantifies this proximity as a function of the semantic
				evidence. <br> <br>Our embedded knowledge resources
				involve: <br>
				<ul>
					<li><font color="#323534"><b>MeSH</b></font> (Medical Subject
						Headings) as a domain-specific biomedical ontology provides around
						25,000 medical and biological concepts hierarchically classified
						in 16 categories. For more information, please visit <a
						href="https://meshb.nlm.nih.gov/search">MeSH</a></li>
					<li><font color="#323534"><b>SNOMED-CT</b></font>
						(Systematised Nomenclature of Medicine, Clinical Terms) as a
						domain-specific biomedical ontology which covers more than 360,000
						medical concepts classified in 18 partially overlapping
						hierarchies. For more information, please visit <a
						href="https://uts.nlm.nih.gov/snomedctBrowser.html">SNOMED-CT</a></li>
					<li><font color="#323534"><b>WordNet</b></font> as a general
						purpose repository which covers 100,000 general concepts. For more
						information, please visit <a href="https://wordnet.princeton.edu/">WordNet</a></li>
					<li><font color="#323534"><b>Your desired knowledge
						</b></font>: Our tool is bale to process your desired ontology as well as the
						background knowledge.</li>
				</ul>
				<br> IC (Information Content) measures quantify the amount of
				information provided by a given term based on its probability of
				appearance in a knowledge resource. With this tool, we involve three
				IC-based similarity measures and recast this issue as an
				optimization problem by using the PSO (particle swarm optimization)
				algorithm as one of most capable optimization approaches. <br>
				In comparison tab, we calculate the similarity of your terms based
				on all these existing criteria. <br> <a
					href="https://link.springer.com/chapter/10.1007/978-3-319-69459-7_11">
					Read more ...</a>

			</div>
		</div>
	</div>
	<%@include file="/Foot.jsp"%>