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

object JCallGraph extends App {
  override def main(args: Array[String]): Unit = {

    val args1: Array[String] = new Array[String](1);
    args1(0) = "/home/voffka/Documents/projects/myfirstproject/src/test/resources/TestCall.jar"
    //getClass().getResource("/TestCall.jar").getFile();
    val maps = processJarsPath(args1)
    
    makeTheGraph(maps, "/home/voffka/Documents/projects/myfirstproject/src/main/resources/output.dot")
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

  def makeTheGraph(nameToClassMap: Map[String, ClassInfo], pathToFile: String): DotGraph = {
    val dotGraph: DotGraph = new DotGraph("CallGraph")
    dotGraph.setGraphAttribute("splines", "ortho")
    dotGraph.setGraphAttribute("nodesep","0.5")
    dotGraph.setGraphAttribute("concentrate","true")
    dotGraph.setNodeShape("box")
    dotGraph.setGraphAttribute("rankdir", "LR")
    val heads = topsWithoutParents(nameToClassMap);
    val numberStream = Stream.iterate(0)(_ + 1).iterator
     val nextId = { var i = 1; () => { i += 1; i } }
    val headGraph = heads.map(head => {
      val first = nextId()
      val second = nextId()

      (head, (first, second))
    })

    val headClasses = List(heads);

    
    val l=List[(ClassInfo,(Int,Int))]()
    val nodesToDraw=headGraph.flatMap(head=>addParentsOfHead(head, l,nameToClassMap,nextId))
    
    
    drawTheGraph(dotGraph, nodesToDraw)

   
    dotGraph.plot(pathToFile)
    return dotGraph;
  }
  
  def drawTheGraph(dotGraph: DotGraph,heads: List[(ClassInfo, (Int, Int))])= heads.foreach(head => {
      val node1=dotGraph.drawNode(head._2._1.toString())
      node1.setLabel("");
      node1.setShape("point")
      val node2=dotGraph.drawNode(head._2._2.toString())
      node2.setLabel("")
      node2.setShape("point")
      val edge:DotGraphEdge=dotGraph.drawEdge(head._2._1.toString(), head._2._2.toString())
      edge.setLabel("\"" + head._1.className + "\"")
      edge.setAttribute("rank", "same")
    })

 
  def topsWithoutParents(nameToClassMap: Map[String, ClassInfo]): List[ClassInfo] =

    return nameToClassMap.values.filter(classInfo => !(nameToClassMap.keySet.contains(classInfo.javaClass.getSuperclassName)
      || nameToClassMap.keySet.toSet.count(interface => classInfo.javaClass.getAllInterfaces.toSet.contains(interface)) > 0)).toList

  def addParentsOfHead(node: (ClassInfo, (Int, Int)), heads: List[(ClassInfo, (Int, Int))], nameToClassMap: Map[String, ClassInfo],nextId:()=>Int): List[(ClassInfo, (Int, Int))] = {
    val classInfo:ClassInfo = node._1
    
   val filter= nameToClassMap.values.filter(childClass => childClass.javaClass.getSuperClasses().indexOf(classInfo.javaClass)>=0||childClass.javaClass.getInterfaces.contains(classInfo.javaClass));
   
  
   
   val childHeads=filter .flatMap(chClass => addParentsOfHead(
        (chClass, (node._2._2, nextId())),
        heads,
        nameToClassMap,nextId))
    return (if (!heads.contains(node))
      node :: heads
    else
      heads)++childHeads;

  }
}
