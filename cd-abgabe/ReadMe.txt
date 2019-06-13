RapidMiner Erweiterung 'NLP Parser'
Klaus Freiberger, Uni Bayreuth 2019

Enthalten in diesem Ordner: 

- rapidminer-process-with-all-parsers.rmp
- text-8.1.0.jar
- rapidminer-extension-template-master
- parser-modelle
- ted-talks-treebank


I. Aufsetzen
	
	1. RapidMiner Studio installieren (zu finden unter https://rapidminer.com/products/studio/)

	2. Text Processing Erweiterung über im Studio über den Marketplace installieren

	3. Die Datei 'text-8.1.0.jar' in den 'lib'-Ordner von RapidMiner legen. 
	   Vermutlich zu finden unter 'C:\Program Files\RapidMiner\RapidMiner Studio\lib'
	   Das führt dazu, dass beim Start von RapidMiner Studio eine Fehlermeldung bezüglich dieser Erweiterung erscheint.
	   Diese Meldung kann mittels 'Ignore' ohne weitere Auswirkungen ignoriert werden.

	4. Im Ordner 'rapidminer-extension-template-master' die Erweiterung 
	   mittels Kommandozeilenbefehl 'gradlew installExtension' installieren 

	5. RapidMiner Studio neustarten
	

II. Verwendung
	
	1. In RapidMiner Studio mittels File -> Import Process die Datei 'rapidminer-process-with-all-parsers.rmp' laden
	   Es wird ein Prozess geladen, der alle 3 Parser so eingebunden hat, dass sie miteinander verglichen werden

	2. Für die Parser-Modelle befinden sich im Ordner 'parser-modelle' für jeden Parser ein Modell
	   Diese wurden entsprechend ihren Parsern umbenannt und müssen in den 'Read Grammar' Operatoren ausgewählt werden

	3. Der Eingabetext für die Parser befindet sich im Ordner 'ted-talks-treebank\en-tok'
	   Hier muss eine Datei in den 'Read Source' Operatoren ausgewählt werden

	4. Der Goldstandard befindet sich im Ordner 'ted-talks-treebank\en-mrg'
	   Hier muss eine Datei in den 'Read GoldStandard' Operatoren ausgewählt werden
	   Wichtig ist hier, dass der Name der Datei identisch ist zum Namen des Eingabetextes

	5. Der Prozess kann gestartet werden


III. Struktur im Code

	Die Operatoren wurden gemäß der Anleitung von RapidMiner entwickelt 
	(https://docs.rapidminer.com/latest/developers/creating-your-own-extension/)
	
	Unter src\main\java im Paket com.rapidminer.extension.operator finden sich alle geschriebenen Klassen
	
	Im Ordner lib wurde die Text-Processing-Extension abgelegt

	In build.gradle werden alle Imports angegeben
