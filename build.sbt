name := "staticparsing" 

version := "0.0.0" 

scalaVersion 	:="2.11.6"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.6.1" % "test"

// https://mvnrepository.com/artifact/org.apache.bcel/bcel
libraryDependencies += "org.apache.bcel" % "bcel" % "6.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" 

unmanagedBase := baseDirectory.value / "src/test/resources"



resolvers += "Spring Dependencies" at "https://repo.springsource.org/libs-milestone"
