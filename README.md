# etcd-clojure

A Clojure library to interact with an [etcd](https://github.com/coreos/etcd) server .

## Usage

The default server endpoing is http://127.0.0.1 running on port 4001, however you can 'connect' to any server by using the connect! function:

	(connect! "http://127.0.0.1:4001")

Setting a value is supported with optional ttl param:

	(set "message" "somevalue")

	(set "message" "somevalue" :ttl 5)

Getting a value:

	(get "message")

Deleting a value:

	(delete "message")

## License

Apache 2.0
