let wordToTranslate = {};
let nextWordReady = false;
let animationComplete = true;

let t, l1, l2;

$(document).ready(function() {
    t = $('#translation');
    l1 = $('#lang1');
    l2 = $('#lang2');

    loadLanguages();
    loadCollections();
    l1.on('input propertychange', function () {
        checkFields();
    });
    l2.on('input propertychange', function () {
        checkFields();
    });

    $('#go_stop_btn').on('click', function () {
        onGoStopButton();
    });

    $('#check_btn').on('click', function () {
        onCheckButton();
    });

    t.keypress(function (event) {
        if (13 === event.which) {
            onCheckButton();
        }
    });

    $('#swap_button').on('click', function () {
        swapLanguages();
    });
});

function getWord() {
    if('visible' === $('#train_markup').css("visibility")) {
        /* Загружаем слово */
        let data = {
            "lang1": l1.val().toLowerCase(),
            "lang2": l2.val().toLowerCase()
        };
        $.ajax({
            type: 'POST',
            url: 'api/training',
            contentType:"application/json; charset=utf-8",
            data: JSON.stringify(data),
            success: function (data) {
                paintWord(data);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                $("#word_message").text('Не удалось получить слово').fadeIn(10);
                setTimeout(function() {
                    $('#word_message').fadeOut(2000);
                }, 5000);
            }
        });
    }
}

function onGoStopButton() {
    /* Загружаем картинку тренировки */
    showTrainingMarkup();
    getWord();
}

function loadCollections() {
    /* todo Загрузить существующие подборки этого пользователя для этой пары языков */
}

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

function checkFields() {
    if (l1.val() && l2.val()) {
        $('#go_stop_btn').prop("disabled", false);
    } else {
        $('#go_stop_btn').prop("disabled", true);
    }
}

function showTrainingMarkup() {
    if('hidden' === $('#train_markup').css("visibility")) {
        $('#swap_button').prop("disabled", true);
        l1.prop("disabled", true);
        l2.prop("disabled", true);
        $('#collection').prop("disabled", true);
        $('#train_markup').css("visibility", "visible");
        $('#go_stop_btn').css('background-image', 'url("../img/stop.png")');
    } else {
        $('#swap_button').prop("disabled", false);
        l1.prop("disabled", false);
        l2.prop("disabled", false);
        $('#collection').prop("disabled", false);
        $('#train_markup').css("visibility", "hidden");
        $('#go_stop_btn').css('background-image', 'url("../img/go.png")');
    }
}

function setTrainMarkupEnabled(enabled) {
    t.prop("disabled", !enabled);
    $('#check_btn').prop("disabled", !enabled);
}

function onCheckButton() {
    if (!$('#check_btn').prop("disabled")) {
        showResultCorrect(wordToTranslate.translation === t.val().toLowerCase());
    }
}

function showResultCorrect(correct) {
    sendCheckResult(correct);

    animationComplete = false;
    if (correct) {
        playSound('../snd/success.mp3');
        $('#check_result').fadeTo(0, 1, function() {
            $(this).css('background-image', 'url("../img/correct.png")');
        }).delay(500).fadeTo(500, 0, function() {
            $('#check_result').css('background-image', '');
            animationComplete = true;
            if (nextWordReady) {
                paintWord(wordToTranslate);
            }
        });
    } else {
        /* todo Показать правильный перевод */
        t.val(wordToTranslate.translation);
        playSound('../snd/error.mp3');
        $('#check_result').fadeTo(0, 1, function() {
            $(this).css('background-image', 'url("../img/wrong.png")');
        }).delay(2500).fadeTo(500, 0, function () {
            $('#check_result').css('background-image', '');
            animationComplete = true;
            if (nextWordReady) {
                paintWord(wordToTranslate);
            }
        });
    }
}

function sendCheckResult(correct) {
    nextWordReady = false;

    let result = wordToTranslate;
    result.correct = correct;
    let data = {
        "lang1": l1.val().toLowerCase(),
        "lang2": l2.val().toLowerCase(),
        "result": result
    };

    $.ajax({
        type: 'POST',
        url: 'api/training',
        contentType:"application/json; charset=utf-8",
        data: JSON.stringify(data),
        success: function (data) {
            wordToTranslate = data;
            nextWordReady = true;
            if (animationComplete) {
                paintWord(data);
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            $("#word_message").text('Не удалось отправить результат проверки').fadeIn(10);
            setTimeout(function() {
                $('#word_message').fadeOut(2000);
            }, 3000);
        }
    });
}

function paintWord(data) {
    setTrainMarkupEnabled(true);
    wordToTranslate = data;
    $('#to_translate').text(data.word);
    t.val("").focus();
}

function swapLanguages() {
    let temp = l1.val();
    l1.val(l2.val());
    l2.val(temp);
}