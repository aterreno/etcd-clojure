# etcd-clojure

A Clojure library to interact with an [etcd](https://github.com/coreos/etcd) server.

Why etcd and not Zookeeper? Well Zookeeper is a bloated terrifying piece of software while etcd is tiny, fast, efficient.

Why this project at all? Googling for etcd & clojure returned zero results.
Now there are a bunch of alternatives, pick up your favourite from etcd [clients matrix](https://github.com/coreos/etcd/blob/master/Documentation/clients-matrix.md).

## Usage

Check on [clojars](https://clojars.org/etcd-clojure) what's the current version.

This library now supports V2 of the etcd API. 

The default server endpoing is http://127.0.0.1 running on port 4001, however you can 'connect' to any server by using the connect! function:

	(connect! "196.0.0.1")

	(connect! "196.0.0.1" 4001)

	(connect! "196.0.0.1" 4001 7001)

##Getting the etcd version
	(version)

##Key Space Operations
###Setting the value of a key, with optional time to live

	(set "key" "value")

	(set "key" "value" :ttl 5)

###Get the value of a key

	(get "key")

###Changing the value of a key

	(set "key" "someothervalue")	

###Deleting a key

	(delete "key")

###Waiting for a change

	(watch "key" callback)

Where callback can be for example:

	(defn callback[arg] (println arg))

###Atomically Creating In-Order Keys

	(create-in-order "key"

###Atomic Compare-and-Swap

This reflects the example on the [etcd api page](https://github.com/coreos/etcd/blob/master/Documentation/api.md)

	(set "foo" "one")
	(set "foo" "three" :prev-exist false)
	(set "foo" "two" :prev-value "one")


###Listing a directory
	(list "/directory")

###Listing a directory in order 
	(list-in-order "/directory")

###Creating a directory 
	(create-dir "/directory")

###Deleting a directory
	(delete-dir "/directory")

###Deleting a value:
	(delete "key")

###Leader Stats
	(stats)

###Self Stats
	(self-stats)

###Store Stats
	(store-stats)

###List machines in the cluster
	(machines)

###Configuration
	(config)

## TODO

- Wait for value (revisit/test)
- Atomic Compare-and-Set (test only)
- Atomic Compare-and-Delete (test, docs)
- connect to cluster, failover and recovery

## License

Apache 2.0


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/aterreno/etcd-clojure/trend.png)](https://bitdeli.com/free "Bitdeli Badge")
