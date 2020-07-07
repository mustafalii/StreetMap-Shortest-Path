# StreetMap-Shortest-Path

## Introduction
Given a data-set representing the roads and intersections in a specific geographic region, this program plots a map of the data and provides shortest path directions between any two arbitrary intersections using Dijkstra’s algorithm. This project taught me a lot about run-time optimization and strengthened my knowledge of data structures. 

## Input Data
Data is stored in tab-delimited text files. See files: ur.txt, monroe.txt. The file nys.txt could not be uploaded due to file size restrictions.
Intersections start with “i”, followed by a unique string ID, and decimal representations of latitude and longitude.
Roads start with “r”, followed by a unique string ID, and the IDs of the two intersections it connects.

## Usage
First, compile the program:
```
javac StreetMap.java
```

The program then accepts the following set of commands:
```
java StreetMap map .txt [ -- show ] [-- directions startIntersection
endIntersection ]
```
You can then also change start and end points with the GUI.

## In action
Given the command:

```
java StreetMap ur.txt --show --directions SUEB HOYT
```

The program takes less than a second to run and outputs:

<img src="/imgs/img1.JPG" >
