LinkLion.org Portal
===================

Linking LOD Portal Repository

- Portal saves mappings (sets of links) which have been generated in a Link Discovery step from LOD sources
- Upload of RDF formatted N-Triple data is supported, reasonable link types are for example 
    - http://www.w3.org/2002/07/owl#sameAs
    - http://open.vocab.org/terms/near
    - http://geovocab.org/spatial#P
- Uploaded data should be enriched by meta data like
    - framework which has been used for the Link Discovery process
    - algorithm which has been used
    - information about the used sources
- RDF data and additional meta data is converted using existing ontologies and
  our own LinkLion.org ontology which is descibed on the project website

installation
------------

- installation needs an configured Virutoso triple store and a MariaDB/MySQL database
- mvn clean install
- deploy war file on your tomcat server
- [local tomcat url/port]/LinkingLOD-0.0.1-SNAPSHOT/FileUpload.html
