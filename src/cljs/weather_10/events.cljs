(ns weather-10.events
  (:require
   [re-frame.core :as rf]
   [ajax.core     :as ajax]
   [ajax.edn      :as edn]))

;;dispatchers

(rf/reg-event-db
  :navigate
  (fn [db [_ route]]
    (assoc db :route route)))

(rf/reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))

(rf/reg-event-fx
  :fetch-docs
  (fn [_ _]
    {:http-xhrio {:method          :get
                  :uri             "/docs"
                  :response-format (ajax/raw-response-format)
                  :on-success       [:set-docs]}}))

(rf/reg-event-db
  :common/set-error
  (fn [db [_ error]]
    (assoc db :common/error error)))

(rf/reg-event-db
  :set-latest
  (fn [db [_ latest]]
    (assoc db
           :latest latest
           :show-twirly false)))


(rf/reg-event-db
  :set-history
  (fn [db [_ history]]
    (assoc db
           :history history
           :show-twirly false)))


(rf/reg-event-db
  :set-summary
  (fn [db [_ summary]]
    (assoc db
           :summary summary
           :show-twirly false)))

;; events

(rf/reg-event-fx
  :get-latest                      ;; usage:  (dispatch [:handler-with-http])
  (fn [{:keys [db]} _]                    ;; the first param will be world"
    {:db   (assoc db :show-twirly true)   ;; causes the twirly-waiting-dialog to show??
     :http-xhrio {:method          :get
                  :uri             "/api/weather/latest"
                  :timeout         8000                                           ;; optional see API docs
                  :response-format (edn/edn-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
                  :on-success      [:set-latest]
                  :on-failure      [:bad-http-result]}}))

(rf/reg-event-fx
  :get-history                      ;; usage:  (dispatch [:handler-with-http])
  (fn [{:keys [db]} _]                    ;; the first param will be "world"
    {:db   (assoc db :show-twirly true)   ;; causes the twirly-waiting-dialog to show??
     :http-xhrio {:method          :get
                  :uri             "/api/weather/history"
                  :timeout         8000                                           ;; optional see API docs
                  :response-format (edn/edn-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
                  :on-success      [:set-history]
                  :on-failure      [:bad-http-result]}}))

(rf/reg-event-fx
  :get-summary                      ;; usage:  (dispatch [:handler-with-http])
  (fn [{:keys [db]} _]                    ;; the first param will be "world"
    {:db   (assoc db :show-twirly true)   ;; causes the twirly-waiting-dialog to show??
     :http-xhrio {:method          :get
                  :uri             "/api/waether/summary"
                  :timeout         8000                                           ;; optional see API docs
                  :response-format (edn/edn-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
                  :on-success      [:set-summary]
                  :on-failure      [:bad-http-result]}}))

;;subscriptions

(rf/reg-sub
  :route
  (fn [db _]
    (-> db :route)))

(rf/reg-sub
  :page
  :<- [:route]
  (fn [route _]
    (-> route :data :name)))

(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(rf/reg-sub
  :common/error
  (fn [db _]
    (:common/error db)))

(rf/reg-sub
  :latest
  (fn [db _]
    (:latest db)))

(rf/reg-sub
  :summary
  (fn [db _]
    (:summary db)))

(rf/reg-sub
 :history
 (fn [db _]
   (:history db)))

(rf/reg-sub
 :show-twirly
 (fn [db _]
   (:show-twirly db)))



