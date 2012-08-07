# Asynchronous Demo with Play! Framework 2 
### A demonstration / presentation on the power of asynchronous programming with Play! Framework 2

-----

##### Setup

1. Install Play! Framework 2 - http://www.playframework.org/documentation/2.0.2/Installing
2. Install PostgreSQL - http://postgresapp.com/ ( on OSX ) or  http://www.postgresql.org/download/
3. Navigate to your development folder and checkout this projecct - git clone https://github.com/mbseid/PlayIntro.git
3. Change line 26 of application.conf ( https://github.com/mbseid/PlayIntro/blob/master/conf/application.conf ) to have the username/password of your postgresql account
	* Example: username: test, password: testpassword, database: playintro would be = "postgres://test:testpasswrod@localhost/playintro"


That’s it!  Wasn't that easy.

-----

##### Run
1. Navigate to the PlayIntro project you just checked out in terminal / cmd prompt
2. Enter "play clean run"
3. In your browser, go to "localhost:9000/"

Enjoy!
