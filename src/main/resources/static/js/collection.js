let collections = [];
let c, cs, ts, ft, w1, w2, d, wc, okb, wl;

function loadCollectionNames() {
    $.ajax({
        type: 'GET',
        url: 'api/collection/names',
        contentType:"application/json; charset=utf-8",
        success: function (data) {
            collections = data;
            for (let i = 0; i < data.length; i++) {
                $('#collections_list').append($('<option />').val(data[i]).text(data[i]));
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

/* Загрузить список слов коллекции */
function loadCollection(name) {
    // console.log("loadCollection: " + name);
    $.ajax({
        type: 'GET',
        url: 'api/collection',
        data: {
            name: name,
            lang1: l1.val(),
            lang2: l2.val()
        },
        contentType:"application/json; charset=utf-8",
        success: function (data) {
            // console.log("Получена подборка '" + name + "': " + JSON.stringify(data));
            for (let i = 0; i < data.length; i++) {
                addToCollection(data[i][0], data[i][1]);
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error('Не удалось получить содержимое подборки');
            $("#collection_message").text('Не удалось получить содержимое подборки').fadeIn(10);
            setTimeout(function() {
                $('#collection_message').fadeOut(3000);
            }, 5000);
        }
    });
}

function onCollectionChange() {
    // console.log("onCollectionChange, collections: " + JSON.stringify(collections));
    if (c.val()) {
        if (collections.includes(c.val())) {
            cs.removeClass('new_collection');
            cs.prop('title', '');
            loadCollection(c.val());
        } else {
            cs.addClass('new_collection');
            cs.prop('title', 'Новая подборка');
            wc.empty();
        }
        okb.prop('disabled', false);
    } else {
        cs.removeClass('new_collection');
        cs.prop('title', '');
        okb.prop('disabled', true);
        wc.empty();
    }
}

function translationPresent(word1, word2) {
    // console.log("translationPresent, word1: " + word1 + ", word2: " + word2);
    let ret = false;
    let collection = $('.words_pair');
    for (let i = 0; i < collection.length; i++) {
        let rWords = getRowWords($(collection[i]));
        if (rWords[0] === word1 && rWords[1] === word2) {
            ret = true;
            let item = $(collection[i]);
            item.addClass('selected_item');
            wc.scrollTop(item.position().top - wc.position().top + wc.scrollTop() - wc.height()/2);
            break;
        }
    }

    return ret;
}

function showTranslation(word1, word2) {
    w1.text(word1);
    w2.text(word2);
    d.removeClass('hidden');
    if (c.val()) {
        if (translationPresent(word1, word2)) {
            ft.prop('title', 'Уже присутствует в подборке');
        } else {
            ft.addClass('filled');
            ft.prop('title', 'Добавить в подборку');
        }
    }
}

function onSearchInput() {
    let word = ts.val().toLowerCase();

    // console.log("onSearchInput: " + word);

    if (ts.val().length < 1) {
        hideTranslation();
        return;
    }

    let data = {
        "from": l1.val().toLowerCase(),
        "word": word,
        "to": l2.val().toLowerCase()
    };

    $.ajax({
        type: 'POST',
        url: 'api/collection/translation',
        contentType:"application/json; charset=utf-8",
        data: JSON.stringify(data),
        success: function (data) {
            // console.log("Слово найдено: " + JSON.stringify(data));
            wl.empty();
            if (data.length === 0) {
                hideTranslation();
                $('.selected_item').removeClass('selected_item');
            } else if (data.length === 1) {
                if (l1.val().toLowerCase() === data[0].lang1 && l2.val().toLowerCase() === data[0].lang2) {
                    showTranslation(data[0].word1, data[0].word2);
                } else if (l2.val().toLowerCase() === data[0].lang1 && l1.val().toLowerCase() === data[0].lang2) {
                    showTranslation(data[0].word2, data[0].word1);
                }
            } else {
                let toHide = true;
                for (let i = 0; i < data.length; i++) {
                    // console.log("Проверяем " + data[i].word1 + " -> " + data[i].word2 + ", word: " + word);
                    if (data[i].word1.startsWith(word)) {
                        if (data[i].word1 === word) {
                            toHide = false;
                            if (l1.val().toLowerCase() === data[i].lang1 && l2.val().toLowerCase() === data[i].lang2) {
                                showTranslation(data[i].word1, data[i].word2);
                            } else if (l1.val().toLowerCase() === data[i].lang2 && l2.val().toLowerCase() === data[i].lang1) {
                                showTranslation(data[i].word2, data[i].word1);
                            }
                        }
                        wl.append($('<option />').val(data[i].word1).text(data[i].word1));
                    } else if (data[i].word2.startsWith(word)) {
                        if (data[i].word2 === word) {
                            toHide = false;
                            if (l1.val().toLowerCase() === data[i].lang1 && l2.val().toLowerCase() === data[i].lang2) {
                                showTranslation(data[i].word1, data[i].word2);
                            } else if (l1.val().toLowerCase() === data[i].lang2 && l2.val().toLowerCase() === data[i].lang1) {
                                showTranslation(data[i].word2, data[i].word1);
                            }
                        }
                        wl.append($('<option />').val(data[i].word2).text(data[i].word2));
                    }
                }
                if (toHide) {
                    hideTranslation();
                }
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error('Не удалось получить перевод');
            $("#collection_message").text('Не удалось получить перевод').fadeIn(10);
            setTimeout(function() {
                $('#collection_message').fadeOut(3000);
            }, 5000);
        }
    });
}

function swapTranslation() {
    let temp = w1.text();
    w1.text(w2.text());
    w2.text(temp);

    /* При перемене языков местами менять также и слова в подборке */
    let collection = $('.words_pair');
    for (let i=0; i< collection.length; i++) {
        let row = $(collection[i]);
        let spans = row.children('span');
        let left = $(spans[0]);
        let right = $(spans[2]);
        let temp = left.text();
        left.text(right.text());
        right.text(temp);
    }
}


function hideTranslation() {
    w1.empty();
    d.addClass('hidden');
    w2.empty();
    ft.removeClass('filled');
    ft.prop('title', '');
    $('.selected_item').removeClass('selected_item');
}

function addToCollection(toAdd1, toAdd2) {
    wl.empty();
    hideTranslation();

    let created = $('<div class="words_pair" />');
    created.prop('title', 'Убрать из подборки');
    created.on('click', function() {
        $(this).fadeOut(400, function () {
            $(this).remove();
        });
    });

    let createdW1 = $('<span />');
    createdW1.text(toAdd1);
    created.append(createdW1);

    let delim = $('<span class="delimiter"/>');
    created.append(delim);

    let createdW2 = $('<span />');
    createdW2.text(toAdd2);
    created.append(createdW2);

    created.hide();
    wc.prepend(created);

    created.fadeIn(200, function () {
        ts.val('');
        ts.focus();
    });
    ts.val('');
    ts.focus();
}

function getRowWords(row) {
    let spans = row.children('span');
    return [$(spans[0]).text(), $(spans[2]).text()];
}

function clearForm() {
    ts.val('');
    hideTranslation();
}

function gatherAndSendData() {
    let data = {
        "langs": [
            l1.val().toLowerCase(),
            l2.val().toLowerCase()
        ],
        "name": c.val(),
        "words": []
    }
    let collection = $('.words_pair');
    for (let i=0; i<collection.length; i++) {
        data.words.push(getRowWords($(collection[i])));
    }

    $.ajax({
        type: 'POST',
        url: 'api/collection',
        contentType:"application/json; charset=utf-8",
        data: JSON.stringify(data),
        success: function (data) {
            // console.log("Подборка успешно сохранена, data: " + JSON.stringify(data));
            playSound('../snd/success.mp3');
            clearForm();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            $("#collection_message").text('Не удалось сохранить подборку').fadeIn(10);
            setTimeout(function() {
                $('#collection_message').fadeOut(3000);
            }, 5000);
        }
    });
}

$(document).ready(function() {
    c = $('#collection_name');
    cs = $('#collection_sign');
    ts = $('#translation_search');
    ft = $('#found_translation');
    w1 = $('#word1');
    w2 = $('#word2');
    d = $('#delimiter');
    wc = $('#words_collection');
    okb = $('#ok_btn');
    wl = $('#words_list');

    loadLanguages();
    loadCollectionNames();

    c.on('input propertychange', function () {
        onCollectionChange();
    });

    ts.on('input', function () {
        onSearchInput();
    });

    $('#swap_button').on('click', function () {
        swapTranslation();
    });

    ft.on('click', function() {
        if (ft.hasClass('filled')) {
            addToCollection(w1.text(), w2.text());
        }
    });

    okb.on('click', function () {
        gatherAndSendData();
    });
});