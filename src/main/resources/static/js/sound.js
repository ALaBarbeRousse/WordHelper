function playSound(file) {
    let audio = new Audio(file);
    audio.volume = 0.2;
    audio.play().catch((e)=>{});
}

/* todo */
function playVoice(language, word) {
    console.log("Это playVoice, language: " + language + ", word: " + word);
}