// This is a manifest file that'll be compiled into application.js, which will include all the files
// listed below.
//
// Any JavaScript/Coffee file within this directory, lib/assets/javascripts, vendor/assets/javascripts,
// or vendor/assets/javascripts of plugins, if any, can be referenced here using a relative path.
//
// It's not advisable to add code directly here, but if you do, it'll appear at the bottom of the
// compiled file.
//
// Read Sprockets README (https://github.com/sstephenson/sprockets#sprockets-directives) for details
// about supported directives.
//
 
// BEGIN VENDOR JS FOR PAGES (plus add in jquery.turbolinks)
//= require pages-plugins/pace/pace.min
//= require jquery
//= require jquery_ujs
//= require pages-plugins/modernizr.custom
//= require pages-plugins/boostrapv3/js/bootstrap.min
//= require pages-plugins/jquery/jquery-easy
//= require pages-plugins/jquery-unveil/jquery.unveil.min
//= require pages-plugins/jquery-bez/jquery.bez.min
//= require pages-plugins/jquery-ios-list/jquery.ioslist.min
//= require pages-plugins/imagesloaded/imagesloaded.pkgd.min
//= require pages-plugins/jquery-actual/jquery.actual.min
//= require pages-plugins/jquery-scrollbar/jquery.scrollbar.min
 //= require pages-plugins/jquery-form/jquery.form
 
// BEGIN CORE TEMPLATE JS FOR PAGES
//= require pages-core/js/pages
 
// BEGIN SITE SCRIPTS
 
//  I prefer to list scripts in a specific order, so I comment out require_tree .
// require_tree .
//= require turbolinks

//= require jquery-ui.min
//= require spin.min
//= require google 

//= require ./plugins/select2.min
//= require ./plugins/classie
//= require ./plugins/switchery.min
//= require ./plugins/MetroJs.min
//= require ./plugins/isotope.pkgd.min
//= require ./plugins/dialogFx
//= require ./plugins/owl.carousel.min
//= require ./plugins/jquery.nouislider.min
//= require ./plugins/jquery.liblink
//= require crazyegg
//= require_self

$(function(){
$(".reportBugButton").on('click', function() {
   $('#reportBugModal').modal();
 });
 });
function reportBug(){
     if($(".descritpion").val() == ""){
       return;
     }
     $.post("/api/v1/report",{
       description: $(".description").val(),
       page_now: window.location.href  
     },
     function(data, status){
         if(status=="success"){
           alert("report successfully! \n Thank for your help. \n We will fixed the problem as soon as possible");
           $("#reportBugModal").modal("hide");
         }else{
         alert("Data: " + data + "\nStatus: " + status);}
     });
}
