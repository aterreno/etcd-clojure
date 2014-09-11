#!/bin/bash
etcd -peer-addr 127.0.0.1:7001 -cors='*' -addr 127.0.0.1:4001 -data-dir ./data/machines/machine1 -name machine1 &
etcd -peer-addr 127.0.0.1:7002 -cors='*' -addr 127.0.0.1:4002 -peers 127.0.0.1:7001,127.0.0.1:7003 -data-dir ./data/machines/machine2 -name machine2 &
etcd -peer-addr 127.0.0.1:7003 -cors='*' -addr 127.0.0.1:4003 -peers 127.0.0.1:7001,127.0.0.1:7002 -data-dir ./data/machines/machine3 -name machine3 &
