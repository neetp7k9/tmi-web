$(function(){
$('body').on('click', '.addNewDocument', function() {
  $(".button_click")[0].id="true";
  console.log(this.id);
  console.log($('#addDocumentModal_Project_Id')[0]);
  $('#addDocumentModal_Project_Id')[0].value = this.id;
  $('#addDocumentModal').modal();
});
$('body').on('click', '.deleteProject', function() {
  $(".button_click")[0].id="true";
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
    $.ajax({
        url: '/api/v1/project?project_id='+this.id,
        type: 'DELETE',
        data: "",
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
