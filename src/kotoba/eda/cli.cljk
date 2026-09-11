(ns kotoba.eda.cli
  (:require [clojure.java.io :as io]
            [kotoba.eda.oss-report :as oss]
            [kotoba.eda.tx-edn :as tx-edn]))

(defn- read-report [base spec]
  (let [path (:eda.job/report spec)]
    (assoc spec :text (slurp (io/file base path)))))

(defn -main [& [manifest-path]]
  (when-not manifest-path
    (throw (ex-info "usage: clojure -M:oss-normalize <manifest.edn>" {})))
  (let [manifest-file (io/file manifest-path)
        base (.getParentFile manifest-file)
        manifest (tx-edn/slurp-tx-edn manifest-file)
        reports (mapv #(read-report base %) (:eda.oss/reports manifest))]
    (binding [*print-namespace-maps* false]
      (prn (oss/normalize-reports reports)))))
