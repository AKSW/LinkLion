<!DOCTYPE html>
<html>
  <head>
    <title>LinkLion - A portal for link discovery.</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta charset="utf-8">
    <!-- Bootstrap -->
    <link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
    <link href="css/style.css" rel="stylesheet" media="screen">
    <link href="http://fonts.googleapis.com/css?family=Sintony:400,700" rel="stylesheet" type="text/css">
    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.3.0/respond.min.js"></script>
    <![endif]-->
  </head>
  <body>
    <div class="navbar navbar-inverse navbar-fixed-top" role="navigation" id="header">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand sintony" href="index.jsp"><img src="images/linklion-icon.png" border="0"></a>
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
        </div><!--/.navbar-collapse -->
      </div>
    </div>

    <!-- Main jumbotron for a primary marketing message or call to action -->
    <div class="jumbotron">
      <div class="container">
		<div class="centered-text"><img src="images/linklion.png" border="0" id="logo"></div>
        <h1 class="sintony-bold centered-text">LinkLion</h1>
        <h4 class="sintony centered-text">A portal for link discovery.</h4>
        <div id="buttons" class="sintony centered-text">
        	<a class="btn btn-primary btn-lg sintony" role="button" href="start.jsp"><span class="glyphicon glyphicon-play-circle"></span>&nbsp;&nbsp;Start</a>
        	<a class="btn btn-primary btn-lg sintony" role="button" href="browse.jsp"><span class="glyphicon glyphicon-globe"></span>&nbsp;&nbsp;Browse</a>
        	<a class="btn btn-primary btn-lg sintony" role="button" href="vocabulary.html"><span class="glyphicon glyphicon-book"></span>&nbsp;&nbsp;View vocabulary</a>
        </div>
      </div>
    </div>

    <div class="container">
        <h2 class="sintony centered-text">Why a central link repository?</h2>
      <!-- Example row of columns -->
      <div class="row">
        <div class="col-md-6">
          <h3 class="sintony"><span class="glyphicon glyphicon-hdd"></span>&nbsp;&nbsp;Store computed links.</h3>
          <p class="sintony">Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui. </p>
          <!-- <p><a class="btn btn-default" href="#" role="button">View details &raquo;</a></p> -->
        </div>
        <div class="col-md-6">
          <h3 class="sintony"><span class="glyphicon glyphicon-check"></span>&nbsp;&nbsp;Retrieve domain-specific gold standards.</h3>
          <p class="sintony">Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui. </p>
          <!-- <p><a class="btn btn-default" href="#" role="button">View details &raquo;</a></p> -->
       </div>
      </div>
      <div class="row">
        <div class="col-md-6">
          <h3 class="sintony"><span class="glyphicon glyphicon-stats"></span>&nbsp;&nbsp;Compare different frameworks.</h3>
          <p class="sintony">Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui. </p>
          <!-- <p><a class="btn btn-default" href="#" role="button">View details &raquo;</a></p> -->
        </div>
        <div class="col-md-6">
          <h3 class="sintony"><span class="glyphicon glyphicon-link"></span>&nbsp;&nbsp;Maintain links when projects shut down.</h3>
          <p class="sintony">Donec id elit non mi porta gravida at eget metus. Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus. Etiam porta sem malesuada magna mollis euismod. Donec sed odio dui. </p>
          <!-- <p><a class="btn btn-default" href="#" role="button">View details &raquo;</a></p> -->
       </div>
      </div>
      <hr>

      <footer>
        <p class="sintony">Created and maintained by <a href="http://dbs.uni-leipzig.de" target="_blank">Database Group Leipzig</a> &amp; <a href="http://aksw.org" target="_blank">AKSW</a>, University of Leipzig, 2014</p>
      </footer>
    </div> <!-- /container -->


    <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="https://code.jquery.com/jquery.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script src="js/bootstrap.min.js"></script>
  </body>
</html>