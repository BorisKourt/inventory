(ns inventory.core
  (:gen-class))

(def inventory
  (atom
    {:caster
     {:spell-slots
      {1 {:total     4
          :quantity 4}
       2 {:total     2
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
                      :spell-slot  2
                      :effect      {:description "Blast of Fire WOW"}}
       "Fire Blasts" {:ingredients {"Bat Guano"    {:quantity    1
                                                    :consumable? true}
                                    "Black Powder" {:quantity    100000
                                                    :consumable? true}}
                      :implements  {"Crystal" {:quantity 1}}
                      :spell-slot  2
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

(defn sfitw [v] (if (empty? v) false v))

(defn find-requirements
  "Check what the spell actually requires, then fetch only these details."
  [{:keys [ingredients implements spell-slot]} caster]
  (-> {}
      (merge (material-requirements caster ingredients :ingredients))
      (merge (material-requirements caster implements :implements))

      ;; Spell Slots
      sfitw
      ))

(defn cast-it [materials]
  (if (or (not materials)
          (some false? (flatten (vals materials))))
    false
    materials))

(defn update-inventory! [materials]
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
                          caster)))))))

(defn cast-spell [nom] ;; & {:keys [spell-slot]}
  (let [caster (:caster @inventory)]
    (println "caster")
    (-> ((:spells caster) nom)                              ;; spells is a vector
        (find-requirements caster)
        cast-it
        update-inventory!

        ;; Cast The Spell
        ;; Swap the atom.

        )))

(defn -main [& args]
  (cast-spell "Fire Blast"))