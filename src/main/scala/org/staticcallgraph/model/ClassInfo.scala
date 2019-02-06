package org.staticcallgraph.model
import scala.beans.BeanProperty

class ClassInfo(@BeanProperty val className:String) {
    @BeanProperty var methods:Map[String,ClassMethodInfo]=Map[String,ClassMethodInfo]();
    def fullName():String=className;
    
    
    override def toString():String=fullName()+"{"+methods.values.mkString(";")+ "}";
    
}

