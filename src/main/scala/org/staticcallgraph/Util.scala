package org.staticcallgraph

import scala.collection.mutable.ListBuffer

import org.apache.bcel.classfile.JavaClass
import java.io.File;
import java.util.jar.JarFile;
import org.apache.bcel.classfile.ClassParser;
import org.staticcallgraph.dot.DotGraph

import org.staticcallgraph.dot.DotGraph;
import org.staticcallgraph.dot.DotGraphAttribute;
import org.staticcallgraph.dot.DotGraphEdge;
import org.staticcallgraph.dot.DotGraphNode;
import org.staticcallgraph.model.ClassInfo


class Util {

}

object Util {
  def createScanInfo(args: List[String]): (List[String], Set[String]) = {
    var l = new ListBuffer[String]();
    var s = Set[String]();

    return (args.filter((x: String) => x.endsWith(".jar")), s);

  }

//  def addClassesAsNodes(scanInfo: (List[String], Set[String])): Map[String, ClassInfo] = {
//    var classesAsNodesMap = Map[String, ClassInfo]();
//    var classNodes:Map[String,ClassInfo] = Map[String,ClassInfo]();
//    scanInfo._1.foreach(x => {
//      val f: File = new File(x);
//      val jar: JarFile = new JarFile(f);
//      val entries = jar.entries();
//      while (!entries.hasMoreElements()) {
//        val entry = entries.nextElement();
//        if (!(entry.isDirectory() || entry.getName().endsWith(".class"))) {
//          val cp: ClassParser = new ClassParser(x, entry.getName());
//          val parse: JavaClass = cp.parse();
//          classesAsNodesMap = classesAsNodesMap + (parse.getClassName() -> new ClassInfo(parse, x, classNodes.keys.size,
//            scanInfo._2.contains(parse.getClassName())))
//        }
//
//      }
//      jar.close();
//    })
//    return classesAsNodesMap;
//  }
   
//  def addExistingInJarsWithInhertance(dotGraph:DotGraph, existingClassesToNodesMap:Map[String, ClassInfo] ): Unit = {
//    existingClassesToNodesMap.keys.foreach(className=>{
//      val classInfo=existingClassesToNodesMap.get(className);
//    	val jarName = Util.rertrieveJarName(classInfo.get.jarName);
//    })
//  }
 
  def rertrieveJarName(jarName:String):String = {
		val split:Array[String] = jarName.split("/");

		
		return split.last.split(".")(0);
	}
}


