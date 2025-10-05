(ns frontend.core
  (:require
   [frontend.handlers :as h]
   [frontend.subs :as s]
   [frontend.uix.hooks :refer [use-subscribe]]
   [re-frame.core :as rf]
   [uix.core :as uix :refer [$ defui]]
   [uix.dom]))

(defui app []
  (uix/use-effect (fn []

                    ;; (rf/dispatch [::h/get-todos])
                    )
                  [])
  [:div "hello"])

(defonce root
  ;; This def is also running in Karma tests, where the element isn't available.
  ;; Avoid errors, we won't need the react root there?
  (when-let [el (js/document.getElementById "app")]
     (uix.dom/create-root el)))

(defn render []
  (uix.dom/render-root ($ app) root))

(defn ^:export init []
  (render))
