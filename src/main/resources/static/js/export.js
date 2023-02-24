let languages = []

$(document).ready(function() {
    /* Первым делом - запросить пары языком, присутствующие в переводах */
    requestForLanguages();

    $('#left').on('input propertychange', function (event) {
        onLanguagesChange(event);
    });
    $('#right').on('input propertychange', function (event) {
        onLanguagesChange(event);
    });
});

function requestForLanguages() {
    // console.log("Это requestForLanguages");
    $.ajax({
        type: 'GET',
        url: 'api/translation/languages',
        contentType:"application/json; charset=utf-8",
        success: function (data) {
            // console.log("Получены языки для экспорта:" + JSON.stringify(data));
            languages = data;
            paintLanguages($('#left'), $('#right'));
        },
        error: function (jqXHR, textStatus, errorThrown) {
            // console.log("Не удалось получить список языков для экспорта");
            $("#message").text('Не удалось получить список языков для экспорта').fadeIn(10);
            setTimeout(function() {
                $('#message').fadeOut(1000);
            }, 1000);
        }
    });

    function paintLanguages(l, r) {
        // console.log("Это paintLanguages, languages: " + JSON.stringify(languages));
        let arr = [];
        for (let i = 0; i < languages.length; i++) {
            for (let j = 0; j < languages[i].length; j++) {
                if (!arr.includes(languages[i][j])) {
                    arr.push(languages[i][j]);
                }
            }
        }
        paintLanguage(l, arr);
        paintLanguage(r, arr);
    }
}

function getAnotherValue(arr, language) {
    if (arr.length >= 2) {
        if (arr.indexOf(language) === 1) {
            return arr[0];
        } else {
            return arr[1];
        }
    }
    return undefined;
}

function onLanguagesChange(e) {
    // console.log("Это onLanguagesChange id: " + JSON.stringify(e.target.id));
    let langSource = $('#' + e.target.id);
    // console.log("Содержимое: " + langSource.val());

    // console.log("langSource id: " + langSource.prop('id'));
    let langTarget = getPairedInput(langSource);
    // console.log("langTarget id: " + langTarget.prop('id'));
    repaintInputList(langTarget, langSource.val());

    function repaintInputList(target, option) {
        // console.log("Надо убрать из списка '" + target.prop('id') + "' значение '" + option +"'");
        let toPopulate = getPairedLanguages(option);
        $(target.prop('list')).children('option').remove();
        paintLanguage(langTarget, toPopulate);
    }

    /* TODO Добавить также фильтрацию по ранее взятым парам */
    function getPairedLanguages(language) {
        let appropriate = [];
        for (let p in languages) {
            if (languages[p].includes(language)) {  // Фильтрация по выбранному языку
                appropriate.push(getAnotherValue(languages[p], language));
            }
        }
        return appropriate;
    }

    function getPairedInput(source) {
        let array = source.parent().children('input');
        for (let i = 0; i < array.length; i++) {
            let item = $(array[i]);
            if (item.prop('id') !== source.prop('id')) return item;
        }
        return undefined;
    }
}

function paintLanguage(target, list) {
    // console.log("paintLanguage: " + target.prop('id') + "; list: " + JSON.stringify(list));
    if (list.length === 0) {
        $(target.prop('list')).children().remove();
        target.val('');
    } else {
        for (let i in list) {
            $(target.prop('list')).append($('<option />').val(list[i]).text(list[i]));
        }
        if (list.length === 1) {
            target.val(list[0]);
        }
    }
}