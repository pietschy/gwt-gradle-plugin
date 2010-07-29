package com.pietschy.gradle.gwt.task

import org.gradle.api.tasks.TaskAction

import org.gradle.api.tasks.OutputDirectory

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Apr 28, 2009
 * Time: 10:55:05 AM
 * To change this template use File | Settings | File Templates.
 */
class CompileGwt extends AbstractGwtTask
{

   def String desitinationDirName = 'war';
  def boolean compileReport = false

   def CompileGwt()
   {
   }

   @TaskAction
   def void compile()
   {
      installAntLogger()
      
      project.ant.java(failonerror: true, fork: true, classname: 'com.google.gwt.dev.Compiler') {

         classpath {

            getSourceDirs('main').each {
               pathElement(location: it)
            }

            gwtDependencies.each {
               pathElement(location: it)
            }

            // libraries like Gin need the classes too
            pathElement(location: project.sourceSets.main.classesDir)
         }

         standardJvmArgs.each {
            jvmarg(value: it)
         }

         standardArgs.each {
            arg(value: it.key)
            arg(value: it.value)
         }

         if (compileReport)
         {
            arg(value: '-compileReport')
         }

         arg(value: '-war')
         arg(value: "${destinationDir}")

         targetModules.each {
            arg(value: it)
         }
      }
   }


   @OutputDirectory
   public File getDestinationDir()
   {
      return new File(destinationRoot, desitinationDirName);
   }


}