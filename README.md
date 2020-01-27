# Web-based-Interface-for-LingoTurk

This repo contains code that provides a Graphical User Interface for the [Ligoturk system] https://github.com/FlorianPusse/Lingoturk .
The lingoturk system was designed to develop crowdsourcing interviews but did not have a GUI. In the Software Engineering course of WS19/20 at Saarland University, we developed the GUI for this system. Our GUI provides an interface similar to a presentation software and allows the experiment designers to easily design their experiments as per their requirements.

This repo contains only the code that we developed in the folder "LingoTurk Public" and does not contain the original files from the Lingoturk System. But in order to make our code functional, you can refer to the original Lingoturk System and add our files from the folder "LingoTurk Public". 

You may need to install [Jsoup] https://jsoup.org/ by including it's corresponding .jar file in the lib folder and rebuilding the project after adding files from this repo.

Requirements
> Java Development Kit 8: [Download JDK] http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

> An empty PostgreSQL database. [Download PostgreSQL] http://www.postgresql.org/download/

> A local copy of the lingoturk directory. Note: It is recommended to clone this repository instead of downloading the ZIP. This will enable you to pull future updates more easily.

Configuring LingoTurk
Before you can run LingoTurk for the first time, you will need to configure it. This section explains how to do that. Installation might take a while, since it will have to download quite a lot.

1. First, you will need to create an Application secret. (The Application secret will be used for cryptographic functions. Therefore, it should never be shared with others!). You can create an Application secret by following these steps:

* Open console
* navigate to the lingoturk directory inside your Lingoturk repository
* enter: ./sbt playGenerateSecret.
* Open the lingoturk/conf/application.conf file in any text editor.
* replace APPLICATION_SECRET in the line play.http.secret.key="APPLICATION_SECRET" by the freshly created ApplicationSecret.

2. As part of the requirements (see first section of this wiki), you downloaded and installed PostgreSQL. You will now create a database. This is done by following these steps:

* Search for the application pgAdmin4 on your computer and open it.
* When prompted for a password, log in with the password that you entered during the PostgreSQL installation.
* In the top left corner, it will say ‘Servers’. Expand this by clicking on the +-sign preceding it. Expand the following node as well.
* You will see a heading ‘Databases’. Right-click on this and create a new database. A new window will open. Choose a name for your database and click on ‘save’.

3. In the previous step, you created a database. In this step, we will configure the database connection. You will need the name of your database for this.

* Open the lingoturk/conf/application.conf file in any text editor.
* Look for the line default.url="postgres://USERNAME:PASSWORD@URL/DATABASE_NAME?characterEncoding=utf8". You are going to replace a few of these words:
* Replace the USERNAME in this line with ‘postgres’ (without the apostrophes).
* Replace the PASSWORD with the password that you entered during the PostgreSQL installation.
* Replace URL with the corresponding URL (i.e. the server your database is running on. In most cases, this will be 'localhost').
* Replace the DATABASE_NAME with the name of the database that you created in the previous step. (note: leave the question-mark following DATABASE_NAME) - Save the file.

Running LingoTurk

Once you have configured LingoTurk, you can start running it. Running LingoTurk is fairly easy. To start the server, follow these steps:

1. Start a terminal/command line
2. Navigate to the local copy of the LingoTurk/lingoturk directory
3. There are two modes the server can be started in:
	* Developing mode: The server will automatically load all changes you make to the Lingoturk files and will also display errors directly in the browser. This makes it easier to find potential problems. This mode should only be used for testing purposes.
	* Production mode: The server won't automatically load the changes you make until it will be restarted. Also, it won't display detailed error messages in the browser. This should always be used while you're running an experiment, as it prevents participants from seeing things that should only be seen by yourself.
4. To start start the server in developing mode enter ./sbt run and to start the server in production mode, enter ./sbt start. You can specify parameters such as ports as additional arguments, e.g. ./sbt "run -Dhttps.port=443 -Dhttp.port=80". If no port is specified, Play will bind the application to port 9000.
5. When your terminal/command line says something similar to "(Server started, use Ctrl+D to stop and go back to the console…)", you can open LingoTurk in your browser:
	Navigate to `http://localhost:9000` in case you’re testing the application locally, without specifying a port on startup. Otherwise the format is `PROTOCOL://SERVER_IP:PORT `
This should open the LingoTurk interface. Go to the next section for the initial LingoTurk setup.

Initial LingoTurk setup
1. Log in to Lingoturk. 
2. Click on the Settings button.
3. Enter your Access key and your Secret Key if you plan to run experiments on MTurk (not necessary for Prolific).
4. Change the URL to the URL that can be used to access the LingoTurk server.
5. Change the LingoTurk password.
6. Click Apply changes.

