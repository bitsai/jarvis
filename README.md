# Jarvis

Voice-driven remote control for applications, inspired by Iron Man's Jarvis.

Currently only runs under OSX.

## Usage

Run a command in the terminal:

    lein run print hello world

Start Jarvis in server mode:

    lein run

Commands are sent to the server using HTTP POST requests:

    curl localhost:8080 -d "print hello world"

## Commands

### Basic

    print <words>: print words in the terminal
    say <words>: read words out loud using OSX "say"
    screensaver: start screensaver
    volume <integer between 0 and 100>: set system volume

### DVD

    dvd audio <integer>: select specified audio track
    dvd eject: eject DVD
    dvd fullscreen <true|false>: enable/disable fullscreen mode
    dvd play: skip trailers, start playing DVD
    dvd quit: quit DVD player
    dvd start: start playback
    dvd stop: stop playback
    dvd subtitle <integer>: select specified subtitle

### Spotify

    spotify album <query>: search and play specified album
    spotify albums <query>: list top 5 album results for specified query
    spotify next: next track
    spotify previous: previous track
    spotify quit: quit Spotify
    spotify start: start playback
    spotify stop: stop playback
    spotify track <query>: search and play specified track
    spotify tracks <query>: list top 5 track results for specified query

### Wolfram Alpha

* If no command is specified, a Wolfram Alpha query is performed.

* Example queries: http://www.wolframalpha.com/examples/

* To enable Wolfram Alpha queries, please fill in "appid" in line 10 of src/jarvis/commands/wolfram.clj.
