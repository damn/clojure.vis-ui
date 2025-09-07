(ns clojure.vis-ui.widget
  (:require [clojure.gdx.graphics.g2d.texture-region :as texture-region]
            [clojure.gdx.scenes.scene2d :as scene2d]
            [clojure.gdx.scenes.scene2d.actor :as actor]
            [clojure.gdx.scenes.scene2d.ui.widget :as widget]
            [clojure.gdx.scenes.scene2d.ui.table :as table]
            [clojure.gdx.scenes.scene2d.utils :as utils]
            [clojure.vis-ui.tooltip :as tooltip])
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)
           (com.badlogic.gdx.scenes.scene2d.ui Button
                                               Image)
           (com.badlogic.gdx.scenes.scene2d.utils ChangeListener
                                                  Drawable)
           (com.badlogic.gdx.utils Align
                                   Scaling)
           (com.kotcrab.vis.ui.widget VisCheckBox
                                      VisImage
                                      VisImageButton
                                      VisLabel
                                      VisSelectBox
                                      VisTable
                                      VisTextButton
                                      VisTextField
                                      VisWindow)))

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

(defn text-field
  [{:keys [text-field/text]
    :as opts}]
  (let [actor (-> (VisTextField. (str text))
                  (widget/set-opts! opts))]
    (when-let [tooltip (:tooltip opts)]
      (tooltip/add! actor tooltip))
    actor))

(defmethod actor/build :actor.type/text-field [opts]
  (text-field opts))

(defn check-box
  [{:keys [text on-clicked checked?]}]
  (let [^Button button (VisCheckBox. (str text))]
    (.setChecked button checked?)
    (.addListener button
                  (proxy [ChangeListener] []
                    (changed [event ^Button actor]
                      (on-clicked (.isChecked actor)))))
    button))

(defmethod actor/build :actor.type/check-box [opts]
  (check-box opts))

(defn table [opts]
  (-> (scene2d/proxy-group VisTable [])
      (table/set-opts! opts)))

(defmethod actor/build :actor.type/table [opts]
  (table opts))

(defn image-button
  [{:keys [drawable/texture-region
           on-clicked
           drawable/scale]
    :as opts}]
  (let [scale (or scale 1)
        [w h] (texture-region/dimensions texture-region)
        drawable (utils/drawable texture-region
                                 :width  (* scale w)
                                 :height (* scale h))
        image-button (VisImageButton. ^Drawable drawable)]
    (when on-clicked
      (.addListener image-button (utils/change-listener on-clicked)))
    (when-let [tooltip (:tooltip opts)]
      (tooltip/add! image-button tooltip))
    (table/set-opts! image-button opts)))

(defmethod actor/build :actor.type/image-button [opts]
  (image-button opts))

(defn text-button
  ([text on-clicked]
   (text-button {:text text
                 :on-clicked on-clicked}))
  ([{:keys [text
            on-clicked]
     :as opts}]
   (let [actor (doto (VisTextButton. (str text))
                 (.addListener (utils/change-listener on-clicked))
                 (table/set-opts! opts))]
     (when-let [tooltip (:tooltip opts)]
       (tooltip/add! actor tooltip))
     actor)))

(defmethod actor/build :actor.type/text-button [opts]
  (text-button opts))

(defn window
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
    (table/set-opts! window opts)))

(defmulti ^:private image* type)

(defmethod image* Drawable [^Drawable drawable]
  (VisImage. drawable))

(defmethod image* Texture [texture]
  (VisImage. (TextureRegion. ^Texture texture)))

(defmethod image* TextureRegion [texture-region]
  (VisImage. ^TextureRegion texture-region))

(defn image
  [object
   {:keys [scaling
           align
           fill-parent?]
    :as opts}]
  (let [^Image image (image* object)]
    (when (= :center align)
      (.setAlign image Align/center))
    (when (= :fill scaling)
      (.setScaling image Scaling/fill))
    (when fill-parent?
      (.setFillParent image true))
    (widget/set-opts! image opts)))
