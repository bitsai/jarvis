# Jarvis

Voice-driven remote control for applications, inspired by Iron Man's Jarvis.

Currently only runs under OSX.

## Usage

To start Jarvis in server mode:

    lein run

Commands are sent to the server using HTTP POST requests:

    curl localhost:8080 -d "print hello world"

To test a command in the terminal:

    lein run print hello world

## Commands

### Basic

    print <words>: prints words in the terminal
    say <words>: reads words out loud using OSX "say"
    screensaver: starts screensaver
    volume <integer between 0 and 100>: sets system volume

### DVD

    dvd audio <integer>: select specified audio track
    dvd eject: eject DVD
    dvd fullscreen <true|false>: enables/disables fullscreen mode
    dvd play: skip any trailers, start playing DVD
    dvd quit: quit DVD player
    dvd start: start playback
    dvd stop: stop playback
    dvd subtitle <integer>: select specified subtitle

### Spotify

    spotify album <query>: search and play specified album
    spotify artist <query>: list top-5 popular albums for specified artist
    spotify next: next track
    spotify previous: previous track
    spotify quit: quit Spotify
    spotify start: start playback
    spotify stop: stop playback
    spotify track <query>: search and play specified track

### Wolfram Alpha

    alpha <query>: answers query
