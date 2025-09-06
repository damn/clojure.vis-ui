(ns clojure.vis-ui.window
  (:require [clojure.gdx.scenes.scene2d :as scene2d]
            [clojure.gdx.scenes.scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Window)
           (com.kotcrab.vis.ui.widget VisWindow)))

(defn create
  [{:keys [title
           modal?
           close-button?
           center?
           close-on-escape?]
    :as opts}]
  (let [window (doto (scene2d/proxy-group VisWindow [^String title true]) ; true = showWindowBorder
                 (.setModal (boolean modal?)))]
    (when close-button?    (.addCloseButton window))
    (when center?          (.centerWindow   window))
    (when close-on-escape? (.closeOnEscape  window))
    window))

; TODO buggy FIXME
(defn title-bar?
  "Returns true if the actor is a window title bar."
  [actor]
  (when (instance? Label actor)
    (when-let [p (actor/parent actor)]
      (when-let [p (actor/parent p)]
        (and (instance? VisWindow actor)
             (= (.getTitleLabel ^Window p) actor))))))
