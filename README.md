LinkLion.org Portal
===================

Linking LOD Portal Repository

- Portal saves mappings (sets of links) which have been generated in a Link Discovery step from LOD sources
- Upload of RDF-formatted N-Triple data is supported. Reasonable link types are, for example: 
    - http://www.w3.org/2002/07/owl#sameAs
    - http://open.vocab.org/terms/near
    - http://geovocab.org/spatial#P
- Uploaded data should be enriched by metadata, i.e.:
    - framework used into the Link Discovery process
    - algorithm used
    - information about used sources
- RDF data and additional metadata are converted using existing ontologies and
  our provided LinkLion.org ontology which is described at http://www.linklion.org:8080/portal/vocabulary.html

Installation
------------

- Installation needs a configured Virtuoso triple store and a MariaDB/MySQL database
- `mvn clean install`
- Deploy war file on your Tomcat server
- `[local Tomcat url:port]/portal`
