// FileName: wekb.js
// the order must be observed!!!

//=require /jquery-3.6.0.min                                //-- externalLibs

//= require /jquery.poshytip.js                              //-- externalLibs

//= require /jquery-editable/js/jquery-editable-poshytip.js //-- externalLibs
//=require /combodate.js                                  //-- externalLibs

//=require /semantic.min.js                                 //-- semantic

//=require inline-content.js

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

    $('.tabular.menu .item')
        .tab()
    ;

    $('.ui.accordion')
        .accordion()
    ;

    $('.ui.popup').each(function() {
        $(this).popup()
    });

    //Editable
    $.fn.editable.defaults.mode = 'inline';
    $.fn.editable.defaults.onblur = 'ignore';
    $.fn.editableform.buttons = '<button type="submit" class="ui icon black button editable-submit"><i aria-hidden="true" class="check icon"></i></button>' +
        '<button type="button" class="ui icon black button editable-cancel"><i aria-hidden="true" class="times icon"></i></button>';
    $.fn.editableform.template =
        '<form class="ui form editableform">' +
        '	<div class="control-group">' +
        '		<div class="ui calendar xEditable-datepicker">' +
        '			<div class="ui input right icon editable-input">' +
        '			</div>' +
        '			<div class="editable-buttons">' +
        '			</div>' +
        '		</div>' +
        '        <div id="characters-count"></div>' +
        '		<div class="editable-error-block">' +
        '		</div>' +
        '	</div>' +
        '</form>';
    $.fn.editableform.loading =
        '<div class="ui active inline loader"></div>';

    $('.xEditableValue').editable({
        format:   "yyyy-MM-dd",
        validate: function(value) {
            if ($(this).attr('data-format') && value) {
                if(! (value.match(/^\d{4}-\d{1,2}-\d{1,2}$/)) ) {
                    return "Wrong format";
                }
            }
            // custom validate functions via semui:xEditable validation="xy"
            var dVal = $(this).attr('data-validation')
            if (dVal) {
                if (dVal.includes('notEmpty')) {
                    if($.trim(value) == '') {
                        return "This field is not allowed to be empty";
                    }
                }
                if (dVal.includes('url')) {
                    var regex = /^(https?|ftp):\/\/(.)*/;
                    var test = regex.test($.trim(value)) || $.trim(value) == ''
                    if (! test) {
                        return "The url must beginn with 'http://' or 'https://' or 'ftp://'."
                    }
                }
                if (dVal.includes('email')) {
                    let regex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)+$/
                    let test = regex.test($.trim(value)) || $.trim(value) === ''
                    if(!test) {
                        return "Please check your mail-addres!"
                    }
                }
                if (dVal.includes('maxlength')) {
                    if(value.length > $(this).attr("data-maxlength")) {
                        return "The value is to long!";
                    }
                }
            }
        },
        success: function(response, newValue) {
            if(!response.success) return response.msg;
        },
        error: function(response, newValue) {
            if(response.status === 500) {
                return 'Service unavailable. Please try later.';
            } else {
                return response.responseText;
            }
        }
    }).on('save', function(e, params){
        if ($(this).attr('data-format')) {
            console.log(params)
        }
    }).on('shown', function() {
        if ($(this).attr('data-format')) {
        }else {
            var dType = $(this).attr('data-type')
            if (dType == "text" && $(this).attr('data-validation') && $(this).attr('data-validation').includes("maxlength")) {
                var maxLength = 255;
                $('input').keyup(function () {
                    if($(this).attr('type') == 'text') {
                        var textlen = maxLength - $(this).val().length;
                        $('#characters-count').text(textlen + '/' + maxLength);
                    }
                });
            }
        }
        $(".table").trigger('reflow')
    });

    $('.xEditableManyToOne').editable({
        tpl: '<select class="ui search selection dropdown"></select>',
        success: function(response, newValue) {
            if(!response.success) return response.msg; //msg will be shown in editable form
        }
    }).on('shown', function(e, obj) {

        $('.table').trigger('reflow');
        obj.input.$input.dropdown({clearable: true}) // reference to current dropdown
    });



    $('#spotlightSearch').search({
        error : {
            source      : 'Cannot search. No source used, and Semantic API module was not included',
            noResults   : 'Your search returned no results',
            logging     : 'Error in debug logging, exiting.',
            noTemplate  : 'A valid template name was not specified.',
            serverError : 'There was an issue with querying the server.',
            maxResults  : 'Results must be an array to use maxResults setting',
            method      : 'The method you called is not defined.'
        },

        type: 'category',
        minCharacters: 3,
        maxResults: 10,
        apiSettings: {

            url: spotlightSearchUrl + "/?q={query}",
            onResponse: function(elasticResponse) {
                var response = { results : {} };
                // translate Elasticsearch API response to work with semantic ui search
                $.each(elasticResponse.results, function(index, item) {
                    var
                        category   = item.category || 'Unknown',
                        maxResults = 10
                    ;
                    if(index >= maxResults) {
                        response.action = {
                            "url": spotlightSearchUrl.replace('spotlightSearch', 'index') + "/?q={query}",
                                "text": "View all "+maxResults+" results"
                        }
                        return response;
                    }
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
                        description : item.description,
                        url         : item.url
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

    $(".simpleReferenceDropdown").each(function() {
        var simpleReferenceDropdownURL = ajaxLookUp + "/?baseClass="+$(this).children('input')[0].getAttribute('data-domain')+"&filter1="+$(this).children('input')[0].getAttribute('data-filter1')+"&q={query}"

        $(this).dropdown({
            clearable: true,
            forceSelection: false,
            error : {
                source      : 'Cannot search. No source used, and Semantic API module was not included',
                noResults   : 'Your search returned no results',
                logging     : 'Error in debug logging, exiting.',
                noTemplate  : 'A valid template name was not specified.',
                serverError : 'There was an issue with querying the server.',
                maxResults  : 'Results must be an array to use maxResults setting',
                method      : 'The method you called is not defined.'
            },
            apiSettings: {
                // this url parses query server side and returns filtered results
                url: simpleReferenceDropdownURL,
                //url: simpleReferenceDropdownURL + "/?baseClass=org.gokb.cred.RefdataValue&filter1=KBComponent.Status&q={query}"
                cache: false
            },
            fields: {
                remoteValues: 'values', // grouping for api results
                values: 'values', // grouping for all dropdown values
                name: 'text',   // displayed dropdown text
                value: 'id'   //
            }
        });
    });



});

