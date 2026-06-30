(ns kotoba.eda.site
  "Hiccup + shadow-css source for the kotoba EDA documentation page."
  (:require [clojure.string :as str]))

(def tokens
  {:color/bg "#f7f7f4"
   :color/panel "#ffffff"
   :color/ink "#111111"
   :color/muted "#555f6d"
   :color/line "#dddddd"
   :color/head "#ecece8"
   :color/link "#0645ad"
   :font/sans "system-ui, sans-serif"
   :font/mono "ui-monospace, SFMono-Regular, Menlo, monospace"
   :radius/surface "8px"})

(def shadow-css
  {:page/body {:font-family (:font/sans tokens)
               :margin "0"
               :color (:color/ink tokens)
               :background (:color/bg tokens)}
   :page/main {:max-width "980px"
               :margin "0 auto"
               :padding "40px 20px"}
   :page/h1 {:font-size "38px"
             :margin "0 0 12px"}
   :page/section {:border-top (str "1px solid " (:color/line tokens))
                  :padding "22px 0"}
   :page/table {:width "100%"
                :border-collapse "collapse"
                :background (:color/panel tokens)}
   :page/cell {:border (str "1px solid " (:color/line tokens))
               :padding "10px"
               :text-align "left"
               :vertical-align "top"}
   :page/th {:background (:color/head tokens)}
   :page/code {:font-family (:font/mono tokens)}
   :page/pre {:overflow "auto"
              :background (:color/panel tokens)
              :border (str "1px solid " (:color/line tokens))
              :padding "14px"
              :font-family (:font/mono tokens)}
   :page/link {:color (:color/link tokens)}})

(def maturity-rows
  [["EDN data contract" "Flow jobs, OSS report manifests, signoff evidence rows" "CI-verifiable"]
   ["CLJC native kernels" "Timing, routing, DRC, LVS, DC solve, stuck-at coverage" "Prototype"]
   ["OSS report normalization" "OpenSTA, OpenROAD, KLayout, Magic, Netgen, ngspice, OpenLane" "CI-verifiable"]
   ["Workbench integration" "Evidence import and links from kotoba EDA" "Demo-to-alpha"]
   ["Tapeout signoff" "Evidence shape only" "Not signoff-ready"]])

(defn style-map->css [m]
  (str/join ";" (map (fn [[k v]] (str (name k) ":" v)) m)))

(defn style-css []
  (str/join
   "\n"
   (map (fn [[selector rules]]
          (str selector " { " (style-map->css rules) "; }"))
        [["body" (:page/body shadow-css)]
         ["main" (:page/main shadow-css)]
         ["h1" (:page/h1 shadow-css)]
         ["section" (:page/section shadow-css)]
         ["table" (:page/table shadow-css)]
         ["th, td" (:page/cell shadow-css)]
         ["th" (:page/th shadow-css)]
         ["code, pre" (:page/code shadow-css)]
         ["pre" (:page/pre shadow-css)]
         ["a" (:page/link shadow-css)]])))

(defn page-hiccup []
  [:html {:lang "en"}
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
    [:title "kotoba EDA"]
    [:style (style-css)]]
   [:body
    [:main
     [:h1 "kotoba EDA"]
     [:p "EDN-driven CLJC kernels for OpenSTA/OpenROAD/KLayout/Netgen/ngspice/ATE class computation."]
     [:section
      [:h2 "Scope"]
      [:p "Native CLJC kernels currently cover timing graph analysis, grid routing, rectangle DRC, netlist LVS, linear DC solve, and stuck-at ATE coverage."]]
     [:section
      [:h2 "Coverage and maturity"]
      [:p "Status date: 2026-06-30. Practical maturity level: alpha evidence pipeline."]
      [:table
       [:thead [:tr [:th "Area"] [:th "Coverage now"] [:th "Maturity"]]]
       [:tbody
        (for [[area coverage maturity] maturity-rows]
          [:tr [:td area] [:td coverage] [:td maturity]])]]
      [:p "Real tapeout still requires foundry PDK/rule decks, real OSS tool execution, waiver review, corner/PVT matrix, and human approval."]]
     [:section
      [:h2 "Files"]
      [:p [:a {:href "sample_flow.edn"} "sample_flow.edn"]]
      [:p [:a {:href "oss_manifest.edn"} "oss_manifest.edn"]]
      [:p [:a {:href "oss-reports/opensta.rpt"} "OpenSTA sample report"]]
      [:p [:a {:href "oss-reports/openroad-route.rpt"} "OpenROAD sample report"]]
      [:p [:a {:href "oss-reports/klayout.drc"} "KLayout DRC sample report"]]
      [:p [:a {:href "oss-reports/netgen.lvs"} "Netgen LVS sample report"]]
      [:p [:a {:href "oss-reports/ngspice.log"} "ngspice sample log"]]
      [:p [:a {:href "https://github.com/kotoba-lang/eda"} "GitHub repository"]]]
     [:section
      [:h2 "Run"]
      [:pre "clojure -M:test\nclojure -M:site\nclojure -M:oss-normalize docs/oss_manifest.edn"]]]]])

(defn- escape-html [x]
  (-> (str x)
      (str/replace "&" "&amp;")
      (str/replace "<" "&lt;")
      (str/replace ">" "&gt;")
      (str/replace "\"" "&quot;")))

(defn- attrs [m]
  (apply str (map (fn [[k v]] (str " " (name k) "=\"" (escape-html v) "\"")) m)))

(declare render-node)

(defn- render-seq [xs]
  (apply str (map render-node xs)))

(defn render-node [node]
  (cond
    (nil? node) ""
    (string? node) (escape-html node)
    (number? node) (str node)
    (seq? node) (render-seq node)
    (vector? node) (let [[tag maybe-attrs & children] node
                         [a c] (if (map? maybe-attrs)
                                 [maybe-attrs children]
                                 [{} (cons maybe-attrs children)])]
                     (str "<" (name tag) (attrs a) ">"
                          (render-seq c)
                          "</" (name tag) ">"))
    :else (escape-html node)))

(defn html []
  (str "<!doctype html>\n" (render-node (page-hiccup)) "\n"))
