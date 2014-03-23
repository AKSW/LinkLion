$(document).ready(function () {

	$('#datasets-table').dynatable({
		inputs: {
		},
		readers: {
			'linksd': function(el, record) {
				return Number(el.innerHTML.replace(/,/g, ''));
			},
			'mappingsd': function(el, record) {
				return Number(el.innerHTML.replace(/,/g, ''));
			}
		},
		writers: {
			'linksd': function(record) {
				return record.linksd.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
			},
			'mappingsd': function(record) {
				return record.mappingsd.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
			}
		}
	});
});