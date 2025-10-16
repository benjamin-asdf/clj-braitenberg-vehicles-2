(ns quill-matter-js.renderer
  (:require [quil.core :as q]))

(defn world-to-screen
  "Convert world coordinates to screen coordinates if needed"
  [{:keys [x y]}]
  {:x x :y y})

(defn render-body
  "Render a single body using its vertices"
  [body]
  (let [vertices (.-vertices body)
        position (.-position body)
        angle (.-angle body)]

    ;; Set rendering style
    (q/push-matrix)
    (q/translate (.-x position) (.-y position))
    (q/rotate angle)

    ;; Draw the shape using vertices
    (q/begin-shape)
    (doseq [vertex vertices]
      (let [x (- (.-x vertex) (.-x position))
            y (- (.-y vertex) (.-y position))]
        (q/vertex x y)))
    (q/end-shape :close)

    (q/pop-matrix)))

(defn render-bodies
  "Render all bodies in the world"
  [bodies {:keys [fill-color stroke-color stroke-weight]
           :or {fill-color [200 200 200 150]
                stroke-color [100 100 100]
                stroke-weight 1}}]

  ;; Set default rendering style
  (apply q/fill fill-color)
  (apply q/stroke stroke-color)
  (q/stroke-weight stroke-weight)

  ;; Render each body
  (doseq [body bodies]
    (when (.-vertices body)
      ;; Check for custom render properties on the body
      (when-let [custom-fill (.. body -render -fillStyle)]
        ;; Parse hex color or use as-is if it's already in the right format
        (if (string? custom-fill)
          (q/fill custom-fill)
          (apply q/fill custom-fill)))

      (when-let [custom-stroke (.. body -render -strokeStyle)]
        (if (string? custom-stroke)
          (q/stroke custom-stroke)
          (apply q/stroke custom-stroke)))

      (when-let [custom-weight (.. body -render -lineWidth)]
        (q/stroke-weight custom-weight))

      ;; Render the body
      (render-body body))))

(defn render-constraints
  "Render constraints (springs, joints, etc.)"
  [constraints {:keys [stroke-color stroke-weight]
                :or {stroke-color [150 150 150 100]
                     stroke-weight 1}}]
  (apply q/stroke stroke-color)
  (q/stroke-weight stroke-weight)
  (q/no-fill)

  (doseq [constraint constraints]
    (when (.-bodyA constraint)
      (let [bodyA (.-bodyA constraint)
            bodyB (.-bodyB constraint)
            pointA (or (.-pointA constraint) #js {:x 0 :y 0})
            pointB (or (.-pointB constraint) #js {:x 0 :y 0})
            posA (.-position bodyA)
            posB (.-position bodyB)]
        (q/line
         (+ (.-x posA) (.-x pointA))
         (+ (.-y posA) (.-y pointA))
         (+ (.-x posB) (.-x pointB))
         (+ (.-y posB) (.-y pointB)))))))

(defn clear-canvas
  "Clear the canvas with background color"
  [{:keys [background] :or {background [240 240 240]}}]
  (apply q/background background))

(defn render
  "Main render function - call this in your draw loop"
  [engine {:keys [show-constraints background bodies-style constraints-style]
           :or {show-constraints true
                background [240 240 240]}}]

  ;; Clear the canvas
  (clear-canvas {:background background})

  ;; Get world and bodies
  (let [world (.-world engine)
        bodies (.-bodies world)
        constraints (when show-constraints
                     (.-constraints world))]

    ;; Render bodies
    (render-bodies bodies bodies-style)

    ;; Render constraints if enabled
    (when (and show-constraints constraints)
      (render-constraints constraints constraints-style))))
