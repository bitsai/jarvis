# Jarvis

Voice-driven interface for stuff, inspired by Iron Man's Jarvis.

Currently only runs on OSX.

## Usage

Run a command in the terminal:

    lein run print hello world

Start Jarvis in server mode:

    lein run

Commands are sent to the server using HTTP requests:

    curl localhost:8080 -data-urlencode input="print hello world"

Browser testing:

    localhost:8080/?input=print hello world

## Commands

### Basic

    print <inputs>: print inputs in the terminal
    say <inputs>: read inputs aloud using OSX "say"
    screensaver: start screensaver
    volume <integer between 0 and 100>: set system volume

### Spotify

    spotify next: next track
    spotify previous: previous track
    spotify start: start playback
    spotify stop: stop playback
    spotify album <query>: play specified album
    spotify albums <query>: list top 5 album results for the query
    spotify track <query>: play specified track
    spotify tracks <query>: list top 5 track results for the query

* Currently, queries only return US-available results. To change this, please update the "country" var in src/jarvis/commands/spotify.clj.

### Wolfram Alpha

* If no command is specified, the inputs are treated as a Wolfram Alpha query.

* Example queries: http://www.wolframalpha.com/examples/

* To enable Wolfram Alpha queries, please fill in the "app-id" var in src/jarvis/commands/wolfram.clj.
