# etcd-clojure

A Clojure library to interact with an [etcd](https://github.com/coreos/etcd) server .

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

## TODO

- Watching a prefix
- Https
- Nodes operations

## License

Apache 2.0
