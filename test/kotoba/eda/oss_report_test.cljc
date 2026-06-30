(ns kotoba.eda.oss-report-test
  (:require [clojure.test :refer [deftest is]]
            [clojure.edn :as edn]
            [kotoba.eda.oss-report :as oss]))

(deftest normalizes-passing-oss-reports
  (let [manifest (edn/read-string (slurp "docs/oss_manifest.edn"))
        reports (mapv (fn [spec]
                        (assoc spec :text (slurp (str "docs/" (:eda.job/report spec)))))
                      (:eda.oss/reports manifest))
        result (oss/normalize-reports reports)]
    (is (= :passed (:eda.oss/status result)))
    (is (= 7 (:eda.oss/pass-count result)))
    (is (= #{:signoff/timing-pvt :signoff/route :signoff/drc :signoff/lvs :signoff/spice-corner}
           (set (map :eda.signoff/type (:eda.signoff/evidence result)))))))

(deftest failed-reports-block-evidence
  (let [row (oss/normalize-report {:eda.job/tool :sw/opensta
                                   :eda.job/operation :op/analyze-timing}
                                  "worst slack -0.011")]
    (is (= :failed (:eda.signoff/status row)))
    (is (= -0.011 (get-in row [:eda.signoff/metrics :worst-slack-ns])))))
