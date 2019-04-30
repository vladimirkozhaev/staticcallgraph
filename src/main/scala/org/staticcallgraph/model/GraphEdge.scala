package org.staticcallgraph.model

class GraphEdge(
  val startClass: ClassInfo,
  val endClass:   ClassInfo, var startNum: Int, var endNum: Int, val label: String) {
  def equas(that: Any) = that match {
    case null            => false;
    case that: GraphEdge => nullOrEquals(that.startClass, startClass) && nullOrEquals(that.endClass, endClass) && nullOrEquals(that.startNum, startNum) && nullOrEquals(that.endNum, endNum)
    case _               => false;
  }

  def nullOrEquals(one: Any, two: Any) = one == null && two == null || (one != null && one.equals(two))
}

object GraphEdge {

  def apply(
    startClass: ClassInfo,
    endClass:   ClassInfo, startNum: Int, endNum: Int, label: String) = new GraphEdge(
    startClass,
    endClass, startNum, endNum, label)
}