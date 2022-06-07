// FileName: wekb.js
// the order must be observed!!!

//=require new/libs/jquery-3.6.0.min
//=require /semantic.min.js         //-- semantic

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