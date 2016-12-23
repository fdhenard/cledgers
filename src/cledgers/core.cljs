(ns cledgers.core
  (:require [goog.dom :as gdom]
            [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]))

;; (def app-state (atom {:count 0}))
;; (def app-state (atom {:app/title "Animals"
;;                       :xaction/list [[1 "Ant"] [2 "Antelope"] [3 "Bird"] [4 "Cat"]
;;                                      [5 "Dog"] [6 "Lion"] [7 "Mouse"] [8 "Monkey"]
;;                                      [9 "Snake"] [10 "Zebra"]]}))
(def app-state (atom {:app/title "Animals"
                      :xaction/list [{:id 1
                                      :amount 30.01
                                      :desc "Eric Swanson - guitar lesson"}
                                     {:id 2
                                      :amount 9.99
                                      :desc "a dang book"}]}))

(defmulti read (fn [env key params] key))

(defmethod read :default
  [{:keys [state] :as env} key params]
  (let [st @state]
    (if-let [[_ value] (find st key)]
      {:value value}
      {:value :not-found})))

;; (defmethod read :xaction/list
;;   [{:keys [state] :as env} key {:keys [start end]}]
;;   {:value (subvec (:xaction/list @state) start end)})

;; (defui NewXaction
;;   Object
;;   (render [this]
;;           (dom/table nil
;;                      (dom/thead nil
;;                                 (dom/tr nil
;;                                         (dom/th nil "Id")
;;                                         (dom/th nil "Desc")
;;                                         (dom/th nil "Amount")
;;                                         (dom/th nil "Add")))
;;                      (dom/tbody nil
;;                                 (dom/tr nil
;;                                         (dom/td nil (dom/input {:type "text"}))
;;                                         (dom/td nil (dom/input {:type "text"}))
;;                                         (dom/td nil (dom/input {:type "text"}))
;;                                         (dom/td nil (dom/button nil "Add")))))))

(defui XactionList
  ;; static om/IQueryParams
  ;; (params [this]
  ;;         {:start 0 :end 10})
  static om/IQuery
  (query [this]
         [:xaction/list])
  Object
  (render [this]
          (let [{:keys [xaction/list]} (om/props this)]
            (dom/div nil
                     (dom/h2 nil "Cledgers")
                     (dom/table nil
                                (dom/thead nil
                                           (dom/tr nil
                                                   (dom/th nil "Id")
                                                   (dom/th nil "Desc")
                                                   (dom/th nil "Amount")))
                                (apply dom/tbody nil
                                       (map (fn [xaction]
                                              (dom/tr nil
                                                      (dom/td nil (:id xaction))
                                                      (dom/td nil (:desc xaction))
                                                      (dom/td nil (:amount xaction))))
                                            list)))))))

(def reconciler
  (om/reconciler {:state app-state
                  :parser (om/parser {:read read})}))

(om/add-root! reconciler
              XactionList (gdom/getElement "app"))
