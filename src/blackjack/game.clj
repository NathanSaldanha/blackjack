(ns blackjack.game
  (:require [card-ascii-art.core :as card]))

(defn new-card []
  "Generates a card number between 1 and 13"
  (inc (rand-int 13)))

;J, Q, K = 10
;[A 5 7] = 1+5+7(13) OR 11+5+7(23)
;A = 11 ou A = 1
(defn JQK->10 [cards]
  (if (> cards 10)
    10
    cards))

(defn A->11 [cards]
  (if (= cards 1)
    11
    cards))

(defn points-card [cards]
  (let [cards-without-JQK (map JQK->10 cards)
        cards-without-A11 (map A->11 cards-without-JQK)
        points-with-A-1 (reduce + cards-without-JQK)
        points-with-A-11 (reduce + cards-without-A11)]
    (if (> points-with-A-11 21)
      points-with-A-1
      points-with-A-11)))

(defn player [player-name]
  (let [card1 (new-card)
        card2 (new-card)
        cards [card1 card2]
        points (points-card cards)]
    {:player-name player-name
     :cards  cards
     :points points}))

(card/print-player (player "Nathan"))
(card/print-player (player "Dealer"))
