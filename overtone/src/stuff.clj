; Things
;
; to start:
;  (use `overtone.live)
;

(definst saw-wave [freq 440 attack 0.01 sustain 0.4 release 0.1 vol 0.4] 
  (* (env-gen (env-lin attack sustain release) 1 1 0 1 FREE)
     (saw freq)
     vol))

; Kick drum
(definst kick [freq 120 dur 0.5 width 0.5]
  (let [freq-env (* freq (env-gen (perc 0 (* 0.99 dur))))
        env (env-gen (perc 0.01 dur) 1 1 0 1 FREE)
        sqr (* (env-gen (perc 0 0.01)) (pulse (* 2 freq) width))
        src (sin-osc freq-env)
        drum (+ sqr (* env src))]
    (compander drum drum 0.2 1 0.1 0.01 0.01)))

; Some swing thing; stolen from the overtone.wiki/Swing.md

(definst c-hat [amp 0.8 t 0.04]
  (let [env (env-gen (perc 0.001 t) 1 1 0 1 FREE)
        noise (white-noise)
        sqr (* (env-gen (perc 0.01 0.04)) (pulse 880 0.2))
        filt (bpf (+ sqr noise) 9000 0.5)]
    (* amp env filt)))


(definst o-hat [amp 0.8 t 0.5]
  (let [env (env-gen (perc 0.001 t) 1 1 0 1 FREE)
        noise (white-noise)
        sqr (* (env-gen (perc 0.01 0.04)) (pulse 880 0.2))
        filt (bpf (+ sqr noise) 9000 0.5)]
    (* amp env filt)))

; define a metronome at a given tempo, expressed in beats per minute.
(def metro (metronome 120))

; https://ccrma.stanford.edu/~jos/pasp/Karplus_Strong_Algorithm.html
; (definst 
(let [freq 220]
   (demo (pluck (* (white-noise) (env-gen (perc 0.001 2) :action FREE)) 1 3 (/ 1 freq))))

(defn swinger [beat]
  ; (at (metro (+ 0 beat)) (sin-osc 400))
  (at (metro (+ 0.0 beat)) (kick))
  (at (metro (+ 0.0 beat)) (c-hat))
  (at (metro (+ 0.2 beat)) (c-hat))
  (at (metro (+ 0.5 beat)) (o-hat))
  (at (metro (+ 0.6 beat)) (kick))
  (at (metro (+ 0.65  beat)) (kick 140))
  (at (metro (+ 1.65  beat)) (kick 180))
  ; (at (metro (+ 1  beat)) (saw-wave 520))
  ; (at (metro (+ 2  beat)) (saw-wave 261.62))
  ; (at (metro (inc beat)) (c-hat))
  ; (at (metro (+ 1.65 beat)) (c-hat))
  (apply-at (metro (+ 2 beat)) #'swinger (+ 2 beat) []))

(swinger (metro))
