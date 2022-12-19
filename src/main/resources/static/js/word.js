let w1, w2, l1, l2, m, words_env;
let inserted = null;
let r;

$(document).ready(function() {
    loadLanguages();

    w1 = $('#word1');
    w2 = $('#word2');
    l1 = $('#lang1');
    l2 = $('#lang2');
    m = $('#message');
    words_env = $('div.dict_article');
    r = $('#result_icon')

    l1.on('input propertychange', function () {
        checkFields();
    });
    l2.on('input propertychange', function () {
        checkFields();
    });
    w1.on('input propertychange', function () {
        checkFields();
        findWord(l1, w1, l2, w2);
    });
    w2.on('input propertychange', function () {
        checkFields();
        findWord(l2, w2, l1, w1);
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

function checkFields() {
    if (l1.val() && l2.val() && w1.val() && w2.val()) {
        $('#ok_btn').prop("disabled", false);
    } else {
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
                    w1.val(''); w1.focus(); w2.val(''); checkFields();
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

function findWord(from, word, to, another) {
    let data = {
        "from": from.val(),
        "word": word.val(),
        "to": to.val()
    };
    $.ajax({
        type: 'POST',
        url: 'api/word/translate',
        contentType:"application/json; charset=utf-8",
        data: JSON.stringify(data),
        success: function (data) {
            if (data && data.translation) {
                another.val(data.translation);  // вставили перевод
                inserted = another;
                showArticleExist(true);
                checkFields();
            } else {
                if (another === inserted) {
                    another.val(null);  // Убрали перевод
                    inserted = null;
                }
                showArticleExist(false);
                checkFields();
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

function playSound(file) {
    let audio = new Audio(file);
    audio.volume = 0.2;
    audio.play().catch((e)=>{});
}

function swapWords() {
    let temp = l1.val();
    l1.val(l2.val());
    l2.val(temp);
    temp = w1.val();
    w1.val(w2.val());
    w2.val(temp);
}