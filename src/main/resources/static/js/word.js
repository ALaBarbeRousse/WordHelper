// let languages = [];
$(document).ready(function() {
    loadLanguages();
    $('#lang1').on('input propertychange', function () {
        checkFields();
    });
    $('#lang2').on('input propertychange', function () {
        checkFields();
    });
    $('#word1').on('input propertychange', function () {
        checkFields();
    });
    $('#word2').on('input propertychange', function () {
        checkFields();
    });

    $('#ok_btn').on("click", function () {
        collectAndSendData();
    });
});

function loadLanguages() {
    $.ajax({
        type: 'GET',
        url: 'api/language',
        contentType:"application/json; charset=utf-8",
        success: function (data) {
            $.each(data, function (index, value) {
                $('#lang_list_1').append($('<option />').val(value.name).text(value.name));
                $('#lang_list_2').append($('<option />').val(value.name).text(value.name));
            });
        },
        error: function (jqXHR, textStatus, errorThrown) {
            $("#message").text('Не удалось получить список языков');
            $("#message").fadeIn(10);
            setTimeout(function() {
                $('#message').fadeOut(3000);
            }, 5000);
        }
    });
}

function checkFields() {
    if ($('#lang1').val() && $('#lang2').val() && $('#word1').val() && $('#word2').val()) {
        $('#ok_btn').prop("disabled", false);
    } else {
        $('#ok_btn').prop("disabled", true);
    }
}

function collectAndSendData() {
    let data = {
        "lang1": $('#lang1').val(),
        "lang2": $('#lang2').val(),
        "word1": $('#word1').val(),
        "word2": $('#word2').val()
    };

    /* todo Про потере фокуса одним из полей слова, если второе поле пусто, попытаться его заполнить из словаря */

    /* Sending POST to store translation */
    $.ajax({
        type: 'POST',
        url: 'api/word',
        contentType:"application/json; charset=utf-8",
        data: JSON.stringify(data),
        success: function (data) {
            $("#success").text('Перевод сохранён. Продолжить?');
        },
        error: function (jqXHR, textStatus, errorThrown) {
            $("#message").text('Не удалось сохранить словарную статью');
            $("#message").fadeIn(10);
            setTimeout(function() {
                $('#message').fadeOut(3000);
            }, 5000);
        }
    });
}