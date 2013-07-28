# Blue Parrot

Like a parrot, the Blue Parrot is an app that (a) plays sound clips 
and (b) speaks text phrases, and does both of those at random times, 
intermixing them randomly.

You can control the max time between what it says. For instance, if you
specify three minutes, it will speak a quote or play a sound clip at least
once every three minutes. Because it does so randomly, some times it will take 
up to three minutes, some times they'll be just seconds apart, but over time
they should average out to about 90 seconds apart (half of three minutes).

(Note that the code currently only pauses one second between checks. This is
for development/debugging.)

There is no UI on the app yet, but you can run it from the command line like
this:

    $ sbt run

Besides fooling with the source code, the two files you can adjust are in the
"testing" subdirectory. Note that all sound files beneath the root sound file
directory are discovered, so they don't have to all be in one directory.

# Sample Sound Files

I currently have about 500 sound files in my directory. Here are some URLs
where I found some great sound clips:

Firefly
  http://www.moviesoundclips.net/sound.php?id=70
  http://wavs.unclebubby.com/firefly/

Nacho Libre
  http://www.moviewavs.com/Movies/Nacho_Libre.html

Short Circuit
  http://www.moviewavs.com/Movies/Short_Circuit.html
  http://www.dailywav.com/program.php?Program=ShortCircuit
  
Star Trek
  http://www.moviesoundclips.net/sound.php?id=42
  http://trekcore.com/audio/

Star Wars
  http://www.rosswalker.co.uk/star_wars_sounds/
  http://www.wheelon.com/sounds/sounds.htm

Wall-E
  http://www.moviesoundclips.net/sound.php?id=158

Many more ...
  http://www.moviewavs.com/Movies.html

