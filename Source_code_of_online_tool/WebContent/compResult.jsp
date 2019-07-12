<div class="inner">

	<form method="post" action="/SimBio/SimServlet"
		enctype="multipart/form-data">
		<div>
			<h2>
				Similarity Result of <span id="Term1"> <strong>${Term1}</strong></span>
				and <span id="Term2"><strong>${Term2}</strong></span> are:
			</h2>
			<table class="resTable" style="width: 90%; border: 1px solid black;">
				<tr>
					<th><strong>Source</strong></th>
					<th><strong>IC-Lin</strong></th>
					<th><strong>IC-Resnik</strong></th>
					<th><strong>IC-JC</strong></th>
				</tr>
				<tr>
					<td>MeSH</td>
					<td>${res00}</td>
					<td>${res01}</td>
					<td>${res02}</td>
				</tr>
				<tr>
					<td>SNOMED-CT</td>
					<td>${res10}</td>
					<td>${res11}</td>
					<td>${res12}</td>
				</tr>
				<tr>
					<td>WordNet</td>
					<td>${res20}</td>
					<td>${res21}</td>
					<td>${res22}</td>
				</tr>
				<tr>
					<td>Your File</td>
					<td>${res30}</td>
					<td>${res31}</td>
					<td>${res32}</td>
				</tr>
				<tr>
					<td>MeSH+SNOMED-CT</td>
					<td>${res40}</td>
					<td>${res41}</td>
					<td>${res42}</td>
				</tr>
				<tr>
					<td>MeSH+WordNet</td>
					<td>${res50}</td>
					<td>${res51}</td>
					<td>${res52}</td>
				</tr>
				<tr>
					<td>SNOMED-CT+MeSH</td>
					<td>${res60}</td>
					<td>${res61}</td>
					<td>${res62}</td>
				</tr>
				<tr>
					<td>SNOMED-CT+WordNet</td>
					<td>${res70}</td>
					<td>${res71}</td>
					<td>${res72}</td>
				</tr>
				<tr>
					<td>WordNet+MeSH</td>
					<td>${res80}</td>
					<td>${res81}</td>
					<td>${res82}</td>
				</tr>
				<tr>
					<td>WordNet+SNOMED-CT</td>
					<td>${res90}</td>
					<td>${res91}</td>
					<td>${res92}</td>
				</tr>
				<tr>
					<td>MeSH+Your File</td>
					<td>${res100}</td>
					<td>${res101}</td>
					<td>${res102}</td>
				</tr>
				<tr>
					<td>SNOMED-CT+Your File</td>
					<td>${res110}</td>
					<td>${res111}</td>
					<td>${res112}</td>
				</tr>
				<tr>
					<td>WordNet+Your File</td>
					<td>${res120}</td>
					<td>${res121}</td>
					<td>${res122}</td>
				</tr>
				<tr>
					<td>Your File+MeSH</td>
					<td>${res130}</td>
					<td>${res131}</td>
					<td>${res132}</td>
				</tr>
				<tr>
					<td>Your File+SNOMED-CT</td>
					<td>${res140}</td>
					<td>${res141}</td>
					<td>${res142}</td>
				</tr>
				<tr>
					<td>Your File+WordNet</td>
					<td>${res150}</td>
					<td>${res151}</td>
					<td>${res152}</td>
				</tr>
			</table>
		</div>
</div>
</div>



