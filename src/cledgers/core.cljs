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
                                      :desc "a dang book"}]
                      :xaction/new {:amount nil
                                    :desc nil}}))

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


;; (defmulti mutate om/dispatch)

;; (defmethod mutate 'xaction/update-new
;;   [{:keys [state ref]} key new-props]
;;   {:remote true
;;    :action ;; OPTIMISTIC UPDATE
;;    (fn []
;;      (swap! state update-in ref merge new-props))
;;    })
;; (defmethod mutate :default
;;   [{:keys [state] :as env} key params]
;;   (.log js/console "here!")
;;   ) }

(defn mutate
  [{:keys [state] :as env} key params]
  ;; (.log js/console "here!")
  ;; (pr-str {:test "hi"})
  (.log js/console "param" (-> params :desc) "key == dosomething" (= key 'xaction/dosomething))
  (if (= 'xaction/dosomething key)
    {:action #(do
                (.log js/console "here")
                (swap! state assoc-in [:xaction/new :desc] (-> params :desc)))}
    {:value :not-found}))

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

(defn new-todo-row [c new]
  (dom/tr nil
          (dom/td nil nil)
          (dom/td nil
                  (dom/input
                   #js {:ref "descIn"
                        :id "desc-in"
                        ;; :onKeyDown (fn [evt]
                        ;;              (let [my-c c
                        ;;                    my-new new]
                        ;;                ;; (.log js/console evt "pressed: " (.-keyCode evt))
                        ;;                (.log js/console "value: " (-> evt .-target .-value))
                        ;;                ))
                        :onChange (fn [evt]
                                    (let [val (-> evt .-target .-value)]
                                      (.log js/console "value: " val)
                                      (om/transact! c
                                                    `[(xaction/dosomething
                                                       {:desc ~val})])))
                        }))
          (dom/td nil
                  (dom/input
                   #js {:ref "amtIn"
                        :id "amt-in"
                        :onKeyDown #(.log js/console % "pressed: " (.-keyCode %))}))
          (dom/td nil
                  ;; (dom/button #js {:onClick } "Add")
                  nil)))

(defn show-new-todo [c])

(defui XactionList
  ;; static om/IQueryParams
  ;; (params [this]
  ;;         {:start 0 :end 10})
  static om/IQuery
  (query [this]
         [:xaction/list :xaction/new])
  Object
  (render [this]
          (let [{:keys [xaction/list xaction/new]} (om/props this)]
            (dom/div nil
                     (dom/h2 nil "Cledgers")
                     (dom/table nil
                                (dom/thead nil
                                           (dom/tr nil
                                                   (dom/th nil "Id")
                                                   (dom/th nil "Desc")
                                                   (dom/th nil "Amount")
                                                   (dom/th nil "Controls")))
                                (apply dom/tbody nil
                                       (concat
                                        [(new-todo-row this new)
                                         (dom/tr nil
                                                 (dom/td nil nil)
                                                 (dom/td nil (:desc new))
                                                 (dom/td nil (:amount new)))]
                                        (map (fn [xaction]
                                               (dom/tr nil
                                                       (dom/td nil (:id xaction))
                                                       (dom/td nil (:desc xaction))
                                                       (dom/td nil (:amount xaction))))
                                             list))))))))

(def reconciler
  (om/reconciler {:state app-state
                  :parser (om/parser {:read read :mutate mutate})}))

(om/add-root! reconciler
              XactionList (gdom/getElement "app"))
