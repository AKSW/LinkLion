<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>Browse | LinkLion - A portal for link discovery.</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta charset="utf-8">
<!-- Bootstrap -->
<link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
<link href="css/style.css" rel="stylesheet" media="screen">
<link href="css/jquery.dynatable.css" rel="stylesheet" media="screen">
<link href="http://fonts.googleapis.com/css?family=Sintony:400,700"
	rel="stylesheet" type="text/css">
<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="https://code.jquery.com/jquery.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="js/bootstrap.min.js"></script>
<script src="js/script.js"></script>
<script src="js/browse.js"></script>
<script src="js/jquery.dynatable.js"></script>
<link rel="icon" href="favicon.ico" type="image/x-icon" />
<link rel="shortcut icon" type="image/x-icon" href="favicon.ico" />
<jsp:useBean id="bean" class="de.linkinglod.beans.BrowsePage" />
</head>
<body>
	<div class="navbar navbar-inverse navbar-fixed-top" role="navigation"
		id="header">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand sintony" href="index.jsp"><img
					src="images/linklion-icon.png" border="0"></a>
			</div>
			<div class="navbar-collapse collapse">
				<ul class="nav navbar-nav">
					<li><a class="sintony align-text" href="index.jsp">LinkLion</a></li>
					<li><a class="sintony align-text" href="start.jsp">Upload</a></li>
					<li><a class="sintony align-text" href="browse.jsp">Browse</a></li>
					<li><a class="sintony align-text" href="vocabulary.html">Vocabulary</a></li>
					<li><a class="sintony align-text" target="_blank"
						href="http://www.linklion.org:8890/sparql">SPARQL</a></li>
					<li><a class="sintony align-text" href="about.html">About</a></li>
				</ul>
			</div>
			<!--/.navbar-collapse -->
		</div>
	</div>

	<div style="padding-top: 100px;">
		<div class="container">
			<h1>Browse</h1>
			<hr>

		</div>
		<div class="container" style="padding-top: 20px;"> 
			<div class="navbar-inner">
				<ul class="nav nav-tabs">
					<li class="active"><a href="browse.jsp">Mappings</a></li>
					<li><a href="browse-datasets.jsp">Datasets</a></li>
				</ul>
			</div>
			<div id="myTabContent" class="tab-content">
				<div id="mapping" class="tab-pane fade in active" style="padding-top: 20px;">
					<div class="panel panel-default">
						<div class="panel-heading">
							<h3 class="panel-title sintony">Browse mappings</h3>
						</div>
						<div class="panel-body" id="browse-mappings">
							<div class="row">
								<h4>
									<small>Click on source or target to view details to a specific mapping.</small>
								</h4>
								<div class="col-lg-6">
									<table class="table" id="mappings-table">
										<thead>
											<tr>
												<th>has source</th>
												<th>has target</th>
												<th>download .nt</th>
												<th data-dynatable-column="linksm">links per mapping</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${bean.mappings}" var="mp">
												<tr>
													<td><a href="${mp.uri}" target="_blank">${mp.srcName}</a></td>
													<td><a href="${mp.uri}" target="_blank">${mp.tgtName}</a></td>
													<td><a href="${mp.storedAt}" target="_blank"><span
															class="glyphicon glyphicon-download"></span></a></td>
													<td>${mp.numLinks}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div id="dataset" class="tab-pane fade in" style="padding-top: 20px;">
				</div>
			</div>
		<hr>
		<footer>
			<p class="sintony">
				Created and maintained by <a href="http://dbs.uni-leipzig.de"
					target="_blank">Database Group Leipzig</a> &amp; <a
					href="http://aksw.org" target="_blank">AKSW</a>, University of
				Leipzig, 2014
			</p>
		</footer>
		</div><!-- /container -->
	</div>
</body>
</html>
