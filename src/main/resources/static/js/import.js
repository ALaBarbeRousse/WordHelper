$(document).ready(function() {
    $("#dict_input").on('input propertychange', function () {
        $("#import_btn").prop('disabled', false);
    });

    $("#import_btn").on('click', function () {
        uploadFile();
    });
});

function uploadFile() {
    $("#import_btn").prop('disabled', true);

    var fileInput = $('#dict_input')[0];

    if (fileInput.files.length === 0) {
      alert('Пожалуйста, выберите файл.');
      return;
    }

    var file = fileInput.files[0];
    var formData = new FormData();
    formData.append('file', file);

    $.ajax({
        type: "POST",
        url: 'api/translation/import',
        data: formData,
        contentType: false,
        processData: false,
        success: function(data) {
            playSound('../snd/success.mp3');
            showImportMessage(data, "success_message");
        },
        error: function(jqXHR, textStatus, errorThrown) {
            playSound('../snd/error.mp3');
            showImportMessage("Ошибка при импорте словаря.", "error_message");
        }
    })
    .always(function() {
        $("#import_btn").prop('disabled', false);
    });
}

function showImportMessage(text, cssClass) {
    var m = $("#import_message");
    m.addClass(cssClass);
    m.text(text).fadeIn(50);
        setTimeout(function() {
            m.fadeOut(1000, function() {
                m.removeClass(cssClass).text("");
            });
        }, 1500);
}