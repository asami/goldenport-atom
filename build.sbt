organization := "org.goldenport"

name := "goldenport-atom"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.1"

scalacOptions += "-deprecation"

scalacOptions += "-unchecked"

resolvers += "Asami Maven Repository" at "http://www.asamioffice.com/maven"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "6.0.3"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.6.1" % "test"

libraryDependencies += "junit" % "junit" % "4.8" % "test"

libraryDependencies += "org.goldenport" %% "scalatestlib" % "0.1.0" % "test"

// libraryDependencies += "com.googlecode.json-simple" % "json-simple" % "1.1"

//
publishTo := Some(Resolver.file("asamioffice", file("target/maven-repository")))