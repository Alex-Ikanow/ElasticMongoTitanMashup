c = new BaseConfiguration();
c.setProperty("storage.directory","/tmp/berkeley-insight")
c.setProperty("storage.backend","berkeleyje")
c.setProperty("index.search.backend", "elasticsearch")
c.setProperty("index.search.hostname", "127.0.0.1")
g = TitanFactory.open(c)
// after sh bin/rexster-console.sh, 
// ?e conf/titan-berkeleydb.groovy
// GraphOfTheGodsFactory.load(g)
