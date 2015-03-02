# ElasticMongoTitanMashup

# ElasicMongoTitanMashup

This is not production code!

It started off life as an investigation into the structure of a DB that had Titan/ES/MongoDB interfaces, but was backed by Elasticsearch (for Indexes), MongoDB (for persistent data), and TitanDB backed by MongoDB and Elasticsearch for edges between elements in the datastore. With automatic sychronization between the different ways of storing the data.

When it became clear that I didn't have time to finish this very complicated effort as a side project (and IKANOW's functionality in this regard is sufficient for our near term goals), this project was mothballed.

Most recently (2015) I quickly adapted what code I had to provide a very simple-and-dirty demonstration of sychronizing Elasticsearch and Titan with data inserted via the MongoDB interface.
