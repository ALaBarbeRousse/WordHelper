let w1, w2, l1, l2, m, words_env;
let r;
let wl1, wl2;

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
});

function loadLanguages() {
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
    } else {
        words_env.removeClass("translation_exists");
        words_env.prop("title", "");
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
            if (data) {
                if (data.suspect) {
                    wl1.empty();
                    for (let i = 0; i < data.suspect.length; i++) {
                        wl1.append($('<option />').val(data.suspect[i]).text(data.suspect[i]));
                    }
                } else {
                    wl1.empty();
                }

                if (data.translation) {
                    w2.val(data.translation);  // вставили перевод
                    showArticleExist(true);
                    validateFields();
                } else {
                    w2.val(null);  // Убрали перевод
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
}