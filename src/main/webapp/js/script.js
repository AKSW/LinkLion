
var add_framework = false;
var add_algorithm = false;
var add_source = false;
var add_target = false;

function check() {
	if($("#upload-file-info").html() != "" &&
			((!add_framework && $("#selected-framework").html() != "") || (add_framework && $("#new-framework-name").val() != "")) && 
			((!add_algorithm && $("#selected-algorithm").html() != "") || (add_algorithm && $("#new-algorithm-name").val() != "")) && 
			((!add_source && $("#selected-source").html() != "") || (add_source && $("#new-source-name").val() != "")) && 
			((!add_target && $("#selected-target").html() != "") || (add_target && $("#new-target-name").val() != ""))) {
		$("#submit-button").prop('disabled', false);
	}
	else
		$("#submit-button").prop('disabled', true);
}

function new_framework(nf) {
	add_framework = nf;
	if(nf) {
		$('#new-framework').show();
	} else {
		$('#new-framework').hide();
	}
}

function new_algorithm(nf) {
	add_algorithm = nf;
	if(nf) {
		$('#new-algorithm').show();
	} else {
		$('#new-algorithm').hide();
	}
}

function new_source(nf) {
	add_source = nf;
	if(nf) {
		$('#new-source').show();
	} else {
		$('#new-source').hide();
	}
}

function new_target(nf) {
	add_target = nf;
	if(nf) {
		$('#new-target').show();
	} else {
		$('#new-target').hide();
	}
}

function frameworkTrigger(name, uri) {
	// frontend
	if(name == "") {
		$("#selected-framework").html("Add new framework");
		new_framework(true);
	} else {
		$("#selected-framework").html(name);
		new_framework(false);
	}
	check();
	// backend
	$("#existing-framework-uri").val(uri);
}


function algorithmTrigger(name, uri) {
	// frontend
	if(name == "") {
		$("#selected-algorithm").html("Add new algorithm");
		new_algorithm(true);
	} else {
		$("#selected-algorithm").html(name);
		new_algorithm(false);
	}
	check();
	// backend
	$("#existing-algorithm-uri").val(uri);
}

function sourceTrigger(name, uri) {
	// frontend
	if(name == "") {
		$("#selected-source").html("Add new dataset");
		new_source(true);
	} else {
		$("#selected-source").html(name);
		new_source(false);
	}
	check();
	// backend
	$("#existing-source-uri").val(uri);
}

function targetTrigger(name, uri) {
	// frontend
	if(name == "") {
		$("#selected-target").html("Add new dataset");
		new_target(true);
	} else {
		$("#selected-target").html(name);
		new_target(false);
	}
	check();
	// backend
	$("#existing-target-uri").val(uri);
}

$(document).ready(function () {
	$(check());
	$(new_framework(false));
	$(new_algorithm(false));
	$(new_source(false));
	$(new_target(false));
	
	$('#file-tip').tooltip();

});