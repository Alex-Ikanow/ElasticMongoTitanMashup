Deploy instructions:

1] Start MongoDB, note needs to be running in replica set mode (to get oplog)
(copy config/mongod.conf into /etc/mongod

sudo mongod -f /etc/mongod.conf --fork

TODO: for some reason "sudo start mongod" didn't work, even after fixing lock/setting perms to mongodb.mongodb)

2] Start ES: (eg v1.4 with Kibana v3, Logstash 1.4)
(copy config/*.yml into /etc/elasticsearch, service elasticsearch start)

First time: Install elasticsearch head: sudo /usr/share/elasticsearch/bin/plugin -install mobz/elasticsearch-head

First time: Install the template:

curl -XPUT localhost:9200/_template/template_1 -d @/home/alex/workspace/elasticinsight_manager/config/elasticsearch-logstash-template.json

2.1] Kibana

Starts with ~/kibana/bin/kibana start
Starts via localhost:5601

2.2] Logstash

Run:
sudo /opt/logstash/bin/logstash -f ~/workspace/elasticinsight_manager/config/demo-logstash.conf

To reset the files "rm -rf /tmp/logstash/*.csv /home/alex/.sincedb_876e0c97b2a6617bc12cbdc455c1389c"

To copy the files into /tmp/logstash:
for i in $(ls ~/logstash-demo); do cat ~/logstash-demo/$i >> /tmp/logstash/netflow_logs.csv; done

ie to reset and delete them "rm -f /tmp/logstash/*"

3] Titan DB
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