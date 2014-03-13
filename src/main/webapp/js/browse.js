$(document).ready(function () {
	$('#mappings-table').dynatable({
		table: {
			defaultColumnIdStyle: 'trimDash'
		},
		inputs: {
			processingText: 'Loading <img src="../images/loading.gif" />'
		}
	});
});