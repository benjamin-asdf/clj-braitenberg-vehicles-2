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
            [uix.core :as uix :refer [$ defui]]
            [uix.dom]
            ["/matter_demo.js"]))


(let [colors (atom (cycle
                     ;;     const colors = ['#000000', '#ffffff',
                     ;;     '#00ffff', '#ff8800'];
                    ["#ff0000" "#00ff00" "#ffff00"
                      "#00ffff"]))
      next-color! (fn []
                    (let [c (first @colors)]
                      (swap! colors next)
                      c))]
  (defn color-change-all!
    []
    (js/window.changeAllBodiesToColor (next-color!)
                                      ;; (rand-nth ["#00ff00"
                                      ;; "#0000ff" "#ffff00"
                                      ;; "#00ffff"])
    )))

(defn color-cycle-white!
    []
  (js/window.changeAllBodiesToColor "#ffffff"))


(defui button [{:keys [on-click children]}]
  ($ :button.btn {:style {:padding "30px"}
                  :on-click on-click}
     children))

(defonce setup!
  (memoize (fn []
             (js/window.matter_setup)
             (js/window.addGround)
             (js/window.createPyramid 200 (rand-nth [0 100 200 -100
                                                     -200])
                                      500 500))))

(defui app []
  (let [[state set-state!] (uix.core/use-state 0)]
    (uix/use-effect
     (fn []
       (setup!))
     [])


    (uix/use-effect
     (fn []
       (let
           [id
            (js/setInterval
             (fn [] (color-change-all!))
             300)
            ]
           (fn []
             (js/clearInterval id)
             )))
     [])

    (uix/use-effect
     (fn []
       (let
           [id
            (js/setInterval
             (fn []
               (when (rand-nth [false false false true])
                 (color-cycle-white!)))
             100)]
           (fn []
             (js/clearInterval id)
             )))
     [])


    (uix/use-effect (fn []
                      (let [id (js/setInterval
                                (fn [] (js/window.applyRandomForce 0.1))
                                10)]
                        (fn [] (js/clearInterval id))))
                    [])


    (uix/use-effect
     (fn []
       (let
           [id
            (js/setInterval
             (fn []
               (js/window.setAllCirclesRadius
                (rand-nth [10 20 30 40 50])
                )
               ;; (js/window.applyRandomForce 0.1)
               )
             100)]
           (fn []
             (js/clearInterval id)
             )))
     [])


    (uix/use-effect
     (fn []
       (let
           [id
            (js/setInterval
             (fn []
               (when (rand-nth [false false false
                                false
                                false
                                false
                                false
                                false
                                false true])
                 (js/window.createPyramid 200 (rand-nth [0 100 200 -100
                                                         -200])
                                          500 500))
               )
             200)]
           (fn []
             (js/clearInterval id)
             )))
     [])



    ($ :<>
       ($ :div {:id "matter-canvas"})
       ($ button {:on-click (fn []

                              (js/window.addBox
                               (js/window.makeBox 400 200 40 40)))} "box!")
       ($ button {:on-click

                  (fn []
                    (js/window.createPyramid
                     200
                     (rand-nth [0 100 200 -100 -200]) 500 500))}
          "pyramid!")

       ($ button {:on-click
                  (fn []
                    (js/window.addGround))}
          "ground")

       ($ button {:on-click
                  (fn []
                    (js/window.changeAllBodiesToColor
                     (rand-nth [
                                "#00ff00"
                                "#0000ff"
                                "#ffff00"
                                "#00ffff"])))}
          "colors")

       ($ button {:on-click
                  (fn []
                    (js/window.resetEngine))}
          "clear"))))


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
  (render)



  )

(comment



  )
