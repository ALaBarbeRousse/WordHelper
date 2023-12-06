const kSampleRate = 44100;
const kNumSamples = 16834;
const kFrequency  = 440;
const kPI_2       = Math.PI * 2;

function playSound(file) {
    let audio = new Audio(file);
    audio.volume = 0.2;
    audio.play().catch((e)=>{});
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
            var context = new AudioContext();
            var arrayBuffer = base64ToArrayBuffer(data.sound);
            context.decodeAudioData(arrayBuffer, function(buffer) {
                var source = context.createBufferSource();
                source.buffer = buffer;
                source.connect(context.destination);
                source.start();

                s.removeClass('disabled');
            });
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