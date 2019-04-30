package org.staticcallgraph

import java.io.File
import java.util.Collections
import java.util.jar.JarFile

import scala.collection.JavaConverters
import scala.collection.JavaConverters._

import org.apache.bcel.classfile.ClassParser
import org.staticcallgraph.model.ClassInfo
import org.staticcallgraph.dot.DotGraph
import org.staticcallgraph.dot.DotGraphEdge
import org.staticcallgraph.model.GraphEdge
import org.staticcallgraph.model.GraphEdge
import org.staticcallgraph.model.GraphEdge
import org.staticcallgraph.model.GraphEdge
import sun.security.util.Length

object JCallGraph extends App {
  override def main(args: Array[String]): Unit = {

    val args1: Array[String] = new Array[String](1);
    args1(0) = "/home/voffka/Documents/projects/myfirstproject/src/test/resources/TestCall.jar"
    //getClass().getResource("/TestCall.jar").getFile();
    val maps = processJarsPath(args1)
    val nextId = { var i = 0; () => { i += 1; i } }
    val edgesSet = createEdgesList(maps.values.toList, nextId)
    println("str is:", edgesSet.size)
    makeTheGraph(edgesSet, "/home/voffka/Documents/projects/myfirstproject/src/main/resources/output.dot")
  }

  def processJarsPath(args: Array[String]): Map[String, ClassInfo] = {
    var nameToClassMap: Map[String, ClassInfo] = Map[String, ClassInfo]();
    args.foreach(f => {
      JarAdder.addJarToClasspath(new File(f));
      def getClassVisitor(cp: ClassParser): ClassVisitor = new ClassVisitor(cp.parse(), nameToClassMap);
      if (args.length == 1)
        println(s"Hello, ${args(0)}")
      else
        println("I didn't get your name.")

      val file = new File(f);
      if (file.exists()) {
        System.err.println("Jar file " + file + " does not exist");
        val jarFile: JarFile = new JarFile(file)
        val entries = jarFile.entries()
        //JavaConversions.asScalaIterator(entries)
        val entryList = Collections.list(entries).asScala;
        nameToClassMap = nameToClassMap ++ entryList.filter(e => (!e.isDirectory()) && e.getName().endsWith(".class")).
          map(jarEntry => getClassVisitor(new ClassParser(f, jarEntry.getName)).start().nameToClassMap).foldLeft(Map[String, ClassInfo]())((map1, map2) => map1.++:(map2))

      }
    })

    nameToClassMap.values.filter(classInfo => !(nameToClassMap.keySet.contains(classInfo.javaClass.getSuperclassName)
      || nameToClassMap.keySet.toSet.count(interface => classInfo.javaClass.getAllInterfaces.toSet.contains(interface)) > 0))
    val tops = topsWithoutParents(nameToClassMap)
    return nameToClassMap;

  }

  def makeTheGraph(set: Set[GraphEdge], pathToFile: String): DotGraph = {
    val dotGraph: DotGraph = new DotGraph("CallGraph")
    dotGraph.setGraphAttribute("splines", "ortho")
    dotGraph.setGraphAttribute("nodesep", "0.5")
    dotGraph.setGraphAttribute("concentrate", "true")
    dotGraph.setNodeShape("box")
    dotGraph.setGraphAttribute("rankdir", "LR")

    drawTheGraph(dotGraph, set)

    dotGraph.plot(pathToFile)
    return dotGraph;
  }

  def drawTheGraph(dotGraph: DotGraph, edges: Set[GraphEdge]) = edges.foreach(e => {
    val node1 = dotGraph.drawNode(e.startNum.toString)
    node1.setLabel("");
    node1.setShape("point")
    val node2 = dotGraph.drawNode(e.endNum.toString)
    node2.setLabel("")
    node2.setShape("point")
    val edge: DotGraphEdge = dotGraph.drawEdge(e.startNum.toString, e.endNum.toString)
    edge.setLabel("\"" + e.label + "\"")
    val prop=e.properties;
    prop foreach(x=>edge.setAttribute(x._1,x._2))
    //e.properties.foreach( node:(String,String)=>edge.setAttribute(key._1,key._2)
    edge.setAttribute("rank", "same")
  })

  def topsWithoutParents(nameToClassMap: Map[String, ClassInfo]): List[ClassInfo] =

    nameToClassMap.values.filter(classInfo => !(nameToClassMap.keySet.contains(classInfo.javaClass.getSuperclassName)
      || nameToClassMap.keySet.toSet.count(interface => classInfo.javaClass.getAllInterfaces.toSet.contains(interface)) > 0)).toList

  def createEdgesList(classInfos: List[ClassInfo], nextId: () => Int): Set[GraphEdge] = {
    var edges: Set[GraphEdge] = Set[GraphEdge]();

    classInfos.foreach(classInfo => {
      edges = edges ++ createEdgesListForClassInfo(classInfo, classInfos, edges, nextId)
    })
    return edges;
  }
  /**
   *
   */

  def createEdgesListForClassInfo(currentClass: ClassInfo, classInfos: List[ClassInfo], edges: Set[GraphEdge], nextId: () => Int): Set[GraphEdge] = {

    var edgeOption = edges.find(edge => edge.startClass == currentClass && edge.endClass == currentClass)
    if (edgeOption.isDefined) {
      return edges
    }

    val classInfoParents = classInfos.filter(superClass => currentClass.javaClass.getSuperClasses().indexOf(superClass.javaClass) >= 0 || currentClass.javaClass.getInterfaces.indexOf(superClass.javaClass) >= 0)

    return classInfoParents.length match {
      case 0 => edges + GraphEdge(currentClass, currentClass, nextId(), nextId(), currentClass.fullName())
      case _ => {
        var edgesSet = Set[GraphEdge]()
        edgesSet = edgesSet ++ edges;

        classInfoParents.foreach(parentClassInfo => {

          edgesSet = edgesSet ++ createEdgesListForClassInfo(parentClassInfo, classInfos, edgesSet, nextId)

        })

        var parentEdges = edgesSet.filter(superClassEdge => {

          (currentClass.javaClass.getSuperClasses.indexOf(superClassEdge.endClass.javaClass) >= 0 || currentClass.javaClass.getInterfaces.indexOf(superClassEdge.endClass.javaClass) >= 0) && superClassEdge.startClass == superClassEdge.endClass
        })

        val firstParentEdge = parentEdges.toList.reverse.last
        val currentClassEdge = GraphEdge(currentClass, currentClass, firstParentEdge.endNum, nextId(), currentClass.fullName())
        edgesSet = edgesSet + currentClassEdge
        parentEdges = parentEdges - firstParentEdge
        parentEdges.foreach(additionalParentEdge => {
          val edge=GraphEdge(additionalParentEdge.endClass, currentClass, additionalParentEdge.endNum, currentClassEdge.startNum, "Extends").addProperty("style","dotted")
          
          edgesSet = edgesSet + edge
          })
        return edgesSet;

      }
    }

  }

}

