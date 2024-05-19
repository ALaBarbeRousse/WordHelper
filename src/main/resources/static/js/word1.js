let w1, w2, l1, l2, m, words_env;
let r;
let wl1, wl2;
let d;
//let gv1, gv2;   // Кнопки загрузки звука

//let wbpl, wbpr; // Левая и правая панели кнопок голосов.
//let lvl, rvl;   // Левый и правый список голосов

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

    d = $('#delete_button');

    loadLanguages();

    $('#swap_button').on('click', function () {
        swapLanguages();
    });

    w1.on('input propertychange', function () {
        validateFields();
        findWordAndTranslation(l1.val(), w1.val(), l2.val());
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

    w1.val("");
    w2.val("");
}

function validateFields() {
    if (l1.val() && l2.val() && w1.val() && w2.val()) {
        $('#ok_btn').prop("disabled", false);
    }
//    else {
//        showArticleExist(false);
//        $('#ok_btn').prop("disabled", true);
//    }
}

function findWordAndTranslation(from, word, to) {
    if (!word.trim()) {
//        console.log("Пусто");
        w2.val('');
        wl1.empty();

        /* Запираем второе слово */
        setFieldReadOnly(w2, true);

//        $("div.word_buttons div.play_voice_btn").remove();
        return;
    }

    /* Отпираем второе поле */
    setFieldReadOnly(w2, false);

    let data = {
        "from": from,
        "word": word,
        "to": to
    };
//    console.log("Данные для получения перевода: " + JSON.stringify(data));

    $.ajax({
        type: 'POST',
        url: 'api/word/translate',
        contentType:"application/json; charset=utf-8",
        data: JSON.stringify(data),
        success: function(data) {
            console.log("SUCCESS: " + JSON.stringify(data));
            if (data) {
                if (data.suspect) {
                    if (data.suspect.length == 0) {
                        w2.val(""); // Опустошаем второе слово
                        setFieldReadOnly(w2, false);  // Отпираем второе слово
                    } else if (data.suspect.length == 1) {
                        console.log("word: " + word + ", translation: " + data.translation);
                        w2.val(data.translation);
                        setFieldReadOnly(w2, true);  // Запираем второе слово

                    } else if (data.suspect.length > 1) {
                        /* Надо заполнить список подозреваемых, если их больше 1 */
                        console.log("suspect: " + JSON.stringify(data.suspect));
                        wl1.empty();
                        for (let i = 0; i < data.suspect.length; i++) {
                            wl1.append($('<option />').val(data.suspect[i]).text(data.suspect[i]));
                        }
                        w2.val("");
                    }
                } else {
                    w2.val("");
                }
            }
        },
        error: function () {
            m.text('Не удалось получить перевод').fadeIn(30);
            playSound('../snd/error.mp3');
            setTimeout(function() {
                m.fadeOut(1000);
            }, 1000);
        }
    });
}

function setFieldReadOnly(field, readOnly) {
    console.log("setFieldReadOnly: " + readOnly);
    if(readOnly) {
        field.attr("readonly", true);
        field.addClass("readonly");
        d.removeAttr("disabled");
    } else {
        field.removeAttr("readonly");
        field.removeClass("readonly");
        d.attr("disabled", true);
    }
}