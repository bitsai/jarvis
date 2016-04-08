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

    find playlists <user> (defaults to .lein-env :spotify :user)
    play playlist <name>
    find albums <query>
    play album <query>
    find tracks <query>
    play track <query>
    next track
    last track
    play
    pause

* By default, searches only return results available in the US. To change this, change .lein-env :spotify :country.

* To enable playlist searches, please fill in :lein-env :spotify :client-id and :client-secret.

### Google search

* If no command is given, the input is treated as a Google search.

* Requires Google Custom Search engine (http://stackoverflow.com/questions/4082966/what-are-the-alternatives-now-that-the-google-web-search-api-has-been-deprecated).

* To enable Google search, please fill in .lein-env :google :api-key and :search-engine-id.
