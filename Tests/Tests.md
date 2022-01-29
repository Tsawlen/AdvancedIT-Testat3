# Testat Aufgabe 3
Die Aufgabenstellung war es, einen Fileserver zu entwickeln, mit welchem man in Datein schreiben kann. Hierfür sollte ein Worker-Pool verwendet und auf die Schreiberpriorität geachtet werden.

## Start des Clients
Um dieses Programm zu testen ist es möglich aus zwei Modi zu wählen. Diese beiden Modi sind Modus 1, der Manuelle Modus, bei welchem man alle Kommandos selber an den Server schicken muss, der andere Modus ist Modus 2, der automatische Modus, bei diesem werden für alle Testfälle automatisch alle Kommandos an den Server geschickt werden.  
Diese Auswahl wird zum Start des Clients gefordert, in Form dieser Abfrage:
```java
Verfügbare Modi: 1 - Manuell; 2 - Automatisch
Bitte wählen sie einen Modus > 
```
Hier muss nur die entsprechende Zahl eingegeben werden, um den entsprechenden Modus zu starten.

### Auswertung des automatischen Clients
Um den automatischen Client auszuführen, verwenden sie bitte die Datein aus dem Ordner "Testdatein", da die automatischen Tests auf diese Datein ausgelegt sind.

## Tests
### Testfall 1: Paralleles Lesen aus der gleichen Datein
In diesem Testfall soll geprüft werden, ob der Server nebenläufig aus der gleichen Datei lesen kann. Hierfür wird folgendes vom Client geschickt und empfangen:
```java
====================================================
Starte parallelen Lese-Test
Sende: READ secondTest.txt,2
Sende: READ secondTest.txt,3
Sende: READ secondTest.txt,4
Sende: READ secondTest.txt,5
Empfangen: Neue Zeile in Zeile 5
Empfangen: Test
Empfangen: Neue Zeile 1
Empfangen: ersetzte Zeile
====================================================
```
Der Server reagiert hierauf wie folgt:
```java
Worker (Lesend) Thread[Thread-1,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-0,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-5,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-4,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-5,5,main] hört auf zu arbeiten!
Worker (Lesend) Thread[Thread-4,5,main] hört auf zu arbeiten!
Worker (Lesend) Thread[Thread-1,5,main] hört auf zu arbeiten!
Worker (Lesend) Thread[Thread-0,5,main] hört auf zu arbeiten!
``` 
#### Auswertung des 1. Testfalls:
Dieser Testfall zeigt sehr gut, dass paralleles Lesen auf der gleichen Datei kein Problem ist. Dies sollte auch kein Problem darstellen, so ist hier kein Fehler zu sehen.
### Testfall 2: Gleichzeitige Schreibeanfragen an das gleiche Dokument
Dieser Testfall soll zeigen, wie der Server auf gleichzeitige Schreibeanfragen reagiert. Dies sollte sequentiell abgearbeitet werden. Hierauf schickt und empfängt der Client folgendes:
```java
====================================================
Starte Test zum schreiben ins gleiche Dokument
Sende: WRITE secondTest.txt,3,Neue Zeile 1
Sende: WRITE secondTest.txt,5,Neue Zeile in Zeile 5
Empfangen: Überschrieben
Empfangen: Überschrieben
====================================================
```
Der Server reagiert auf diese Anfrage wie folgt: 
```java
Worker (Schreibend) Thread[Thread-1,5,main] fängt an zu arbeiten!
Worker (Schreibend) Thread[Thread-1,5,main] hört auf zu arbeiten!
Worker (Schreibend) Thread[Thread-2,5,main] fängt an zu arbeiten!
Worker (Schreibend) Thread[Thread-2,5,main] hört auf zu arbeiten!
```
#### Auswertung des 2. Testfalls
Dies zeigt, dass der Server nicht nebenläufig auf die Datei schreibend zugreift. Dies ist exakt so, wie es gedacht ist. Dies wurde darüber realisiert, dass für jeden Zugriff auf eine Datei erst eine Monitor-Entry-Methode durchlaufen werden muss. Dies ist so realisiert, dass Schreiber Priorität haben und Schreiber nicht nebenläufig arbeiten dürfen, im Gegensatz zu Lesern.
### Testfall 3: Nebenläufiges Schreiben auf unterschiedliche Dokumente
In diesem Testfall soll gezeigt werden, dass zwar auf das gleiche Dokument nicht nebenläufig geschrieben werden darf, jedoch auf unterschiedliche Dokumente nebenläufig geschrieben werden darf. Der Client sendet und empfängt folgendes:
```java
====================================================
Starte Test zum schreiben in unterschiedliche Dokumente
Sende: WRITE secondTest.txt,3,Neue Zeile 1
Sende: WRITE thirdTest.txt,5,Neue Zeile in Zeile 5
Empfangen: Überschrieben
Empfangen: Überschrieben
====================================================
``` 
Der Server reagiert wie folgt:
```java
Worker (Schreibend) Thread[Thread-3,5,main] fängt an zu arbeiten!
Worker (Schreibend) Thread[Thread-2,5,main] fängt an zu arbeiten!
Worker (Schreibend) Thread[Thread-2,5,main] hört auf zu arbeiten!
Worker (Schreibend) Thread[Thread-3,5,main] hört auf zu arbeiten!
```
#### Auswertung des 3. Testfalls
Dieses Verhalten beweist, dass nebenläufiges Schreiben auf unterschiedliche Dokumente möglich ist. Dies ist damit zu Begründen, dass für jedes Dokument geprüft wird, ob dieses ein Monitor hinterlegt hat und wenn nicht, wird einer erstellt. Dies ist so realisiert, dass eine Monitor-Methode prüft ob ein Monitor existiert und gibt diesen zurück oder erstellt einen neuen und schickt diesen zurück. Die Prüfung ist ebenfalls ein Monitor, damit nicht zur gleichen Zeit ein Monitor erstellt wird, damit die Datenstruktur nicht inkonsistent werden.
### Testfall 4: Schreiber-Priorität
Dieser Testfall soll zeigen, dass Schreiber-Aufträge abgearbeitet werden, bevor lesende Zugriffe abgearbeitet werden, egal in welcher Reihenfolge diese ankommen. Hierfür sender der Client folgendes:
```java
====================================================
Starte Schreiber-Prioritäts-Test
Sende: READ secondTest.txt,2
Sende: WRITE secondTest.txt,3,Neue Zeile 1
Sende: READ secondTest.txt,3
Sende: READ secondTest.txt,4
Sende: WRITE secondTest.txt,5,Neue Zeile in Zeile 5
Sende: READ secondTest.txt,5
Empfangen: Überschrieben
Empfangen: Überschrieben
Empfangen: Test
Empfangen: Neue Zeile in Zeile 5
Empfangen: ersetzte Zeile
Empfangen: Neue Zeile 1
====================================================
```
Der Server reagiert hierauf wie folgt: 
```java
Worker (Schreibend) Thread[Thread-0,5,main] fängt an zu arbeiten!
Worker (Schreibend) Thread[Thread-0,5,main] hört auf zu arbeiten!
Worker (Schreibend) Thread[Thread-1,5,main] fängt an zu arbeiten!
Worker (Schreibend) Thread[Thread-1,5,main] hört auf zu arbeiten!
Worker (Lesend) Thread[Thread-2,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-4,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-5,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-3,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-2,5,main] hört auf zu arbeiten!
Worker (Lesend) Thread[Thread-4,5,main] hört auf zu arbeiten!
Worker (Lesend) Thread[Thread-3,5,main] hört auf zu arbeiten!
Worker (Lesend) Thread[Thread-5,5,main] hört auf zu arbeiten!
```
#### Auswertung des 4. Testfalls
Die Abarbeitungsreihenfolge zeigt, dass, obwohl die Schreiber-Aufträge nicht als erstes kommen, diese doch als erstes Abgearbeitet werden. Daraus ist zu schließen, dass die Schreiber-Priorität gewährleistet ist!
### Testfall 5: Nebenläufiges Lesen und Schreiben aus unterschiedlichen Datein
In diesem Testfall soll gezeigt werden, dass während nebenläufig aus einer Datei gelesen wird, ein anderer Worker in eine andere Datei schreiben kann. Hierfür sendet der Client folgendes:
```java
====================================================
Starte Test zum parallelen Lesen aus einer Datei und schreiben in eine andere!
Sende: READ secondTest.txt,2
Sende: WRITE thirdTest.txt,3,Paralleler Zugriff
Sende: READ secondTest.txt,5
Empfangen: Neue Zeile in Zeile 5
Empfangen: Überschrieben
Empfangen: Test
====================================================
```
Der Server reagiert wie folgt:
```java
Worker (Lesend) Thread[Thread-0,5,main] fängt an zu arbeiten!
Worker (Schreibend) Thread[Thread-2,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-1,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-1,5,main] hört auf zu arbeiten!
Worker (Schreibend) Thread[Thread-2,5,main] hört auf zu arbeiten!
Worker (Lesend) Thread[Thread-0,5,main] hört auf zu arbeiten!
```
#### Auswertung des 5. Testfalls
Dies zeigt erneut, dass jede Datei ihren eigenen Monitor hat und demnach nebenläufige Zugriffe auf unterschiedliche Datein zumeist möglich sind.
### Testfall 6: Mehr Aufträge als verfügbare Worker
In diesem Test soll gezeigt werden, wie der Server auf mehr eingehende Aufträge reagiert als dieser Worker hat. Hierfür sendet der Client folgendes:
```java
====================================================
Starte Test mit mehr Anfragen als aktive Worker
Sende: READ secondTest.txt,2
Sende: READ secondTest.txt,3
Sende: READ secondTest.txt,4
Sende: READ secondTest.txt,5
Sende: READ secondTest.txt,6
Sende: READ secondTest.txt,7
Sende: READ secondTest.txt,8
Sende: READ secondTest.txt,9
Sende: READ secondTest.txt,10
Sende: READ secondTest.txt,11
Sende: READ secondTest.txt,12
Sende: READ secondTest.txt,13
Empfangen: You were expecting the sixth row, but it was me! DIO!
Empfangen: ersetzte Zeile
Empfangen: Neue Zeile 1
Empfangen: Eingefügte Zeile
Empfangen: Test
Empfangen: Neue Zeile in Zeile 5
Empfangen: null
Empfangen: neue Zeile Test
Empfangen: Zeile vor der neuen Zeile
Empfangen: neue Zeile
Empfangen: 
Empfangen: null
====================================================
```
Der Server regiert auf diese Anfrage wie folgt:
```java
Worker (Lesend) Thread[Thread-1,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-2,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-4,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-5,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-0,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-3,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-4,5,main] hört auf zu arbeiten!
Worker (Lesend) Thread[Thread-0,5,main] hört auf zu arbeiten!
Worker (Lesend) Thread[Thread-1,5,main] hört auf zu arbeiten!
Worker (Lesend) Thread[Thread-3,5,main] hört auf zu arbeiten!
Worker (Lesend) Thread[Thread-5,5,main] hört auf zu arbeiten!
Worker (Lesend) Thread[Thread-2,5,main] hört auf zu arbeiten!
Worker (Lesend) Thread[Thread-3,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-4,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-5,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-2,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-1,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-0,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-5,5,main] hört auf zu arbeiten!
Worker (Lesend) Thread[Thread-3,5,main] hört auf zu arbeiten!
Worker (Lesend) Thread[Thread-2,5,main] hört auf zu arbeiten!
Worker (Lesend) Thread[Thread-0,5,main] hört auf zu arbeiten!
Worker (Lesend) Thread[Thread-4,5,main] hört auf zu arbeiten!
Worker (Lesend) Thread[Thread-1,5,main] hört auf zu arbeiten!
```
#### Auswertung des 6. Testfalls
Dieser Testfall sollte selber ausgeführt werden, da man nur dann die Zeitverzögerte Reaktion des Servers sieht und wie die Worker auf die Warteschlange zugreifen.
### Testfall 7: Nebenläufiges Lesen aus unterschiedlichen Datein
Dieser Testfall soll das Verhalten des Servers darauf zeigen, wenn dieser aus unterschiedlichen Datein nebenläufig lesen soll. Hierfür sendet der Client folgendes:
```java
====================================================
Starte Test zum lesen aus verschiedenen Datein
Sende: READ secondTest.txt,2
Sende: READ thirdTest.txt,3
Sende: READ secondTest.txt,5
Empfangen: Paralleler Zugriff
Empfangen: Test
Empfangen: Neue Zeile in Zeile 5
====================================================
```
Hier reagiert der Server wie folgt:
```java
Worker (Lesend) Thread[Thread-5,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-0,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-4,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-4,5,main] hört auf zu arbeiten!
Worker (Lesend) Thread[Thread-5,5,main] hört auf zu arbeiten!
Worker (Lesend) Thread[Thread-0,5,main] hört auf zu arbeiten!
```
#### Auswertung des 7. Testfalls
Dies zeigt wie Testfall 5, dass nebenläufige Lesezugriffe immer möglich sind!
### Testfall 8: Eine Zeile lesen, welche nicht existiert
Dieser Test soll zeigen, wie der Server reagiert, wenn dieser eine Zeile lesen soll, welche nicht in dem Dokument vorhanden ist. Hierfür sendet der Server folgendes:
```java
====================================================
Starte Test zum lesen einer nicht vorhandenen Zeile!
Sende: READ secondTest.txt,50
Empfangen: Line does not exist!
====================================================
```
Der Server reagiert wie folgt:
```java
Worker (Lesend) Thread[Thread-2,5,main] fängt an zu arbeiten!
Worker (Lesend) Thread[Thread-2,5,main] hört auf zu arbeiten!
```
#### auswertung des 8. Testfalls
Dies zeigt, dass der Server nicht abstürzt sondern einfach einen Fehler zurückgibt.
### Testfall 9: Verschiedene Falsche Kommandos
In diesem Testfall soll die Reaktion des Servers auf verschiedene Fehlerhafte Kommandos gezeigt werden. Hierfür sendet der Client folgende Kommandos und bekommt folgende Reaktionen:
```java
====================================================
Starte Test zum Error Handling!
Sende: READ secondTest.txt
Sende: RED secondTest.txt,2
Sende: WITE secondTest.txt,2,Hallo
Sende: WRITE secondTest,2,Hallo
Sende: WRITE secondTest.txt
Sende: WRITE secondTest.txt,2
Empfangen: STATUS 404: Command not found! Command was RED
Empfangen: STATUS 404: Command not found! Command was WITE
Empfangen: Invalid command, the Data is missing!
Empfangen: Invalid command, line Number missing!
Empfangen: Internal Server Error!
Empfangen: The file does not exists!
====================================================
```
#### Auswertung des 9. Testfalls
Dieser Test zeigt, dass der Server bei den meisten falschen Kommandos eine passende Fehlerbeschreibung schickt und bei keinem der Fehler abstürzt, sonder lediglich eine Fehlernachricht schickt!

## Auswertung
Mit all diesen Testfällen sollte bewiesen sein, dass dieser Server seine Aufgabe mit gegebenen Anforderungen erfüllen kann!