let w1, w2, l1, l2, m, words_env;
let r;
let wl1, wl2;
let gv1, gv2;   // Кнопки загрузки звука

let wbpl, wbpr; // Левая и правая панели кнопок голосов.
let lvl, rvl;   // Левый и правый список голосов

$(document).ready(function() {
    w1 = $('#word1');
    w2 = $('#word2');
    l1 = $('#lang1');
    l2 = $('#lang2');
    m = $('#message');
    words_env = $('div.dict_article');
    r = $('#result_icon')
    wl1 = $('#word_list_1');
    wl2 = $('#word_list_2');

    loadLanguages();

    $('#swap_button').on('click', function () {
        swapLanguages();
    });
});

function loadLanguages() {
//    console.log("Загрузка языков");
    $.ajax({
        type: 'GET',
        url: 'api/language',
        contentType:"application/json; charset=utf-8",
        success: function (data) {
//            console.log("Получены языки: "+ JSON.stringify(data));
            $.each(data.list, function (index, value) {
                if(data.usedLanguages[0] === value.id) {
                    l1.val(value.name)
                }
                if(data.usedLanguages[1] === value.id) {
                    l2.val(value.name)
                }
                $('#lang_list_1').append($('<option />').val(value.name).text(value.name));
                $('#lang_list_2').append($('<option />').val(value.name).text(value.name));
            });
        },
        error: function (jqXHR, textStatus, errorThrown) {
            m.text('Не удалось получить список языков').fadeIn(10);
            setTimeout(function() {
                m.fadeOut(3000);
            }, 5000);
        }
    });
}

function swapLanguages() {
    let temp = l1.val();
    l1.val(l2.val());
    l2.val(temp);
}