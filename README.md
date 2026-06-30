# kotoba EDA

EDN-driven CLJC kernels for native kotoba EDA computation.

This repository does not claim to be a drop-in replacement for full OpenROAD,
OpenSTA, KLayout, Netgen, ngspice, or commercial ATE stacks. It provides a
portable kotoba-native execution model with deterministic evidence output:

- OpenSTA class: timing graph / PVT slack analysis
- OpenROAD class: grid routing and overflow checks
- KLayout class: rectangle width / spacing DRC
- Netgen class: connectivity LVS comparison
- ngspice class: linear DC modified nodal analysis
- ATE class: stuck-at fault coverage from vectors
- OSS report normalizer: OpenROAD/OpenSTA/KLayout/Magic/Netgen/ngspice/OpenLane
  reports to `:eda.signoff/*` evidence

All inputs and outputs are EDN maps. The kernels are pure CLJC so the same data
model can run in a browser, CI, or a host runner.

## Run

```sh
clojure -M:test
clojure -M -e "(require '[kotoba.eda.core :as eda] '[clojure.edn :as edn]) (println (eda/run-flow (edn/read-string (slurp \"docs/sample_flow.edn\"))))"
clojure -M:oss-normalize docs/oss_manifest.edn
```

## Data contract

The main entry point is:

```clojure
(kotoba.eda.core/run-flow flow-edn)
```

Each flow contains `:eda.flow/jobs`. A job has:

- `:eda.job/id`
- `:eda.job/tool`
- `:eda.job/operation`
- tool-specific EDN payload

Each result is signoff evidence with `:eda.signoff/type`, `:eda.signoff/tool`,
`:eda.signoff/operation`, `:eda.signoff/status`, metrics, and deterministic CID.

## OSS verification mode

`kotoba.eda.oss-report` converts real OSS tool reports into the same evidence
shape as the CLJC kernels. This lets CI or a host runner execute OpenROAD,
OpenSTA, KLayout, Magic, Netgen, ngspice, or OpenLane, then store only stable
EDN evidence in the kotoba workbench.

Manifest example:

```edn
{:eda.oss/reports
 [{:eda.job/tool :sw/opensta
   :eda.job/operation :op/analyze-timing
   :eda.job/report "oss-reports/opensta.rpt"
   :eda.signoff/corner "tt_1p80v_25c"}]}
```

The normalizer is intentionally strict: negative slack, route overflow, DRC
violations, LVS mismatches, or ngspice fatal errors become failed evidence.
