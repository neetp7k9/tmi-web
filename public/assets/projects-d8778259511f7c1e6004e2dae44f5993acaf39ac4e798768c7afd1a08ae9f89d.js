(function($) {

    'use strict';

    $(document).ready(function() {
        // Initializes search overlay plugin.
        // Replace onSearchSubmit() and onKeyEnter() with 
        // your logic to perform a search and display results
        $(".list-view-wrapper").scrollbar();

        $('[data-pages="search"]').search({
            // Bind elements that are included inside search overlay
            searchField: '#overlay-search',
            closeButton: '.overlay-close',
            suggestions: '#overlay-suggestions',
            brand: '.brand',
             // Callback that will be run when you hit ENTER button on search box
            onSearchSubmit: function(searchString) {
                console.log("Search for: " + searchString);
            },
            // Callback that will be run whenever you enter a key into search box. 
            // Perform any live search here.  
            onKeyEnter: function(searchString) {
                console.log("Live search for: " + searchString);
                var searchField = $('#overlay-search');
                var searchResults = $('.search-results');

                /* 
                    Do AJAX call here to get search results
                    and update DOM and use the following block 
                    'searchResults.find('.result-name').each(function() {...}'
                    inside the AJAX callback to update the DOM
                */

                // Timeout is used for DEMO purpose only to simulate an AJAX call
                clearTimeout($.data(this, 'timer'));
                searchResults.fadeOut("fast"); // hide previously returned results until server returns new results
                var wait = setTimeout(function() {

                    searchResults.find('.result-name').each(function() {
                        if (searchField.val().length != 0) {
                            $(this).html(searchField.val());
                            searchResults.fadeIn("fast"); // reveal updated results
                        }
                    });
                }, 500);
                $(this).data('timer', wait);

            }
        })

    });

    
    $('.panel-collapse label').on('click', function(e){
        e.stopPropagation();
    })
    
})(window.jQuery);
$(function(){
$('body').on('click', '.addNewDocument', function() {
  $(".button_click")[0].id="true";
  console.log(this.id);
  console.log($('#addDocumentModal_Project_Id')[0]);
  $('#addDocumentModal_Project_Id')[0].value = this.id;
  $('#addDocumentModal').modal();
});
$('body').on('click', '#addNewProjectButton', function() {
               $('#addProjectModal').modal();
});

$('body').on('click', 'NewAddProject', function() {
               createProject();
});

$('#text_search_form').submit(function(event) {
    event.preventDefault();
    search();
})
$('#import_form').submit(function(event) {
    event.preventDefault();
    import_file();
})

$(function(){
$("#tabs").tabs();
})
$("input#image_for_search[type=file]").on('change',function(){
    var formData = new FormData($("#searchForm")[0]);
    var opts = {
        lines: 13 // The number of lines to draw
      , length: 28 // The length of each line
      , width: 14 // The line thickness
      , radius: 42 // The radius of the inner circle
      , scale: 1 // Scales overall size of the spinner
      , corners: 1 // Corner roundness (0..1)
      , color: '#000' // #rgb or #rrggbb or array of colors
      , opacity: 0.25 // Opacity of the lines
      , rotate: 0 // The rotation offset
      , direction: 1 // 1: clockwise, -1: counterclockwise
      , speed: 1 // Rounds per second
      , trail: 60 // Afterglow percentage
      , fps: 20 // Frames per second when using setTimeout() as a fallback for CSS
      , zIndex: 2e9 // The z-index (defaults to 2000000000)
      , className: 'spinner' // The CSS class to assign to the spinner
      , top: '50%' // Top position relative to parent
      , left: '50%' // Left position relative to parent
      , shadow: false // Whether to render a shadow
      , hwaccel: false // Whether to use hardware acceleration
      , position: 'absolute' // Element positioning
    }

    var target = document.getElementById('dropzone')
    var spinner = new Spinner(opts).spin(target);
    $.ajax({
        url: '/api/v1/document',
        type: 'POST',
        data: formData,
        cache: false,
        dataType: 'json',
        processData: false, // Don't process the files
        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
        success: function(data, textStatus, jqXHR)
        {
            if(typeof data.error === 'undefined')
            {
                // Success so call function to process the form
                console.log(data);
                window.location.href = "/crop_to_search?id="+data["document"][1]+"&search_id="+data["document"][0];
            }
            else
            {
                // Handle errors here
                console.log('ERRORS: ' + data.error);
            }
        },
        error: function(jqXHR, textStatus, errorThrown)
        {
            // Handle errors here
            console.log('ERRORS: ' + textStatus);
            // STOP LOADING SPINNER
        }
    });
});

});
function addDocument(){
      var opts = {
        lines: 13 // The number of lines to draw
      , length: 28 // The length of each line
      , width: 14 // The line thickness
      , radius: 42 // The radius of the inner circle
      , scale: 1 // Scales overall size of the spinner
      , corners: 1 // Corner roundness (0..1)
      , color: '#000' // #rgb or #rrggbb or array of colors
      , opacity: 0.25 // Opacity of the lines
      , rotate: 0 // The rotation offset
      , direction: 1 // 1: clockwise, -1: counterclockwise
      , speed: 1 // Rounds per second
      , trail: 60 // Afterglow percentage
      , fps: 20 // Frames per second when using setTimeout() as a fallback for CSS
      , zIndex: 2e9 // The z-index (defaults to 2000000000)
      , className: 'spinner' // The CSS class to assign to the spinner
      , top: '50%' // Top position relative to parent
      , left: '50%' // Left position relative to parent
      , shadow: false // Whether to render a shadow
      , hwaccel: false // Whether to use hardware acceleration
      , position: 'absolute' // Element positioning
    }

    var target = $("body")[0]
    var spinner = new Spinner(opts).spin(target);
    var formData = new FormData($("#formData")[0]);
    $.ajax({
        url: '/api/v1/document',
        type: 'POST',
        data: formData,
        cache: false,
        dataType: 'json',
        processData: false, // Don't process the files
        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
        success: function(data, textStatus, jqXHR)
        {
            if(typeof data.error === 'undefined')
            {
                // Success so call function to process the form
                location.reload();
            }
            else
            {
                // Handle errors here
                console.log('ERRORS: ' + data.error);
            }
        },
        error: function(jqXHR, textStatus, errorThrown)
        {
            // Handle errors here
            console.log('ERRORS: ' + textStatus);
            // STOP LOADING SPINNER
        }
    });
     $('#addDocumentModal').modal('hide');
};
function createProjectNew(){
    console.log("project created start");
    if($("#project_name:first") == ""){
      return;
    }
    $.post("/api/v1/project",
    {
        name: $("#project_name_2")[0].value
    },
    function(data, status){
        if(status=="success"){location.reload();}else{
        alert("Data: " + data + "\nStatus: " + status);}
    });
    console.log("project created end");
};
function createProject(){
    console.log("project created start");
    if($("#project_name:first") == ""){
      return;
    }
    $.post("/api/v1/project",
    {
        name: $("#project_name")[0].value
    },
    function(data, status){
        if(status=="success"){location.reload();}else{
        alert("Data: " + data + "\nStatus: " + status);}
    });
    console.log("project created end");
};
function search(){
  if($("#text_input")[0].value != ""){
    var formData = new FormData($("#text_search_form")[0]);
    $.ajax({
        url: '/api/v1/search_by_text',
        type: 'POST',
        data: formData,
        cache: false,
        dataType: 'json',
        processData: false, // Don't process the files
        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
        success: function(data, textStatus, jqXHR)
        {
            if(typeof data.error === 'undefined')
            {
                // Success so call function to process the form
                console.log(data);
                window.location.href = "/searches/"+data;
            }
            else
            {
                // Handle errors here
                console.log('ERRORS: ' + data.error);
            }
        },
        error: function(jqXHR, textStatus, errorThrown)
        {
            // Handle errors here
            console.log('ERRORS: ' + textStatus);
            // STOP LOADING SPINNER
        }
    });
  }
}
function import_file(){
    var formData = new FormData($("#import_form")[0]);
    $.ajax({
        url: '/api/v1/import_file',
        type: 'POST',
        data: formData,
        cache: false,
        dataType: 'json',
        processData: false, // Don't process the files
        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
        success: function(data, textStatus, jqXHR)
        {
            if(typeof data.error === 'undefined')
            {
                // Success so call function to process the form
                console.log(data.project_id);
                console.log(data.dir);
                console.log(data.file);
		for(var filename in data.file){
                   import_now(filename,data.project_id,data.dir);
		}
            }
            else
            {
                // Handle errors here
                console.log('ERRORS: ' + data.error);
            }
        },
        error: function(jqXHR, textStatus, errorThrown)
        {
            // Handle errors here
            console.log('ERRORS: ' + textStatus);
            // STOP LOADING SPINNER
        }
    });
}
function import_now(filename,project_id,dir){
    data =  { name: filename, project_id: project_id, dir: dir };
    console.log(data);
    $.ajax({
        url: '/api/v1/import_file',
        type: 'GET',
        data:  "data=1&data2=2",
        cache: false,
        dataType: 'json',
        processData: false, // Don't process the files
        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
        success: function(data, textStatus, jqXHR)
        {
            if(typeof data.error === 'undefined')
            {
                // Success so call function to process the form
                console.log(data);

            }
            else
            {
                // Handle errors here
                console.log('ERRORS: ' + data.error);
            }
        },
        error: function(jqXHR, textStatus, errorThrown)
        {
            // Handle errors here
            console.log('ERRORS: ' + textStatus);
            // STOP LOADING SPINNER
        }
    });
}
;

$(function() {
  $('#dropzone').on('dragover', function() {
    $(this).addClass('hover');
  });

  $('#dropzone').on('dragleave', function() {
    $(this).removeClass('hover');
  });

  //image fadeout: Puan take a look
  $('#dropzone').mouseover(function(){
        $('.camera').fadeTo('slow', 0.4);
        $('.draghere').fadeIn(200);
        });
        $('#dropzone').mouseleave(function(){
        $('.camera').fadeTo('fast',1.0);
        $('.draghere').fadeOut();
  });

  $('#dropzone input').on('change', function(e) {
    var file = this.files[0];

    $('#dropzone').removeClass('hover');

    if (this.accept && $.inArray(file.type, this.accept.split(/, ?/)) == -1) {
      return alert('File type not allowed.');
    }
    $("#dropzone-text").remove();
    $("#dropzone").attr("style", "");
/*
    $('#dropzone').addClass('dropped');
    $('#dropzone img').remove();

    if ((/^image\/(gif|png|jpeg)$/i).test(file.type)) {
      var reader = new FileReader(file);

      reader.readAsDataURL(file);

      reader.onload = function(e) {
        var data = e.target.result,
            $img = $('<img />').attr('src', data).fadeIn();

        $('#dropzone div').html($img);
      };
    } else {
      var ext = file.name.split('.').pop();

      $('#dropzone div').html(ext);
    }
*/
  });
});


/* ============================================================
 * Gallery
 * Showcase your portfolio or even use it for an online store!
 * For DEMO purposes only. Extract what you need.
 * ============================================================ */


$(function() {

    /* GRID
    -------------------------------------------------------------*/

    /* 
        Wait for the images to be loaded before applying
        Isotope plugin. 
    */
    var $gallery = $('.gallery');
    $gallery.imagesLoaded(function() {
        applyIsotope();
    });

    /*  Apply Isotope plugin 
        isotope.metafizzy.co
    */
    var applyIsotope = function() {
        $gallery.isotope({
            itemSelector: '.gallery-item',
            masonry: {
                columnWidth: 280,
                gutter: 10,
                isFitWidth: true
            }
        });
    }
    
    /*
        Show a sliding item using MetroJS
        http://www.drewgreenwell.com/projects/metrojs
    */
    $(".live-tile,.flip-list").liveTile();


     /* DETAIL VIEW
    -------------------------------------------------------------*/

    /*
        Toggle detail view using DialogFx
        http://tympanus.net/Development/DialogEffects/
    */
    $('body').on('click', '.gallery-item', function() {
        console.log($(".button_click")[0].id);
        console.log(this);
      
        if($(".button_click")[0].id == "false"){
          switch(this.id){
            case "addProject" :
                $('#addProjectModal').modal();
                break;
            case "addDocument" :
                $('#addDocumentModal').modal();
                break;
            case "addImage" :
                window.location.href = "/crop?id="+$(".page_info")[0].id;
                break;
            default : 
              if($(".page_now")[0].id == "document")
              { //  var dlg = new DialogFx($('#itemDetails').get(0));
                //  dlg.toggle();
                window.location.href = "/documents/"+this.id;
              }else
                if($(".page_now")[0].id == "image"){}else
                window.location.href = "/projects/"+this.id;
          }
        }else{
          $(".button_click")[0].id = "false"
        }
    });

    /*
        Look for data-image attribute and apply those
        images as CSS background-image 
    */
    $('.item-slideshow > div').each(function() {
        var img = $(this).data('image');
        $(this).css({
            'background-image': 'url(' + img + ')',
            'background-size': 'cover'
        })
    });

    /* 
        Touch enabled slideshow for gallery item images using owlCarousel
        www.owlcarousel.owlgraphic.com
    */
    $(".item-slideshow").owlCarousel({
        items: 1,
        nav: true,
        navText: ['<i class="fa fa-chevron-left"></i>', '<i class="fa fa-chevron-right"></i>'],
        dots: true
    });


     /* FILTERS OVERLAY
    -------------------------------------------------------------*/

    $('[data-toggle="filters"]').click(function() {
        $('#filters').toggleClass('open');
    });

    /*
    $("#slider-margin").noUiSlider({
        start: [20, 80],
        margin: 30,
        connect: true,
        range: {
            'min': 0,
            'max': 100
        }
    });
     */
});
// Place all the behaviors and hooks related to the matching controller here.
// All this logic will automatically be available in application.js.

$("input#document_for_import[type=file]").on('change',function(){
    alert("hi");
    var formData = new FormData($("#importForm")[0]);
    var opts = {
        lines: 13 // The number of lines to draw
      , length: 28 // The length of each line
      , width: 14 // The line thickness
      , radius: 42 // The radius of the inner circle
      , scale: 1 // Scales overall size of the spinner
      , corners: 1 // Corner roundness (0..1)
      , color: '#000' // #rgb or #rrggbb or array of colors
      , opacity: 0.25 // Opacity of the lines
      , rotate: 0 // The rotation offset
      , direction: 1 // 1: clockwise, -1: counterclockwise
      , speed: 1 // Rounds per second
      , trail: 60 // Afterglow percentage
      , fps: 20 // Frames per second when using setTimeout() as a fallback for CSS
      , zIndex: 2e9 // The z-index (defaults to 2000000000)
      , className: 'spinner' // The CSS class to assign to the spinner
      , top: '50%' // Top position relative to parent
      , left: '50%' // Left position relative to parent
      , shadow: false // Whether to render a shadow
      , hwaccel: false // Whether to use hardware acceleration
      , position: 'absolute' // Element positioning
    }

    var target = document.getElementById('dropzone')
    var spinner = new Spinner(opts).spin(target);
    $.ajax({
        url: '/api/v1/document',
        type: 'POST',
        data: formData,
        cache: false,
        dataType: 'json',
        processData: false, // Don't process the files
        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
        success: function(data, textStatus, jqXHR)
        {
            if(typeof data.error === 'undefined')
            {
                // Success so call function to process the form
                console.log(data);
                // Success so call function to process the form
                location.reload();
            }
            else
            {
                // Handle errors here
                console.log('ERRORS: ' + data.error);
            }
        },
        error: function(jqXHR, textStatus, errorThrown)
        {
            // Handle errors here
            console.log('ERRORS: ' + textStatus);
            // STOP LOADING SPINNER
        }
    });
});

});

// Place all the behaviors and hooks related to the matching controller here.
// All this logic will automatically be available in application.js.






