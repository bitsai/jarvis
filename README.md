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

    print <inputs>: print inputs in the terminal
    say <inputs>: read inputs aloud using OSX "say"
    screensaver: start screensaver
    volume <integer between 0 and 100>: set system volume

### Spotify

    spotify album <query>: play specified album
    spotify albums <query>: list top 5 album results for specified query
    spotify next: next track
    spotify previous: previous track
    spotify start: start playback
    spotify stop: stop playback
    spotify track <query>: play specified track
    spotify tracks <query>: list top 5 track results for specified query

* Currently, queries only return US-available results. To change this, update the "country" var in src/jarvis/commands/spotify.clj.

### Wolfram Alpha

* If no command is specified, a Wolfram Alpha query is performed.

* Example queries: http://www.wolframalpha.com/examples/

* To enable Wolfram Alpha queries, please fill in "appid" in line 10 of src/jarvis/commands/wolfram.clj.
