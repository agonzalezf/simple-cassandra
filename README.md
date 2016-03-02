# simple-cassandra
The objective of this project is to have a way to access Cassandra in the same way as we access SQL with the aproach of Spring JDBC.
Internally, we use Datastax Cassandra java driver.

We use mapper in order to have specific implementation of the result object for the mapping between java and cassandra column family type.
