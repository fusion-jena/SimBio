<%@page import="net.tanesha.recaptcha.*"%>

<div class="inner">

	<form id="FormSim" method="post"
		action="${pageContext.request.contextPath}/SimServlet"
		enctype="multipart/form-data" onsubmit='showLoading();'>
		<div>
			<table style="width: 95%" table-layout:="fixed">
				<tr>
					<td
						style="border: 1px solid #869a34; border-radius: 15px; width: 70%;">
						<h2>Input</h2>


						<table style="width: 100%;" class="myTable">
							<tr>
								<td><span title="Enter your term">Term1</span></td>
								<td><input name="Term1" id="Term1" type="text"
									value="${Term1}" title="Enter your term" required></td>
								<td><span title="Enter your term">From</span></td>
								<td><select id="Term1Source" name="Term1Source"
									onchange="change(this)">
										<option value="MeSH" ${MeshSel1}
											title="25,000 medical concepts">MeSH</option>
										<option value="SNOMEDCT" ${SnomedSel1}
											title="360,000 medical concepts">SNOMED-CT</option>
										<option value="WordNet" ${WordnetSel1}
											title="100,000 general concepts">WordNet</option>
										<option id="YourFile" value="YourFile" ${YourfileSel1}
											title="Select your desired ontology">Your File</option>
								</select></td>
								<td><input name="Term1OntPath" id="Term1OntPath"
									type="file" style="display: none" accept=".owl,.rdf">
									<h5>
										<label id="msgOldFile1">Using your last uploaded file
											if you do not enter a new file</label>
									</h5> <input name="oldFile1" id="oldFile1" type="text"
									value="${fileS1}" style="display: none;"></td>
							</tr>
							<tr>
								<td><span title="Enter your term">Term2</span></td>
								<td><input name="Term2" id="Term2" type="text"
									value="${Term2}" title="Enter your term" required></td>
								<td><span title="Enter your term">From</span></td>
								<td><select id="Term2Source" name="Term2Source"
									onchange="change(this)">
										<option value="MeSH" ${MeshSel2}
											title="25,000 medical concepts">MeSH</option>
										<option value="SNOMEDCT" ${SnomedSel2}
											title="360,000 medical concepts">SNOMED-CT</option>
										<option value="WordNet" ${WordnetSel2}
											title="100,000 general concepts">WordNet</option>
										<option id="YourFile" value="YourFile" ${YourfileSel2}
											title="Select your desired ontology">Your File</option>
								</select></td>
								<td><input name="Term2OntPath" id="Term2OntPath"
									type="file" style="display: none" accept=".owl,.rdf">
									<h5>
										<label id="msgOldFile2">Using your last uploaded file
											if you do not enter a new file</label>
									</h5> <input name="oldFile2" id="oldFile2" type="text"
									value="${fileS2}" style="display: none;"></td>
							</tr>
						</table>
						<div>
							<h2>Arguments</h2>
							<table style="width: 90%;">
								<tr>
									<td style="width: 20%; vertical-align: top;">
										<p>
											<span> Similarity IC</span> <select id="SimICComboBox"
												name="SimICComboBox">
												<option value="Lin" ${LinSel}
													title="IC-based similarity measure">Lin</option>
												<option value="Resnik" ${ResnikSel}
													title="IC-based similarity measure">Resnik</option>
												<option value="JC" ${JCSel}
													title="IC-based similarity measure">Jiang and
													Conrath</option>
											</select>
										</p>
									</td>
									<td style="width: 80%; layout: fixed">
										<table>
											<tr>
												<td><input id="defaultSetting" name="defaultSetting"
													type="checkbox" checked="${defCheck}"
													onchange="change(this)"> Default Parameter Setting
													for Optimization Algorithm</td>
											</tr>
											<tr>
												<td id="HisIterLabel" style="display: none; width: 50px;">History
													Iteration:</td>
												<td><input name="HisIter" id="HisIter" type="number"
													value="${histV}" step="1" min="1" max="100000"
													title="Number of keeping history of no changes (greater than 1)"
													required style="display: none; width: 50px;"></td>
												<td id="fitThresholdLabel" style="display: none;">Fitness
													Threshold:</td>
												<td><input name="fitThreshold" id="fitThreshold"
													type="number" value="${fitV}" step="0.1" min="0" max="1"
													title="Threshold for fitness between 0 and 1" required
													style="display: none; width: 50px;"></td>
											</tr>
											<tr>
												<td id="betaWightLabel" style="display: none; width: 50px;">Probably
													Local Weight:</td>
												<td><input name="betaWight" id="betaWight"
													type="number" step="0.1" min="0" max="1" value="${betaV}"
													title="weight for local best between 0 and 1" required
													style="display: none; width: 50px;"></td>
												<td id="landaWeightLabel"
													style="display: none; width: 50px;">Probably Global
													Weight:</td>
												<td><input name="landaWeight" id="landaWeight"
													type="number" step="0.1" min="0" max="1" value="${landaV}"
													title="weight for global best between 0 and 1" required
													style="display: none; width: 50px;"></td>
											</tr>
										</table>
									</td>
								</tr>
							</table>
						</div>

						<div class="form_settings">
							<table>
								<tr>
									<td>
										<div class="g-recaptcha"
											data-sitekey="6Lco5UsUAAAAACxR12iYrG1P7L7zUnjDpj2gVmPN"></div>
									</td>
								</tr>
								<tr>
									<td><input class="submit" name="DoSim"
										value="Similarity Calculation" type="submit"
										onclick="return check();"></td>
								</tr>
							</table>

						</div>
					</td>
					<td
						style="border: 1px solid #869a34; border-radius: 15px; width: 30%;">
						<div class="form_settings">
							<h2>Similarity Result:</h2>
							<textarea name="outSim" rows="15"
								style="width: 90%; font-weight: bold; color: red;">${res}</textarea>
						</div>

						<div id="loadingmsg" style="display: none;">
							<img src="${pageContext.request.contextPath}/style/load.gif"
								border="0" />
						</div>
						<div id="loadingover" style="display: none;"></div>
					</td>
				</tr>
			</table>
		</div>
</div>

<!-- <script>
	jQuery.validator.setDefaults({
		debug : true,
		success : "valid"
	});
</script>-->








