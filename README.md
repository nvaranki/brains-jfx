# The [Thinker](http://varankin.com/?page=software/13&lang=en)™ desktop application

The Thinker is a math simulator instrument to perform visual examination of numerous
computational processes running concurrently.

A math model is described as an unlimited hierarchy of math modules.
Each function or group of functions inside a module can evaluate the
result in its own computing thread. Modules exchange computed values
in asynchronous manner. Each math value can be visualized at runtime
as either a plain number or floating timeline graph.

Screenshots:

![Schematics](http://varankin.com/software/13/net10x10_sc.png)

![Timelines](http://varankin.com/software/13/self_summer_2x2_07.png)

Documentation:
[Introduction](http://varankin.com/software/13/Thinker-1.0.202201291222.pdf), 
[Functional Description](http://varankin.com/software/13/Thinker-fd-1.0.2022010291228.pdf). 

This is a NetBeans/Ant project built on JavaFX API. It requires: 
[../brains-appl](https://github.com/nvaranki/brains-appl) 
[../brains-db-api](https://github.com/nvaranki/brains-db-api) 
[../brains-db-neo4j](https://github.com/nvaranki/brains-db-neo4j) 
[../stream](https://github.com/nvaranki/stream) 
[../utility](https://github.com/nvaranki/utility) 
