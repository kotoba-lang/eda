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

All inputs and outputs are EDN maps. The kernels are pure CLJC so the same data
model can run in a browser, CI, or a host runner.

## Run

```sh
clojure -M:test
clojure -M -e "(require '[kotoba.eda.core :as eda] '[clojure.edn :as edn]) (println (eda/run-flow (edn/read-string (slurp \"docs/sample_flow.edn\"))))"
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

