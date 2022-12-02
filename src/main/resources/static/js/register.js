function fillInLangList() {
    $.ajax({
        type: 'GET',
        url: 'api/language',
        contentType:"application/json; charset=utf-8",
        success: function (data) {
            $.each(data, function (index, value) {
                $('#lang_list').append($('<option />').val(value.name).text(value.name));
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

$(document).ready(function() {
    $('#name').focus();

    fillInLangList();

    $("#ok_btn").on("click", function () {
        gatherDataAndSend();
    });

    $(document).on('keypress', function (e) {
        if(e.which === 13) {
            gatherDataAndSend();
        }
    });
});

function gatherDataAndSend() {
    let obj = {};
    obj.name = $("#name").val();
    obj.login = $("#login").val();
    obj.password = $("#pwd").val();
    obj.language = $("#lang").val();

    $.ajax({
        type: 'POST',
        url: 'api/student',
        data: JSON.stringify(obj),
        contentType:"application/json; charset=utf-8",
        success: function(data, textStatus, jqXHR) {
            history.back();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            $("#message").text('Не удалось создать нового пользователя');
            $("#message").fadeIn(10);
            setTimeout(function() {
                $('#message').fadeOut(3000);
            }, 5000);
        }
    });
}