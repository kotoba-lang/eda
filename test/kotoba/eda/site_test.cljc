(ns kotoba.eda.site-test
  (:require [clojure.test :refer [deftest is]]
            [kotoba.eda.site :as site]))

(deftest renders-hiccup-and-shadow-css
  (let [html (site/html)]
    (is (vector? (site/page-hiccup)))
    (is (contains? site/shadow-css :page/table))
    (is (re-find #"Coverage and maturity" html))
    (is (re-find #"oss_manifest.edn" html))))
