let w1, w2, l1, l2, m, words_env;
let r;
let wl1, wl2;
let gv1, gv2;   // Кнопки загрузки звука

$(document).ready(function() {
    loadLanguages();

    w1 = $('#word1');
    w2 = $('#word2');
    l1 = $('#lang1');
    l2 = $('#lang2');
    m = $('#message');
    words_env = $('div.dict_article');
    r = $('#result_icon')
    wl1 = $('#word_list_1');
    wl2 = $('#word_list_2');
    gv1 = $('#getVoice1');
    gv2 = $('#getVoice2');

//    let ac;
//    if(!window.AudioContext) {
//            m.text('Браузер не поддерживает воспроизведение аудио').fadeIn(10);
//            setTimeout(function() {
//                m.fadeOut(3000);
//            }, 5000);
//        } else {
//            console.log("Audio OK");
//            window.AudioContext = window.webkitAudioContext;
//            ac = window.AudioContext;
//        }

    l1.on('input propertychange', function () {
        validateFields();
    });
    l2.on('input propertychange', function () {
        validateFields();
    });
    w1.on('input propertychange', function () {
        validateFields();
        findWordAndTranslation(l1.val(), w1.val(), l2.val());
    });
    w2.on('input propertychange', function () {
        validateFields();
        findWord(l2.val(), w2.val());
    });

    $('#ok_btn').on('click', function () {
        collectAndSendData();
    });

    w1.keypress(function (event){
        if (13 === event.which) {
            collectAndSendData();
        }
    });
    w2.keypress(function (event){
        if (13 === event.which) {
            collectAndSendData();
        }
    });

    $('#swap_button').on('click', function () {
        swapWords();
    });

    gv1.on('click', function() {
        if(gv1.hasClass('play_voice_btn')) {
            playVoice(l1.val(), w1.val(), gv1);
        } else if(gv1.hasClass('get_voice_btn')) {
            getVoice(l1.val(), w1.val(), gv1);
        }
    });
    gv2.on('click', function() {
        if(gv2.hasClass('play_voice_btn')) {
            playVoice(l2.val(), w2.val(), gv2);
        } else if(gv2.hasClass('get_voice_btn')) {
            getVoice(l2.val(), w2.val(), gv2);
        }
    });
});

function setLoadVoiceEnabled(enabled) {
    if(enabled) {
        gv1.removeClass("disabled");
        gv2.removeClass("disabled");
    } else {
        gv1.addClass("disabled");
        gv2.addClass("disabled");
    }
}

function loadLanguages() {
//    console.log("Загрузка языков");
    $.ajax({
        type: 'GET',
        url: 'api/language',
        contentType:"application/json; charset=utf-8",
        success: function (data) {
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

function validateFields() {
    if (l1.val() && l2.val() && w1.val() && w2.val()) {
        $('#ok_btn').prop("disabled", false);
    } else {
        showArticleExist(false);
        $('#ok_btn').prop("disabled", true);
    }
}

function collectAndSendData() {
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
                r.fadeTo(0, 1, function() {
                    $(this).css('background', 'url("../img/correct.png") no-repeat center');
                    playSound('../snd/success.mp3');
                }).delay(200).fadeTo(200, 0, function () {
                    r.css('background', 'none');
                    /* Удалить слова из полей, передвинуть фокус на первое */
                    w1.val('');
                    wl1.empty();
                    w1.focus();
                    w2.val('');
                    wl2.empty();
                    validateFields();
                });
            },
            error: function (jqXHR, textStatus, errorThrown) {
                m.text('Не удалось сохранить словарную статью').fadeIn(10);
                setTimeout(function() {
                    m.fadeOut(3000);
                }, 5000);
            }
        });
    }
}

/* Показать/убрать жёлтую рамку и тултип */
function showArticleExist(exists) {
    if (exists) {
        words_env.addClass("translation_exists");
        words_env.prop("title", "Перевод уже существует");
        setLoadVoiceEnabled(true);
    } else {
        words_env.removeClass("translation_exists");
        setLoadVoiceEnabled(false);
    }
}

function findWord(language, word) {
    if (!word.trim()) {
        wl2.empty();
        return;
    }

    let data = {
        "from": language,
        "word": word
    };

    $.ajax({
        type: 'POST',
        url: 'api/word/translate',
        contentType:"application/json; charset=utf-8",
        data: JSON.stringify(data),
        success: function (data) {
            if (data) {
                if (data.suspect) {
                    wl2.empty();
                    for (let i = 0; i < data.suspect.length; i++) {
                        wl2.append($('<option />').val(data.suspect[i]).text(data.suspect[i]));
                    }
                } else {
                    wl2.empty();
                }
            }
        },
        error: function () {
            m.text('Не удалось получить слово').fadeIn(10);
            setTimeout(function() {
                m.fadeOut(3000);
            }, 5000);
        }
    });
}

function findWordAndTranslation(from, word, to) {
    if (!word.trim()) {
        w2.val('');
        wl1.empty();
        return;
    }

    let data = {
        "from": from,
        "word": word,
        "to": to
    };
    $.ajax({
        type: 'POST',
        url: 'api/word/translate',
        contentType:"application/json; charset=utf-8",
        data: JSON.stringify(data),
        success: function (data) {
//            console.log("DATA: " + JSON.stringify(data));

            if (data) {
                if (data.suspect) {
                    if(data.suspect.length == 1 && word == data.suspect[0]) {
                        wl1.empty();
                        w1.val(data.suspect[0]);

                        if(data.wordVoicePresent) {
                            gv1.removeClass('get_voice_btn');
                            gv1.addClass('play_voice_btn');
                            gv1.removeClass('hidden');
                            gv1.attr('title', 'Произнести');
                        } else {
                            gv1.removeClass('play_voice_btn');
                            gv1.removeClass('hidden');
                            gv1.addClass('get_voice_btn');
                            gv1.attr('title', 'Загрузить звук');
                        }
                    } else {
                        gv1.removeClass('play_voice_btn');
                        gv1.addClass('hidden');
                        gv1.attr('title', 'Загрузить звук');
                        wl1.empty();
                        for (let i = 0; i < data.suspect.length; i++) {
                            wl1.append($('<option />').val(data.suspect[i]).text(data.suspect[i]));
                        }
                    }
                } else {
                    wl1.empty();
                }

                if (data.translation) {
                    w2.val(data.translation);  // вставили перевод
                    showArticleExist(true);
                    validateFields();

                    if(data.translationVoicePresent) {
                        gv2.removeClass("get_voice_btn");
                        gv2.removeClass('hidden');
                        gv2.addClass("play_voice_btn");
                        gv2.attr("title", "Произнести");
                    } else {
                        gv2.removeClass("play_voice_btn");
                        gv2.removeClass('hidden');
                        gv2.addClass("get_voice_btn");
                        gv2.attr("title", "Загрузить звук");
                    }
                } else {
                    w2.val(null);  // Убрали перевод
                    gv2.addClass('hidden');
                    showArticleExist(false);
                    validateFields();
                }
            }
        },
        error: function () {
            m.text('Не удалось получить перевод').fadeIn(10);
            setTimeout(function() {
                m.fadeOut(3000);
            }, 5000);
        }
    });
}

function swapWords() {
    let temp = l1.val();
    l1.val(l2.val());
    l2.val(temp);

    temp = w1.val();
    w1.val(w2.val());
    w2.val(temp);

    temp = gv1.attr('class');
    gv1.attr('class', gv2.attr('class'));
    gv2.attr('class', temp);
}

function getVoice(lang, word, source) {
    console.log("Это getVoice, lang: " + lang + ", word: " + word + ", source: " + source);
    source.addClass('disabled');
    $.ajax({
        type: 'GET',
        url: 'api/voice',
        data: {
            lang: lang,
            word: word
        },
        contentType:"application/json; charset=utf-8",
        success: function (data) {
            source.removeClass('disabled');
            source.removeClass('get_voice_btn');
            source.addClass('play_voice_btn');
        },
        error: function (jqXHR, textStatus, errorThrown) {
            m.text('Не удалось озвучивание для слова " + word + "').fadeIn(10);
            setTimeout(function() {
                m.fadeOut(3000);
                source.removeClass('disabled');
            }, 5000);
        }
    });
}