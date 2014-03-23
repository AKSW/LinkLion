$(document).ready(function () {
	$('#mappings-table').dynatable({
		inputs: {
		},
		readers: {
			'links per mapping': function(el, record) {
				return Number(el.innerHTML.replace(/,/g, ''));
			}
		},
		writers: {
			'links per mapping': function(record) {
				return record.links.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
			}
		}
	});

	$('#datasets-table').dynatable({
		inputs: {
		},
		readers: {
			'links per dataset': function(el, record) {
				return Number(el.innerHTML.replace(/,/g, ''));
			},
			'mappings per dataset': function(el, record) {
				return Number(el.innerHTML.replace(/,/g, ''));
			}
		},
		writers: {
			'links per dataset': function(record) {
				return record.links.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
			},
			'mappings per dataset': function(record) {
				return record.mappingsAssoc.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
			}
		}
	});
});