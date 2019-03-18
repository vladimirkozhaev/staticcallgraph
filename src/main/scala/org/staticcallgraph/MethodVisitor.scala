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
import org.apache.bcel.generic.InvokeInstruction

class MethodVisitor(val mg: MethodGen, val visitedClass: JavaClass, val classInfo: ClassInfo, var nameToClassMap: Map[String, ClassInfo]) extends EmptyVisitor {
  val cp = mg.getConstantPool();
  //val format = "M:" + visitedClass.getClassName() + ":" + mg.getName() + "(" + mg.getArgumentTypes().mkString(",") + ")" + " " + "(%s)%s:%s(%s)";
  var methodCalls: ListBuffer[String] = ListBuffer[String]();
  //val method: ClassMethodInfo = new ClassMethodInfo(mg.getMethod().getName(), mg.getMethod().getArgumentTypes().toList.map(_.toString()));
  val method: ClassMethodInfo = classInfo.methods.getOrElse(
    ClassMethodInfo.getMethodKey(mg.getName, mg.getArgumentTypes().map(_.toString()).toList),
    new ClassMethodInfo(mg.getMethod().getName(), mg.getMethod().getArgumentTypes().toList.map(_.toString())))
 

  if (!classInfo.methods.get(method.getName()).isDefined) {
    classInfo.methods += (method.toString() -> method)

  }

  def start(): Map[String, ClassInfo] = {
    if (mg.isAbstract() || mg.isNative()) {
      return nameToClassMap;

    }

    mg.getInstructionList().getInstructions().foreach(f => {

      if (!visitInstruction(f))
        f.accept(this);
    })

    return nameToClassMap;
  }

  def visitInstruction(i: Instruction): Boolean = {
    val opcode = i.getOpcode();
    return ((InstructionConst.getInstruction(opcode) != null)
      && !(i.isInstanceOf[ConstantPushInstruction])
      && !(i.isInstanceOf[ReturnInstruction]));
  }

  def extracted(i: InvokeInstruction) = {
    val referenceType: ReferenceType = i.getReferenceType(cp);
    val calledClassName: String = i.getClassName(cp);

    val calledMethodName = i.getMethodName(cp);
      val calledClassInfo = nameToClassMap.getOrElse(calledClassName, new ClassInfo(calledClassName));

    if (nameToClassMap.keySet.count(_.equals(calledClassInfo.fullName())) > 0) {
      nameToClassMap += (calledClassInfo.fullName() -> calledClassInfo)

    }

    val calledMethod: ClassMethodInfo = calledClassInfo.methods.getOrElse(ClassMethodInfo.getMethodKey(
      calledMethodName,
      i.getArgumentTypes(cp).toList.map(_.toString())), new ClassMethodInfo(
      calledMethodName, i.getArgumentTypes(cp).toList.map(_.toString())))
    method.calls = (calledClassInfo, calledMethod) :: method.calls

    if (!calledClassInfo.methods.get(calledMethod.toString()).isDefined) {
      calledClassInfo.methods += (calledMethod.toString() -> calledMethod)

    }
  }
  override def visitINVOKEVIRTUAL(i: INVOKEVIRTUAL): Unit = {
    extracted(i)
  }

  def splitClassOnNameAndPackage(className: String): (String, String) = {
    val lastIndexOfDot = className.lastIndexOf(".")

    return (className.substring(Math.max(0, lastIndexOfDot)), className.substring(lastIndexOfDot + 1, className.length()))
  }

  override def visitINVOKEINTERFACE(i: INVOKEINTERFACE) {
    extracted(i)
  }

  override def visitINVOKESPECIAL(i: INVOKESPECIAL) = {
    extracted(i)
  }

  override def visitINVOKEDYNAMIC(i: INVOKEDYNAMIC) = {
    extracted(i)
  }

}