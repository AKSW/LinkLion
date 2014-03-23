$(document).ready(function () {
	$('#mappings-table').dynatable({
		inputs: {
		},
		readers: {
			'linksm': function(el, record) {
				return Number(el.innerHTML.replace(/,/g, ''));
			}
		},
		writers: {
			'linksm': function(record) {
				return record.linksm.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
			}
		}
	});

});