[maven]: http://maven.apache.org/
[java]: https://www.oracle.com/java/index.html
[git]: https://git-scm.com/
[cytoscape]: https://cytoscape.org/
[directappinstall]: http://manual.cytoscape.org/en/stable/App_Manager.html#installing-apps
[Microbetag]:https://github.com/hariszaf/microbetag
[scnetviz]:https://github.com/RBVI/scNetViz
[cytoscape App Store]:https://apps.cytoscape.org
[documentation]:https://hariszaf.github.io/microbetag/docs/cytoApp/

MGG,  Cytoscape APP
=======================================

  MGG is a cytoscape app that enables the straightforward and user-friendly way to perform the [microbetag][Microbetag] workflow and to visualise microbetag-annotated networks.
  MGG was built based on the [scNetViz][scnetviz] source code. MGG allows users to import their data, retrieve the important annotations and investigate
  them. It provides a visual style to distinguish annotated nodes, it utilizes CyPanels for node and edge filtering and visualization of important annotations,
  and supports enrichment analysis based on assigned phenotypic traits and clusters.

        
Requirements to use
=====================

* [Cytoscape][cytoscape] 3.7 or above
* Internet connection to allow App to connect to remote services


Installation via from Cytoscape
======================================

MGG will be in the [Cytoscape App Store][cytoscape App Store] soon!!

Manual Installation 
======================================
Building manually:
Requirements to build 

* Java 8+ with jdk
* Maven 3.4 or above

Commands below assume [Git][git] command line tools have been installed

```Bash
#Can also just download repo and unzip it

git clone https://github.com/ermismd/MGG

cd MGG

mvn clean test install
```

The above command will create a jar file under **target/** named
**mgG-\<VERSION\>.jar**

In Windows Explorer, go to C:\Users\Your-User-Name. You will find the CytoscapeConfiguration directory inside there. Inside the CytoscapeConfiguration open 3,
then open apps. You will find the installed folder there. Copy and paste the MgG jar file to the installed folder .


In Linux and Mac the procedure is the same. Find the CytoscapeConfiguration directory and then paste the mgG jar inside the installed folder.

Documentation and Help
======================================
[Documentation][documentation]

Copyrights and Licence
======================================
Apache-2.0 license
