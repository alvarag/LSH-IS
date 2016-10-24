# LSH-IS: Instance selection of linear complexity for big data

This is an open-source filter for Weka based on locality sensitive hashing. Two algorithms are available and both have linear complexity.


###Cite this software as:
 **Á. Arnaiz-González, J-F. Díez Pastor, Juan J. Rodríguez, C. García Osorio.** _Instance selection of linear complexity for big data._ Knowledge-Based Systems, in press. [doi: 10.1016/j.knosys.2016.05.056](doi: 10.1016/j.knosys.2016.05.056)




#How to use

##Download and build with ant
- Download source code: It is host on GitHub. To get the sources and compile them we will need git instructions. The specifically command is:
```git clone https://github.com/alvarag/LSH-IS.git ```
- Build jar file: 
```ant dist_all ```
It generates the jar file under /dist/weka



##How to run

Include the file instanceselection.jar into the path. Example: 

```java -cp instanceselection.jar:weka.jar weka.gui.GUIChooser```

The new filter can be found in: weka/filters/supervised/instance.
