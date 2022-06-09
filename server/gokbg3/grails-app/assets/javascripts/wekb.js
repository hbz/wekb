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

});

