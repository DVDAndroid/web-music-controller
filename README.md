# web-music-controller

[![Build Status](https://travis-ci.org/DVDAndroid/web-music-controller.svg?branch=master)](https://travis-ci.org/DVDAndroid/web-music-controller)

Through this app you can control music from your web browser

The app starts two server: the web server at port `9620`, which provides the files, and the web socket at port `9621` which enable the data transfer

# How to connect

  - Browse to http://<device ip>:9620
  - Connect via cable

# Cable connection

  - You must have installed ADB in your machine.
  - Open terminal/CMD window and type

    ```sh
    adb forward tcp:9620 tcp:9620
    adb forward tcp:9621 tcp:9621
    ```

  - Your can browse from your PC to [http://localhost:9620](http://localhost:9620)


# Access with username and password

From the app you can set up a username and a password to access to the controller.
However, connection in not secure and password may be spied

# Credits

kabouzeid (Phonograph) for default album art image.