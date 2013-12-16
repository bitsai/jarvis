import android
import sys
import urllib

droid = android.Android()
html = "file:///sdcard/sl4a/scripts/jarvis.html"
url = "http://192.168.1.2:8080"

droid.webViewShow(html)

while True:
  input = droid.eventWait().result["data"]
  if input == "exit":
    sys.exit()
  try:
    r = urllib.urlopen(url, input)
    output = r.read().replace("\n", "<br>")
  except Exception as e:
    output = str(e)
  droid.eventPost("output", output)
