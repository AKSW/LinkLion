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
					<li><a class="sintony align-text" href="start.jsp">Start</a></li>
					<li><a class="sintony align-text" href="browse.jsp">Browse</a></li>
					<li><a class="sintony align-text" href="vocabulary.html">Vocabulary</a></li>
	        		<li><a class="sintony align-text" target="_blank" href="http://www.linklion.org:8890/sparql">SPARQL</a></li>
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
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title sintony">Browse mappings</h3>
				</div>
				<form action="rest/browse/mapping" method="post"
					enctype="multipart/form-data" accept-charset="UTF-8">
					<div class="panel-body" id="browse-mappings">
						<div class="row">
							<h4>
								<small>Select mapping from the dropdown menu.</small>
							</h4>
							<div class="col-lg-6">

								<div class="config-item btn-group">
									<button type="button" class="btn btn-default" id="mapping">Select
										mapping</button>
									<button type="button" class="btn btn-default dropdown-toggle"
										data-toggle="dropdown">
										<span class="caret"></span> <span class="sr-only">Toggle
											Dropdown</span>
									</button>
									<ul class="dropdown-menu" role="menu">
										<c:forEach items="${bean.mappings}" var="mp">
											<li role="presentation"><a href="javascript:void(0);"
												onclick='mappingTrigger("${mp.value}", "${mp.key}");'>${mp.value}</a></li>
										</c:forEach>
									</ul>
									<input type="hidden" name="mapping-uri"
										id="mapping-uri" value=""> &nbsp; <span
										class='label label-info' id="selected-mapping"></span>
								</div>

							</div>
						</div>
						<div class="row text-center">
<!--							<h4 style="padding-left: 0;">
								<small>Upload to the portal</small>
							</h4>
-->
								<div class="config-item text-center" style="position: relative;">
									<input id="submit-button" disabled="disabled" type="submit"
										value="Browse mappings" class="btn btn-primary" />
								</div>
						</div>
					</div>

				</form>
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
		</div>
	</div>
	<!-- /container -->

</body>
</html>
