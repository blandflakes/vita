(ns vita
  (:require [clojure.string :as str]
            [clojure.edn :as edn]
            [clj-htmltopdf.core :refer [->pdf]])
  (:use [hiccup.page]
        [hiccup.element :refer [link-to]]))

(defn #^:private vita->html
  []
  (let [vita (edn/read-string (slurp "vita.edn"))]
    (html5
      {:lang "en"}
      (include-css "style.css")
      [:head [:title (str (:name vita) ": " (:title vita))]]
      [:body
       [:h1 (str (:name vita) " - " (:title vita))]
       [:p (:summary vita)]
       (let [contact (->> vita
                          :personas
                          (map (fn [[name url]] (link-to url name)))
                          (cons (System/getenv "PHONE_CONTACT"))
                          (cons (System/getenv "EMAIL_CONTACT"))
                          (filter identity))]
         [:span (interpose " | " contact)])
       [:div
        [:h2 "Stack"]
        [:h3 "Languages"]
        [:p "Primarily: " (str/join ", " (get-in vita [:stack :languages :core]))]
        [:p "Also: " (str/join ", " (get-in vita [:stack :languages :auxiliary]))]
        [:h3 "Tools"]
        [:p (str/join ", " (get-in vita [:stack :tools]))]
        [:h3 "Patterns"]
        [:p (str/join ", " (:patterns vita))]
        [:h3 "Platforms"]
        [:p (str/join ", " (get-in vita [:stack :platforms] vita))]]
       [:div
        [:h2 "Experience"]
        (for [company (filter (complement :irrelevant) (:experience vita))]
          [:div
           [:h3 (link-to (:url company) (:company company))]
           (for [role (:roles company)]
             [:div
              [:h4 (if-let [team (:team role)]
                     (str (:title role) ", " team)
                     (:title role))]
              [:p (str (:started role) " - " (get role :ended "present"))]
              [:ul
               (for [responsibility (:responsibilities role)]
                 [:li responsibility])]])])]
       (when-let [projects (seq (:side-projects vita))]
         [:div
          [:h2 "Projects"]
          (for [project projects]
            [:div
             [:h3 (:name project)]
             [:p (:description project)]
             (when-let [links (:links project)]
               [:div
                [:p "Links:"]
                [:ul
                 (for [[label link] links]
                   [:li (link-to link label)])]])])])
       [:div
        [:h2 "Education"]
        (for [program (:education vita)]
          [:div
           [:h3 (str (:degree program) " " (:major program) ", " (:graduated program))]
           [:p (str (:institution program) ", " (:location program))]])]
       [:div
        [:h2 "Interests"]
        [:p (str/join ", " (:diversions vita))]]])))

(defn -main
  "Generates HTML and PDF versions of vita.edn"
  []
  (let [rendered (vita->html)]
    (spit "index.html" rendered)
    (->pdf rendered "resume.pdf")))