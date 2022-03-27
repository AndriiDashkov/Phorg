Please read the file completely before using the application.
*********************************************************
Lisence
********************************************************
This application is distributed under Apache free lisence,
and doesn't require any payments.
If you use this application, you agree that
results, problems and any other issues concerning using
can't be application's authors responsibility. 

Java is a registered trademark of Oracle. All rights 
are reserved. 

********************************************************
WINDOWS issues
********************************************************
Requirements
____________

1. Windows 10.
2. JRE version 1.8 and later.

Installation
____________

1.Just put the folder with phorg.jar to any location (folders 'Help' and 'Icons' are required).
2.Add the JRE library path  ('bin' folder)  to the PATH 
variable in Windows enviroment settings. If the Java 
Runtime Environment has been already installed on your 
computer, and PATH has indicated the path link, then skip 
this step. For example, if JRE is in C:\sys\Java\jre7 folder, then 
path 'C:\sys\Java\jre7\bin\' (with 'bin' folder in the end) 
must be in your PATH variable.
For more detailed information about changing PATH value see
the following link:

https://www.java.com/en/download/help/path.xml

3.Start the application by the double click on phorg.jar file.

 or

4.When you don't want to change the PATH, or 
you work with a huge number of images and need additional 
memory from JVM, then use a special script file - phorg.bat. 
Change the following line in this script 

java -Xms1G -Xmx1G -jar phorg.jar

with 

C:\sys\Java\jre7\bin\java -Xms1G -Xmx1G -jar phorg.jar

where 'C:\sys\Java\jre7\bin\' must be YOUR path to the 
jre library.  

5. The application creates a special folder ('Phorg')  in 
the current user 'Documents' folder after the first start. In 
case of removing or deleting this folder the application
starts to work from the very beginning, without any initial
information about it.

********************************************************
LINUX issues
********************************************************
Requirements
____________

1.JRE version 1.8 and later.

Installation
____________

1.Just put the folder with phorg_linux.jar to any location and 
take care of priviliges for all files in the phorg folder.

(maybe you will need to use commands like these:

chmod +x phorg_linux.sh
or
sudo chmod +x phorg_linux.sh
)


2.Add the JRE library path  ('bin' folder)  to the PATH variable in
Linux enviroment settings. If the Java Runtime Environment has
been already installed on your computer, skip this step.
For example, if JRE is in /usr/jre7 folder, then 
path '/usr/jre7/bin' (with 'bin' folder at the end) 
must be in your PATH variable.
For more detailed information about changing PATH value see
the following link:

https://www.java.com/en/download/help/path.xml

3.Start the application by the double click on phorg_linux.sh script file.

4.When you don't want to change the PATH value, 
change the following line in phorg_linux.sh script 

java -Xms1G -Xmx1G -jar phorg_linux.jar

with 

/usr/jre7/bin/java -Xms1G -Xmx1G -jar phorg_linux.jar

where '/usr/jre7/bin/' must be YOUR path to the 
jre library.  

5. The application creates a special folder ('Phorg')  in 
/home/user/Documents  after first start. In  
case of removing or deleting this folder the application
starts to work from the very beginning, without any initial
information about it.

******************************************************
Localization and Internationalization
******************************************************

Supported languages : English, Russian. 

You can switch between languages directly in the 
application using the  Settings menu.


******************************************************
Troubleshooting
******************************************************
1.If the phorg.jar can't be started, then the most probable
reason is the absence of JVM (Java Virtual Mashine) on your
computer. You should install it from Java site.

2.If you encounter with the situation when you use the script
and JVM can't allocate the memory according to -Xms1G key, 
then change the command

java -Xms1G -Xmx1G -jar phorg.jar
(java -Xms1G -Xmx1G -jar phorg_linux.jar)

to the following 

java -jar phorg.jar
(java -jar phorg_linux.jar)

3.On the other hand, you can increase the memory key to
the value -Xms2G if you are going to work with huge images.


