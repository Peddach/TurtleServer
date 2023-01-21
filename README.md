## TurtleServer

### Table of Content:

- [Was ist TurtleServer](#was-ist-turtleserver)
- [World Management](#world-management)
- [Chateingabe](#chateingabe)
- [Petropia Plugin](#petropia-plugin)
- [Zeit](#zeit)
- [Spielerdaten](#spielerdaten)
- [CloudNet](#cloudnet)
- [Prefixe](#prefixe--wip-)

### Was ist TurtleServer
TurtleServer ist der Kern des Petropia.de Netzwerkes und ist vereint mehrfach wiederverwendete 
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

#### Namenskonvention:
Jede Welt hat eine eindeutige ID. Diese folgt folgenden Schema für Spielmodies:
**PREFIX_NAME** <br> Der Prefix ist für Spielmodies der Name des Modus und für die restlichen, 
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

```java
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
```

### Petropia Plugin
#### Erklärung:
Anstelle des ``JavaPlugin``s welches Bukkit bereitstellt wird eine Subcass namens 
``PetropiaPlugin`` welches TurtleServer bereitstellt und das ``JavaPlugin`` extendet genutzt. Diese 
Subclass stellt die Methode ``PetropiaPlugin.getMessageUtil`` zur verfügung. Hiermit werden 
Nachrichten in der standartisierten Formatierung an einen Spieler gesendet. Die meisten Methoden 
nutzten sogenannte Components, welcher aus Kyori's Adventure Libary stammen. Weitere 
Informationen dazu können hier gefunden werden: https://docs.adventure.kyori.net.

#### Beispiel:

```java
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ExampleMessage {
    public void errorMessage(Player player) {
        Component message = Component.text("Dein Name ist ")
                .color(NamedTextColor.GRAY)
                .append(Component.text(player.name()).color(NamedTextColor.RED));
        ExamplePlugin.getInstance().getMessageUtil().sendMessage(player, message);
    }
}
```

### Zeit
#### Erklärung:
Es gibt ebenfalls eine Klasse namens ``TimeUtil``, welche es erlaubt einen unix timestamp in ein 
für Menschen lesbaren String umzuwandeln im format ***dd.MM.yyyy HH:mm*** und Sekunden in einen 
String mit Tagen, Stunden, Minuten und Sekunden umzuwandeln
#### Beispiel:

```java
public class ExampleTime {
    public void time() {
        String readableDate = TimeUtil.unixTimestampToString(Instant.now().getEpochSecond());
        String readableTime = TimeUtil.formatSeconds(99999);
    }
}
```
### Spielerdaten
#### Erklärung:
TurtleServer speichert Spielerdaten als Json Dokument in MongoDB. Das Json Dokument wird dann 
mithilfe des ORMs Morphia auf die Klasse PetropiaPlayer gemaped. Der PetropiaPlayer enthält 
verschiedene Daten wie z.B. Spielernamen, UUID, Namenshistorie (wird nicht vollständig sein, wenn 
spieler nicht online war mit geänderten Namen), skinTextur/Signatur, onlinestatus, akuteller 
Server, letzter Server (wenn offline), stats, uvm. Spielerdaten können mithilfe der Instanz des 
MongoDBHandlers abgefragt werden. Sollte ein Spieler auf den lokalen Server online sein, wird 
dieser im Cache gespeichert und somit kann auf ihn im selben Tick zugeriffen werden. Sollte der 
Spieler nicht auf dem aktuellen Server online sein, so kann dieser geladen werden mithilfe der 
UUID oder optionaler (aber nicht ratsamer) Weise mithilfe des Namens abgefragt werden. Der 
MongoDBHandler gibt somit ein CompletableFuture zuück mit dem PetropiaPlayer, welches completed 
wird, sobald der PetropiaPlayer geladen wurde.

#### Beispiel:

```java
public class ExamplePlayer {
    public void logPlayerName(String uuid) {
        TurtleServer.getMongoDBHandler().getPetropiaPlayerByUUID(uuid)
                .thenAccept(petropiaPlayer -> {
                    String name = petropiaPlayer.getUserName();
                    YourPlugin.getInstance().getLogger().info("Name: " + name);
        });
    }
}
```
### CloudNet
#### Erklärung
TurtleServer bietet einen einfachen Wrapper für Metadaten des aktuellen Services und um Spieler 
mit der Lobby oder anderen Servern zu verbinden.

#### Beispiel:

```java
import de.petropia.turtleServer.server.TurtleServer;

public class ExampleCL {
    public void cloudNetExample(Player player) {
        String serviceName = TurtleServer.getInstance().getCloudNetAdapter().getServerInstanceName();
        String taskName = TurtleServer.getInstance().getCloudNetAdapter().getServerTaskName();
        TurtleServer.getInstance().getCloudNetAdapter().sendPlayerToLobby(player);
    }
}
```

### Prefixe (WIP)
#### Erklärung:
Custom Prefixe sind geplant. Jedoch kann man sie mit dem aktuellen System wie folgt deaktivieren
#### Beispiel:
````java
public class ExamplePrefix {
    public void prefixOff(){
        PrefixManager.getInstance().getPrefixGroups().forEach(prefixGroup -> prefixGroup.getTeam().setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER));
    }
}
````
