function playSound(file) {
    let audio = new Audio(file);
    audio.volume = 0.2;
    audio.play().catch((e)=>{});
}

function sound(data) {
//    console.log("Это sound, data: " + JSON.stringify(data));
    if(data) {
        var context = new AudioContext();
        var arrayBuffer = base64ToArrayBuffer(data);
        context.decodeAudioData(arrayBuffer, function(buffer) {
            var source = context.createBufferSource();
            source.buffer = buffer;
            source.connect(context.destination);
            source.start();
        });
    }
}

function playVoice(language, word, s) {
    let data = {
        'language': language,
        'word': word
    };

    s.addClass('disabled');
    $.ajax({
        type: 'POST',
        url: 'api/word/voice/random',
        contentType:'application/json; charset=utf-8',
        data: JSON.stringify(data),
        success: function(data) {
            sound(data.sound);
            setTimeout(function() {
                s.removeClass('disabled');
            }, 1000);
        },
        error: function() {
            m.text('Не удалось получить озвучку').fadeIn(10);
            setTimeout(function() {
                m.fadeOut(3000);
                s.removeClass('disabled');
            }, 5000);
        }
    });
}

function base64ToArrayBuffer(base64) {
  var binary_string = window.atob(base64);
  var len = binary_string.length;
  var bytes = new Uint8Array(len);
  for (var i = 0; i < len; i++) {
    bytes[i] = binary_string.charCodeAt(i);
  }
  return bytes.buffer;
}