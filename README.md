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

## Facebook integration

Follow the setup instructions here: https://developers.facebook.com/docs/messenger-platform/quickstart

If running locally, use ngrok to make webhook available to Facebook: https://ngrok.com/

To enable Facebook integration, please set $FACEBOOK_APP_ID, $FACEBOOK_PAGE_ID, and $FACEBOOK_PAGE_ACCESS_TOKEN.

## Commands

### Help

    help

### Basic

    print <input>
    say <input>
    set volume <integer between 0 and 100>
    start screensaver

### Spotify

    my playlist
    view my playlist <name>
    play my playlist <name> track <integer>
    play my playlist <name>
    find playlist <query>
    view playlist <query>
    play playlist <query> track <integer>
    play playlist <query>
    find album <query>
    view album <query>
    play album <query> track <integer>
    play album <query>
    next track
    previous track
    play music
    stop music

By default, searches only return results available in the US. This can be overriden using $SPOTIFY_COUNTRY.

To enable playlist commands, please set $SPOTIFY_CLIENT_ID, $SPOTIFY_CLIENT_SECRET, and $SPOTIFY_USER_ID.

### Weather announcement

    announce weather near <location>

To enable weather announcement, please set $WOLFRAM_ALPHA_APP_ID.

### Wolfram Alpha

If no command is given, the input is treated as a Wolfram Alpha query.

To enable Wolfram Alpha queries, please set $WOLFRAM_ALPHA_APP_ID.
