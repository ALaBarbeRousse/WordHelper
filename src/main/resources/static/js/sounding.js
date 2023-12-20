let l1, l2;
let m;
let tl;
//let w1, w2, words_env;
//let r;
//let wl1, wl2;
//let gv1, gv2;   // Кнопки загрузки звука
let lt; // Кнопка загрузки случайного перевода

$(document).ready(function() {
    loadLanguages();

//    w1 = $('#word1');
//    w2 = $('#word2');
    l1 = $('#lang1');
    l2 = $('#lang2');
    lt = $('#translation_load_btn');
    m = $('#message');
    tl = $('#translation_list');
//    words_env = $('div.dict_article');
//    r = $('#result_icon')
//    wl1 = $('#word_list_1');
//    wl2 = $('#word_list_2');
//    gv1 = $('#getVoice1');
//    gv2 = $('#getVoice2');

//    l1.on('input propertychange', function () {
//        validateFields();
//    });
//    l2.on('input propertychange', function () {
//        validateFields();
//    });
    lt.on('click', function() {
        loadRandomTranslation();
    });
//    w1.on('input propertychange', function () {
//        validateFields();
//        findWordAndTranslation(l1.val(), w1.val(), l2.val());
//    });
//    w2.on('input propertychange', function () {
//        validateFields();
//        findWord(l2.val(), w2.val());
//    });

//    $('#ok_btn').on('click', function () {
//        collectAndSendData();
//    });

//    w1.keypress(function (event){
//        if (13 === event.which) {
//            collectAndSendData();
//        }
//    });
//    w2.keypress(function (event){
//        if (13 === event.which) {
//            collectAndSendData();
//        }
//    });

//    $('#getVoice1').on('click', function() {
//        getVoice($('#lang1').val(), $('#word1').val())
//    });
//    $('#getVoice2').on('click', function() {
//        getVoice($('#lang2').val(), $('#word2').val())
//    });
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

//function getVoice(lang, word) {
//    console.log("Это sounding getVoice, lang: " + lang + ", word: " + word);
//
////    $.ajax({
////        type: 'GET',
////        url: 'api/voice'
////    });
//    $.ajax({
//        type: 'GET',
//        url: 'api/voice',
//        data: {
//            lang: lang,
//            word: word
//        },
//        contentType:"application/json; charset=utf-8",
//        success: function (data) {
//            console.log("Озвучивание успешно получено.")
//        },
//        error: function (jqXHR, textStatus, errorThrown) {
//            m.text('Не удалось озвучивание для слова " + word + "').fadeIn(10);
//            setTimeout(function() {
//                m.fadeOut(3000);
//            }, 5000);
//        }
//    });
//}

function loadRandomTranslation() {
//    m.text("Загружаем случайный перевод: " + l1.val() + "-" + l2.val()).fadeIn(10);
    setTimeout(function() {
        m.fadeOut(3000);
    }, 5000);
    $.ajax({
        type: 'GET',
        url: 'api/voice/random',
        data: {
            lang1: l1.val(),
            lang2: l2.val()
        },
        contentType:"application/json; charset=utf-8",
        success: function (data) {
//            console.log("Озвучивание успешно получено: " + JSON.stringify(data));
            tl.empty();
            for(var i = 0; i < data.length; i++) {
                tl.append("<div class='tl_item'>" + data[i].word.writing + " &rarr; " + data[i].translation.writing + "</div");
            }
            tl.append("<div id='sound_fetch_btn' title='Найти озвучку'></>");
            /* Установить слушатель */
            $('#sound_fetch_btn').click(function() {
                findSounding(data, $('#sound_fetch_btn'));
            });
        },
        error: function (jqXHR, textStatus, errorThrown) {
            m.text('Не удалось загрузить перевод для озвучивания.').fadeIn(10);
            setTimeout(function() {
                m.fadeOut(3000);
            }, 5000);
        }
    });
}

function findSounding(data, source) {
//    console.log("Это findSounding, data: " + JSON.stringify(data));

    source.addClass('disabled');
    $("#translation_load_btn").addClass('disabled');
    $("#sound_fetch_btn").addClass('busy');

    var array = [];
    for(var i = 0; i < data.length; i++) {
        array.push({"language": data[i].wordLanguage.name, "word": data[i].word.writing});
        array.push({"language": data[i].translationLanguage.name, "word": data[i].translation.writing});
    }

    $.ajax({
        type: 'POST',
        url: 'api/voice/voices',
        data: JSON.stringify(array),
        contentType:"application/json; charset=utf-8",
        success: function (data) {
            playSound('../snd/success.mp3');
            m.text('Озвучка благополучно найдена.').fadeIn(10);
            setTimeout(function() {
                m.fadeOut(1000);
            }, 1000);
            $("#translation_load_btn").removeClass('disabled');
            tl.empty();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            m.text('Не удалось найти озвучивание.').fadeIn(10);
            playSound('../snd/error.mp3');
            setTimeout(function() {
                m.fadeOut(3000);
                source.removeClass('disabled');
            }, 5000);
        }
    });
}