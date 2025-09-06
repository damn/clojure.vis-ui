(ns clojure.vis-ui.tooltip
  (:require [clojure.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.scenes.scene2d.stage :as stage]
            [clojure.gdx.utils.align :as align]
            [clojure.vis-ui.label :as label])
  (:import (com.kotcrab.vis.ui.widget Tooltip)))

(defn- update-fn [tooltip]
  (when-not (string? tooltip-text)
    (let [actor (Tooltip/.getTarget tooltip)
          ; acturs might be initialized without a stage yet so we do when-let
          ; FIXME double when-let
          ctx (when-let [stage (actor/get-stage actor)]
                (stage/get-ctx stage))]
      (when ctx ; ctx is only set later for update!/draw! ... not at starting of initialisation
        (Tooltip/.setText tooltip (str (tooltip-text ctx)))))))

(defn add!
  [actor tooltip-text]
  (let [text? (string? tooltip-text)
        label (doto (label/create (if text? tooltip-text ""))
                (.setAlignment (align/k->value :center)))]
    (doto (proxy [Tooltip] []
            ; hooking into getWidth because at
            ; https://github.com/kotcrab/vis-blob/master/ui/src/main/java/com/kotcrab/vis/ui/widget/Tooltip.java#L271
            ; when tooltip position gets calculated we setText (which calls pack) before that
            ; so that the size is correct for the newly calculated text.
            (getWidth []
              (update-fn this)
              (proxy-super getWidth)))
      (.setTarget  actor)
      (.setContent label)))
  actor)

(defn remove! [actor]
  (Tooltip/removeTooltip actor))
