package org.staticcallgraph

import org.apache.bcel.classfile.ClassParser
import java.io.File
import java.util.jar.JarFile
import scala.collection.JavaConverters
import scala.collection.JavaConversions
import java.util.Enumeration
import java.util.Spliterators
import java.util.Spliterator
import java.util.stream.StreamSupport
import java.util.Collections
import java.util.jar.JarEntry
import scala.collection.JavaConverters._
import org.staticcallgraph.model.ClassInfo
import org.staticcallgraph.model.ClassInfo

object JCallGraph extends App {
  override def main(args: Array[String]): Unit = {

    val args1: Array[String] = new Array[String](1);
    args1(0) = "/home/voffka/Documents/projects/myfirstproject/src/test/resources/TestCall.jar"
    getClass().getResource("/TestCall.jar").getFile();
    val maps = processJarsPath(args1)
    println("maps:", maps)

  }

  def processJarsPath(args: Array[String]): Map[String, ClassInfo] = {
    var nameToClassMap: Map[String, ClassInfo] = Map[String, ClassInfo]();
    args.foreach(f => {
      println("pathToJar:"+f)
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
        //foldLeft(Map[String, ClassInfo]())(_::getClassVisitor(_).start().nameToClassMap);

        // println("methods:"+nameToClassMap)

      }
    })
    return nameToClassMap;

  }

}
