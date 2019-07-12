<%@include file="/Head.jsp"%>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/sim.js"></script>
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
					<li class="selected"><a
						href="${pageContext.request.contextPath}/sim.jsp">Similarity</a></li>
					<li><a
						href="${pageContext.request.contextPath}/comparison.jsp">Comparison</a></li>
					<li><a href="${pageContext.request.contextPath}/Contact.jsp">Contact
							Us</a></li>
				</ul>
			</div>
		</div>
		<div id="content_header"></div>
		<div id="site_content">
			<div id="content">
				<%@include file="/Similarity.jsp"%>
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
			if (document.getElementById("Term1Source").value === 'YourFile') {
				var chatinput = document.getElementById("Term1OntPath").value;
				if (chatinput == "" || chatinput.length == 0
						|| chatinput == null) {
					var te = new String("${fileS1}").valueOf();
					if (te === "") {
						alert("Please Enter Your File for Term1");
						return (false);
					}
				}
				return (true);
			}
			if (document.getElementById("Term2Source").value === 'YourFile') {
				var chatinput = document.getElementById("Term2OntPath").value;
				if (chatinput == "" || chatinput.length == 0
						|| chatinput == null) {
					var te2 = new String("${fileS2}").valueOf();
					if (te2 === "") {
						alert("Please Enter Your File for Term2");
						return (false);
					}
				}
				return (true);
			}
		}

		function change(obj) {
			var selectBox = obj;
			if (selectBox.id == "Term2Source") {
				var selected = selectBox.options[selectBox.selectedIndex].value;
				var brw2 = document.getElementById("Term2OntPath");

				if (selected === 'YourFile') {
					brw2.style.display = "block";
					var te = new String("${fileS2}").valueOf();
					if (te === "") {
						document.getElementById("msgOldFile2").style.display = "none";
					} else {
						document.getElementById("msgOldFile2").style.display = "block";
					}
				} else {
					brw2.style.display = "none";
					document.getElementById("msgOldFile2").style.display = "none";
				}
			}
			if (selectBox.id == "Term1Source") {
				var selected = selectBox.options[selectBox.selectedIndex].value;
				var brw1 = document.getElementById("Term1OntPath");

				if (selected === 'YourFile') {
					brw1.style.display = "block";
					var te2 = new String("${fileS1}").valueOf();
					if (te2 === "") {
						document.getElementById("msgOldFile1").style.display = "none";
					} else {
						document.getElementById("msgOldFile1").style.display = "block";
					}
				} else {
					brw1.style.display = "none";
					document.getElementById("msgOldFile1").style.display = "none";
				}
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
			var te = new String("${fileS1}").valueOf();
			if (te === "") {
				document.getElementById("msgOldFile1").style.display = "none";
			} else {
				document.getElementById("msgOldFile1").style.display = "block";
			}
			var te2 = new String("${fileS2}").valueOf();
			if (te2 === "") {
				document.getElementById("msgOldFile2").style.display = "none";
			} else {
				document.getElementById("msgOldFile2").style.display = "block";
			}

			if ("${YourfileSel1}" === "Selected") {
				document.getElementById("Term1OntPath").style.display = "block";
			} else {
				document.getElementById("Term1OntPath").style.display = "none";
			}

			if ("${YourfileSel2}" === "Selected") {
				document.getElementById("Term2OntPath").style.display = "block";
			} else {
				document.getElementById("Term2OntPath").style.display = "none";
			}

			if ("${defCheck}" == "true" || "${defCheck}" === "") {
				document.getElementById('defaultSetting').checked = true;
			} else {
				document.getElementById('defaultSetting').checked = false;
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