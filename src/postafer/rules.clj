;; Copyright(c) 2017 - [Rafik Naccache](rafik@fekr.tech)
;; A Sample rules file for the postafer parser
(ns postafer.rules)

(def rules
  [{:id :sample-rule
    :rule [:action
           #{#{"P"}}
           #{:get-value #{"V"}}

           :objet
           #{#{"D"}}
           #{:get-value #{"N"}}]}
   
   {;;Rule 0 "Montrez moi les chaussures noires"
    :id :rule0
    :rule [:intent       ;;<----- A atep
           #{:get-value #{"NPP"}}    ;;<----- A status in the parse machine (a set of possible sets of POS TAGS)
           #{#{"NC"}}
           
           :product
           #{#{"DET"}}
           #{:get-value #{"NC"}}
           
           :qualif
           #{:multi :get-value #{"ADJ"}}]}
   
   {;;Rule 1 "Je cherche une montre analogique"
    :id :rule1
    :rule [:intent       ;;<----- A atep
           #{#{"CLS"}}    ;;<----- A status in the parse machine (a set of possible sets of POS TAGS)
           #{:get-value #{"V"}}
           
           :product
           #{#{"DET"}}
           #{:get-value #{"NC"}}
           
           :qualif
           #{:multi :get-value #{"ADJ"}}]}])
