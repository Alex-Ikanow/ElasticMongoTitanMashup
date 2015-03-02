Deploy instructions:

1] Start MongoDB, note needs to be running in replica set mode (to get oplog)
(copy config/mongod.conf into /etc/mongod

sudo mongod -f /etc/mongod.conf --fork

TODO: for some reason "sudo start mongod" didn't work, even after fixing lock/setting perms to mongodb.mongodb)

2] Start ES:
(copy config/*.yml into /etc/elasticsearch, service elasticsearch start)

First time: Install elasticsearch head: sudo /usr/share/elasticsearch/bin/plugin -install mobz/elasticsearch-head

2.1] TODO Kibana

3] TODO: do something with TitanDB
(curr root dir is ~/Downloads/titan-0.5.4-hadoop1)

Start rexster which connects to [1] with bin/titan_bdb_es.sh

Otherwise, I have a rexster script conf/titan-berkeleydb.groovy that connects to the DB
OTHER NOTES: gremlin will connect, rexster opens a port on 8182 that has a browsable UI

To delete all the data:
 - rm -rf /tmp/berkeley-insight
 - curl -XDELETE localhost:9200/titan
(you can clear mongodb-inserted data by dropping the collection used to insert it) 

If it gets locked:
 - rm -rf /tmp/berkeley-insight/*.lck

4] Add objects to MongoDB...

4.1] Add objects to MongoDB manually

4.2] Add objects to MongoDB via Logstash