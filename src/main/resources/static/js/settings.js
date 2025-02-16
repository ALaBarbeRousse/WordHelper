let abs; // Чекбок разрешения фоновой загрузки звуков
let cavl;// Надпись "Проверять неозвученные слова..."
let cav; // Величина интервала
let cau; // Единица интервала
let ssb; // Кнопка записи настроек
let rsb; // Кнопка сброса настроек
let m;   // Строчка сообщения

let settings, tempora = new Object();

$(document).ready(function() {
    loadSettings();

    abs = $("#allow_bg_sounding_cb");
    calv = $('#check_amnt_value_label');
    cav = $('#check_amnt_value');
    cau = $('#check_amnt_unit');
    ssb = $('#send_settings_btn');
    rsb = $('#reset_settings_btn');
    m = $('#message');

    abs.on('input propertychange', function() {
        checkFields(abs.prop('checked'));
        validateFields();
    })
    cav.on('input propertychange', function() {
        validateFields();
    });
    cau.on('input propertychange', function() {
        validateFields();
    });

    ssb.on('click', function() {
        saveAndExit();
    });
    rsb.on('click', function() {
        location.href='/main';
    })
});

function saveAndExit() {
    ssb.prop('disabled', true);

    $.ajax({
        type: 'POST',
        url: '/api/settings/background/sounding',
        contentType:"application/json; charset=utf-8",
        data: JSON.stringify(tempora),
        success: function(data) {
            m.addClass('success');
            m.text("Успешно записано.");
            playRetardedSound('../snd/success.mp3', function() {
                location.href='/main';
            });
        },
        error: function() {
            m.addClass('failure');
            m.text("Ошибка при записи.");
            ssb.prop('disabled', false);
            playSound('../snd/error.mp3');
        }
    });
};

function validateFields() {
//    console.log("Это validateFields, settings: " + JSON.stringify(settings));
    tempora.enabled = abs.prop('checked');
    tempora.value = parseInt(cav.val());
    tempora.unit = cau.val();
    if(JSON.stringify(settings) === JSON.stringify(tempora)) {
        ssb.prop('disabled', true);
    } else {
        ssb.prop('disabled', false);
    }
}

function checkFields(checked) {
    if(checked) {
        calv.removeClass('disabled');
        cav.prop('disabled', false);
        cau.prop('disabled', false);
    } else {
        calv.addClass('disabled');
        cav.prop('disabled', true);
        cau.prop('disabled', true);
    }
}

function loadSettings() {
    console.log("Грузим настройки фоновой загрузки озвучки.")
    $.ajax({
        type: 'GET',
        url: '/api/settings/background/sounding',
        contentType:"application/json; charset=utf-8",
        success: function(data) {
//            console.log('Настройки загружены: ' + data);
            if(data) {
                settings = $.parseJSON(data);
                abs.prop('checked', settings.enabled);
                cav.prop('value', settings.value);
                cau.prop('value', settings.unit);
                checkFields(settings.enabled);
                validateFields();
            }
        },
        error: function() {
            console.log('Ошибка при загрузке настроек');
        }
    });
}