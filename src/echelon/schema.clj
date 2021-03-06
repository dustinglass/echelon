(ns echelon.schema
  (:require [datomic.api :refer [tempid]]))

;; Lobbying codes taken from http://1.usa.gov/1lm74is
(def issue-codes
  {"ACC" "Accounting"
   "ADV" "Advertising"
   "AER" "Aerospace"
   "AGR" "Agriculture"
   "ALC" "Alcohol & Drug Abuse"
   "ANI" "Animals"
   "APP" "Apparel/Clothing Industry/Textiles"
   "ART" "Arts/Entertainment"
   "AUT" "Automotive Industry"
   "AVI" "Aviation/Aircraft/Airlines"
   "BAN" "Banking"
   "BEV" "Beverage Industry"
   "BNK" "Bankruptcy"
   "BUD" "Budget/Appropriations"
   "CAW" "Clean Air & Water (Quality)"
   "CDT" "Commodities (Big Ticket)"
   "CHM" "Chemicals/Chemical Industry"
   "CIV" "Civil Rights/Civil Liberties"
   "COM" "Communications/Broadcasting/Radio/TV"
   "CON" "Constitution"
   "CPI" "Computer Industry"
   "CPT" "Copyright/Patent/Trademark"
   "CSP" "Consumer Issues/Safety/Protection"
   "DEF" "Defense"
   "DIS" "Disaster Planning/Emergencies"
   "DOC" "District of Columbia"
   "ECN" "Economics/Economic Development"
   "EDU" "Education"
   "ENG" "Energy/Nuclear"
   "ENV" "Environmental/Superfund"
   "FAM" "Family Issues/Abortion/Adoption"
   "FIN" "Financial Institutions/Investments/Securities"
   "FIR" "Firearms/Guns/Ammunition"
   "FOO" "Food Industry (Safety, Labeling, etc.)"
   "FOR" "Foreign Relations"
   "FUE" "Fuel/Gas/Oil"
   "GAM" "Gaming/Gambling/Casino"
   "GOV" "Government Issues"
   "HCR" "Health Issues"
   "HOM" "Homeland Security"
   "HOU" "Housing"
   "IMM" "Immigration"
   "IND" "Indian/Native American Affairs"
   "INS" "Insurance"
   "INT" "Intelligence and Surveillance"
   "LAW" "Law Enforcement/Crime/Criminal Justice"
   "LBR" "Labor Issues/Antitrust/Workplace"
   "MAN" "Manufacturing"
   "MAR" "Marine/Maritime/Boating/Fisheries"
   "MED" "Medical/Disease Research/Clinical Labs"
   "MIA" "Media (Information/Publishing)"
   "MMM" "Medicare/Medicaid"
   "MON" "Minting/Money/Gold Standard"
   "NAT" "Natural Resources"
   "PHA" "Pharmacy"
   "POS" "Postal"
   "REL" "Religion"
   "RES" "Real Estate/Land Use/Conservation"
   "RET" "Retirement"
   "ROD" "Roads/Highway"
   "RRR" "Railroads"
   "SCI" "Science/Technology"
   "SMB" "Small Business"
   "SPO" "Sports/Athletics"
   "TAR" "Miscellaneous Tariff Bills"
   "TAX" "Taxation/Internal Revenue Code"
   "TEC" "Telecommunications"
   "TOB" "Tobacco"
   "TOR" "Torts"
   "TOU" "Travel/Tourism"
   "TRA" "Transportation"
   "TRD" "Trade (Domestic & Foreign)"
   "TRU" "Trucking/Shipping"
   "UNM" "Unemployment"
   "URB" "Urban Development/Municipalities"
   "UTI" "Utilities"
   "VET" "Veterans"
   "WAS" "Waste (hazardous/solid/interstate/nuclear)"
   "WEL" "Welfare"})

(defn string->issue-code [code]
  (keyword (str "lobbying.issue-code/" code)))

(def issue-code-attributes
  (for [[code doc] issue-codes]
    {:db/id (tempid :db.part/user)
     :db/ident (string->issue-code code)
     :db/doc doc}))

;;Helper functions for datomic. We're not doing too many fancy things
;;here with datomic and the main struggle has just been understanding
;;the layout of the data, so we've abstracted away the creation of the
;;attribute maps for datomic.

(defn- enum [key] {:db/id (tempid :db.part/user) :db/ident key})

(defn- proto-prop [prop doc]
  {:db/id (tempid :db.part/db)
   :db/ident prop
   :db/doc doc
   :db.install/_attribute :db.part/db})

(defn- prop-fn [m]
  (fn [prop doc]
    (-> (proto-prop prop doc)
        (merge m))))

(def bool-prop
  (prop-fn {:db/valueType :db.type/boolean
            :db/cardinality :db.cardinality/one}))

(def long-prop
  (prop-fn {:db/valueType :db.type/long
            :db/cardinality :db.cardinality/one}))

(def double-prop
  (prop-fn {:db/valueType :db.type/double
            :db/cardinality :db.cardinality/one}))

(def float-prop
  (prop-fn {:db/valueType :db.type/float
            :db/cardinality :db.cardinality/one}))

(def dec-prop
  (prop-fn {:db/valueType :db.type/bigdec
            :db/cardinality :db.cardinality/one}))

(def string-prop
  (prop-fn {:db/valueType :db.type/string
            :db/cardinality :db.cardinality/one}))

(def fulltext-prop
  (prop-fn {:db/valueType :db.type/string
            :db/cardinality :db.cardinality/one
            :db/fulltext true}))

(def indexed-string-prop
  (prop-fn {:db/valueType :db.type/string
            :db/cardinality :db.cardinality/one
            :db/index true}))

(def unique-string-prop
  (prop-fn {:db/valueType :db.type/string
            :db/cardinality :db.cardinality/one
            :db/index true
            :db/unique :db.unique/identity}))

(def indexed-prop
  (prop-fn {:db/valueType :db.type/string
            :db/cardinality :db.cardinality/one}))

(def instant-prop
  (prop-fn {:db/valueType :db.type/instant
            :db/cardinality :db.cardinality/one}))

(def ref-prop
  (prop-fn {:db/valueType :db.type/ref
            :db/cardinality :db.cardinality/one}))

(def ref-props
  (prop-fn {:db/valueType :db.type/ref
            :db/cardinality :db.cardinality/many}))

(def component-prop
  (prop-fn {:db/valueType :db.type/ref
            :db/cardinality :db.cardinality/one
            :db/isComponent true}))

(def component-props
  (prop-fn {:db/valueType :db.type/ref
            :db/cardinality :db.cardinality/many
            :db/isComponent true}))

;;Various grouping of attributes from the schema

(def data-attributes
  [(long-prop       :data/position
              "Remembering the order in which we received the given data.")])

(def being-framework-attributes
  [(enum            :being.record/being)
   (indexed-string-prop     :being/id "A uuid for the being")
   (ref-prop        :record/type "A record's type.")
   (ref-prop        :record/represents
                    "Indicates that the record entity with this
                    attribute represents a being. This should be the
                    only property that will ever be overwritten.")])

(def address-attributes
  [(string-prop     :address/first-line  "First line for an address")
   (string-prop     :address/second-line "Second line for an address")
   (string-prop     :address/zipcode     "Zipcode for an address")
   (string-prop     :address/city        "City for an address")
   (string-prop     :address/state       "State for an address")
   (string-prop     :address/country     "Country for an address")])

(def client-attributes
  [(enum            :lobbying.record/client)
   (fulltext-prop   :lobbying.client/name         "Client name.")
   (fulltext-prop   :lobbying.client/description  "Client description.")
   (bool-prop :lobbying.client/state-or-local-government
              "Whether the client is a state or local government.")
   (component-prop  :lobbying.client/main-address "Main address for the client.")
   (component-prop  :lobbying.client/principal-place-of-business
                    "Primary location where a taxpayers's business is
                   performed (bit.ly/1s3ZbG7)")])

(def registrant-attributes
  [(enum            :lobbying.record/registrant)
   (fulltext-prop   :lobbying.registrant/name
                    "Registrant name. Could be self employed
                    individual, organization, or lobbying firm.")
   (fulltext-prop   :lobbying.registrant/organization-name
                    "Name for a lobbying firm or organization.")
   (fulltext-prop   :lobbying.registrant/first-name
                    "First name of a self employed individual registring for lobbying.")
   (fulltext-prop   :lobbying.registrant/last-name
                    "Last name of a self employed individual registring for lobbying.")
   (fulltext-prop   :lobbying.registrant/prefix
                    "Prefix for a self employed individual registring for lobbying.")
   (fulltext-prop   :lobbying.registrant/description  "Registrant description.")
   (bool-prop       :lobbying.registrant/self-employed-individual
                    "Whether an individual is self employed.")
   (bool-prop       :lobbying.registrant/organization-or-lobbying-firm
                    "Whether a registrant is an organization or lobbying firm.")
   (component-prop  :lobbying.registrant/main-address
                    "Main address for reaching the registrant.")
   (component-prop  :lobbying.registrant/principal-place-of-business
                    "Primary location where a taxpayers's business is
                    performed (bit.ly/1s3ZbG7)")])

(def contact-attributes
  [(enum            :lobbying.record/contact)
   (string-prop     :lobbying.contact/name-prefix  "Contact name prefix.")
   (fulltext-prop   :lobbying.contact/name  "Contact name.")
   (string-prop     :lobbying.contact/phone "Contact phone.")
   (string-prop     :lobbying.contact/email "Contact email.")])

(def lobbyist-attributes
  [(enum            :lobbying.record/lobbyist)
   (fulltext-prop   :lobbying.lobbyist/first-name "First name of lobbyist.")
   (fulltext-prop   :lobbying.lobbyist/last-name  "Last name of lobbyist.")
   (bool-prop       :lobbying.lobbyist/is-new     "Whether the lobbyist is new.")
   (string-prop     :lobbying.lobbyist/suffix     "Suffix of lobbyist.")
   (fulltext-prop   :lobbying.lobbyist/covered-official-position
                    "No idea!")])

(def activity-attributes
  [(enum            :lobbying.record/activity)
   ;;Both types
   (component-props :lobbying.activity/lobbyists
                    "The foreign entities for the activity.")

   ;;Activities from registrations
   (fulltext-prop   :lobbying.activity/general-details
                    "Details about the lobbying generally done by the
                    registrant for the client on various issues.")
   (ref-props       :lobbying.activity/issue-codes
                    "The issue codes generally associated with the activity.")

   ;;Activities from reports
   (fulltext-prop   :lobbying.activity/specific-issues
                    "Details about the lobbying specifically done by the
                    registrant for the client.")
   (ref-prop       :lobbying.activity/issue-code
                   "The issue code associated with a specific activity.")
   (string-prop     :lobbying.activity/houses-and-agencies
                    "Details about the lobbying specifically done by the
                    registrant for the client.")
   (string-prop     :lobbying.activity/foreign-entity-interest
                    "The explanation given for why a foreign entity
                    is interested in a particular activity. ")
   (bool-prop     :lobbying.activity/no-foreign-entity-interest
                  "Whether the lobbyist indicated if a foreign entity was interested.")])


(def foreign-entity-attributes
  [(enum            :lobbying.record/foreign-entity)
   (fulltext-prop   :lobbying.foreign-entity/name
                    "Name of foreign entity.")
   (dec-prop        :lobbying.foreign-entity/amount
                    "Amount contributed to lobbying efforts.")
   (dec-prop        :lobbying.foreign-entity/ownership-percentage
                    "Ownership percentage in client.")
   (component-prop  :lobbying.foreign-entity/main-address
                    "Main address for foreign entity")
   (component-prop  :lobbying.foreign-entity/principal-place-of-business
                    "Principal place of business for foreign entity.")])

(def affiliated-organization-attributes
  [(enum            :lobbying.record/affiliated-organization)
   (fulltext-prop   :lobbying.affiliated-organization/name
                    "Name of foreign entity.")
   (component-prop  :lobbying.affiliated-organization/main-address
                    "Main address for affiliated organization.")
   (component-prop  :lobbying.affiliated-organization/principal-place-of-business
                    "Principal place of business for affiliated organization.")])

(def common-form-attributes
  [ ;;Common parts of each form
   (bool-prop       :lobbying.form/amendment
                    "Whether the form is an amendment.")
   (instant-prop    :lobbying.form/signature-date
                    "We have no idea what this means.")
   (component-prop  :lobbying.form/client
                    "The client for the form.")
   (component-prop  :lobbying.form/registrant
                    "The registrant for the form.")
   (bool-prop       :lobbying.form/client-registrant-same
                    "Whether the client and registrant are the same entity")
   (component-prop  :lobbying.form/contact
                    "The contact for the form.")
   (component-prop  :lobbying.form/individual
                    "Potentially used, if the registrant is an individual for the form.")
   (ref-prop        :lobbying.form/source "Where the data came from.")
   (enum            :lobbying.form/sopr-html)
   (unique-string-prop     :lobbying.form/document-id
                           "Id of a document (provided by sopr).")
   (string-prop     :lobbying.form/filepath
                    "File which data for entity and subcomponents were pulled from.")  ])

(def registration-form-attributes
  [(enum            :lobbying.record/registration)

   (indexed-string-prop
    :lobbying.registration/house-id
    "Id given out by the clerk of the house of representatives
    identifying a registrant. Unsure what this actually means.")
   (indexed-string-prop
    :lobbying.registration/senate-id
    "Id given out by the senate identifying a registrant.")

   (bool-prop       :lobbying.registration/new-registrant
                    "Whether the form is for an new registrant.")
   (bool-prop       :lobbying.registration/new-client-for-existing-registrant
                    "Whether the form is for an new client for an
                    existing registrant.")
   (instant-prop    :lobbying.registration/effective-date
                    "No idea what this one actually means.")
   (component-props :lobbying.registration/affiliated-organizations
                    "The affiliated organizations for the engagement.")
   (component-props :lobbying.registration/foreign-entities
                    "The foreign entities for the engagement.")
   (component-prop  :lobbying.registration/activity
                    "Initial description of lobbying activity")])

(def report-form-attributes
  [(enum            :lobbying.record/report)

   (indexed-string-prop
    :lobbying.report/house-id
    "Id given out by the clerk of the house of representatives
    identifying a client and registrant. Still unsure what it actually
    means.")
   (indexed-string-prop
    :lobbying.report/senate-id
    "Id given out by the senate identifying a client and registrant.")

   (long-prop        :lobbying.report/quarter
                     "The quarter the report covers.")
   (long-prop        :lobbying.report/year
                     "The year the report covers.")

   (bool-prop       :lobbying.report/terminated
                    "Whether the report indicates the
                    client-registrant relationship has been
                    terminated.")
   (instant-prop    :lobbying.report/termination-date
                    "If the report is a termination, this is the date
                    the terimation occurred.")

   (dec-prop        :lobbying.report/income
                    "The income the registrant was paid for it's services.")
   (dec-prop        :lobbying.report/expense
                    "The expenses the registrant incurred while providing services.")
   (enum :lobbying.reporting-method/a)
   (enum :lobbying.reporting-method/b)
   (enum :lobbying.reporting-method/c)
   (ref-prop        :lobbying.report/reporting-method
                    "How the expenses are reported.")
   (bool-prop       :lobbying.report/no-activity
                    "Flag indicating whether there was any activity
                    during this period.")
   (bool-prop       :lobbying.report/expense-less-than-five-thousand
                    "Flag indicating whether the expenses incurred
                    were less than five thousand dollars.")
   (bool-prop       :lobbying.report/income-less-than-five-thousand
                    "Flag indicating whether the income gained was
                    less than five thousand dollars.")

   (component-props :lobbying.report/removed-foreign-entities
                    "Removed foreign entities.")
   (component-props :lobbying.report/added-foreign-entities
                    "Added foreign entities.")
   (component-props :lobbying.report/removed-affiliated-organizations
                    "Removed affiliated organizations.")
   (component-props :lobbying.report/added-affiliated-organizations
                    "Added affiliated organizations.")

                                        ;TODO: not done
   (component-props :lobbying.report/removed-lobbyists
                    "Removed lobbyists.")
   (component-props :lobbying.report/added-lobbyists
                    "Added lobbyists.")
   (component-props :lobbying.report/activities
                    "List of lobbying activities that quarter")
   (ref-props       :lobbying.report/removed-lobbying-issues
                    "Removed lobbying issues.")
   (ref-props       :lobbying.report/added-lobbying-issues
                    "Added lobbying issues.")])

(def parsed-attributes
  [(indexed-string-prop
    :parsed/name
    "This property is the result of using extract-names and str on the
     names of the various types of records. Although extract-names can
     return several different names, each indexed attribute in datomic
     can only have one value. Thus, in a wonderful hack, we call
     rand-nth on the extracted names. Most of the time this will mean
     that the only element in the list is selected and everything is
     fine. Any other time, it means that one of the elements from the
     list is selected to represent it in the index. This would
     normally suck, but because we are doing entity resolution, we can
     easily recover any of the other representations that are linked
     to it. So, you should only use this attribute when you are
     looking for beings not records.")])

(def schema
  (vec (concat data-attributes
               issue-code-attributes
               address-attributes
               being-framework-attributes
               client-attributes
               registrant-attributes
               contact-attributes
               lobbyist-attributes
               affiliated-organization-attributes
               foreign-entity-attributes
               activity-attributes
               common-form-attributes
               registration-form-attributes
               report-form-attributes
               parsed-attributes)))
