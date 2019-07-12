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
							Us </a></li>
				</ul>
			</div>
		</div>
		<div id="content_header"></div>
		<div id="site_content">
			<div id="content">
				<%@include file="/comp.jsp"%>
			</div>
		</div>
	</div>
	<%@include file="/Foot.jsp"%>
	<script>
		jQuery.validator.setDefaults({
			debug : true,
			success : "valid"
		});
	</script>
	<script>
		function check() {
			if (document.getElementById("YourFile").checked === true) {
				var chatinput = document.getElementById("OntPath").value;
				if (chatinput == "" || chatinput.length == 0
						|| chatinput == null) {
					var te2 = new String("${fileC}").valueOf();
					if (te2 === "") {
						alert("Please Enter Your File");
						return (false);
					}
				}
				return (true);
			}
		}

		function change(obj) {
			if (document.getElementById('YourFile').checked === true) {
				document.getElementById('OntPath').disabled = false;
				var te = new String("${fileC}").valueOf();
				if (te === "") {
					document.getElementById("msgOldFile").style.display = "none";
				} else {
					document.getElementById("msgOldFile").style.display = "block";
				}
			} else {
				document.getElementById('OntPath').disabled = true;
				document.getElementById("msgOldFile").style.display = "none";
			}

			if (document.getElementById('defaultSetting').checked === false) {
				document.getElementById("HisIter").style.display = "block";
				document.getElementById("fitThreshold").style.display = "block";
				document.getElementById("betaWight").style.display = "block";
				document.getElementById("landaWeight").style.display = "block";
				document.getElementById("HisIterLabel").style.display = "block";
				document.getElementById("fitThresholdLabel").style.display = "block";
				document.getElementById("betaWightLabel").style.display = "block";
				document.getElementById("landaWeightLabel").style.display = "block";
				document.getElementById("HisIter").value = "${histV}";
				document.getElementById("fitThreshold").value = "${fitV}";
				document.getElementById("betaWight").value = "${betaV}";
				document.getElementById("landaWeight").value = "${landaV}";
			} else {
				document.getElementById("HisIter").style.display = "none";
				document.getElementById("fitThreshold").style.display = "none";
				document.getElementById("betaWight").style.display = "none";
				document.getElementById("landaWeight").style.display = "none";
				document.getElementById("HisIterLabel").style.display = "none";
				document.getElementById("fitThresholdLabel").style.display = "none";
				document.getElementById("betaWightLabel").style.display = "none";
				document.getElementById("landaWeightLabel").style.display = "none";
			}
			if ("${histV}" === "")
				document.getElementById('HisIter').value = 10;
			if ("${fitV}" === "")
				document.getElementById('fitThreshold').value = 0.7;
			if ("${betaV}" === "")
				document.getElementById('betaWight').value = 0.4;
			if ("${landaV}" === "")
				document.getElementById('landaWeight').value = 0.5;
		}

		function showLoading() {
			document.getElementById('loadingmsg').style.display = 'block';
			document.getElementById('loadingover').style.display = 'block';
		}

		window.onload = function() {

			var te = new String("${fileC}").valueOf();
			if (te === "") {
				document.getElementById("msgOldFile").style.display = "none";
			} else {
				document.getElementById("msgOldFile").style.display = "block";
			}

			if ("${defCheck}" == "true" || "${defCheck}" === "") {
				document.getElementById('defaultSetting').checked = true;
			} else {
				document.getElementById('defaultSetting').checked = false;
			}

			if ("${MeshC}" == "true" || "${MeshC}" === "") {
				document.getElementById('MeshCheck').checked = true;
			} else {
				document.getElementById('MeshCheck').checked = false;
			}

			if ("${SnomedC}" == "true" || "${SnomedC}" === "") {
				document.getElementById('SnomedCheck').checked = true;
			} else {
				document.getElementById('SnomedCheck').checked = false;
			}

			if ("${WordnetC}" == "true" || "${WordnetC}" === "") {
				document.getElementById('WordnetCheck').checked = true;
			} else {
				document.getElementById('WordnetCheck').checked = false;
			}

			if ("${YourfileC}" == "true") {
				document.getElementById('YourFile').checked = true;
				document.getElementById('OntPath').disabled = false;
			} else {
				document.getElementById('YourFile').checked = false;
				document.getElementById('OntPath').disabled = true;
			}

			if ("${LinC}" == "true" || "${LinC}" === "") {
				document.getElementById('LinCheck').checked = true;
			} else {
				document.getElementById('LinCheck').checked = false;
			}

			if ("${ResnikC}" == "true" || "${ResnikC}" === "") {
				document.getElementById('ResnikCheck').checked = true;
			} else {
				document.getElementById('ResnikCheck').checked = false;
			}

			if ("${JCC}" == "true" || "${JCC}" === "") {
				document.getElementById('JCCheck').checked = true;
			} else {
				document.getElementById('JCCheck').checked = false;
			}

			if (document.getElementById('YourFile').checked === true) {
				document.getElementById('OntPath').disabled = false;
			} else {
				document.getElementById('OntPath').disabled = true;
			}

			if (document.getElementById('defaultSetting').checked === false) {
				document.getElementById("HisIter").style.display = "block";
				document.getElementById("fitThreshold").style.display = "block";
				document.getElementById("betaWight").style.display = "block";
				document.getElementById("landaWeight").style.display = "block";
				document.getElementById("HisIterLabel").style.display = "block";
				document.getElementById("fitThresholdLabel").style.display = "block";
				document.getElementById("betaWightLabel").style.display = "block";
				document.getElementById("landaWeightLabel").style.display = "block";
				document.getElementById("HisIter").value = "${histV}";
				document.getElementById("fitThreshold").value = "${fitV}";
				document.getElementById("betaWight").value = "${betaV}";
				document.getElementById("landaWeight").value = "${landaV}";
			} else {
				document.getElementById("HisIter").style.display = "none";
				document.getElementById("fitThreshold").style.display = "none";
				document.getElementById("betaWight").style.display = "none";
				document.getElementById("landaWeight").style.display = "none";
				document.getElementById("HisIterLabel").style.display = "none";
				document.getElementById("fitThresholdLabel").style.display = "none";
				document.getElementById("betaWightLabel").style.display = "none";
				document.getElementById("landaWeightLabel").style.display = "none";
			}

			if ("${histV}" === "")
				document.getElementById('HisIter').value = 10;
			if ("${fitV}" === "")
				document.getElementById('fitThreshold').value = 0.7;
			if ("${betaV}" === "")
				document.getElementById('betaWight').value = 0.4;
			if ("${landaV}" === "")
				document.getElementById('landaWeight').value = 0.5;

		}
	</script>