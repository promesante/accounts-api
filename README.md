
Clojure REPL Driven Development (RDD)

Building a Toy Digital Bank with Emacs, Mount, Pedestal and Datomic

getting first hand experience in Clojure REPL driven development (RDD)

In order to get a tutorial like Duct's Guide mentioned above, but for a project based on mount instead of integrant, I have built an API similar to the one shown in mount's tutorial and example: the [accounts API of a toy digital bank](https://github.com/promesante/accounts-api). And this post will show how its implementation evolved, step by step, in a typical RDD way.

Instead of explaining each of those steps here, in the post series, we have kept track of them by means of git branches and [pull requests](https://github.com/promesante/accounts-api/pulls?q=is%3Apr+is%3Aclosed), and then depict here just what, in our opinion, wouldn't be clear enough by just taking a look at each of those PRs.


# Endpoints

For this first version of the API, we will implement de following endpoints:

1. **account view**, `GET /accounts/:account-id`: details of the single account with the `account-id` set
2. **transaction list**, `GET /accounts/:account-id/transactions`: list of transactions already performed on the account with the `account-id` set
3. **transaction create**, `POST /accounts/:account-id`: creating (executing) a transaction on the account with the `account-id` set;

Types of transactions:

1. **deposit**: positive `amount`, no `account` (attribute exclusive for transfers)
1. **withdrawal**: negative `amount`
1. **transfer**: negative `amount`, setting target account's id in `account`

Examples:

Deposit:

```json
{
  "amount": 1000.00,
  "description": "appartment rent - march 2021"
}
```

Withdrawal:

```json
{
  "amount": -1000.00,
  "description": "appartment rent - march 2021"
}
```

Transfer:

```json
{
  "amount": -1000.00,
  "account": "account-1",
  "description": "appartment rent - march 2021"
}
```


# Application Structure

Each of those endpoints will hold the following two modules:
1. `db`: Datomic database management
2. `web`: REST API; mainly, Pedestal interceptors


# Implementation Strategy and Post Series Structure

The aspects exposed in the previous two sections, **Endpoints**, and **Application Structure**, will determine this post series' structure, as well as the implementation path exposed below, along [pull request](https://github.com/promesante/accounts-api/pulls?q=is%3Apr+is%3Aclosed) sequence, in the following iterative approch:

1. **account view**: [part 2]({% post_url 2021-04-28-clojure_repl_driven_development_part_2 %}) of this series
    1. database
	2. web
	3. end-to-end testing
	4. **RDD session demo**: [part 3]({% post_url 2021-04-28-clojure_repl_driven_development_part_3 %})
2. **transaction list**: [part 4]({% post_url 2021-04-28-clojure_repl_driven_development_part_4 %})
    1. database
	2. web
	3. end-to-end testing
3. **transaction create**: [part 5]({% post_url 2021-04-28-clojure_repl_driven_development_part_5 %})
    1. database
	2. web
	3. end-to-end testing

Among these parts of the series, the most **important** one is [part 3]({% post_url 2021-04-28-clojure_repl_driven_development_part_3 %}), **1.4, RDD session demo**, as it actually fulfills the most the whole series goal: **getting first hand experience in Clojure REPL driven development (RDD)**.

But before beginning those steps, let's tackle initial project setup which, due to its very own nature, is not reflected in any PR.


# Initial Project Setup

We will use [clj-new](https://github.com/seancorfield/clj-new): we need to add the following alias inside your `:aliases` map in  `~/.clojure/deps.edn`:

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


# References

We will take as reference the following articles. Each of them has been an excellent tutorial for me, for its corresponding tool below. So, if you don't have experience in any of them, I'd suggest to read them before going on with this series, as it assumes that level of understanding about each:

* **Emacs Setup**: [My Optimal GNU Emacs Settings for Developing Clojure (Revised)](http://fgiasson.com/blog/index.php/2016/06/14/my-optimal-gnu-emacs-settings-for-developing-clojure-revised/)
* **Mount**: [project README](https://github.com/tolitius/mount#mount-and-develop)
* **Pedestal**:
    * [Your First API](http://pedestal.io/guides/your-first-api)
	* [Unit testing](http://pedestal.io/reference/unit-testing)
* **Datomic**: 
    * [Datomic Official Tutorial](https://docs.datomic.com/on-prem/tutorial/introduction.html)
	* [Datomic Missing Link Tutorial](https://github.com/ftravers/datomic-tutorial)


# Example Usage

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

