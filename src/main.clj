(ns main
  (:require
   [akvo.commons.config :as config]
   [akvo.commons.gae :as gae]
   [akvo.commons.gae.query :as query]
   [clojure.java.io :as io]
   [clojure.pprint :refer [pprint]]))

(def config-repo-path "/akvo-flow-server-config")

(def repo-directory-file-list
  (filter #(re-find #"akvoflow" (.getName %))
          (.listFiles (io/file config-repo-path))))

(defn build-ds-spec [config]
  {:instance-id (:app-id config)
   :hostname (:domain config)
   :port 443
   :service-account-id (:service-account-id config)
   :private-key-file (:private-key-file config)})

(defn ds-spec [directory]
  (as-> directory c
    (.getAbsolutePath c)
    (str c "/appengine-web.xml")
    (io/file c)
    (config/get-config c)
    (build-ds-spec c)))

(defn query-instances [ds-specs query fetch-options]
  (reduce (fn [m {:keys [instance-id] :as ds-spec}]
            (try
              (gae/with-datastore [ds ds-spec]
                (let [r (-> (query/result ds query fetch-options) seq count)]
                  (pprint {instance-id r})
                  (assoc m instance-id r)))
              (catch Exception _
                (println (str "Error while query " instance-id))
                (assoc m instance-id 0))))
          {}
          ds-specs))

(comment

  (spit "result.edn" (query-instances (map ds-spec repo-directory-file-list)
                                      {:kind "SurveyInstance"
                                       :filter (query/= "deviceIdentifier"
                                                        "Akvo Flow Web")
                                       :keys-only true}
                                      {}))

  (reduce + 0 (-> "result.edn" slurp read-string vals))

  (-> "result.edn" slurp read-string keys sort)

  )
