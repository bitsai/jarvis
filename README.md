# Jarvis

Voice-driven interface for stuff, inspired by Iron Man's Jarvis.

Currently only runs on OSX.

## Usage

Run a command in the terminal:

    lein run print hello world

Start Jarvis in server mode:

    lein run

Opening the interface in the browser:

    localhost:8080

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
    spotify playlists <user>: list playlists for user (defaults to .lein-env :user)
    spotify track <query>: play specified track
    spotify tracks <query>: list top 5 track results for the query

* By default, queries only return results available in the US. To change this, please update :country in .lein-env.

* To enable playlist searches, please fill in :client-id and :client-secret in .lein-env.

### Wolfram Alpha

* If no command is specified, the inputs are treated as a Wolfram Alpha query.

* Example queries: http://www.wolframalpha.com/examples/

* To enable Wolfram Alpha queries, please fill in :app-id in .lein-env.
