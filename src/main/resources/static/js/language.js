let l1, l2;

$(document).ready(function() {
    l1 = $('#lang1');
    l2 = $('#lang2');

    $('#swap_button').on('click', function () {
        swapLanguages();
    });
});

function loadLanguages() {
    $.ajax({
        type: 'GET',
        url: 'api/language',
        contentType:"application/json; charset=utf-8",
        success: function (data) {
            $.each(data.list, function (index, value) {
                let capitalized = value.name.charAt(0).toUpperCase() + value.name.slice(1);
                if(data.usedLanguages[0] === value.id) {
                    l1.val(capitalized)
                }
                if(data.usedLanguages[1] === value.id) {
                    l2.val(capitalized)
                }
                $('#lang_list_1').append($('<option />').val(capitalized).text(capitalized));
                $('#lang_list_2').append($('<option />').val(capitalized).text(capitalized));
            });
        },
        error: function (jqXHR, textStatus, errorThrown) {
            $("#lang_message").text('Не удалось получить список языков').fadeIn(10);
            setTimeout(function() {
                $('#message').fadeOut(3000);
            }, 5000);
        }
    });
}

function swapLanguages() {
    let temp = l1.val();
    l1.val(l2.val());
    l2.val(temp);
}