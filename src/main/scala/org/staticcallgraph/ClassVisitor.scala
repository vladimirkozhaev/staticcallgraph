package org.staticcallgraph

import org.apache.bcel.classfile.EmptyVisitor
import org.apache.bcel.classfile.JavaClass
import org.apache.bcel.generic.ConstantPoolGen
import org.apache.bcel.classfile.ConstantPool
import org.apache.bcel.classfile.Method
import org.apache.bcel.generic.MethodGen
import scala.beans.BeanProperty
import org.staticcallgraph.model.ClassInfo
import org.staticcallgraph.model.ClassInfo
import org.staticcallgraph.model.ClassMethodInfo
import org.staticcallgraph.model.ClassMethodInfo

class ClassVisitor(val clazz: JavaClass, var nameToClassMap: Map[String, ClassInfo]) extends EmptyVisitor {

  var constants = new ConstantPoolGen(clazz.getConstantPool());
  var classReferenceFormat = "C:" + clazz.getClassName() + " %s";
  val DCManager = new DynamicCallManager();
  @BeanProperty
  var methodCalls = List[String]();
  var classInfo: ClassInfo = null;
  override def visitJavaClass(jc: JavaClass): Unit = {
    jc.getConstantPool().accept(this);
    val methods = jc.getMethods();

    val classFullName = jc.getClassName();

    classInfo = nameToClassMap.getOrElse(classFullName, new ClassInfo(jc.getClassName()))
    if (!nameToClassMap.keySet.contains(classFullName)) {
      nameToClassMap += (classFullName -> classInfo);
    }
    methods.foreach(method => {
      DCManager.retrieveCalls(method, jc);
      DCManager.linkCalls(method);
      method.getAttributes();
      val methodInfo = new ClassMethodInfo(method.getName(), method.getAttributes().map(_.toString()).toList);

      method.accept(this);

    })
  }

  override def visitConstantPool(constantPool: ConstantPool): Unit = {
    constantPool.getConstantPool.filter(const => const != null).foreach(constant => {
      if (constant.getTag() == 7) {
        val referencedClass =
          constantPool.constantToString(constant);
        System.out.println(String.format(classReferenceFormat, referencedClass));
      }
    })
  }

  override def visitMethod(method: Method): Unit = {
    val mg = new MethodGen(method, clazz.getClassName(), constants)
    val visitor = new MethodVisitor(mg, clazz, classInfo, nameToClassMap);
    methodCalls = methodCalls ++ visitor.start();
  }

  def start(): ClassVisitor = {
    visitJavaClass(clazz);
    return this;
  }

}