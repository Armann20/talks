(println "Generic code example")

(defn log-payment
  "Generates the log entry message for a payment"
  [payment]
  (let [currency (str (first payment) (second payment) (nth payment 2))
        amount (Double/parseDouble (reduce str "" (drop 3 payment)))
        timestamp (System/currentTimeMillis)]
    (str timestamp ": " amount " paid in " currency)))

(println (log-payment "USD123.45"))
(println (log-payment "EUR678.90"))
(newline)


(println "Macro code example")

(defn process-tokens [tokens]
  (let [first-num (first tokens)
        op (second tokens)
        second-num (nth tokens 2)
        remaining (drop 3 tokens)]
    (if (empty? remaining)
      (list op first-num second-num)
      (list op first-num
            (process-tokens (cons second-num remaining))))))

(defmacro infix
  "Interprets infix expressions with multiple operands"
  [infixed-expression]
  (process-tokens (seq infixed-expression)))

(println (infix (1 + 1)))
(println (infix ((infix (1 + 2)) * 4 / 5)))
