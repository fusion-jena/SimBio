<%@include file="/Head.jsp"%>

<body>

	<div id="main">
		<div id="header">
			<div id="logo">
				<div id="logo_text">
					<h1>
						<a href="${pageContext.request.contextPath}/index.jsp">Sim<span
							class="logo_colour">Bio</span></a>
					</h1>
				</div>
			</div>
			<div id="menubar">
				<ul id="menu">
					<li><a href="${pageContext.request.contextPath}/index.jsp">Home</a></li>
					<li><a href="${pageContext.request.contextPath}/sim.jsp">Similarity</a></li>
					<li><a href="${pageContext.request.contextPath}/comparison.jsp">Comparison</a></li>
					<li class="selected"><a
						href="${pageContext.request.contextPath}/Contact.jsp">Contact
							Us</a></li>
				</ul>
			</div>
		</div>
		<div id="content_header"></div>
		<div id="site_content">
			<div id="content">
				<div class="inner">
					<form id="FormSim" method="post" action="/SimBio/ContactServlet"
						enctype="multipart/form-data">
						<table>
							<tr>
								<td style="width: 100%; text-align: justify;"><h2>Impressum:</h2>
									<br> By using the services of our website ''SimBio'',
									we do not keep any personal data from you. Only for the
									processing of your ontology files, we need to store them
									temporarily, but they get deleted afterwards immediately. This
									website is provided by FUSION group members, who belong to
									Heinz-Nixdorf Chair for Distributed Information Systems,
									Friedrich Schiller University of Jena, Institute of Computer
									Science. Please visit our <a
									href="http://fusion.cs.uni-jena.de/"> website </a> for more
									detail about our projects and activities. <br> We are
									neither obligated nor willing to participate in a dispute
									settlement procedure before a consumer arbitration board. <br>
									EU Commission online dispute resolution platform: <a
									href="https://ec.europa.eu/consumers/odr">https://ec.europa.eu/consumers/odr</a>
								</td>
							</tr>
							<tr>
								<td>
									<h2>Get in touch</h2>
									<p>Write your feedback for us...</p>
									<ul>
										<li><h2>Email</h2> <a href="#">simbiotool[-at-]gmail[-dot-]com</a></li>
										<li><h2>Address</h2> Friedrich-Schiller-University Jena <br />
											Institute of Computer Science <br /> Heinz-Nixdorf-Professur
											for Distributed Information Systems <br /> Ernst-Abbe-Platz
											1-4 <br /> D-07743 Jena <br /></li>
									</ul>
								</td>
							</tr>
							<tr>
								<td><h2>Publication</h2>
									<ul>
										<li><b>A particle swarm-based approach for semantic
												similarity computation.</b> Samira Babalou, Alsayed Algergawy,
											Birgitta König-Ries. In OTM Confederated International
											Conferences On the Move to Meaningful Internet Systems.
											Springer, Cham, 2017.</li>
									</ul></td>
							</tr>
						</table>
				</div>
			</div>
		</div>
	</div>
	<%@include file="/Foot.jsp"%>