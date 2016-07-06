crop_info=""
$(function () {

  'use strict';

  var console = window.console || { log: function () {} };
  var $image = $('#image');
  var $dataX = $('#dataX');
  var $dataY = $('#dataY');
  var $dataHeight = $('#dataHeight');
  var $dataWidth = $('#dataWidth');
  var $dataRotate = $('#dataRotate');
  var $dataScaleX = $('#dataScaleX');
  var $dataScaleY = $('#dataScaleY');
  var options = {
        autoCropArea: 0.4,
        preview: '.img-preview',
        crop: function (e) {
          $dataX.val(Math.round(e.x));
          $dataY.val(Math.round(e.y));
          $dataHeight.val(Math.round(e.height));
          $dataWidth.val(Math.round(e.width));
          $dataRotate.val(e.rotate);
          $dataScaleX.val(e.scaleX);
          $dataScaleY.val(e.scaleY);
        }
      };


  // Tooltip
  $('[data-toggle="tooltip"]').tooltip();
  // Cropper
  $image.on({
    'build.cropper': function (e) {
      console.log(e.type);
    },
    'built.cropper': function (e) {
      console.log(e.type);
    },
    'cropstart.cropper': function (e) {
      console.log(e.type, e.action);
    },
    'cropmove.cropper': function (e) {
      console.log(e.type, e.action);
    },
    'cropend.cropper': function (e) {
      console.log(e.type, e.action);
    },
    'crop.cropper': function (e) {
      console.log(e.type, e.x, e.y, e.width, e.height, e.rotate, e.scaleX, e.scaleY);
      crop_info = e.x + " " +  e.y + " " +  e.width + " " +  e.height + " " +  e.rotate + " " +  e.scaleX + " " +  e.scaleY;
         
    },
    'zoom.cropper': function (e) {
      console.log(e.type, e.ratio);
    }
  }).cropper(options);


  // Buttons
  if (!$.isFunction(document.createElement('canvas').getContext)) {
    $('button[data-method="getCroppedCanvas"]').prop('disabled', true);
  }

  if (typeof document.createElement('cropper').style.transition === 'undefined') {
    $('button[data-method="rotate"]').prop('disabled', true);
    $('button[data-method="scale"]').prop('disabled', true);
  }






  // Methods
  $('.docs-buttons').on('click', '[data-method]', function () {
    var $this = $(this);
    var data = $this.data();
    var $target;
    var result;

    if ($this.prop('disabled') || $this.hasClass('disabled')) {
      return;
    }

    if ($image.data('cropper') && data.method) {
      data = $.extend({}, data); // Clone a new one

      if (typeof data.target !== 'undefined') {
        $target = $(data.target);

        if (typeof data.option === 'undefined') {
          try {
            data.option = JSON.parse($target.val());
          } catch (e) {
            console.log(e.message);
          }
        }
      }

      result = $image.cropper(data.method, data.option, data.secondOption);

      switch (data.method) {
        case 'scaleX':
        case 'scaleY':
          $(this).data('option', -data.option);
          break;

        case 'getCroppedCanvas':
          if (result) {
            // Bootstrap's Modal
            //$('#getCroppedCanvasModal').modal().find('.modal-body').html(result);

           var d = document.createElement('div');
           d.className="croppedBox"
           d.innerHTML="<span class='cross_sign' onclick='this.parentNode.parentNode.removeChild(this.parentNode)'>&nbsp;X&nbsp;</span>" + 
                       "<input class='crop_info' type='hidden' value=\"" + crop_info + "\"/>";
           result.className="cropped";
           d.appendChild(result);
           $('#imageCollection').append(d);           
          }

          break;
      }

      if ($.isPlainObject(result) && $target) {
        try {
          $target.val(JSON.stringify(result));
        } catch (e) {
          console.log(e.message);
        }
      }

    }
  });


  // Keyboard
  $(document.body).on('keydown', function (e) {

    if (!$image.data('cropper') || this.scrollTop > 300) {
      return;
    }

    switch (e.which) {
      case 37:
        e.preventDefault();
        $image.cropper('move', -1, 0);
        break;

      case 38:
        e.preventDefault();
        $image.cropper('move', 0, -1);
        break;

      case 39:
        e.preventDefault();
        $image.cropper('move', 1, 0);
        break;

      case 40:
        e.preventDefault();
        $image.cropper('move', 0, 1);
        break;
    }

  });


});
function uploadAll(){
    var image_array = $(".cropped");
    var crop_info_array = $(".crop_info");
    image_count = 0;
    for (var i = 0; i < image_array.length; i++) {
      (function(){ 
          var j = i;
          image_array[j].toBlob(function (blob) {
          var formData = new FormData();
          console.log(j);
          console.log(crop_info_array.size());
          crop_info = crop_info_array[j].value;
          formData.append('document_id', $(".document")[0].id);
          formData.append('crop_info', crop_info);
          formData.append('file', blob);

         $.ajax('/api/v1/image', {
            method: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function () {
              console.log('Upload success');
            },
            error: function () {
               
              console.log('Upload error');
            },
            complete:  function(jqXHR, extStatus ){
              image_count++;
              console.log(image_count)
              if(image_count == image_array.length)
                window.location.href = "/documents/"+$(".document")[0].id; 
            }
          });
        },'image/jpeg')
      })();
    }
}


function searchFullDocument(){
    $.ajax({
        url: '/api/v1/search_by_document?document_id='+$(".document")[0].id+'&search_id=' + $(".search")[0].id,
        type: 'GET',
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
        },
        complete:  function(jqXHR, extStatus ){
           window.location.href = "/searches/"+ $(".search")[0].id;
        }
    });
}


function searchAll(){
    var image_array = $(".cropped");
    var crop_info_array = $(".crop_info");
    image_count = 0;
    for (var i = 0; i < image_array.length; i++) {
      (function(){
          var j = i;
          image_array[j].toBlob(function (blob) {
          var formData = new FormData();
          crop_info = crop_info_array[j].value;
          formData.append('document_id', $(".document")[0].id);
          formData.append('search_id', $(".search")[0].id);
          formData.append('crop_info', crop_info);
          formData.append('file', blob);
    
          $.ajax('/api/v1/search_by_image', {
             method: "POST",
             data: formData,
             processData: false,
             contentType: false,
             success: function (data) {
               console.log(data);
               console.log('Upload success');
             },
             error: function () {
               console.log('Upload error');
             },
             complete:  function(jqXHR, extStatus ){
               image_count++;
               console.log(image_count)
              if(image_count == image_array.length)
                 window.location.href = "/searches/"+ $(".search")[0].id;
             }
          });
        },'image/jpeg');
       })();
    }
}


