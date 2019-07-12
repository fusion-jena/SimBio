<%@page import="net.tanesha.recaptcha.*"%>

<div class="inner">

	<form method="post"
		action="${pageContext.request.contextPath}/CompServlet"
		enctype="multipart/form-data" onsubmit='showLoading();'>
		<div>
			<table style="width: 90%">
				<tr>
					<td
						style="border: 1px solid #869a34; border-radius: 15px; width: 70%;">
						<h2>Input</h2>
						<table style="width: 60%;">
							<tr>
								<td><span title="Enter your term">Term1</span></td>
								<td><input name="Term1" id="Term1" type="text"
									value="${Term1}" title="Enter your term" required></td>
								<td><span title="Enter your term">Term2</span></td>
								<td><input name="Term2" id="Term2" type="text"
									value="${Term2}" title="Enter your term" required></td>
							</tr>
						</table>


						<h2>Arguments</h2>
						<h3 style="padding: 5px 0 0 15px;">Sources:</h3>
						<table style="width: 70%;">
							<tr>
								<td><input type="checkbox" checked="${MeshC}"
									id="MeshCheck" name="MeshCheck"> MeSH</td>
								<td><input type="checkbox" checked="${SnomedC}"
									id="SnomedCheck" name="SnomedCheck"> SNOMED-CT</td>
								<td><input type="checkbox" checked="${WordnetC}"
									id="WordnetCheck" name="WordnetCheck"> WordNet</td>
								<td><input id="YourFile" name="YourFile" type="checkbox"
									onchange="change(this)" checked="${YourfileC}"> Your
									File</td>
								<td><input id="OntPath" name="OntPath" type="file"
									accept=".owl,.rdf" disabled>
									<h5>
										<label id="msgOldFile">Using your last uploaded file
											if you do not enter a new file</label>
									</h5> <input name="oldFile" id="oldFile" type="text"
									value="${fileC}" style="display: none;"></td>
							</tr>
						</table>
						<h3 style="padding: 5px 0 0 15px;">Information Content type:</h3>
						<table style="width: 60%;">
							<tr>
								<td><input type="checkbox" checked="${LinC}" id="LinCheck"
									name="LinCheck"> Lin</td>
								<td><input type="checkbox" checked="${ResnikC}"
									id="ResnikCheck" name="ResnikCheck"> Resnik</td>
								<td><input type="checkbox" checked="${JCC}" id=JCCheck
									name="JCCheck"> Jiang and Conrath</td>

							</tr>
						</table>
						<h3 style="padding: 5px 0 0 15px;">PSO configuration:</h3>
						<table>
							<tr>
								<td>&nbsp &nbsp &nbsp &nbsp <input id="defaultSetting"
									name="defaultSetting" type="checkbox" checked="${defCheck}"
									onchange="change(this)"> Default Parameter for
									Optimization Algorithm
								</td>
							</tr>
							<tr style="padding: 5px 0 0 15px;">
								<td id="HisIterLabel" style="display: none">History
									Iteration:</td>
								<td><input name="HisIter" id="HisIter" type="number"
									value="${histV}" step="1" min="1" max="100000"
									title="Number of keeping history of no changes (greater than 1)"
									required style="display: none; width: 50px;"></td>
								<td id="fitThresholdLabel" style="display: none">Fitness
									Threshold:</td>
								<td><input name="fitThreshold" id="fitThreshold"
									type="number" value="${fitV}" step="0.1" min="0" max="1"
									title="Threshold for fitness between 0 and 1" required
									style="display: none; width: 50px;"></td>
							</tr>
							<tr>
								<td id="betaWightLabel" style="display: none">Probably
									Local Weight:</td>
								<td><input name="betaWight" id="betaWight" type="number"
									value="${betaV}" step="0.1" min="0" max="1"
									title="weight for local best between 0 and 1" required
									style="display: none; width: 50px;"></td>
								<td id="landaWeightLabel" style="display: none">Probably
									Global Weight:</td>
								<td><input name="landaWeight" id="landaWeight"
									type="number" value="${landaV}" step="0.1" min="0" max="1"
									title="weight for global best between 0 and 1" required
									style="display: none; width: 50px;"></td>
							</tr>
						</table>

						<div class="form_settings">
							<div class="g-recaptcha"
								data-sitekey="6Lco5UsUAAAAACxR12iYrG1P7L7zUnjDpj2gVmPN"></div>
							<span style="font-weight: bold; color: red;">${res}</span> <input
								class="submit" name="DoCompare" value="Similarity Comparison"
								type="submit" onclick="return check();">
						</div>
						<div id='loadingmsg' style='display: none;'>
							<img src="${pageContext.request.contextPath}/style/load.gif"
								border="0" />
						</div>
						<div id='loadingover' style='display: none;'></div>
					</td>
				</tr>

			</table>

		</div>
</div>


