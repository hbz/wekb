// FileName: wekb.js
// the order must be observed!!!

//=require /jquery-3.6.0.min        //-- externalLibs
//=require /semantic.min.js         //-- semantic
//=require /echarts.min.js        //-- externalLibs
//=require /select2/js/select2.full.js        //-- externalLibs

console.log('+ wekb.js')


$(function () {

    $('.ui.sticky')
        .sticky({
            context: '#rightBox',
            pushing: true,
            setSize: true
        })
    ;

    $('.ui.dropdown')
        .dropdown()
    ;

    $('.menu .item')
        .tab()
    ;

    $('.ui.category.search').search({
        error : {
            source          : '',
            noResults       : '',
            logging         : '',
            noEndpoint      : '',
            noTemplate      : '',
            serverError     : '',
            maxResults      : '',
            method          : ''
        },

        type: 'category',
        minCharacters: 3,
        apiSettings: {

            url: globalSearchUrl + "/?q={query}",
            onResponse: function(elasticResponse) {
                var response = { results : {} };

                // translate Elasticsearch API response to work with semantic ui search
                $.each(elasticResponse.hits, function(index, item) {

                    var category   = item.category || 'Unknown';
                    // create new object category
                    if (response.results[category] === undefined) {
                        response.results[category] = {
                            name    : category,
                            results : []
                        };
                    }
                    // add result to category
                    response.results[category].results.push({
                        title       : item.title,
                        url         : item.url,
                        description : item.description
                    });
                });
                return response;
            },
            onError: function(errorMessage) {
                // invalid response
                console.log(errorMessage);
            }
        }
    });

});

