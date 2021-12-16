// FileName: application-public.js

//=require libs/jquery-2.2.0.min
//=require libs/bootstrap4
//=require libs/select2

//=require gokb/inline-content

console.log('+ application-public.js');

$(document).ready(function() {

  /** Bootstrap 4.6.  - Loading Tabs from URL with hash **/
      // Javascript to enable link to tab
  let hash = location.hash;

  if (hash) {
    $('.nav-item a[href="' + hash + '"]').tab('show');
  }
  // Change hash for page-reload
  $('.nav-item a').on('shown.bs.tab', function(e) {
    location.hash = e.target.hash;
  })
});
