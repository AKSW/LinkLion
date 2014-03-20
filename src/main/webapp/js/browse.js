$(document).ready(function () {
        $('#mappings-table').dynatable({
                table: {
                        defaultColumnIdStyle: 'trimDash'
                },
                inputs: {
                        processingText: 'Loading <img src="../images/loading.gif" border="0"/>'
                },
                readers: {
                    'links': function(el, record) {
                        return Number(el.innerHTML.replace(/,/g, ''));
                    }   
                },
                writers: {
                    'links': function(record) {
                        return record.links.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
                    }
                }
        });
});