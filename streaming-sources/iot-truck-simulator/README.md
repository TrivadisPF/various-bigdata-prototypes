# Geo-Fencing with Kafka, Kafka Streams and KSQL

This project will investigate the existing functionality of Kafka for geo-spatial analytics, especiall the so-called geo-fencing and extend the functionality if needed in a reusable way.

## Context

Situations we **need** to solve with this project:

* Perform geo-fencing on streaming data
* 

Situations we **do not have** to solve with this project:

* n.a.

## High-Level overview

An important underlying concept behind location-based applications is called geofencing. Geofencing is a process that allows acting on users and/or devices who enter/exit a specific geographical area, known as a geo-fence. A geo-fence can be dynamically generated—as in a radius around a point location, or a geo-fence can be a predefined set of boundaries (such as secured areas, buildings, boarders of counties, states or countries).
 
Geofencing lays the foundation for realizing use cases around fleet monitoring, asset tracking, phone tracking across cell sites, connected manufacturing, ride-sharing solutions and many others. 
 
GPS tracking tells constantly and in real time where a device is located and forms the stream of events which needs to be analyzed against the much more static set of geo-fences. Many of the use cases mentioned above require low-latency actions taken place, if either a device enters or leaves a geo-fence or when it is approaching such a geo-fence. That’s where streaming data ingestion and streaming analytics and therefore the Kafka ecosystem comes into play. 

This project should investigate how location analytics applications can be implemented using Kafka and KSQL & Kafka Streams. The design of such solution is so that it can scale with both an increasing amount of position events as well as geo-fences will be discussed as well. 

## Requirements

### Must

1. provide a persistent store for configuring geo-fences
2. support adding geo-fences while system is running
3. support for defining hierachical geo fences
4. a high-volume data stream of positional messages (holding geo-coordinate) should be supported
2. thounds to millions of geo-fences should be supported, i.e. the solution needs to be scalable and state hold in stream processing needs to be partionable
3. implement functionality for checking if a given geo coordinate is NEAR a geo-fence, where the distance for NEAR should be configurable
4. implement functionality for checking if a given geo coordinate is ENTERING/EXITING a geo-fence
5. implement functionality for checking if a given geo coordiante is currently IN a geo-fence
6. impelment functionality for checking if a given geo coordiante is IN a geo-fence for a given time

### Optional

1. Implement a GUI for adding geo-fences in an easy manner
2. Implement a dashboard for tracking moving objects against geo-fences
 
## References

1. [Java GeoTools library](http://geotools.org/)
2. [Tracking PoC](https://idismobile-tkseoraclecloud.developer.ocp.oraclecloud.com/idismobile-tkseoraclecloud/#projects/tracking)

### Input

(1) Wie kann man die Geo-Fences „geschickt“ auswählen um nicht zu viele Distanzen zu berechnen?
Der Algorithmus zur Berechnung einer Distanz zwischen Punkt und Polygon ist – soweit ich weiß – aufwendig. Wenn ich eine ressourceneffiziente Lösung implementieren möchte, habe ich also Interesse daran, die Anzahl dieser Berechnungen soweit zu reduzieren wie möglich. Wenn ich mir vorstelle, dass meine Polygone nicht disjunkt sind, sondern vielmehr eine Hierarchie bilden (etwa ein Polygon „Schweiz“, ein darin enthaltenes für „Kanton Bern“, ein darin enthaltenes für den Verwaltungskreis „Bern-Mittelland“, ein darin enthaltenes für die Stadt „Bern“ und darin enthaltene für die Stadtteile „Innere Stadt“ etc.), so wäre es doch gut, wenn der Algorithmus auf einer möglichst hohen Ebene feststellt, dass ein Punkt nicht in dem Geo-Fence liegt und die Berechnungen für die enthaltenen Geo-Fences ausspart.
Das ginge natürlich nur, wenn ich nur an der Tatsache, ob der Punkt in einem Bereich liegt oder nicht, interessiert bin; geht es um Distanzen, habe ich zunächst nicht viel gewonnen… Es sei denn, ich akzeptiere Toleranzen (wenn ich weiß, dass ein Punkt in der Schweiz liegt, aber noch nicht in im Kanton Bern, finde ich bestimmt eine obere Schranke der Distanz.)
 
(2) „dynamische“ Berechnungsfrequenz
Es wäre sinnvoll, wenn man die Intervalllänge der Berechnungen in Abhängigkeit von Anwendungsfall und aktuellen Aufenthaltsort bestimmen kann. Befinden sich unsere Leichter gerade im Hafen, sind wir beispielsweise daran interessiert, den aktuellen Standpunkt in etwa alle fünf Minuten zu erhalten; während sie sich aber noch auf dem Rhein befinden, reicht eine Info alle 30 Minuten (wenn überhaupt) locker aus.
 
(3) „Rückweg“ / IoT
Spannend könnte ich mir auch vorstellen, einen „Rückkanal“ vorzusehen um auf Informationen / Steuerbefehle an das Device zu übertragen. In Zusammenhang mit Punkt (2) etwa die Anweisung, wann die nächste Positionsmeldung erwartet wird, um so auch die Energieressourcen auf dem Gerät zu schonen und Sendevorgänge zu reduzieren…
Das hat zwar nichts mit Skalierbarkeit und Geospatial Services mit Kafka im engeren Sinne zu tun, würde aber den Blick über den Tellerrand wagen. Und mit NB-IoT, LoRa, Sigfox, LTE-M etc. stehen ja für solche Use-Cases interessante Funktechnologien zur Verfügung.s