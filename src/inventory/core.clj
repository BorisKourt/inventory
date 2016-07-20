(ns inventory.core
  (:gen-class))

(def inventory
  (atom
    {:caster
     {:spell-slots
      {1 {:total    4
          :quantity 4}
       2 {:total    2
          :quantity 2}}

      :ingredients
      {"Bat Guano"    {:quantity 100}
       "Lodestone"    {:quantity 1}
       "Diamond Dust" {:quantity 20}
       "Black Powder" {:quantity 100}}

      :implements
      {"Holy Symbol" {:quantity 1
                      :weight   1}
       "Crystal"     {:quantity 1
                      :bonus    1
                      :weight   0.5}}

      :spells
      {"Fire Blast"  {:ingredients {"Bat Guano"    {:quantity    1
                                                    :consumable? true}
                                    "Black Powder" {:quantity    10
                                                    :consumable? true}}
                      :implements  {"Crystal" {:quantity 1}}
                      :spell-level 2
                      :effect      {:description "Blast of Fire WOW"}}
       "Fire Blasts" {:ingredients {"Bat Guano"    {:quantity    1
                                                    :consumable? true}
                                    "Black Powder" {:quantity    100000
                                                    :consumable? true}}
                      :implements  {"Crystal" {:quantity 1}}
                      :spell-level 2
                      :effect      {:description "Blast of Fire WOW"}}}}}))


;; ToDo, Add spell slots into the process.
;; V1: Spell slots just check whether you have any of the lowest possible slot (2 in this case)
;; V2: Cast a spell at higher slots. Level 1 spell,

(defn material-requirements [caster-inventory spell-requirements material]
  (when spell-requirements
    (let [requirements
          (map
            (fn [[n required]]
              (if-let [have ((material caster-inventory) n)]
                (if-not (>= (:quantity have)
                            (:quantity required))
                  false
                  (if-not (:consumable? required)
                    {n have}
                    {n (update have
                               :quantity
                               (fn [quantity]
                                 (- quantity (:quantity required))))}))
                false))
            spell-requirements)]
      {material requirements})))

(defn sfitw [v] (if (or (nil? v)
                        (empty? v)) false v))

(defn spell-slots
  "Checks if there's a spell slot available"
  [caster-inventory spell-requirements]
  (when spell-requirements
    (let [caster-slots (:spell-slots caster-inventory)]
      (if-not (< 0 (:quantity (caster-slots spell-requirements)))
        false
        (do (println "YAS")
            spell-requirements)))))

(defn find-requirements
  "Check what the spell actually requires, then fetch only these details."
  [{:keys [ingredients implements spell-level]} caster]
  (-> {}
      (merge (material-requirements caster ingredients :ingredients))
      (merge (material-requirements caster implements :implements))
      (merge (spell-slots caster spell-level))
      ;; Spell Slots
      sfitw
      ))

(defn cast-it [requirements]
  (if (or (not requirements)
          (some false? (flatten (vals requirements))))
    false
    requirements))

(defn update-inventory! [materials spell-slot]
  (if-not materials
    false
    (let [merged-materials
          (into {}
                (map (fn [[k v]]
                       {k (into {} v)})
                     materials))]
      (swap! inventory update-in                            ;; Update Function
             [:caster]                                      ;;
             (fn [caster]
               (into {}
                     (map (fn [[k v]]
                            (if-let [material (k merged-materials)]
                              {k (merge v material)}
                              {k v}))
                          caster))))))
  (if-not spell-slot
    false
    (swap! inventory update-in
           [:caster :spell-slots spell-slot :quantity]
           dec)))

(defn cast-spell [nom]                                      ;; & {:keys [spell-slot]}
  (let [caster (:caster @inventory)]
    (println "caster")
    (-> ((:spells caster) nom)                              ;; spells is a vector
        (find-requirements caster)
        cast-it
        ;; update-inventory!
        ;; Cast The Spell
        ;; Swap the atom.
        )))

(defn -main [& args]
  (cast-spell "Fire Blast"))