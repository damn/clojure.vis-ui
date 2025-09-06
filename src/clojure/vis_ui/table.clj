(ns clojure.vis-ui.table
  (:require [clojure.gdx.scenes.scene2d :as scene2d])
  (:import (com.kotcrab.vis.ui.widget VisTable)))

(defn create []
  (scene2d/proxy-group VisTable []))
