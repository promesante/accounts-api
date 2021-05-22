# Clojure REPL Driven Development (RDD) #

## Building a Toy Digital Bank with Emacs, Mount, Pedestal and Datomic ##

Project exposed in my [blog](https://promesante.github.io/), in its [Clojure REPL Driven Development - Building a Toy Digital Bank with Emacs, Mount, Pedestal and Datomic](https://promesante.github.io/2021/04/28/clojure_repl_driven_development_part_1.html) post series, with the following purpose:

**Getting first hand experience in Clojure REPL driven development (RDD)**

## Overview ##

The [Guide to the Duct Framework](https://github.com/duct-framework/docs/blob/master/GUIDE.rst) goes well beyond the scope suggested by that title: it is one of the best articles I happened to have read on Clojure RDD, and on reloaded workflows in particular. It shows a demo of a reloaded workflow in a strictly practical fashion, building a REST API from the ground up, and showing step-by-step the whole path. This approach is particularly illuminating for getting a first hand experience in RDD.

In order to get a tutorial like that, but for a project based on [mount](https://github.com/tolitius/mount) instead of [integrant](https://github.com/weavejester/integrant), I have built this API, similar to the [example](https://github.com/tolitius/mount/tree/master/dev/clj/app) supplied with mount, and explained in mount's official [tutorial](https://github.com/tolitius/mount#mount-and-develop), explaining how its implementation evolved, step by step, in a typical RDD way, in my [blog](https://promesante.github.io/), in its [post series](https://promesante.github.io/2021/04/28/clojure_repl_driven_development_part_1.html) mentioned above.

## Endpoints ##

1. **account view**, `GET /accounts/:account-id`: details of the single account with the `account-id` set
2. **transaction list**, `GET /accounts/:account-id/transactions`: list of transactions already performed on the account with the `account-id` set
3. **transaction create**, `POST /accounts/:account-id`: creating (executing) a transaction on the account with the `account-id` set;

## Transactions ##

Described below, along an example of the `JSON` body accompanying the corresponding `HTTP POST` for each one:

### Deposit ###

```json
{
  "amount": 1000.00,
  "description": "appartment rent - march 2021"
}
```

Positive `amount`, no `account` (attribute exclusive for transfers)

### Withdrawal ###

```json
{
  "amount": -1000.00,
  "description": "appartment rent - march 2021"
}
```

Negative `amount`

### Transfer ###

```json
{
  "amount": -1000.00,
  "account": "account-1",
  "description": "appartment rent - march 2021"
}
```

Negative `amount`, setting target account's id in `account`

## Application Structure ##

Each of those endpoints will hold the following two modules:
1. `db`: Datomic database management
2. `web`: REST API; mainly, Pedestal interceptors


## Implementation Strategy and Post Series Structure ##

The aspects exposed in the previous two sections, **Endpoints**, and **Application Structure**, will determine this post series' structure, as well as the implementation path exposed below, along [pull request](https://github.com/promesante/accounts-api/pulls?q=is%3Apr+is%3Aclosed) sequence, in the following iterative approch:

1. **account view**: [part 2](https://promesante.github.io/2021/04/28/clojure_repl_driven_development_part_2.html) of this series
    1. database
	2. web
	3. end-to-end testing
	4. **RDD session demo**: [part 3](https://promesante.github.io/2021/04/28/clojure_repl_driven_development_part_3.html)
2. **transaction list**: [part 4](https://promesante.github.io/2021/04/28/clojure_repl_driven_development_part_4.html)
    1. database
	2. web
	3. end-to-end testing
3. **transaction create**: [part 5](https://promesante.github.io/2021/04/28/clojure_repl_driven_development_part_5.html)
    1. database
	2. web
	3. end-to-end testing

Among these parts of the series, the most **important** one is [part 3](https://promesante.github.io/2021/04/28/clojure_repl_driven_development_part_3.html), **1.4, RDD session demo**, as it actually fulfills the most the whole series goal: **getting first hand experience in Clojure REPL driven development (RDD)**.

## References ##

We have taken as reference the following articles.

* **Emacs Setup**: [My Optimal GNU Emacs Settings for Developing Clojure (Revised)](http://fgiasson.com/blog/index.php/2016/06/14/my-optimal-gnu-emacs-settings-for-developing-clojure-revised/)
* **Mount**: [project README](https://github.com/tolitius/mount#mount-and-develop)
* **Pedestal**:
    * [Your First API](http://pedestal.io/guides/your-first-api)
	* [Unit testing](http://pedestal.io/reference/unit-testing)
* **Datomic**: 
    * [Datomic Official Tutorial](https://docs.datomic.com/on-prem/tutorial/introduction.html)
	* [Datomic Missing Link Tutorial](https://github.com/ftravers/datomic-tutorial)

Each of them has been an excellent tutorial for me, for its corresponding tool above. Hence, if you don't have experience in any of them, I'd suggest to read them before going on with this series, as we assume that level of understanding about each.

## Setup ##

We have used [clj-new](https://github.com/seancorfield/clj-new): we need to add the following alias inside your `:aliases` map in  `~/.clojure/deps.edn`:

```clojure
    ;; add this inside your :aliases map:
    :new {:extra-deps {com.github.seancorfield/clj-new
                         {:mvn/version "1.1.297"}}
            :exec-fn clj-new/create
            :exec-args {:template "app"}}
```

Then, let's create the project:


```shell
$ clojure -X:new :name accounts/accounts
```

We are thus ready to begin coding.

## Implementation ##

The part which deserves explanation are Pedestal Interceptors:
1. its **organization** and
2. **data structure** deviced to handle data, to store data in, or take it from, making it flow step by step along the interceptor chain bound to every endpoint

### Interceptor Organization ###

As suggested in Pedestal documentation, we embraced interceptors as much as possible, and organized them as shown below:

1. **validate** HTTP `request` parameters
2. **retrieve** data from database
3. **update** data into database
2. **prepare** (**retrieve** or **update**) data for each of the corresponding operations just mentioned
4. **display** data in `response` as the result of the interceptor chain execution

We will now explain interceptors bound to the **transaction create** endpoint in particular, as it has associated much more than the other endpoints; we will list and briefly describe them below:

#### Validate ####

We may group them the following way:

* [validating HTTP request params](https://github.com/promesante/accounts-api/pull/11/files): as this endpoint is the first HTTP POST, this validation interceptors target its request JSON body
* [retrieving accounts for its validation](https://github.com/promesante/accounts-api/pull/12)
* [validating accounts](https://github.com/promesante/accounts-api/pull/13): 
    * whether they are actually available
	* in the case of executing a debit transaction against it, whether it has sufficient funds

#### Update Preparation ####

In these interceptors, data is prepared to be handled in the following interceptor set, `update`, in which they will be transacted against our Datomic database. Then, it will be left in data structure's `:tx-data` field.

These interceptors prepare the following entities:

* account's **new balance**
* **new transaction** is deviced

#### Update ####

Each of these two entities is taken from data structures's `:tx-data` entry, and then transacted against our Datomic database.

#### Display ####

Transaction just created is bound to data structures's `:result` key in order to have it ready for the `entity-render` interceptor to set it in `response`.


### Data Structure ###

To handle data, making it flow step by step along the interceptor chain bound to every endpoint, we have to device a data structure to store data in, or take it from, the following way:

1. `:request`: this is the data which comes with the HTTP `GET request`, that is bound to this key in the interceptor chain's Pedestal context
2. `:query-data`: `prepare-retrieve` interceptors bind data to this key, leaving it prepared for `retrieve` interceptors
3. `:retrieved`: `retrieve` interceptors store retrieved data here
4. `:result`: `display` interceptors store data here in order to have it ready for the `entity-render` interceptor to set it in `response`

The following are examples of this data structure for each endpoint, along with the corresponding post series part:

**account view**: [part 2](https://promesante.github.io/2021/04/28/clojure_repl_driven_development_part_2.html)

```clojure
  {:request {:path-params {:account-id "account-1"}}
   :query-data {:debit {:id "account-1"}}
   :retrieved {:accounts {:report #:account{:id "account-1", :balance 10000.0}}}
   :result {#:account{:id "account-1", :balance 10000.0}}}
```

**transaction list**: [part 4](https://promesante.github.io/2021/04/28/clojure_repl_driven_development_part_4.html)

```clojure
  {:request {:path-params {:account-id "account-1"}}
   :query-data {:report {:id "account-1"}}
   :retrieved
   {:txs
    ({:db/id 17592186045435,
      :transaction/id "trx-10",
      :transaction/amount 1000.0,
      :transaction/description "thomas' present",
      :transaction/transfer-account-id #:db{:id 17592186045419},
      :transaction/balance 10000.0}
     {:db/id 17592186045427,
      :transaction/id "trx-4",
      :transaction/amount -1000.0,
      :transaction/description "appartment rent - febr 2021",
      :transaction/balance 9000.0}
     {:db/id 17592186045422,
      :transaction/id "trx-1",
      :transaction/amount 10000.0,
      :transaction/description "first deposit",
      :transaction/balance 10000.0})}
   :result
    ({:db/id 17592186045435,
      :transaction/id "trx-10",
      :transaction/amount 1000.0,
      :transaction/description "thomas' present",
      :transaction/transfer-account-id #:db{:id 17592186045419},
      :transaction/balance 10000.0}
     {:db/id 17592186045427,
      :transaction/id "trx-4",
      :transaction/amount -1000.0,
      :transaction/description "appartment rent - febr 2021",
      :transaction/balance 9000.0}
     {:db/id 17592186045422,
      :transaction/id "trx-1",
      :transaction/amount 10000.0,
      :transaction/description "first deposit",
      :transaction/balance 10000.0})}
```
The only difference is the key inside `:retrieved` data got from the database is bound to:` :txs`.

**transaction create**: [part 5](https://promesante.github.io/2021/04/28/clojure_repl_driven_development_part_5.html)

```clojure
  {:request
   {:path-params {:account-id "account-1"}
    :json-params {:amount 1000.0 :description "test"}}
   :query-data {:debit {:id "account-1"}}
   :retrieved {:accounts {:credit #:account{:id "account-1", :balance 10000.0}}}
   :tx-data
   {:credit
    {:id "account-1"
     :new-balance 11000.0
     :tx {:amount 1000.0 :description "test" :balance 11000.0}}}
   :result {:amount 1000.0 :description "test" :balance 11000.0}}
```

The only key new in this endpoint is `:tx-data`: `prepare-update` interceptors leave data there, "prepared" for `update` interceptors to actually run the corresponding Datomic transaction:


## Usage ##

As project goals emphasize REPL coupled with Emacs, all the examples given below run in that context. And they will follow the "Implementation Strategy and Post Series Structure" exposed above.

### Account View ###

#### Database ####

We will:
1. startup our API by means of mount state manager
2. invoke `load-database` function in order to load the database's schema and run the migrations to load data into the database bound to that schema
3. query the account created and updated by the migrations
4. stop the API again by means of mount

These tasks are shown below in the context of an actual REPL session:

```clojure
user> (start)
{:started ["#'accounts.conf/config" "#'accounts.db.conn/conn"]}
user> (c/load-database)
{:db-before datomic.db.Db@4eac34a8,
 :db-after datomic.db.Db@72d65ed1,
 :tx-data
 [#datom[13194139534317 50 #inst "2021-05-03T22:29:57.745-00:00" 13194139534317 true] #datom[17592186045418 64 10000.0 13194139534317 true] #datom[17592186045418 64 0.0 13194139534317 false] #datom[17592186045419 64 20000.0 13194139534317 true] #datom[17592186045419 64 0.0 13194139534317 false] #datom[17592186045420 64 30000.0 13194139534317 true] #datom[17592186045420 64 0.0 13194139534317 false]],
 :tempids {}}
user> (q/pull-account-by-id "account-1")
#:account{:id "account-1", :balance 10000.0}
user> (stop)
{:stopped ["#'accounts.db.conn/conn"]}
user>
```

#### Web ####

This session will begin and end in exactly the same way as our previous one..

Functionality added in this interceptor set can be tested with:
1. `response-for` function from `io.pedestal.test` namespace, wrapped in our own util function, `test-request`
2. in order to type a bit less in this task, that we will have to run quite repetitively, we can wrap it in our own function, `account-view`, in our `user` namespace, which has basically that purpose: holding development utils

```clojure
accounts.web.interceptors.validate-test> 
user> (start)
{:started
 ["#'accounts.conf/config"
  "#'accounts.db.conn/conn"
  "#'accounts.web.server/server"]}
user> (c/load-database)
{:db-before datomic.db.Db@8cbce164,
 :db-after datomic.db.Db@c4fe9953,
 :tx-data
 [#datom[13194139534317 50 #inst "2021-05-05T09:11:57.819-00:00" 13194139534317 true] #datom[17592186045418 64 10000.0 13194139534317 true] #datom[17592186045418 64 0.0 13194139534317 false] #datom[17592186045419 64 20000.0 13194139534317 true] #datom[17592186045419 64 0.0 13194139534317 false] #datom[17592186045420 64 30000.0 13194139534317 true] #datom[17592186045420 64 0.0 13194139534317 false]],
 :tempids {}}
user> (test-request :get "/accounts/account-1")
{:status 200,
 :body "{\"account/id\":\"account-1\",\"account/balance\":10000.0}",
 :headers
 {"Strict-Transport-Security" "max-age=31536000; includeSubdomains",
  "X-Frame-Options" "DENY",
  "X-Content-Type-Options" "nosniff",
  "X-XSS-Protection" "1; mode=block",
  "X-Download-Options" "noopen",
  "X-Permitted-Cross-Domain-Policies" "none",
  "Content-Security-Policy"
  "object-src 'none'; script-src 'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:;",
  "Content-Type" "application/json;charset=UTF-8"}}
user> (account-view)
{:status 200,
 :body "{\"account/id\":\"account-1\",\"account/balance\":10000.0}",
 :headers
 {"Strict-Transport-Security" "max-age=31536000; includeSubdomains",
  "X-Frame-Options" "DENY",
  "X-Content-Type-Options" "nosniff",
  "X-XSS-Protection" "1; mode=block",
  "X-Download-Options" "noopen",
  "X-Permitted-Cross-Domain-Policies" "none",
  "Content-Security-Policy"
  "object-src 'none'; script-src 'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:;",
  "Content-Type" "application/json;charset=UTF-8"}}
user> (stop)
{:stopped ["#'accounts.web.server/server" "#'accounts.db.conn/conn"]}
user>
```

### Transaction List ###

We will:
1. run the Datomic query
2. invoke the endpoint by means of `response-for` function from `io.pedestal.test` namespace, wrapped in our own util function, `test-request` and then wrapped in turn in `transaction-list`

```clojure
user> (start)
{:started
 ["#'accounts.conf/config"
  "#'accounts.db.conn/conn"
  "#'accounts.web.server/server"]}
user> (c/load-database)
{:db-before datomic.db.Db@46f9a83b,
 :db-after datomic.db.Db@7403a3ef,
 :tx-data
 [#datom[13194139534332 50 #inst "2021-05-06T09:22:22.918-00:00" 13194139534332 true] #datom[17592186045418 64 10000.0 13194139534332 true] #datom[17592186045418 64 9000.0 13194139534332 false] #datom[17592186045419 64 20000.0 13194139534332 true] #datom[17592186045419 64 19000.0 13194139534332 false] #datom[17592186045420 64 26000.0 13194139534332 true] #datom[17592186045420 64 28000.0 13194139534332 false]],
 :tempids {}}
user> (q/pull-account-by-id "account-1")
#:account{:id "account-1", :balance 10000.0}
user> (q/pull-transactions-by-account-id "account-1")
({:db/id 17592186045435,
  :transaction/id "trx-10",
  :transaction/amount 1000.0,
  :transaction/description "thomas' present",
  :transaction/transfer-account-id #:db{:id 17592186045419},
  :transaction/balance 10000.0}
 {:db/id 17592186045427,
  :transaction/id "trx-4",
  :transaction/amount -1000.0,
  :transaction/description "appartment rent - febr 2021",
  :transaction/balance 9000.0}
 {:db/id 17592186045422,
  :transaction/id "trx-1",
  :transaction/amount 10000.0,
  :transaction/description "first deposit",
  :transaction/balance 10000.0})
user> (transaction-list)
{:status 200,
 :body
 "[{\"db/id\":17592186045435,\"transaction/id\":\"trx-10\",\"transaction/amount\":1000.0,\"transaction/description\":\"thomas' present\",\"transaction/transfer-account-id\":{\"db/id\":17592186045419},\"transaction/balance\":10000.0},{\"db/id\":17592186045427,\"transaction/id\":\"trx-4\",\"transaction/amount\":-1000.0,\"transaction/description\":\"appartment rent - febr 2021\",\"transaction/balance\":9000.0},{\"db/id\":17592186045422,\"transaction/id\":\"trx-1\",\"transaction/amount\":10000.0,\"transaction/description\":\"first deposit\",\"transaction/balance\":10000.0}]",
 :headers
 {"Strict-Transport-Security" "max-age=31536000; includeSubdomains",
  "X-Frame-Options" "DENY",
  "X-Content-Type-Options" "nosniff",
  "X-XSS-Protection" "1; mode=block",
  "X-Download-Options" "noopen",
  "X-Permitted-Cross-Domain-Policies" "none",
  "Content-Security-Policy"
  "object-src 'none'; script-src 'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:;",
  "Content-Type" "application/json;charset=UTF-8"}}
user> (stop)
{:stopped ["#'accounts.web.server/server" "#'accounts.db.conn/conn"]}
user> 
```

It is basically the same as for the previous endpoint.

### Transaction Create ###

It is handled by means of an HTTP POST. We deviced its JSON body structure according to transaction type. In all of them, amount must be a `double`.

1. **deposit**: positive `amount`, no `account` (attribute exclusive for transfers)
1. **withdrawal**: negative `amount`
1. **transfer**: negative `amount`, setting target account's id in `account`

Examples:

**Deposit**

```json
{
  "amount": 1000.00,
  "description": "appartment rent - march 2021"
}
```

**Withdrawal**

```json
{
  "amount": -1000.00,
  "description": "appartment rent - march 2021"
}
```

**Transfer**

```json
{
  "amount": -1000.00,
  "account": "account-1",
  "description": "appartment rent - march 2021"
}
```

Although in this body there is no explicit indication on transation type, each of them can be distinguished by the following criteria:
* if `:account` attribute is present, transaction is a `transfer`; otherwise, it is a `deposit` or `withdrawal`
* single difference between `deposit` and `withdrawal` takes place just in its `amount` sign: positive for `deposits`, and viceversa

`transfer` transactions are basically splitted into a `deposit` into the target account, and a `withdrawal` from the source one.

To actually handle each of these transactions, several interceptors has two versions, carrying the following sufixes, usually delegating its actual implementation into a common function, with `type` as the identifying parameter:
* **credit**: when money is put into an account
* **debit**: when it is taken from it

We will:
2. **deposit** U$S 2,000 into `account-1`
3. **withdraw** U$S 1,000 from it
4. **transfer** U$S 1,000 from `account-1` to `account-2`

Immediately before and after each of these transactions, we query database on each account involved in the transaction as well as that account's transaction log.

```clojure
user> (start)
{:started
 ["#'accounts.conf/config"
  "#'accounts.db.conn/conn"
  "#'accounts.web.server/server"]}
user> (c/load-database)
{:db-before datomic.db.Db@ae9a6f92,
 :db-after datomic.db.Db@a1ccb95e,
 :tx-data
 [#datom[13194139534332 50 #inst "2021-05-07T10:03:22.467-00:00" 13194139534332 true] #datom[17592186045418 64 10000.0 13194139534332 true] #datom[17592186045418 64 9000.0 13194139534332 false] #datom[17592186045419 64 20000.0 13194139534332 true] #datom[17592186045419 64 19000.0 13194139534332 false] #datom[17592186045420 64 26000.0 13194139534332 true] #datom[17592186045420 64 28000.0 13194139534332 false]],
 :tempids {}}
user> (q/pull-account-by-id "account-1")
#:account{:id "account-1", :balance 10000.0}
user> (q/pull-transactions-by-account-id "account-1")
({:db/id 17592186045435,
  :transaction/id "trx-10",
  :transaction/amount 1000.0,
  :transaction/description "thomas' present",
  :transaction/transfer-account-id #:db{:id 17592186045419},
  :transaction/balance 10000.0}
 {:db/id 17592186045427,
  :transaction/id "trx-4",
  :transaction/amount -1000.0,
  :transaction/description "appartment rent - febr 2021",
  :transaction/balance 9000.0}
 {:db/id 17592186045422,
  :transaction/id "trx-1",
  :transaction/amount 10000.0,
  :transaction/description "first deposit",
  :transaction/balance 10000.0})
user> (deposit-1)
{:status 200,
 :body
 "{\"amount\":2000.0,\"description\":\"second deposit\",\"balance\":12000.0}",
 :headers
 {"Strict-Transport-Security" "max-age=31536000; includeSubdomains",
  "X-Frame-Options" "DENY",
  "X-Content-Type-Options" "nosniff",
  "X-XSS-Protection" "1; mode=block",
  "X-Download-Options" "noopen",
  "X-Permitted-Cross-Domain-Policies" "none",
  "Content-Security-Policy"
  "object-src 'none'; script-src 'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:;",
  "Content-Type" "application/json;charset=UTF-8"}}
user> (q/pull-account-by-id "account-1")
#:account{:id "account-1", :balance 12000.0}
user> (q/pull-transactions-by-account-id "account-1")
({:db/id 17592186045439,
  :transaction/id "trx-930809",
  :transaction/amount 2000.0,
  :transaction/description "second deposit",
  :transaction/balance 12000.0}
 {:db/id 17592186045435,
  :transaction/id "trx-10",
  :transaction/amount 1000.0,
  :transaction/description "thomas' present",
  :transaction/transfer-account-id #:db{:id 17592186045419},
  :transaction/balance 10000.0}
 {:db/id 17592186045427,
  :transaction/id "trx-4",
  :transaction/amount -1000.0,
  :transaction/description "appartment rent - febr 2021",
  :transaction/balance 9000.0}
 {:db/id 17592186045422,
  :transaction/id "trx-1",
  :transaction/amount 10000.0,
  :transaction/description "first deposit",
  :transaction/balance 10000.0})
user> (withdrawal)
{:status 200,
 :body
 "{\"amount\":-1000.0,\"description\":\"appartment rent - march 2021\",\"balance\":11000.0}",
 :headers
 {"Strict-Transport-Security" "max-age=31536000; includeSubdomains",
  "X-Frame-Options" "DENY",
  "X-Content-Type-Options" "nosniff",
  "X-XSS-Protection" "1; mode=block",
  "X-Download-Options" "noopen",
  "X-Permitted-Cross-Domain-Policies" "none",
  "Content-Security-Policy"
  "object-src 'none'; script-src 'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:;",
  "Content-Type" "application/json;charset=UTF-8"}}
user> (q/pull-account-by-id "account-1")
#:account{:id "account-1", :balance 11000.0}
user> (q/pull-transactions-by-account-id "account-1")
({:db/id 17592186045442,
  :transaction/id "trx-930817",
  :transaction/amount -1000.0,
  :transaction/description "appartment rent - march 2021",
  :transaction/balance 11000.0}
 {:db/id 17592186045439,
  :transaction/id "trx-930809",
  :transaction/amount 2000.0,
  :transaction/description "second deposit",
  :transaction/balance 12000.0}
 {:db/id 17592186045435,
  :transaction/id "trx-10",
  :transaction/amount 1000.0,
  :transaction/description "thomas' present",
  :transaction/transfer-account-id #:db{:id 17592186045419},
  :transaction/balance 10000.0}
 {:db/id 17592186045427,
  :transaction/id "trx-4",
  :transaction/amount -1000.0,
  :transaction/description "appartment rent - febr 2021",
  :transaction/balance 9000.0}
 {:db/id 17592186045422,
  :transaction/id "trx-1",
  :transaction/amount 10000.0,
  :transaction/description "first deposit",
  :transaction/balance 10000.0})
user> (q/pull-account-by-id "account-2")
#:account{:id "account-2", :balance 20000.0}
user> (q/pull-transactions-by-account-id "account-2")
({:db/id 17592186045434,
  :transaction/id "trx-9",
  :transaction/amount -1000.0,
  :transaction/description "thomas' present",
  :transaction/transfer-account-id #:db{:id 17592186045418},
  :transaction/balance 20000.0}
 {:db/id 17592186045433,
  :transaction/id "trx-8",
  :transaction/amount 2000.0,
  :transaction/description "peter's present",
  :transaction/transfer-account-id #:db{:id 17592186045420},
  :transaction/balance 26000.0}
 {:db/id 17592186045428,
  :transaction/id "trx-5",
  :transaction/amount -1000.0,
  :transaction/description "credit card - febr 2021",
  :transaction/balance 19000.0}
 {:db/id 17592186045423,
  :transaction/id "trx-2",
  :transaction/amount 20000.0,
  :transaction/description "first deposit",
  :transaction/balance 20000.0})
user> (transfer)
{:status 200,
 :body
 "{\"amount\":-1000.0,\"description\":\"anne's present\",\"account-id\":\"account-2\",\"balance\":10000.0}",
 :headers
 {"Strict-Transport-Security" "max-age=31536000; includeSubdomains",
  "X-Frame-Options" "DENY",
  "X-Content-Type-Options" "nosniff",
  "X-XSS-Protection" "1; mode=block",
  "X-Download-Options" "noopen",
  "X-Permitted-Cross-Domain-Policies" "none",
  "Content-Security-Policy"
  "object-src 'none'; script-src 'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:;",
  "Content-Type" "application/json;charset=UTF-8"}}
user> (q/pull-account-by-id "account-1")
#:account{:id "account-1", :balance 10000.0}
user> (q/pull-transactions-by-account-id "account-1")
({:db/id 17592186045446,
  :transaction/id "trx-930830",
  :transaction/amount -1000.0,
  :transaction/description "anne's present",
  :transaction/balance 10000.0}
 {:db/id 17592186045442,
  :transaction/id "trx-930817",
  :transaction/amount -1000.0,
  :transaction/description "appartment rent - march 2021",
  :transaction/balance 11000.0}
 {:db/id 17592186045439,
  :transaction/id "trx-930809",
  :transaction/amount 2000.0,
  :transaction/description "second deposit",
  :transaction/balance 12000.0}
 {:db/id 17592186045435,
  :transaction/id "trx-10",
  :transaction/amount 1000.0,
  :transaction/description "thomas' present",
  :transaction/transfer-account-id #:db{:id 17592186045419},
  :transaction/balance 10000.0}
 {:db/id 17592186045427,
  :transaction/id "trx-4",
  :transaction/amount -1000.0,
  :transaction/description "appartment rent - febr 2021",
  :transaction/balance 9000.0}
 {:db/id 17592186045422,
  :transaction/id "trx-1",
  :transaction/amount 10000.0,
  :transaction/description "first deposit",
  :transaction/balance 10000.0})
user> (q/pull-account-by-id "account-2")
#:account{:id "account-2", :balance 21000.0}
user> (q/pull-transactions-by-account-id "account-2")
({:db/id 17592186045448,
  :transaction/id "trx-930831",
  :transaction/amount 1000.0,
  :transaction/description "anne's present",
  :transaction/balance 21000.0}
 {:db/id 17592186045434,
  :transaction/id "trx-9",
  :transaction/amount -1000.0,
  :transaction/description "thomas' present",
  :transaction/transfer-account-id #:db{:id 17592186045418},
  :transaction/balance 20000.0}
 {:db/id 17592186045433,
  :transaction/id "trx-8",
  :transaction/amount 2000.0,
  :transaction/description "peter's present",
  :transaction/transfer-account-id #:db{:id 17592186045420},
  :transaction/balance 26000.0}
 {:db/id 17592186045428,
  :transaction/id "trx-5",
  :transaction/amount -1000.0,
  :transaction/description "credit card - febr 2021",
  :transaction/balance 19000.0}
 {:db/id 17592186045423,
  :transaction/id "trx-2",
  :transaction/amount 20000.0,
  :transaction/description "first deposit",
  :transaction/balance 20000.0})
user> 
```

