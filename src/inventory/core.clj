(ns inventory.core
  (:gen-class))

(defonce inventory
         (atom
           {:casting
            {:spell-slots
             {1 {:total     4
                 :remaining 4}
              2 {:total     2
                 :remaining 2}}

             :ingredients
             [{:name     "Bat Guano"
               :quantity 100}
              {:name     "Lodestone"
               :quantity 1}
              {:name     "Diamond Dust"
               :quantity 20}
              {:name     "Black Powder"
               :quantity 100}]

             :implements
             [{:name     "Holy Symbol"
               :quantity 1
               :weight   1}
              {:name     "Crystal"
               :quantity 1
               :bonus    1
               :weight   0.5}]

             :spells
             [{:name        "Fire Blast"
               :ingredients [{:name     "Bat Guano"
                              :quantity 1}
                             {:name     "Black Powder"
                              :quantity 10}]
               :implement   ["Crystal"]
               :spell-slot  2
               :effect      {:description "Blast of Fire WOW"}}]}}))

;; To-Do, First: Separate this function into three.
;; Find a spell function. Return just the spell.
;; Find ingredients.
;; Leave a cast-spell function that uses these.

;; To-Do, Figureout how to check for implements the same way we check
;; for the ingredients below.

;; To-Do, Bonus: Also figure out the spell slot requirement. Whether its met or not.

(defn cast-spell [name] ;; Spell slot?
  (let [caster (:casting @inventory)
        spells (:spells caster)
        spell (first (filterv #(= name (:name %)) spells))]
    (if-not spell
      (str "No Such Spell: " name)
      (let [ingredients
            (->> (map
                   (fn [spell-requirement]
                     (map (fn [inventory-ingredient]
                            (when (and (= (:name inventory-ingredient)
                                          (:name spell-requirement))
                                       (>= (:quantity inventory-ingredient)
                                           (:quantity spell-requirement)))
                              (update inventory-ingredient
                                      :quantity
                                      (fn [quantity]
                                        (- quantity (:quantity spell-requirement))))))
                          (:ingredients caster)))
                   (:ingredients spell))
                 flatten
                 (remove nil?))
            ;; implements - figure out this part here yo.
            ]
        ingredients))))




(defn -main [& args])