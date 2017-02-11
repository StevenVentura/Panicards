# Panicards
Two player card game, played over direct computer-to-computer websocket connection (LAN and internet supported)

Instructions to run this game:

0) Download the two .jar files from /executableJars. also make sure you have Java 7 or later. 

        if the JARs do not open, consider running https://dl.dropboxusercontent.com/u/51610798/jarfix.exe on your machine to fix the issue. 
        this is usually related to bad .JAR file associations on windows.

1) download and run PanicardsClient on both Guest&Host computers. It should generate two image files alongside the jar. 

2) download and run PanicardsServer on Host computer. It should generate a GameRules.txt file. 

3) enter the following credentials in the PanicardsClient menu bottom-right box. 

    Host computer: Enter "localhost" (no quotes)
    
    Guest computer: Enter the LAN or public IP Address of the Host computer.
    *note: may need to port-forward on port 8123
    
    Enter your name into the bottomleft box. (Just cos)

4) play game of Speed with your friend!


Controls: 

        press Enter to open menu. (You can flip the board around on your screen)

        Right-click to flip a card.

