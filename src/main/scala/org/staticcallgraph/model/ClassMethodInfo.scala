package org.staticcallgraph.model

import scala.beans.BeanProperty

class ClassMethodInfo(@BeanProperty val name:String, @BeanProperty val parameters:List[String]) extends Equals {
  @BeanProperty var calls:List[(ClassInfo,ClassMethodInfo)]=List[(ClassInfo,ClassMethodInfo)]();

  def canEqual(other: Any) = {
    other.isInstanceOf[org.staticcallgraph.model.ClassMethodInfo]
  }

  override def equals(other: Any) = {
    other match {
      case that: org.staticcallgraph.model.ClassMethodInfo => that.canEqual(ClassMethodInfo.this) && name == that.name && parameters == that.parameters
      case _ => false
    }
  }

  override def hashCode() = {
    val prime = 41
    prime * (prime + name.hashCode) + parameters.hashCode
  }
  
  override def toString=ClassMethodInfo.methodKey(name,parameters)
   
}

object ClassMethodInfo{
    def getMethodKey(name:String,parameters:List[String]):String={
      return name+"("+parameters.mkString(",")+ ")";
    }
    
    def methodKey(name:String,parameters:List[String]):String=name +"(" +parameters.mkString(",") +")";
    
    
    
}