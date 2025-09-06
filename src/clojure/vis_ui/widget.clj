(ns clojure.vis-ui.widget
  (:require [clojure.gdx.scenes.scene2d.actor :as actor])
  (:import (com.kotcrab.vis.ui.widget VisSelectBox)))

(defn select-box [{:keys [items selected]}]
  (doto (VisSelectBox.)
    (.setItems ^"[Lcom.badlogic.gdx.scenes.scene2d.Actor;" (into-array items))
    (.setSelected selected)))

(defmethod actor/build :actor.type/select-box [opts]
  (select-box opts))
