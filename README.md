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
    set volume <integer between 0 and 100>
    start screensaver

### Spotify

    show playlist (for $SPOTIFY_USER_ID)
    play playlist <name>
    find album <query>
    play album <query>
    find track <query>
    play track <query>
    next track
    previous track
    play music
    stop music

* By default, searches only return results available in the US. This can be overriden using $SPOTIFY_COUNTRY.

* To enable playlist commands, please set $SPOTIFY_CLIENT_ID and $SPOTIFY_CLIENT_SECRET.

### Weather Announcement

    weather near <location>

* To enable weather announcement, please set $WOLFRAM_ALPHA_APP_ID.

### Wolfram Alpha

* If no command is given, the input is treated as a Wolfram Alpha query.

* To enable Wolfram Alpha queries, please set $WOLFRAM_ALPHA_APP_ID.
