LSql is a command-line tool written in Java that allows sql-like queries to run against a Lucene database.  It can be run in interactive mode, or can automatically parse a list of commands from a file.  Example LSql queries are:

SELECT field1,field2 WHERE +field1:value <br>
This command will display two fields in a grid on the command screen.<br>
<br><br>
UPDATE field1=newvalue WHERE +field2:value <br>
This command will change the value of field1 to 'newvalue'.<br>
<br>
The commands SELECT, UPDATE, INSERT, DELETE, ADD, and INSERT are supported.  This tool is useful for running automated queries against a Lucene database without writing any code, or for making quick changes from the command line.  The tool also supports commands such as OPTIMIZE and INFO.<br>
<br><br>
With this tool, you can treat a Lucene index the same as you would a database.<br>
<br><br>
<b>We welcome any contributions to the source code!</b>