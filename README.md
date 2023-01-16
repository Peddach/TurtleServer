# TurtleServer

### Was ist TurtleServer
Turtle Server ist der Kern des Petropia.de Netzwerkes und ist vereint mehrfach wiederverwendete 
Methoden und Klassen und verwaltet Spielerdaten.

### World Management
#### Erklärung:
Welten werden in einer Mysql Weltendatenbank als .zip Datei gespeichert. Jede Welt verfügt über 
eine einzigartige ID, welche meist dem Namen der Welt auf dem Bauserver entspricht. Da das 
Laden einer Welt nicht innerhalb eines Ticks geschehen kann, da es sonst zu 
Performance-Einbrüchen kommen würde, werden die Welten Asynchron geladen und ein 
CompletableFuture wird zurückgegeben. Wie dies Funktioniert kann hier nachgelesen werden: 
https://www.baeldung.com/java-completablefuture. Aktuell sind folgende Funktionen implementiert:
- Welten in Datenbank speichern
- Lokale Welt (Welt auf Server) kopieren
- Welt aus Datenbank laden
- Lokale Welt entladen und löschen
- Welt aus Datenbank löschen
- Welten asynchron vorgenerieren
- Welten linken (z.B. Netherportal in Welt x für zu Welt y und umgekehrt)

#### Namenskonvention
Jede Welt hat eine eindeutige ID. Diese folgt folgenden Schema für Spielmodies:
**PREFIX_NAME** <br> Der Prfix ist für Spielmodies der Name des Modus und für die restlichen, 
wie z.B. der Hub nur "Hub". Wenn der Name des Spielmodus kürzer als 6 Buchstagen ist und 
zusammengeschrieben, wird der volle Name als Prefix genutzt. Wenn der name länger ist und/oder 
Leerzeichen enthält, wird er abgekürzt. Im Folgenden sind alle aktuellen Spielmodies und deren Prefixe:
- Bingo ➡️ Bingo_
- ChickenLeague ➡️ CL_
- SurviveTheNight ➡️ STN_
#### Beispiel:

```java
public class Example {
    public void loadWorldAndTeleport(Player player) {
        //Welt mit ID CL_Spawn wird geladen und heißt auf dem lokalen Server Spawn123
        WorldManager.loadWorld("CL_Spawn", "Spawn123").thenAccept(world -> {
            player.teleport(world.getSpawnLocation());
            YourPlugin.getInstance().getMessageUtil().sendMessage(player, Component.text("Du wurdest teleportiert", NamedTextColor.GREEN));
        });
        YourPlugin.getInstance().getMessageUtil().sendMessage(player, Component.text("Du wirst teleportiert!", NamedTextColor.GREEN));
    }
}
```

### Chateingabe

#### Erklärung: 
Mithilfe des ``ChatInputBuilder`` lässt sich ein ``ChatInput`` erstellen. Ein ChatInput ist eine 
Aufforderung eine Zahl, einen String, usw. in den Chat einzugeben. Währenddessen kann der 
Spieler keine Commands ausführen oder Chatnachrichten in den globalen Chat senden. Um kreterien 
zu setzten, damit die Eingabe gültig ist, werden die Methoden ``greaterThanZero`` und
``mustBePositive`` verwendet. GreaterThanZero ist besonders bei doubles vorteilhaft. Jede 
Chateingabe 
definiert ebenfalls min. 1 Callback, welcher registriert werden kann mit 
``onInputWithInt/String/Double`` und einem dem Datentyp entsprechden 
Consumer. Zusätzlich kann ein Callback registriert werden, wenn der spieler die Eingabe abbricht 
mit ``onCancel`` und einer Runnable als Argument.

#### Beispiel:

````java
import de.petropia.turtleServer.api.chatInput.ChatInputBuilder;
import net.kyori.adventure.text.Component;

public class ExampleInput {
    public chatInputExample(Player player) {
        Component message = Component.text("Bitte gib den Preis an");
        new ChatInputBuilder(player, message)
                .greaterThanZero(true)
                .onInputWithDouble(price -> player.sendMessage("Der Preis beträgt " + price))
                .onCancel(() -> player.kill())
                .build();   //Nicht vergessen!!!
    }
}
````

### Petropia Plugin