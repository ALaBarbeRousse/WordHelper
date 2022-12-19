function playSound(file) {
    let audio = new Audio(file);
    audio.volume = 0.2;
    audio.play().catch((e)=>{});
}