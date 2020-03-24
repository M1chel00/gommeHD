# Aufruf des Programms über die Konsole
---

## Spielen auf lokalem Rechner  
java -jar pppg.jar -red {human,random,simple,remote} -blue {human,random,simple,remote} -size {3,5,7,9,11}  
Im Falle -red remote oder -blue remote, müssen -nameblue/-namered {String} und -hostblue/-hostred {String} ergänzt werden

## Spielen als Remote
java -jar pppg.jar --network -netname {String} -netplayer {human,random,simple} -nethost {host:port}

## optionale Argumente  
-delay {>=0} --debug --win (deaktiviert alle externen Fenster außer der Graphik)

## RMIRegistry starten
rmiregistry -J-Djava.rmi.server.codebase=file:<Pfad zu benötigten Klassen>