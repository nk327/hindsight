# Hindsight <img src="https://github.com/cparish312/hindsight/blob/main/assets/hindsight_icon.png" width="40">

Look back on your digital experience with Hindsight! (very alpha)

---

## Overview
Hindsight is an android app that takes a screenshot every 2 seconds. The server upload functionality allows a user to upload the screenshots to a server running on their personal computer.

### Demo
<a href="https://www.loom.com/share/669eecf3c04648d4aae1565ead56273f">
    <img style="max-width:300px;" src="https://cdn.loom.com/sessions/thumbnails/669eecf3c04648d4aae1565ead56273f-with-play.gif">
</a>

## Setup
### Server
1) **Conda Environment:**
*   Install [conda](https://docs.anaconda.com/free/miniconda/miniconda-install/) if you don't have it already
*   Create the conda environment: `conda env create -f hindsight_server_env.yml`
*   Activate the env using: `conda activate hindsight_server`
*   If running on a Mac run `pip install ocrmac` to utilize OCR
2) **SSL Configuration:**
*   Openssl is used to create ssl keys for running the server over Https. The keys are expected in `$Home/.hindsight_server` but that can be changed in `hindsight_server/run_server.py`. Copy `hindsight_server/template_san.cnf` to `$Home/.hindsight_server/san.cnf` and replace the `${}` sections. 
*   Run `openssl req -new -nodes -keyout server.key -out server.csr -config san.cnf`
*   Run `openssl x509 -req -days 365 -in server.csr -signkey server.key -out server.crt -extensions v3_ca -extfile san.cnf`. You now have the public and private keys for running the server.
*   You need to generate a `.der` file for the app to authenticate the identify of the server. Run `openssl x509 -outform der -in server.crt -out hindsight_server.der`
*   You can change the directory that images are stored in by modifying `RAW_SCREENSHOTS_DIR` within `hindsight_server/config.py`
3) **Start the server:** `python hindsight_server/run_server.py`

### App
1) Open `hindsight_android` in Android Studio.
2) In `hindsight_android/app/src/main/java/com/connor/hindsight/network/RetrofitClient.kt` change `BASE_URL` to the base url of your server including the port. This should be printed out when `python hindsight_server.py` is run and should also be the ip of `${computer_ip}` in the `san.cnf`.
3) Move `hindsight_server.der` into `hindsight_android/app/src/main/res/raw/`
4) You should be good to run the app on your device!

## Features
Currently the app has 2 "working" functionalities.
1) **Screen Recording:** Toggle screen recording on or off.
2) **Server Upload:** Upload screenshots directly to your server.
* The screenshots timeline can be viewed and searched by running `python timeline_view.py`

## Developing
You can easily access and build applications on top of Hindsight.
To access the database you can use the functions within `hindsight_server/db.py`:
```python
from db import HindsightDB

db = HindsightDB()

frames_df = db.get_frames()

search_results = db.search(text="hindsight")
```

## Settings
* **User Activity Detection:** This setting is recommended as, depending on your phone usage, it can signifantly save battery life. The app will only take a screenshot if the user has been active since the last screenshot.

## Permissions
* **Screen Broadcasting:** The App requires screen broadcasting each time recording is started. Ideally this is only needed if the phone is turned off or if the user intentionally stops recording (pausing is available as well). However, the app is very buggy so it may be required more often.
* **Accessibility Access:** In order to use the `Only Take Screenshot when User is Active` setting and for the screenshot to be saved with the name of the application running, Accessibility access is required. Unfortunetly, this is the only robust way I could find to introduce this functionality.

## Contributions
Feedback and contributions are welcome! Please reach out if you have ideas or want to help improve Hindsight.

## Considerations
* **Security:** Lots of security concerns so would love feedback in that area!
* **Battery Usage:** It uses around 4% of my new google pixel8's battery. Hopefully this can be reduced even more, and I'm very curious if battery life will be too big of a limitation for other phones.
* **iPhone:** Unfortunetly, this would not work on iPhone as the broadcast functionality would require user permission each time the phone screen turns off.
* **Testing:** This has only been tested on a Pixel 8 and M3 Mac