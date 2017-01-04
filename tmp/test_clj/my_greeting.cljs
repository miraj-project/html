(ns test-clj.my-greeting)

(enable-console-print!)

(js/Polymer
 (clj->js
  {:properties
   {:president {:type js/Boolean, :value true, :readOnly true},
    :x
    {:type js/Number,
     :value 0,
     :notify true,
     :reflectToAttribute true,
     :observer :_x33783},
    :lname {:type js/String, :value "Lincoln", :observer :_lname33784},
    :bool-b {:type js/Boolean, :value true, :readOnly true},
    :y {:type js/Number, :value 99},
    :greeting {:type js/String, :value "Hello Miraj!"}},
   :_down160561 (fn [x] (do (.log js/console "DOWN"))),
   :_lname33784
   (fn
    [new old]
    (.log js/console (str "Old pres: " old "; new: " new))),
   :is :my-greeting,
   :_special.tap160560
   (fn [e] (.log js/console "you tapped the h1 element")),
   :bar (fn [] (.log js/console "BAR")),
   :created (fn [] (.log js/console "CREATED")),
   :behaviors
   [:Polymer.PaperButtonBehavior :Polymer.PaperCheckedElementBehavior],
   :_foo (fn [] (.log js/console "FOO")),
   :_dblclick160563 (fn [x] (.log js/console "DBLCLICK")),
   :_click160562 (fn [x] (.log js/console "CLICK")),
   :_x33783 (fn [new old] (+ new old)),
   :attached (fn [] (.log js/console "ATTACHED")),
   :_mouseover160564 (fn [x] (.log js/console "MOUSEOVER")),
   :hostAttributes
   {:string-attribute "Value", :boolean-attribute true, :tabindex 0},
   :listeners
   {:special.tap :_special.tap160560,
    :down :_down160561,
    :click :_click160562,
    :dblclick :_dblclick160563,
    :mouseover :_mouseover160564}}))
