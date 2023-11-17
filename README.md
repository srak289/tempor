# Time tracking system

* The user should be able to add new entries
* select the current task
* stop the current task or "clock out"
* compute hours worked for the week
* rotate database
* backup database

* database should be either Sqlite or some custom binary file packed
  * [x] have to look at java's sqlite support


## Design

* initial implementation will be running the program to load a shell
  * the shell will provide a rudimentary CLI loop
* CLI loop
  * should allow open/close of different DB
  * should have default DB path
* select current project
* list projects
* list codes
* print current time on current code
* list current code / project
* compute hours for period
* compute hours for longer given period
* save database for indefinite period
* truncate database


### Resources

* https://www.w3schools.com/java/java_interface.asp
* https://www.sqlitetutorial.net/sqlite-java/
* https://www.tutorialspoint.com/sqlite/sqlite_java.htm
* https://ask.replit.com/t/help-with-java-sqlite-3-in-replit/11189
  * https://replit.com/@drybowser48/Spirit-Fight-Arena-Legends-WIP#UserDatabase.java
* https://www.javatpoint.com/java-sqlite
