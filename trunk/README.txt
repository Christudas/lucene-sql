LSql 0.9
By: Jonathan Esposito
March 16, 2009

Lucene is a java utility that allows the user to execute SQL-like commands 
against the repository.  Lsql currently supports SELECT, UPDATE, ADD, and 
DEL.

COMMAND OPTIONS
   -v Will verobsely print messages
   -i The location of the Lucene index
   -c The Lucene query that will retrieve the documents to be updated, added, etc.

HOW TO USE LSQL

1. To SELECT a field
   java -cp lsql.jar LSql -v -i c:\INDEXLOCATION -c "SELECT field1='value' WHERE +field1:value"
   java -cp lsql.jar LSql -v -i c:\INDEXLOCATION -c "SELECT * WHERE +field1:value"

2. To UPDATE a field
   java -cp lsql.jar LSql -v -i c:\INDEXLOCATION -c "UPDATE field1='newvalue', field2='newvalue' WHERE +field1:oldvalue" 

3. To ADD a field
   java -cp lsql.jar LSql -v -i c:\INDEXLOCATION -c " ADD newfield1='newvalue' WHERE +field1:value" 

4. To ADD a field
   java -cp lsql.jar LSql -v -i c:\INDEXLOCATION -c " ADD newfield1='newvalue' WHERE +field1:value" 