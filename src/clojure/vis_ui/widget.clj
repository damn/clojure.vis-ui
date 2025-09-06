(ns clojure.vis-ui.widget
  (:require [clojure.gdx.scenes.scene2d.actor :as actor])
  (:import (com.kotcrab.vis.ui.widget VisLabel
                                      VisSelectBox)))

(defn select-box [{:keys [items selected]}]
  (doto (VisSelectBox.)
    (.setItems ^"[Lcom.badlogic.gdx.scenes.scene2d.Actor;" (into-array items))
    (.setSelected selected)))

(defmethod actor/build :actor.type/select-box [opts]
  (select-box opts))

(defn label [{:keys [label/text] :as opts}]
  (doto (VisLabel. ^CharSequence text)
    (actor/set-opts! opts)))

(defmethod actor/build :actor.type/label [opts]
  (label opts))
