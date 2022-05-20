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

(defn more-card [player]
  (let [card (new-card)
        cards (conj (:cards player) card)
        new-player (update player :cards conj card)
        points (points-card cards)]
    (assoc new-player :points points)))

(defn player-decision-continue? [player]
  (= (read-line) "sim"))

(defn dealer-decision-continue? [player-points dealer]
  (let [dealer-points (:points dealer)]
    (if (> player-points 21) false (<= dealer-points player-points))))

;função game, responsavel por perguntar para o jogador se ele quer mais uma carta
(defn game [player fn-decision-continue?]
  (println (:player-name player) ": mais carta?")
  (if (fn-decision-continue? player)
    (let [player-with-more-cards (more-card player)]
      (card/print-player player-with-more-cards)
      (recur player-with-more-cards fn-decision-continue?))
    player))

(defn end-game [player dealer]
  (let [player-points (:points player)
        dealer-points (:points dealer)
        player-name (:player-name player)
        dealer-name (:player-name dealer)
        message (cond
                  (and (> player-points 21) (> dealer-points 21)) "Ambos perderam"
                  (= player-points dealer-points) "empatou"
                  (> player-points 21) (str dealer-name " ganhou")
                  (> dealer-points 21) (str player-name " ganhou")
                  (> player-points dealer-points) (str player-name " ganhou")
                  (> dealer-points player-points) (str dealer-name " ganhou"))]
    (card/print-player player)
    (card/print-player dealer)
    (print message)))

(def player-1 (player "Nathan Mariano"))
(card/print-player player-1)

(def dealer (player "Dealer"))
(card/print-masked-player dealer)

(def player-after-game (game player-1 player-decision-continue?))
(def partial-dealer-decision-continue? (partial dealer-decision-continue? (:points player-after-game)))
(def dealer-after-game (game dealer partial-dealer-decision-continue?))

(end-game player-after-game dealer-after-game)
