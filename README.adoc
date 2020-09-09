# roaster
Developer and Intergration test framework based on coff:ee

A roaster egy Java SE+ teszt framework, melynek célja, hogy az fejlesztőknek a tesztek írásánál gyakori algoritmusait összegyűjtse,
azokra egy alapvető megoldást nyújtson, melyet, ha szükséges saját igényeinkre szabhatunk.

A projekt ezeken a technológiákon alapszik:

* Java 11+
* CDI 2.0+
* https://rest-assured.io[REST-assured]

Bővebb leírás a https://i-cell-mobilsoft-open-source.github.io/roaster/[roaster dokumentáció] oldalon található.

== Maven central
A roaster megtalálható az official maven repository-ban is,
a projekten elég behúzni a BOM-ot mely minden almodult lekezel:

.dependencyManagement beállítások
[source, xml]
----
<dependencyManagement>
    <dependency>
        <groupId>hu.icellmobilsoft.roaster</groupId>
        <artifactId>roaster-bom</artifactId>
        <version>${version.hu.icellmobilsoft.roaster}</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
</dependencyManagement>
----

.modulok használata
[source, xml]
----
<dependencies>
    <dependency>
        <groupId>hu.icellmobilsoft.roaster</groupId>
        <artifactId>roaster-restassured</artifactId>
    </dependency>
    <dependency>
        <groupId>hu.icellmobilsoft.roaster</groupId>
        <artifactId>roaster-oracle</artifactId>
    </dependency>
    ...egyéb roaster modulok...
</dependencies>
----

Bővebb leírás a https://i-cell-mobilsoft-open-source.github.io/roaster/[roaster dokumentáció] oldalon található.

Copyright (C) 2020 i-Cell Mobilsoft Zrt.
