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
    screensaver
    volume <integer between 0 and 100>

### Spotify

    find playlist (for $SPOTIFY_USER_ID)
    play playlist <name>
    find album <query>
    play album <query>
    find track <query>
    play track <query>
    next track
    previous track
    play
    stop

* By default, searches only return results available in the US. This can be overriden using $SPOTIFY_COUNTRY.

* To enable playlist searches, please set $SPOTIFY_CLIENT_ID and $SPOTIFY_CLIENT_SECRET.

### Google search

* If no command is given, the input is treated as a Google search.

* Requires Google Custom Search engine (http://stackoverflow.com/questions/4082966/what-are-the-alternatives-now-that-the-google-web-search-api-has-been-deprecated).

* To enable Google search, please set $GOOGLE_API_KEY and $GOOGLE_SEARCH_ENGINE_ID.
