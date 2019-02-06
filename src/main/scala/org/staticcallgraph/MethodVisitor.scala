package org.staticcallgraph

import org.apache.bcel.generic.MethodGen
import org.apache.bcel.classfile.JavaClass
import com.sun.org.apache.bcel.internal.generic.Type
import org.apache.bcel.generic.EmptyVisitor
import org.apache.bcel.generic.Instruction
import org.apache.bcel.generic.InstructionConst
import org.apache.bcel.generic.ConstantPushInstruction
import org.apache.bcel.generic.ReturnInstruction
import org.apache.bcel.generic.INVOKEVIRTUAL
import org.apache.bcel.generic.INVOKEINTERFACE
import org.apache.bcel.generic.INVOKESPECIAL
import org.apache.bcel.generic.INVOKEDYNAMIC
import scala.collection.mutable.ListBuffer
import org.staticcallgraph.model.ClassInfo
import org.staticcallgraph.model.ClassMethodInfo
import org.staticcallgraph.model.ClassMethodInfo
import org.apache.bcel.generic.ReferenceType

class MethodVisitor(val mg: MethodGen, val visitedClass: JavaClass, val classInfo: ClassInfo, var nameToClassMap: Map[String, ClassInfo]) extends EmptyVisitor {
  val cp = mg.getConstantPool();
  val format = "M:" + visitedClass.getClassName() + ":" + mg.getName() + "(" + mg.getArgumentTypes().mkString(",") + ")" + " " + "(%s)%s:%s(%s)";
  var methodCalls: ListBuffer[String] = ListBuffer[String]();
  val method: ClassMethodInfo = new ClassMethodInfo(mg.getMethod().getName(), mg.getMethod().getArgumentTypes().toList.map(_.toString()));
  def start(): List[String] = {
    if (mg.isAbstract() || mg.isNative()) {
      return List[String]();

    }

    classInfo.methods += (method.toString() -> method)

    mg.getInstructionList().getInstructions().foreach(f => {

      if (!visitInstruction(f))
        f.accept(this);
    })

    return methodCalls.toList;
  }

  def visitInstruction(i: Instruction): Boolean = {
    val opcode = i.getOpcode();
    return ((InstructionConst.getInstruction(opcode) != null)
      && !(i.isInstanceOf[ConstantPushInstruction])
      && !(i.isInstanceOf[ReturnInstruction]));
  }

  override def visitINVOKEVIRTUAL(i: INVOKEVIRTUAL): Unit = {
    val referenceType: ReferenceType = i.getReferenceType(cp);
    val className: String = i.getClassName(cp);

   
    val calledMethodName = i.getMethodName(cp);
    val calledClassInfo = nameToClassMap.getOrElse(className, new ClassInfo(className));
    val calledMethod:ClassMethodInfo = calledClassInfo.methods.getOrElse(ClassMethodInfo.getMethodKey(
      calledMethodName,
      i.getArgumentTypes(cp).toList.map(_.toString())), new ClassMethodInfo(
      calledMethodName, i.getArgumentTypes(cp).toList.map(_.toString())))
      method.calls=(calledClassInfo,calledMethod)::method.calls
      nameToClassMap+=(calledClassInfo.fullName()->calledClassInfo)   
  }

  def splitClassOnNameAndPackage(className: String): (String, String) = {
    val lastIndexOfDot = className.lastIndexOf(".")

    return (className.substring(Math.max(0, lastIndexOfDot)), className.substring(lastIndexOfDot + 1, className.length()))
  }

  override def visitINVOKEINTERFACE(i: INVOKEINTERFACE) {
    methodCalls += String.format(format, "I", i.getReferenceType(cp), i.getMethodName(cp), i.getArgumentTypes(cp).mkString(","));
  }

  override def visitINVOKESPECIAL(i: INVOKESPECIAL) = {
    methodCalls += String.format(format, "O", i.getReferenceType(cp), i.getMethodName(cp), i.getArgumentTypes(cp).mkString(","));
  }

  override def visitINVOKEDYNAMIC(i: INVOKEDYNAMIC) = {
    methodCalls += String.format(format, "D", i.getType(cp), i.getMethodName(cp),
      i.getArgumentTypes(cp).mkString(","));
  }

}