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
					<li class="selected"><a
						href="${pageContext.request.contextPath}/comparison.jsp">Comparison</a></li>
					<li><a href="${pageContext.request.contextPath}/Contact.jsp">Contact
							Us</a></li>
				</ul>
			</div>
		</div>
		<div id="content_header"></div>
		<div id="site_content">
			<div id="content">
				<%@include file="/compResult.jsp"%>
			</div>

		</div>
	</div>
	<%@include file="/Foot.jsp"%>