(ns miraj.html.protocols
  (:refer-clojure :exclude [list load meta]))

;; HTML5 Event protocols
;; https://www.w3.org/TR/uievents/

(defprotocol ^{:miraj/protocol? true}
    Http
  (request [rqst])
  (response [rsp])
  (error [e]))

(defprotocol ^{:miraj/protocol? true}
    UI
  "UI Events https://www.w3.org/TR/uievents/#events-uievent-types"
  (abort [x])
  (error [x])
  (load [x])
  (resize [x])
  (scroll [x])
  (select [x])
  (unload [x]))

(defprotocol ^{:miraj/protocol? true}
    Focus
  "Focus Events https://www.w3.org/TR/uievents/#events-focusevent"
  (blur [x])
  (focus [x])
  (focusin [x])
  (focusout [x]))

(defprotocol ^{:miraj/protocol? true}
    Mouse
  "Mouse Events https://www.w3.org/TR/uievents/#events-mouseevents"
  (click [x])
  (dblclick [x])
  (mousedown [x])
  (mouseenter [x])
  (mouseleave [x])
  (mousemove [x])
  (mouseout [x])
  (mouseover [x])
  (mouseup [x]))

(defprotocol ^{:miraj/protocol? true}
    Wheel
  "Wheel Events https://www.w3.org/TR/uievents/#events-wheelevents"
  (wheel [x]))

(defprotocol ^{:miraj/protocol? true}
    Input
  "Input Events https://www.w3.org/TR/uievents/#events-inputevents"
  (beforeinput [x])
  (input [x]))

(defprotocol ^{:miraj/protocol? true}
    Keyboard
  "Keyboard Events https://www.w3.org/TR/uievents/#events-keyboardevents"
  (keydown [x])
  (keyup   [x]))

(defprotocol ^{:miraj/protocol? true}
    Composition
  "Composition Events https://www.w3.org/TR/uievents/#events-compositionevents"
  (compositionstart [x])
  (compositionupdate   [x])
  (compositionupend   [x]))

(defprotocol ^{:miraj/protocol? true}
    Progress)

(defprotocol ^{:miraj/protocol? true}
    Animation)

(defprotocol ^{:miraj/protocol? true}
    Clipboard)

(defprotocol ^{:miraj/protocol? true}
    SpeechSynthesis
  (soundstart [x])
  (soundend [x]))

;; Legacy Events https://www.w3.org/TR/uievents/#legacy-event-types

;; DOMActivate, DomFocusIn, etc.
