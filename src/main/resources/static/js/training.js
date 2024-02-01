let t, l1, l2;

let trainingWords;
let trainResults = [];
let trainingId;

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

function stopTraining() {
    if (trainResults.length) {
        /* Отправить результат тренировки на сервер */
        let data = {
            "id": trainingId,
            "results": trainResults
        }
        $.ajax({
            type: 'POST',
            url: 'api/training/result',
            contentType:"application/json; charset=utf-8",
            data: JSON.stringify(data),
            success: function (data) {
                // console.log("Ответ на отправку результата тренировки: " + JSON.stringify(data));
                playSound('../snd/alert.mp3');
                $("#word_message").text('Результаты тренировки сохранены').fadeIn(10);
                setTimeout(function() {
                    $('#word_message').fadeOut(300, function() {
                        showTrainingMarkup(true);
                    });
                }, 300);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                playSound('../snd/error.mp3');
                $("#word_message").text('Не удалось отправить результаты тренировки').fadeIn(10);
                setTimeout(function() {
                    $('#word_message').fadeOut(1000, function() {
                        showTrainingMarkup(true);
                    });
                }, 1000);
            }
        });
    } else {
        showTrainingMarkup(true);
    }
}

function startTraining() {
//     console.log("Это startTraining, trainResults: " + JSON.stringify(trainResults));

    trainResults = [];
    /* Загружаем слова для тренировки */
    let data = {
        "lang1": l1.val().toLowerCase(),
        "lang2": l2.val().toLowerCase(),
        "collection": $('#collection').val()
    };
    $.ajax({
        type: 'POST',
        url: 'api/training',
        contentType:"application/json; charset=utf-8",
        data: JSON.stringify(data),
        success: function (data) {
            console.log("Слова для тренировки: " + JSON.stringify(data));
            if (data.words.length) {
                showTrainingMarkup(false);
                trainingId = data.id;
                trainingWords = data.words;
                $('#word_total').text(data.words.length);
                paintWord();
                $('#translation').focus();
            } else {
                $('#go_stop_btn').hide();
                $("#train_message").text('Слов для тренировки нет').fadeIn(10);
                setTimeout(function() {
                    $('#train_message').fadeOut(1000, function () {
                        $('#go_stop_btn').show();
                        $('#go_stop_btn').focus();
                    });
                }, 1000);
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            $("#word_message").text('Не удалось загрузить слова').fadeIn(10);
            setTimeout(function() {
                $('#word_message').fadeOut(2000);
            }, 5000);
        }
    });
    /* Обнуляем счётчики */
    $('.incorrect').text('0');
    $('.correct').text('0');
    $('#word_no').text('0');
}

function onGoStopButton() {
    /* Загружаем картинку тренировки */
    if('visible' === $('#train_markup').css("visibility")) {
        stopTraining();
    } else {
        startTraining();
    }
}

function loadCollections() {
    /* Загрузить существующие подборки этого пользователя для этой пары языков */
    $.ajax({
        type: 'GET',
        url: 'api/collection/names',
        contentType:"application/json; charset=utf-8",
        success: function (data) {
            for (let i = 0; i < data.length; i++) {
                $('#collection_list').append($('<option />').val(data[i]).text(data[i]));
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error('Не удалось получить список подборок');
            $("#collection_message").text('Не удалось получить список подборок').fadeIn(10);
            setTimeout(function() {
                $('#collection_message').fadeOut(3000);
            }, 5000);
        }
    });
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

function showTrainingMarkup(visible) {
    if(visible) {
        $('#swap_button').prop("disabled", false);
        l1.prop("disabled", false);
        l2.prop("disabled", false);
        $('#collection').prop("disabled", false);
        $('#train_markup').css("visibility", "hidden");
        $('#go_stop_btn').css('background-image', 'url("../img/go.png")');
    } else {
        $('#swap_button').prop("disabled", true);
        l1.prop("disabled", true);
        l2.prop("disabled", true);
        $('#collection').prop("disabled", true);
        $('#train_markup').css("visibility", "visible");
        $('#go_stop_btn').css('background-image', 'url("../img/stop.png")');
    }
}

function setTrainMarkupEnabled(enabled) {
    t.prop("disabled", !enabled);
    $('#check_btn').prop("disabled", !enabled);
}

function onCheckButton() {
//     console.log("Это onCheckButton, trainingWords: " + JSON.stringify(trainingWords[0]));
    if (!$('#check_btn').prop("disabled")) {
        if(trainingWords[0].t === t.val().toLowerCase()) {
//            console.log("Да, это верно");
            trainResults.push({
                "id": trainingWords[0].p,
                "correct": true
            });
            incCorrect();

//            setTimeout(function() {
//                sound(trainingWords[0].ts);
//            }, 1000);

            showResultCorrect(true);
            incCounter();
            trainingWords.shift();
        } else {
//            console.log("Нет, неверно");
            setTimeout(function() {
                sound(trainingWords[0].ts);
                trainResults.push({
                    "id": trainingWords[0].p,
                    "correct": false
                });
                incIncorrect();

                showResultCorrect(false);
                incCounter();
                trainingWords.shift();
            }, 500);
        }
    }
}

function showResultCorrect(correct) {
    if (correct) {
        playSound('../snd/success.mp3');
        $('#check_result').fadeTo(0, 1, function() {
            $(this).css('background-image', 'url("../img/correct.png")');
        }).delay(300).fadeTo(300, 0, function() {
            $('#check_result').css('background-image', '');
            if (trainingWords.length > 0) {
                paintWord();
            } else {
                paintEndTraining();
            }
        });
    } else {
        t.val(trainingWords[0].t);
        playSound('../snd/error.mp3');
        $('#check_result').fadeTo(0, 1, function() {
            $(this).css('background-image', 'url("../img/wrong.png")');
        }).delay(1500).fadeTo(1000, 0, function () {
            $('#check_result').css('background-image', '');
            if (trainingWords.length > 0) {
                paintWord();
            } else {
                paintEndTraining();
            }
        });
    }
}

function paintWord() {
//     console.log("paintWord, trainingWords: " + JSON.stringify(trainingWords));
    /* Берём первое слово и показываем его */
    sound(trainingWords[0].ws);
    $('#to_translate').text(trainingWords[0].w);
    t.val('');
    setTrainMarkupEnabled(true);
}

function swapLanguages() {
    let temp = l1.val();
    l1.val(l2.val());
    l2.val(temp);
}

function incCorrect() {
    let corr = $('.correct');
    if (corr.text()) {
        corr.text(parseInt(corr.text()) + 1);
    } else {
        corr.text(1);
    }
}
function incIncorrect() {
    let incorr = $('.incorrect');
    if (incorr.text()) {
        incorr.text(parseInt(incorr.text()) + 1);
    } else {
        incorr.text(1);
    }
}

function incCounter() {
    let c = $('#word_no');
    c.text(parseInt(c.text()) + 1);
}

function paintEndTraining() {
    // console.log("Надо завершать");
    $('#to_translate').text('');
    t.val('');
    setTrainMarkupEnabled(false);

    $('#go_stop_btn').focus();

    $("#word_message").text('Тренировка завершена').fadeIn(10);
    setTimeout(function() {
        $('#word_message').fadeOut(1000);
    }, 1000);
}