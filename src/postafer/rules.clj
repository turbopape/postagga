(ns postafer.rules)

(def rules
  [
   {;;Rule 0 "Montrez moi les chaussures noires"
    :id :rule0
    :rule [:intent       ;;<----- A atep
           #{:get-value #{"NPP"}}    ;;<----- A status in the parse machine (a set of possible sets of POS TAGS)
           #{#{"NC"}}
           
           :product
           #{#{"DET"}}
           #{:get-value #{"NC"}}
           
           :qualif
           #{:multi :get-value #{"ADJ"}}]}])
