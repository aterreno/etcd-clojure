# etcd-clojure

A Clojure library to interact with an [etcd](https://github.com/coreos/etcd) server .

## Usage

	(connect! "http://127.0.0.1:4001")

	(set "message" "somevalue")

	(get "message")

	(delete "message")

## License

Apache 2.0
