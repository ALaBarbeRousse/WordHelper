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

    $("#export_btn").on('click', function (){
        gatherAndSendData();
    });
});

function getFullLanguageList() {
    let arr = [];
    for (let i = 0; i < languages.length; i++) {
        for (let j = 0; j < languages[i].length; j++) {
            if (!arr.includes(languages[i][j])) {
                arr.push(languages[i][j]);
            }
        }
    }
    return arr;
}

function requestForLanguages() {
    // console.log("Это requestForLanguages");
    $.ajax({
        type: 'GET',
        url: 'api/translation/languages',
        contentType:"application/json; charset=utf-8",
        success: function (data) {
            // console.log("Получены языки для экспорта:" + JSON.stringify(dщтata));
            languages = data;
            paintLanguages($('#left'), $('#right'));
        },
        error: function (jqXHR, textStatus, errorThrown) {
            // console.log("Не удалось получить список языков для экспорта");
            $("#export_message").text('Не удалось получить список языков для экспорта').fadeIn(10);
            setTimeout(function() {
                $('#export_message').fadeOut(1000);
            }, 1000);
        }
    });

    function paintLanguages(l, r) {
        // console.log("Это paintLanguages, languages: " + JSON.stringify(languages));
        let arr = getFullLanguageList();

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

    /* TODO Проверяем, можно ли добавлять строчку */
    /* todo */

    /* Проверяем, можно ли отправлять */
    $("#export_btn").prop('disabled', !checkFields());

    function repaintInputList(target, option) {
        // console.log("repaintInputList '" + target.prop('id') + "' убираем значение '" + option +"'");
        let toPopulate = "";
        if (option) {
            toPopulate = getPairedLanguages(option);
        } else {
            toPopulate = getFullLanguageList();
            target.val("");
        }
        // console.log("Получен список для заполнения: " + JSON.stringify(toPopulate));
        $(target.prop('list')).children('option').remove();
        // console.log("Очистили список " + target.prop('id'));
        paintLanguage(langTarget, toPopulate);
    }

    /* Добавить также фильтрацию по ранее взятым парам */
    function getPairedLanguages(language) {
        // console.log("Получаем список для заполнения: " + language + ". При этом languages: " + JSON.stringify(languages));
        let appropriate = [];
        for (let p in languages) {
            // console.log("Проверяем язык на включение в список: " + languages[p]);
            if (languages[p].includes(language)) {  // Фильтрация по выбранному языку
                // console.log("Берём новый язык из пары: " + languages[p]);
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
    // console.log("Заполняем список: " + target.prop('id') + "; list: " + JSON.stringify(list));
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

function checkFields() {
    let fields = $("input.export_input");
    for (let i = 0; i < fields.length; i++) {
        if (!$(fields[i]).val()) {
            return false;
        }
    }
    return true;
}

function gatherAndSendData() {
    // console.log("gatherAndSendData");
    let rows = $(".lang_row");
    let data = [];
    for (let i = 0; i < rows.length; i++) {
        let inputs = $(rows[i]).children("input");
        let rowData = [];
        for (let j = 0; j < inputs.length; j++) {
            rowData.push($(inputs[j]).val());
        }
        data.push(rowData);
    }

    function isArrayOfStrings(key, value) {
        if (key && Array.isArray(value)) {
            value.forEach(function(item){
                if(typeof item !== 'string'){
                    return false;
                }
            });
            return true;
        }
        return false;
    }

    $.ajax({
        type: 'POST',
        url: 'api/translation/export',
        contentType:"application/json; charset=utf-8",
        data: JSON.stringify(data),
        success: function (data) {
            $("<a />", {
                "download": "dictionaries.json",
                "href" : "data:application/json," + encodeURIComponent(JSON.stringify(data, null, 2))
            }).appendTo("body")
                .click(function() {
                    $(this).remove();
                })[0].click();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log("Ошибка при экспорте словарей.");
            $("#export_message").text('Ошибка при экспорте словарей.').fadeIn(10);
            setTimeout(function() {
                $('#export_message').fadeOut(1000);
            }, 1000);
        }
    });
}