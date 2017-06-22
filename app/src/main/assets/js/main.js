if (!window.WebSocket) {
  $("#error_message").text("Your browser doesn't support WebSocket. Please update your browser");
  $("#error_link").attr("href", "https://browsehappy.com/").text("UPDATE YOUR BROWSER");

  $("#container").hide();
  $("#error_container").show();
  $("body").css("background-color", "#f44336");
}

var albumArt = $('#albumart');
var track = $('#track');
var artist = $('#album-artist');
var notification;
var oldVolume;

var url = window.location.toString().replace("http://", "").replace("https://", "");
url = url.substr(0, url.indexOf(":"));

var websocket = new WebSocket("ws://" + url + ":9621");
websocket.onmessage = function (message) {
  var info = message.data.split(": ");
  switch (info[0]) {
    case "ALBUM":
      artist.fadeTo(500, 0.50, function () {
        artist.html(info[1]);
      }).fadeTo(500, 1);
      break;
    case "TRACK":
      $("#intro_container").fadeTo(500, 0, function () {
        $("#container").show("fast");
        $(this).hide();
        $("body").css("cursor", "default");
      });

      track.fadeTo(500, 0.50, function () {
        track.html(info[1]);
      }).fadeTo(500, 1);
      break;
    case "PLAYING":
      var playing = info[1] === 'true';
      var title = playing ? '\u25B6 ' : "";

      setPlaying(playing, true);
      document.title = title + " Web Music Controller";
      break;
    case "ALBUM_ART":
      albumArt.fadeTo(500, 0.50, function () {
        albumArt.attr('src', info[1]);
      }).fadeTo(500, 1, function () {
        var vibrant = new Vibrant(albumArt[0], 64, 1);
        var swatches = vibrant.swatches();
        var color = swatches["Vibrant"];

        try {
          $("body").animate({
            backgroundColor: color.getHex()
          }, 500);
          $("#info, .material-icons, .big-material-icons").animate({
            color: color.getTitleTextColor()
          }, 500);
        } catch (e) {
          $("body").animate({
            backgroundColor: "#E0E0E0"
          }, 500);
          $("#info, .material-icons, .big-material-icons").animate({
            color: "#616161"
          }, 500);
        }

        if (notification !== undefined) notification.close();

        if (window.Notification && Notification.permission !== "denied") {
          Notification.requestPermission(function () {
            notification = new Notification(track.text(), {
              body: artist.text(),
              icon: albumArt.attr("src"),
              silent: true
            });
            notification.onclick = function () {
              window.focus();
              this.cancel();
            };

            setTimeout(function () {
              notification.close()
            }, 5000);
          });
        }
      });
      break;
    case "VOLUME":
      var volume = parseInt(info[1]);
      $("#volume_slider")[0].MaterialSlider.change(volume);

      if (volume === 0) {
        $("#volume_button_container").find("i").text("volume_off");
      } else {
        $("#volume_button_container").find("i").text("volume_up");
      }
      break;
  }
};

websocket.onclose = function () {
  showErrorSnackbar();
};
websocket.onerror = function () {
  showErrorSnackbar();
};

function forceReload() {
  $("body").css("cursor", "wait");
  websocket.send("reload");
}

function showErrorSnackbar() {
  $("#error_snackbar")[0].MaterialSnackbar.showSnackbar({
    message: 'Cannot connect to the device. Restart app and reload this page',
    timeout: 999999999,
    actionText: 'Reload',
    actionHandler: function () {
      window.location.reload(true);
    }
  });
}

$(document).ready(function () {
  $(document).keydown(function (ev) {
    var mute = ev.keyCode === 173;
    var volumeDown = ev.keyCode === 174;
    var volumeUp = ev.keyCode === 175;
	var pause = ev.keyCode === 32;

    var isFirefox = typeof InstallTrigger !== 'undefined';
    if (isFirefox) {
      mute = ev.keyCode === 181;
      volumeDown = ev.keyCode === 182;
      volumeUp = ev.keyCode === 183;
    }

    var currVolume = parseInt($("#volume_slider").val());

    if (mute) un_mute();
    if (volumeDown) setVolume(currVolume - 1);
    if (volumeUp) setVolume(currVolume + 1);
	if (pause) setPlaying(false);
  });
});

$("#volume_slider").on('input', function () {
  setVolume(this.value);
});

function setVolume(level) {
  if (level === -1 || level === 16) level--;

  if (level === 0) {
    $("#volume_button_container").find("i").text("volume_off");
  } else {
    $("#volume_button_container").find("i").text("volume_up");
  }

  websocket.send("volume=" + level)
}

function un_mute() {
  var isMuted = $("#volume_button_container").find("i").text() === "volume_off";
  var el = $("#volume_slider");

  if (isMuted) {
    setVolume(oldVolume);
  } else {
    oldVolume = el.val();
    setVolume(0);
  }
}

function previous() {
  websocket.send("previous");
}

function next() {
  websocket.send("next");
}

function setPlaying(playing, remote) {
  var el = $("#play_pause");
  if (playing) {
    el.text("pause");
  } else {
    el.text("play_arrow");
  }
  if (!remote) websocket.send("play_pause");
}

$(document).on('contextmenu', function (e) {
  $("#options_menu").css("top", e.clientY).css("left", e.clientX).show("fast");
  e.preventDefault();
});

$(document).on('mouseup', function (e) {
  var container = $("#options_menu");

  if (!container.is(e.target) && container.has(e.target).length === 0) {
    container.hide("fast");
  }
});

function openUrl(link) {
  window.open(link, "_blank");
}
