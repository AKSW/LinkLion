<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<title>Start | LinkLion - A portal for link discovery.</title>
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
<script src="js/start.js"></script>
<jsp:useBean id="bean" class="de.linkinglod.beans.StartPage" />
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
				<a class="navbar-brand sintony" href="index.html"><img
					src="images/linklion-icon.png" border="0"></a>
			</div>
			<div class="navbar-collapse collapse">
				<ul class="nav navbar-nav">
					<li><a class="sintony align-text" href="index.html">LinkLion</a></li>
					<li><a class="sintony align-text" href="start.jsp">Start</a></li>
					<li><a class="sintony align-text" href="browse.jsp">Browse</a></li>
					<li><a class="sintony align-text" href="vocabulary.html">Vocabulary</a></li>
					<li><a class="sintony align-text" href="about.html">About</a></li>
				</ul>
			</div>
			<!--/.navbar-collapse -->
		</div>
	</div>

	<div style="padding-top: 100px;">
		<div class="container">
			<h1>Start</h1>
			<hr>

		</div>
		<div class="container" style="padding-top: 20px;">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h3 class="panel-title sintony">Upload mapping</h3>
				</div>
				<form action="rest/file/upload" method="post"
					enctype="multipart/form-data" accept-charset="UTF-8">
					<div class="panel-body" id="configuration">
						<div class="row">
							<h4>
								<small>Load mapping file <a href="javascript:void(0);" id="file-tip" data-toggle="tooltip" 
									data-placement="right" title="An N-Triples file containing mappings, e.g. &lt;:Source001&gt; &lt;owl:sameAs&gt; &lt;:Target001&gt;.">[?]</a></small>
							</h4>
							<div class="col-lg-6">

								<div class="config-item" style="position: relative;">
									<a class="btn btn-default" href="javascript:;"> Choose
										N-Triples file... <input type="file" class="browse-file"
										name="file" size="40"
										onchange='$("#upload-file-info").html($(this).val()); check();'>
									</a> &nbsp; <span class='label label-info' id="upload-file-info"></span>
								</div>

							</div>
							<div class="col-lg-6"></div>
						</div>
						<div class="row">
							<h4>
								<small>Select existent framework or add new framework</small>
							</h4>
							<div class="col-lg-6">

								<div class="config-item btn-group">
									<button type="button" class="btn btn-default" id="framework">Select
										framework</button>
									<button type="button" class="btn btn-default dropdown-toggle"
										data-toggle="dropdown">
										<span class="caret"></span> <span class="sr-only">Toggle
											Dropdown</span>
									</button>
									<ul class="dropdown-menu" role="menu">
										<c:forEach items="${bean.frameworks}" var="fw">
											<li role="presentation"><a href="javascript:void(0);"
												onclick='frameworkTrigger("${fw.value}", "${fw.key}");'>${fw.value}</a></li>
										</c:forEach>
										<li role="presentation" class="divider"></li>
										<li role="presentation"><a href="javascript:void(0);"
											onclick='frameworkTrigger("", "");'><i>Add new
													framework</i></a></li>
									</ul>
									<input type="hidden" name="existing-framework-uri"
										id="existing-framework-uri" value=""> &nbsp; <span
										class='label label-info' id="selected-framework"></span>
								</div>

							</div>
							<div class="col-lg-6" id="new-framework" style="display: none;">

								<div class="input-group">
									<span class="input-group-addon">Name</span> <input
										name="new-framework-name" id="new-framework-name" type="text" class="form-control"
										placeholder="New Framework">
								</div>
								<div class="input-group">
									<span class="input-group-addon">Version</span> <input
										name="new-framework-version" type="text" class="form-control"
										placeholder="1.0">
								</div>
								<div class="input-group">
									<span class="input-group-addon">Homepage</span> <input
										name="new-framework-url" type="text" class="form-control"
										placeholder="http://new-framework.org">
								</div>

							</div>
						</div>
						<div class="row">
							<h4>
								<small>Select existent algorithm or add new algorithm</small>
							</h4>
							<div class="col-lg-6">

								<div class="config-item btn-group">
									<button type="button" class="btn btn-default" id="algorithm">Select
										algorithm</button>
									<button type="button" class="btn btn-default dropdown-toggle"
										data-toggle="dropdown">
										<span class="caret"></span> <span class="sr-only">Toggle
											Dropdown</span>
									</button>
									<ul class="dropdown-menu" role="menu">
										<c:forEach items="${bean.algorithms}" var="alg">
											<li role="presentation"><a href="javascript:void(0);"
												onclick='algorithmTrigger("${alg.value}", "${alg.key}");'>${alg.value}</a></li>
										</c:forEach>
										<li role="presentation" class="divider"></li>
										<li role="presentation"><a href="javascript:void(0);"
											onclick='algorithmTrigger("", "");'><i>Add new
													algorithm</i></a></li>
									</ul>
									<input type="hidden" name="existing-algorithm-uri"
										id="existing-algorithm-uri" value=""> &nbsp; <span
										class='label label-info' id="selected-algorithm"></span>
								</div>

							</div>
							<div class="col-lg-6" id="new-algorithm" style="display: none;">

								<div class="input-group">
									<span class="input-group-addon">Name</span> <input
										name="new-algorithm-name" id="new-algorithm-name" type="text" class="form-control"
										placeholder="New Algorithm">
								</div>
								<div class="input-group">
									<span class="input-group-addon">Homepage</span> <input
										name="new-algorithm-url" type="text" class="form-control"
										placeholder="http://new-algorithm.org">
								</div>

							</div>
						</div>
						<div class="row" id="source-dataset-details">
							<h4>
								<small>Enter source dataset details</small>
							</h4>
							<div class="col-lg-6">

								<div class="config-item btn-group">
									<button type="button" class="btn btn-default" id="source">Select
										source</button>
									<button type="button" class="btn btn-default dropdown-toggle"
										data-toggle="dropdown">
										<span class="caret"></span> <span class="sr-only">Toggle
											Dropdown</span>
									</button>
									<ul class="dropdown-menu" role="menu">
										<c:forEach items="${bean.datasets}" var="dset">
											<li role="presentation"><a href="javascript:void(0);"
												onclick='sourceTrigger("${dset.value}", "${dset.key}");'>${dset.value}</a></li>
										</c:forEach>
										<li role="presentation" class="divider"></li>
										<li role="presentation"><a href="javascript:void(0);"
											onclick='sourceTrigger("", "");'><i>Add new dataset</i></a></li>
									</ul>
									<input type="hidden" name="existing-source-uri"
										id="existing-source-uri" value=""> &nbsp; <span
										class='label label-info' id="selected-source"></span>
								</div>

							</div>

							<div class="col-lg-6" id="new-source" style="display: none;">

								<div class="input-group">
									<span class="input-group-addon">Name</span> <input
										name="new-source-name" id="new-source-name" type="text" class="form-control"
										placeholder="Source Dataset">
								</div>
								<div class="input-group">
									<span class="input-group-addon">URI Space</span> <input
										name="new-source-urispace" type="text" class="form-control"
										placeholder="http://source-namespace.org">
								</div>

							</div>
						</div>
						<div class="row" id="target-dataset-details">
							<h4>
								<small>Enter target dataset details</small>
							</h4>
							<div class="col-lg-6">
								<div class="config-item btn-group">
									<button type="button" class="btn btn-default" id="target">Select
										target</button>
									<button type="button" class="btn btn-default dropdown-toggle"
										data-toggle="dropdown">
										<span class="caret"></span> <span class="sr-only">Toggle
											Dropdown</span>
									</button>
									<ul class="dropdown-menu" role="menu">
										<c:forEach items="${bean.datasets}" var="dset">
											<li role="presentation"><a href="javascript:void(0);"
												onclick='targetTrigger("${dset.value}", "${dset.key}");'>${dset.value}</a></li>
										</c:forEach>
										<li role="presentation" class="divider"></li>
										<li role="presentation"><a href="javascript:void(0);"
											onclick='targetTrigger("", "");'><i>Add new dataset</i></a></li>
									</ul>
									<input type="hidden" name="existing-target-uri"
										id="existing-target-uri" value=""> &nbsp; <span
										class='label label-info' id="selected-target"></span>
								</div>
							</div>

							<div class="col-lg-6" id="new-target" style="display: none;">

								<div class="input-group">
									<span class="input-group-addon">Name</span> <input
										name="new-target-name" id="new-target-name" type="text" class="form-control"
										placeholder="Source Dataset" onchange="check();">
								</div>
								<div class="input-group">
									<span class="input-group-addon">URI Space</span> <input
										name="new-target-urispace" type="text" class="form-control"
										placeholder="http://target-namespace.org">
								</div>

							</div>
						</div>
						<div class="row text-center">
							<h4 style="padding-left: 0;">
								<small>Upload to the portal</small>
							</h4>
								<div class="config-item text-center" style="position: relative;">
									<input id="submit-button" disabled="disabled" type="submit"
										value="Upload mapping" class="btn btn-primary" />
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
