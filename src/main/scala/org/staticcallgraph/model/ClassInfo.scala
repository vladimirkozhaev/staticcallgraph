package org.staticcallgraph.model
import scala.beans.BeanProperty
import org.apache.bcel.classfile.JavaClass
import jdk.nashorn.internal.objects.annotations.Getter
import jdk.nashorn.internal.objects.annotations.Setter

class ClassInfo(@BeanProperty val className:String) {
    @BeanProperty var methods:Map[String,ClassMethodInfo]=Map[String,ClassMethodInfo]();
    @BeanProperty var javaClass:JavaClass=null;
    def fullName():String=className;
    
    
    
    override def toString():String=fullName()+"{"+methods.values.mkString(";")+ "}";
    
}

