var listenButton = document.getElementById("listen");

if (!('webkitSpeechRecognition' in window)) {
    listenButton.disabled = true;
} else {
    var recognizer = new webkitSpeechRecognition();
    var recognizing = false;

    recognizer.onend = function(event) {
        listenButton.innerHTML = "Listen";
        recognizing = false;
    }

    recognizer.onresult = function(event) {
        var transcript = event.results[0][0].transcript;
        input.value = transcript;
        input.form.submit();
    }

    recognizer.onstart = function(event) {
        listenButton.innerHTML = "Listening";
    }

    function recognize() {
        if (recognizing) {
            recognizer.stop();
        } else {
            listenButton.innerHTML = "Initializing";
            recognizing = true;
            recognizer.start();
        }
    }
}
