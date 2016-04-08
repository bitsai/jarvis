var input = document.getElementById("input");
var listenButton = document.getElementById("listen");

if (!('webkitSpeechRecognition' in window)) {
    listenButton.disabled = true;
} else {
    var recognizer = new webkitSpeechRecognition();
    var recognizing = false;

    recognizer.onstart = function(event) {
        listenButton.innerHTML = "Listening";
        recognizing = true;
    }

    recognizer.onresult = function(event) {
        input.value = event.results[0][0].transcript;
        input.form.submit();
    }

    recognizer.onend = function(event) {
        listenButton.innerHTML = "Listen";
        recognizing = false;
    }

    function recognize() {
        if (recognizing) {
            recognizer.stop();
        } else {
            recognizer.start();
        }
    }
}
