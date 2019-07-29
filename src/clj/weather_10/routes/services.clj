(ns weather-10.routes.services
  (:require
    [reitit.swagger :as swagger]
    [reitit.swagger-ui :as swagger-ui]
    [reitit.ring.coercion :as coercion]
    [reitit.coercion.spec :as spec-coercion]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.multipart :as multipart]
    [reitit.ring.middleware.parameters :as parameters]
    [weather-10.middleware.formats :as formats]
    [weather-10.middleware.exception :as exception]
    [weather-10.services.home    :as srvh]
   [weather-10.services.summary :as srvs]
   [weather-10.services.history :as srvhis]
    [ring.util.http-response :refer :all]
    [clojure.java.io :as io]))

(defn service-routes []
  ["/api"
   {:coercion spec-coercion/coercion
    :muuntaja formats/instance
    :swagger {:id ::api}
    :middleware [;; query-params & form-params
                 parameters/parameters-middleware
                 ;; content-negotiation
                 muuntaja/format-negotiate-middleware
                 ;; encoding response body
                 muuntaja/format-response-middleware
                 ;; exception handling
                 exception/exception-middleware
                 ;; decoding request body
                 muuntaja/format-request-middleware
                 ;; coercing response bodys
                 coercion/coerce-response-middleware
                 ;; coercing request parameters
                 coercion/coerce-request-middleware
                 ;; multipart
                 multipart/multipart-middleware]}

   ;; swagger documentation
   ["" {:no-doc true
        :swagger {:info {:title "my-api"
                         :description "https://cljdoc.org/d/metosin/reitit"}}}

    ["/swagger.json"
     {:get (swagger/create-swagger-handler)}]

    ["/api-docs/*"
     {:get (swagger-ui/create-swagger-ui-handler
             {:url "/api/swagger.json"
              :config {:validator-url nil}})}]]

   ["/ping"
    {:get (constantly (ok {:message "pong"}))}]


   ["/math"
    {:swagger {:tags ["math"]}}

    ["/plus"
     {:get {:summary "plus with spec query parameters"
            :parameters {:query {:x int?, :y int?}}
            :responses {200 {:body {:total pos-int?}}}
            :handler (fn [{{{:keys [x y]} :query} :parameters}]
                       {:status 200
                        :body {:total (+ x y)}})}
      :post {:summary "plus with spec body parameters"
             :parameters {:body {:x int?, :y int?}}
             :responses {200 {:body {:total pos-int?}}}
             :handler (fn [{{{:keys [x y]} :body} :parameters}]
                        {:status 200
                         :body {:total (+ x y)}})}}]]

   ["/weather"
    {:swagger {:tags ["weather endpoints"]}}

    ["/latest"
     {:get  {:summary    "returns the current weather conditions"
             :parameters {:query {}}
             :responses  {200 {:body {:date     inst?
                                      :readings [{:sunset          inst?
                                                  :day-summary     string?
                                                  :wind-speed      number?
                                                  :sunrise         inst?
                                                  :icon            string?
                                                  :wind-bearing    number?
                                                  :wind-direction  string?
                                                  :temperature-max number?
                                                  :temperature-min number?
                                                  :location        string?
                                                  :temperature     number?
                                                  :week-summary    string?
                                                  :moon-phase-icon string?
                                                  :phase-of-moon   string?
                                                  :age-of-moon     number?
                                                  :date            string?
                                                  :dt              number?
                                                  :type            string?
                                                  :height          number?}]}}}
             :handler    (fn [_]
                           {:status 200
                            :body (srvh/prepare-home-page-data)})}
      :post {:summary "plus with spec body parameters"
             :parameters {:body {}}
             :responses {200 {:body {:date     inst?
                                      :readings [{:sunset          inst?
                                                  :day-summary     string?
                                                  :wind-speed      number?
                                                  :sunrise         inst?
                                                  :icon            string?
                                                  :wind-bearing    number?
                                                  :wind-direction  string?
                                                  :temperature-max number?
                                                  :temperature-min number?
                                                  :location        string?
                                                  :temperature     number?
                                                  :week-summary    string?
                                                  :moon-phase-icon string?
                                                  :phase-of-moon   string?
                                                  :age-of-moon     number?
                                                  :date            string?
                                                  :dt              number?
                                                  :type            string?
                                                  :height          number?}]}}}
             :handler (fn [_]
                        {:status 200
                         :body (srvh/prepare-home-page-data)})}}]]

   ["/files"
    {:swagger {:tags ["files"]}}

    ["/upload"
     {:post {:summary "upload a file"
             :parameters {:multipart {:file multipart/temp-file-part}}
             :responses {200 {:body {:name string?, :size int?}}}
             :handler (fn [{{{:keys [file]} :multipart} :parameters}]
                        {:status 200
                         :body {:name (:filename file)
                                :size (:size file)}})}}]

    ["/download"
     {:get {:summary "downloads a file"
            :swagger {:produces ["image/png"]}
            :handler (fn [_]
                       {:status 200
                        :headers {"Content-Type" "image/png"}
                        :body (-> "public/img/warning_clojure.png"
                                  (io/resource)
                                  (io/input-stream))})}}]]])
