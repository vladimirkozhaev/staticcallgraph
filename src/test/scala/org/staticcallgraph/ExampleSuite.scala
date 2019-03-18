package org.staticcallgraph

import org.scalatest.FunSuite
import org.staticcallgraph.model.ClassInfo

class SetSuite extends FunSuite {

  

  test("Get resource") {
    val jarsList = "src/test/resources/TestCall.jar" :: Nil
    val map: Map[String, ClassInfo] = JCallGraph.processJarsPath(jarsList.toArray)

    assert(map.size == 4)
    val a: ClassInfo = map.get("testproject.ClassA").get
    assert(a != null)
    assert(a.methods.size == 2)
    a.methods.keys.foreach(f => println(f))
    val fooA = a.methods.get("fooA()").get
//    assert(fooA.calls.size == 1)

    JCallGraph.topsWithoutParents(map)
    //assert(tops.size == 1)

  }

}