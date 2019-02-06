package org.staticcallgraph

import org.scalatest.FunSuite

class SetSuite extends FunSuite {

  test("An empty Set should have size 0") {
    assert(Set.empty.size == 0)
  }

  test("Two jars") {
    val list="sex.jar"::"box.jar"::Nil
    assert(Set.empty.size == 0)
    assert(Util.createScanInfo(list)._1.size ==  2)
  }
  
  
   
  
  
   test("Get resource") {
    val jarsList="src/test/resources/TestCall.jar"::Nil
	  val map=JCallGraph.processJarsPath(jarsList.toArray)
	  
	  assert(map.size==4)
  }
  
  
}