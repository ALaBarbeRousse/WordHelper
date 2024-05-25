let w1, w2, l1, l2, m, words_env;
let r;
let wl1, wl2;
let d;
let wsw, tsw;   // Контейнеры кнопок звука
let ok;         // Кнопка OK
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
    tsw = $('#translation_sound_wrapper');

    ok = $('#ok_btn');

    loadLanguages();

    $('#swap_button').on('click', function () {
        swapLanguages();
    });

    w1.on('input propertychange', function () {
        validateFields();
        findWordAndTranslation(l1.val(), w1.val(), l2.val());
    });
    w2.on('input propertychange', function () {
        validateFields();
    });

    ok.on('click', function() {
        collectAndSendData();
    });

    d.on('click', function() {
        askForDeletion(l1.val(), w1.val(), l2.val(), w2.val());
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
    playSound('../snd/switch-5.mp3');

    let temp = l1.val();
    l1.val(l2.val());
    l2.val(temp);

    /* Опустошаем оба поля со словами */
    w1.val("");
    w2.val("");
    validateFields();
}

function validateFields(translation, translationVoicePresent) {
//    console.log("Это validateFields, translation: " + translation + ", translationVoicePresent: " + translationVoicePresent);

    if (l1.val().trim() && w1.val().trim() ) {
        if (translation) {
            // Перевод есть
//            console.log('Закрываем второе слово');
            setFieldReadOnly(w2, true);
            d.attr("disabled", false);

            if (translationVoicePresent) {
                tsw.html('<button id="translation_voice_button" class="select_sound_button control_button" title="Произнести"></button>');

                /* todo Играем звук перевода*/
                $('#translation_voice_button').off('click');
                $('#translation_voice_button').on('click', function() {
                    console.log("Играем звук перевода");
                });
            } else {
                tsw.html('<button id="translation_voice_button" class="load_voice_button control_button" title="Загрузить"></button>');

                /* Грузим звук перевода */
                $('#translation_voice_button').off('click');
                $('#translation_voice_button').on('click', function() {
//                    console.log("Грузим звук перевода: " + w2.val());
                    loadSound(w2.val());
                });
            }
        } else {
            // Перевода нет
//            console.log('Открываем второе слово');
            setFieldReadOnly(w2, false);
            d.attr("disabled", true);
        }
    } else {
        wsw.empty();
    }

    if (l2.val().trim() && w2.val().trim()) {
//        console.log("Есть второе слово");
        if (translationVoicePresent) {
            tsw.html('<button id="translation_voice_button" class="select_sound_button control_button" title="Произнести"></button>');

            /* todo Играем звук перевода*/
            $('#translation_voice_button').off('click');
            $('#translation_voice_button').on('click', function() {
                console.log("Играем звук перевода");
            });
        } else {
            tsw.html('<button id="translation_voice_button" class="load_voice_button control_button" title="Загрузить"></button>');

            /* Грузим звук перевода */
            $('#translation_voice_button').off('click');
            $('#translation_voice_button').on('click', function() {
//                console.log("Грузим звук перевода: " + w2.val());
                loadSound(w2.val());
            });
        }
    } else {
//        console.log("Второго слова нет");
        tsw.empty();
    }

    if (l1.val().trim() && l2.val().trim() && w1.val().trim() && w2.val().trim()) {
//        console.log("4 поля, translation: " + translation);
        if (!translation) {
//            console.log("Открываем OK");
            ok.prop("disabled", false);
        } else {
//            console.log("Закрываем OK");
            ok.prop("disabled", true);
        }
    } else {
        ok.prop("disabled", true);
    }
}

function findWordAndTranslation(from, word, to) {
    if (!word.trim()) {
//        console.log("Пусто");
        w2.val("");
        validateFields();
        wl1.empty();

        /* Запираем второе слово */
//        console.log("setFieldReadOnly 1");
//        setFieldReadOnly(w2, true);

        /* Убираем кнопки звука */
        wsw.empty();
        tsw.empty();

        return;
    }

    /* Отпираем второе поле */
//    console.log("setFieldReadOnly 2");
//    setFieldReadOnly(w2, false);

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
//            console.log("SUCCESS: " + JSON.stringify(data));
            if (data) {
                /* Что бы ни было получено, добавляем кнопку загрузки звука */
                wsw.html('<button id="word_voice_button" class="load_voice_button control_button" title="Загрузить"></button');

                /* Грузим звук слова */
                $('#word_voice_button').off('click');
                $('#word_voice_button').on('click', function() {
//                    console.log('Грузим звук слова: ' + w1.val());
                    loadSound(w1.val());
                });

                if (data.suspect) {
                    if (data.suspect.length == 0) {
                        w2.val(""); // Опустошаем второе слово

                        validateFields();
//                        console.log("setFieldReadOnly 3");
//                        setFieldReadOnly(w2, false);  // Отпираем второе словоыцфз
                    } else {
//                        console.log("WORD: " + word);
                        if (data.wordVoicePresent) {
                            /* Ставим для кнопки звука слова другой класс - select_sound_button */
                            $('#word_voice_button').removeClass("load_voice_button");
                            $('#word_voice_button').attr('title', 'Произнести');
                            $('#word_voice_button').addClass("select_sound_button");

                            /* todo Играем звук слова */
                            $('#word_voice_button').off('click');
                            $('#word_voice_button').on('click', function() {
                                console.log('Играем звук слова');
                            });
                        }

                        if (data.suspect.length == 1) {
//                            console.log("LENGTH 1 word: " + word + ", translation: " + data.translation + ", translationVoicePresent: " + data.translationVoicePresent);
                            w2.val(data.translation);
                            validateFields(data.translation, data.translationVoicePresent);
//                            console.log("setFieldReadOnly 4");
//                            setFieldReadOnly(w2, true);  // Запираем второе слово

                            /* Опустошаем список подозреваемых */
                            wl1.empty();
                            if (data.suspect[0] != word) {
                                wl1.append($('<option />').val(data.suspect[0]).text(data.suspect[0]));
                            }
                        } else if (data.suspect.length > 1) {
                            /* Надо заполнить список подозреваемых, если их больше 1 */
//                            console.log("Заполняем suspect: " + JSON.stringify(data.suspect));
                            wl1.empty();
                            for (let i = 0; i < data.suspect.length; i++) {
                                wl1.append($('<option />').val(data.suspect[i]).text(data.suspect[i]));
                            }
                            /* Опустошаем поле перевода */
                            if (data.translation) {
//                                console.log("Ставим перевод, найденный из нескольких подозреваемых: " + data.translation);
                                w2.val(data.translation);
                                d.removeAttr("disabled");
                                validateFields(data.translation, data.translationVoicePresent);
                            } else {
//                                console.log("Очищаем перевод");
                                w2.val("");
                                validateFields();
                            }
                        }
                    }
                } else {
                    w2.val("");
                    validateFields();
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
//        d.removeAttr("disabled");
    } else {
        field.removeAttr("readonly");
        field.removeClass("readonly");
//        d.attr("disabled", true);
    }
}

function collectAndSendData() {
//    console.log("Это collectAndSendData");
    if (l1.val() && l2.val() && w1.val() && w2.val()) {
        let data = {
            "lang1": l1.val(),
            "lang2": l2.val(),
            "word1": w1.val(),
            "word2": w2.val()
        };

        /* Sending POST to store translation */
        $.ajax({
            type: 'POST',
            url: 'api/word',
            contentType:"application/json; charset=utf-8",
            data: JSON.stringify(data),
            success: function (data) {
                playSound('../snd/success.mp3');
                /* Удалить слова из полей, передвинуть фокус на первое */
                w1.val('');
                wl1.empty();
                w1.focus();
                w2.val('');
                wl2.empty();
                validateFields();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                playSound('../snd/error.mp3');
                m.text('Не удалось сохранить словарную статью').fadeIn(5);
                setTimeout(function() {
                    m.fadeOut(2000);
                }, 2000);
            }
        });
    }
}

function loadSound(word) {
    console.log("Грузим звук: " + word);
}

function askForDeletion(wordLang, word, translationLang, translation) {
//    console.log("Это deleteTranslation, wordLang: " + wordLang + ", word " + word + ", translationLang: " + translationLang + ", translation: " + translation);
    $('<div></div>').appendTo('body')
        .html('<div><h5>Удаляется перевод</h5></div><div><h3>' + word + '&harr;' + translation + '</h3></div><div><h5>Продолжить?</h5></div>')
        .dialog({
            modal: true,
            title: 'Удаление перевода',
            zIndex: 10000,
            autoOpen: true,
            width: '560px',
            resizable: false,
            buttons: {
                "Нет": function() {
                    $(this).dialog("close");
                },
                "Да": function() {
                    // $(obj).removeAttr('onclick');
                    // $(obj).parents('.Parent').remove();
                    sendDeletion(wordLang, word, translationLang, translation);
                    $(this).dialog("close");
                }
            },
            close: function(event, ui) {
                $(this).remove();
            }
        });
};

function sendDeletion(wordLang, word, translationLang, translation) {
    let data = {
        "lang1": wordLang,
        "word1": word,
        "lang2": translationLang,
        "word2": translation
    }
    console.log("Удаляется перевод: " + JSON.stringify(data));
    $.ajax({
        type: 'DELETE',
        url: 'api/word/translate',
        contentType:"application/json; charset=utf-8",
        data: JSON.stringify(data),
        success: function (data) {
            console.log("Перевод удалён, data: " + JSON.stringify(data));
        },
        error: function (jqXHR, textStatus, errorThrown) {
            $("#message").text('Не удалось удалить перевод');
            $("#message").fadeIn(10);
            setTimeout(function() {
                $('#message').fadeOut(5000, function() {
                    $("#message").text('');
                });
            }, 5000);
        }
    });
}