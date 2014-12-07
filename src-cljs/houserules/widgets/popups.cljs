(ns houserules.widgets.popups
  (:require [reagent.core :as reagent :refer [atom]]))

(def current-popup (atom nil))