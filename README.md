# Jarvis

Voice-driven interface for stuff, inspired by Iron Man's Jarvis.

Currently only runs on OSX.

## Usage

Run a command in the terminal:

    lein run print hello world

Start Jarvis in server mode:

    lein run

Open the interface in the browser:

    localhost:3000

## Commands

### Basic

    print <input>
    say <input>
    start screensaver
    set volume <integer between 0 and 100>

### Spotify

    find playlists <user> (defaults to :user in .lein-env)
    play playlist <name>
    find albums <query>
    play album <query>
    find tracks <query>
    play track <query>
    next track
    last track
    play
    pause

* By default, searches only return results available in the US. To change this, change :country :country in .lein-env.

* To enable playlist searches, please fill in :client-id and :client-secret in .lein-env.

### Wolfram Alpha

* If no command is specified, the inputs are treated as a Wolfram Alpha query.

* Example queries: http://www.wolframalpha.com/examples/

* To enable Wolfram Alpha queries, please fill in :app-id in .lein-env.
