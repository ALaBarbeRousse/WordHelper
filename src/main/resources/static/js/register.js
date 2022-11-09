$(document).ready(function() {
    $('#name').focus();

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