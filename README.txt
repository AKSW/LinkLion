initial version
---------------

- file gets parsed and converted to Jena RDF model
- TripleStoreCommunication is not working, can be replaced
- Mapping is not used, can be replaced
- debug log is written to local tomcat catalina.out

installation
------------

- mvn clean install
- deploy *.war on local tomcat
- [local tomcat url/port]/LinkingLOD-0.0.1-SNAPSHOT/FileUpload.html
