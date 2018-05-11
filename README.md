# LSH-IS: Instance selection of linear complexity for big data

This is an open-source filter for Weka based on locality sensitive hashing. Two algorithms are available and both have linear complexity.


### Cite this software as:
 **Á. Arnaiz-González, J-F. Díez Pastor, Juan J. Rodríguez, C. García Osorio.** _Instance selection of linear complexity for big data._ Knowledge-Based Systems, 107, 83-95. [doi: 10.1016/j.knosys.2016.05.056](https://doi.org/10.1016/j.knosys.2016.05.056)

```
@article{ArnaizGonzalez2016,   
  title = "Instance selection of linear complexity for big data",   
  journal = "Knowledge-Based Systems ",   
  volume = "107",   
  pages = "83 - 95",   
  year = "2016",   
  issn = "0950-7051",   
  doi = "10.1016/j.knosys.2016.05.056",   
  author = "\'{A}lvar Arnaiz-Gonz\'{a}lez and Jos\'{e} F. D\'{i}ez-Pastor and Juan J. Rodr\'{i}guez and C\'{e}sar Garc\'{i}a-Osorio"   
}
```


# How to use

## Download and build with ant
- Download source code: It is host on GitHub. To get the sources and compile them we will need git instructions. The specifically command is:
```git clone https://github.com/alvarag/LSH-IS.git ```
- Build jar file: 
```ant dist_weka ```
It generates the jar file under /dist/weka



## How to run

Include the file instanceselection.jar into the path. Example: 

```java -cp instanceselection.jar:weka.jar weka.gui.GUIChooser```

The new filter can be found in: weka/filters/supervised/instance.
