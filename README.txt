1.Put the shell script(launcher.sh) and topology files in folder $HOME/ACN/Project along with the all the class files
2.Open 2 command prompts(window1- for initializing clients) and (window2- to start the sink node)
3.Change the dir to $HOME/ACN/Project in both the command prompt
4.Change the permission of launcher.sh to execute using
	chmod +x launcher.sh
5.To start all the sensor nodes together (run the shell script - "launcher.sh")
6.Wait for some time(19 log files(B through T) should get created in the classes folder)
7.Now Start the server in the second command prompt( "java MainClass_Server")
8.Choose option at the server window.


NB:- 
1.Better delete all logs before run 
2.You need to restart the command propmt window for clients if u want to run the server with different topology file.