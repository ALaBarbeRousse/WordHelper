let w1, w2, l1, l2, m, words_env;
let r;
let wl1, wl2;
let d;
let wsw, tsw;   // Контейнеры кнопок звука
//let gv1, gv2;   // Кнопки звука

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

    wsw = $('#word_sound_wrapper');
    tsw = $('#translation_sound_wrapper')

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

    /* Опустошаем оба поля со словами */
    w1.val("");
    w2.val("");

    /* Убираем звуковые кнопки */
    wsw.empty();
    tsw.empty();

    /* Убираем кнопку удаления */
    d.attr("disabled", true);
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

        /* Убираем кнопку удаления */
        d.attr("disabled", true);

        /* Убираем кнопки звука */
        wsw.empty();
        tsw.empty();

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
                /* Что бы ни было получено, добавляем кнопку загрузки звука */
                wsw.html('<button id="word_voice_button" class="load_voice_button control_button" title="Загрузить"></button');

                /* todo Грузим звук */
                $('#word_voice_button').off('click');
                $('#word_voice_button').on('click', function() {
                    console.log('Грузим звук слова');
                });

                if (data.suspect) {
                    if (data.suspect.length == 0) {
                        w2.val(""); // Опустошаем второе слово
                        setFieldReadOnly(w2, false);  // Отпираем второе слово
                    } else {
//                        console.log("WORD: " + word);
                        if (data.wordVoicePresent) {
                            /* Ставим для кнопки звука слова другой класс - select_sound_button */
                            $('#word_voice_button').removeClass("load_voice_button");
                            $('#word_voice_button').attr('title', 'Произнести');
                            $('#word_voice_button').addClass("select_sound_button");

                            /* todo Играем звук */
                            $('#word_voice_button').off('click');
                            $('#word_voice_button').on('click', function() {
                                console.log('Играем звук слова');
                            });
                        }

                        if (data.suspect.length == 1) {
                            console.log("word: " + word + ", translation: " + data.translation);
                            w2.val(data.translation);
                            setFieldReadOnly(w2, true);  // Запираем второе слово

                            /* Опустошаем список подозреваемых */
                            wl1.empty();
                            if (data.suspect[0] != word) {
                                wl1.append($('<option />').val(data.suspect[0]).text(data.suspect[0]));
                            }


                            /* Добавляем кнопку загрузки звука перевода */
                            if (data.translationVoicePresent) {
                                tsw.html('<button id="translation_voice_button" class="select_sound_button control_button" title="Произнести"></button>');

                                /* todo Играем звук перевода*/
                                $('#translation_voice_button').off('click');
                                $('#translation_voice_button').on('click', function() {
                                    console.log("Играем звук перевода");
                                });
                            } else {
                                tsw.html('<button id="translation_voice_button" class="load_voice_button control_button" title="Загрузить"></button>');

                                /* todo Грузим звук перевода */
                                $('#translation_voice_button').off('click');
                                $('#translation_voice_button').on('click', function() {
                                    console.log("Грузим звук перевода");
                                });
                            }
                        } else if (data.suspect.length > 1) {
                            /* Надо заполнить список подозреваемых, если их больше 1 */
                            console.log("Заполняем suspect: " + JSON.stringify(data.suspect));
                            wl1.empty();
                            for (let i = 0; i < data.suspect.length; i++) {
                                wl1.append($('<option />').val(data.suspect[i]).text(data.suspect[i]));
                            }
                            /* Опустошаем поле перевода */
                            w2.val("");
                            /* Удаляем кнопку звука перевода */
                            tsw.empty();
                        }
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
//    console.log("setFieldReadOnly: " + readOnly);
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