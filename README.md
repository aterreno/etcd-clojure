# etcd-clojure

A Clojure library to interact with an [etcd](https://github.com/coreos/etcd) server .

Why etcd and not Zookeper? Well Zookeeper is a bloated terrifying piece of software while etcd is tiny, fast, efficient.

Why this project at all? Googling for etcd & clojure returned zero results.

## Usage

Check on [clojars](https://clojars.org/etcd-clojure) what's the current version.

The default server endpoing is http://127.0.0.1 running on port 4001, however you can 'connect' to any server by using the connect! function:

	(connect! "http://127.0.0.1:4001")

Setting a value is supported with optional ttl param:

	(set "message" "somevalue")

	(set "message" "somevalue" :ttl 5)

You can also use the atomic test and set by providing the :prev-val param:

	(set "message" "somenewvalue" :prev-val "somevalue")

Getting a value:

	(get "message")

Listing the root:

	(get "/")

Deleting a value:

	(delete "message")

Get the machines in the cluster

	(machines)

Watch for changes to a certain key

	(watch "message" callback)

Where callback can be for example:

	(defn callback[arg] (println arg))

## TODO

- Align code to http://docs.etcd.apiary.io/
- Keep adding 'integration' tests
- Watching a prefix with index
- Https
- Handle paths /foo/foo/etc
- List a subkeys
- failover and recovery
- connect to cluster
- refactor to eliminate repetition of '(parse-string (:body (client/ etc..'

## License

Apache 2.0


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/aterreno/etcd-clojure/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

