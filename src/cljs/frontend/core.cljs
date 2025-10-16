(ns frontend.core
  (:require ["/icons/Cross$default" :as Cross]
            ["@mui/icons-material/Home$default" :as Home]
            ["@mui/material/AppBar$default" :as AppBar]
            ["@mui/material/Button$default" :as Button]
            ["@mui/material/Checkbox$default" :as Checkbox]
            ["@mui/material/Container$default" :as Container]
            ["@mui/material/CssBaseline$default" :as CssBaseline]
            ["@mui/material/Stack$default" :as Stack]
            ["@mui/material/SvgIcon$default" :as SvgIcon]
            ["@mui/material/TextField$default" :as TextField]
            ["@mui/material/Toolbar$default" :as Toolbar]
            ["@mui/material/Typography$default" :as Typography]
            ["@mui/material/styles" :refer [createTheme ThemeProvider]]
            [frontend.handlers :as h]
            [frontend.subs :as s]
            [frontend.uix.hooks :refer [use-subscribe]]
            [re-frame.core :as rf]
            [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [uix.core :as uix :refer [$ defui]]
            [uix.dom]
            ["matter-js" :refer [Engine Render Runner Bodies Composite]]

            [quill-matter-js.engine :as en]
            [quill-matter-js.renderer :as re]

            ;; ["matter-js"]
            ;; ["/matter_demo.js"]
            ))


(defn inspect-body [body]
  (clj->js
    {:id (.-id body)
     :type (.-type body)
     :label (.-label body)
     :position [(.. body -position -x) (.. body -position -y)]
     :bounds {:min [(.. body -bounds -min -x) (.. body -bounds -min -y)]
              :max [(.. body -bounds -max -x) (.. body -bounds -max -y)]}
     :vertices-count (.-length (.-vertices body))}))

(def frame-rate 60)

(defn update-state [state]
  (->
   state
   (update :mj/engine en/update (/ 1000 frame-rate))))

(defn add-box [engine]
  (let [boxA (Bodies.rectangle 400 200 80 80)]
    (set! boxA.qtype "rect")
    (Composite.add (.. engine -world) #js [boxA])))

(defn add-bodies
  [engine bodies]
  (Composite.add (.. engine -world) (clj->js bodies)))

(defn ground []
  ;;             var ground = Bodies.rectangle(100, height - 20, width * 2, 20, { isStatic: true });
  (let [ground (Bodies.rectangle 300 590 600 20 #js {:isStatic true})]
    (set! ground.qtype "rect")
    ground))

(defn setup
  []
  (q/frame-rate frame-rate)
  (let [e (en/engine nil)]
    (add-box e)
    (add-bodies e [(ground)])
    {:mj/engine e}))

(defn pulse [low high rate]
  (let [diff (- high low)
        half (/ diff 2)
        mid (+ low half)
        s (/ (q/millis) 1000.0)
        x (q/sin (* s (/ 1.0 rate)))]
    (+ mid (* x half))))

(defn draw-state [state]
  (q/no-stroke)
  (q/background 255)
  (q/fill 200 0 0)
  (re/render (:mj/engine state) {}))

(defn sketch [host]
  (q/sketch
   :host host
   :size [600 600]
   :setup #'setup
   :update #'update-state
   :draw #'draw-state
   :features [:keep-on-top]
   :middleware [m/fun-mode]))

(defui app []
  (let [!host (uix/use-ref)
        _
        (uix/use-effect
         (fn []
           (sketch (.-current !host)))
         [])]
    ($ :div
       {:ref !host
        :style {:padding "30px"}})))

(def theme (createTheme
            #js {:components #js {:Checkbox #js {}}}))

(defui app-wrapper []
  ($ ThemeProvider
     {:theme theme}
     ;; Reset CSS, like body padding etc.
     ($ CssBaseline)
     ($ app)))

(defonce root
  ;; This def is also running in Karma tests, where the element isn't available.
  ;; Avoid errors, we won't need the react root there?
  (when-let [el (js/document.getElementById "app")]
     (uix.dom/create-root el)))

(defn render []
  (uix.dom/render-root ($ app-wrapper) root))

(defn ^:export init []
  (render))

(comment


  (js/console.log (inspect-body (first bodies)))
  (js/Object.getOwnPropertyNames (first bodies))
  (.-type (first bodies))


  (js/console.log (.-parts (first bodies)))
  (js/console.log (first (.-parts (first bodies))))


  (.- (first bodies) qtype)

  ;; #js
  ;; ["id" "type" "label" "parts" "plugin" "angle" "vertices" "position"
  ;;  "force" "torque" "positionImpulse" "constraintImpulse"
  ;;  "totalContacts" "speed" "angularSpeed" "velocity" "angularVelocity"
  ;;  "isSensor" "isStatic" "isSleeping" "motion" "sleepThreshold"
  ;;  "density" "restitution" "friction" "frictionStatic" "frictionAir"
  ;;  "collisionFilter" "slop" "timeScale" "render" "events" "bounds"
  ;;  "chamfer" "circleRadius" "positionPrev" "anglePrev" "parent" "axes"
  ;;  "area" "mass" "inertia" "deltaTime" "_original" "inverseInertia"
  ;;  "inverseMass" "sleepCounter"]


  )
