package com.pietschy.gradle.gwt.task

import org.gradle.api.tasks.TaskAction

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Apr 28, 2009
 * Time: 10:55:05 AM
 * To change this template use File | Settings | File Templates.
 */
class GwtDevelopmentShell extends AbstractGwtShellTask
{
   private String developmentModeDirName = 'shell'
   private String developmentModeLibsDirName = 'shellLibs'
   def String startupUrl
   def ShellLogLevel logLevel


   @TaskAction
   def void runShell()
   {
      installAntLogger()
      
      printUsageHints()

      prepareShellLibsDir()

      prepareWebAppLibs()

      Timer t = new Timer()
      DeployWebAppFiles deployWebAppFiles = new DeployWebAppFiles(project, developementModeDir)
      deployWebAppFiles.run()

      // start copying the web dir over 30 seconds after start and then every 10 seconds.
      t.schedule(deployWebAppFiles, 30000, 10000)

      project.ant.java(failonerror: true, fork: true, classname: getDevModeClassName()) {
                                          
         classpath {

            shellLibs.each {
               pathElement(location: it)
            }
            
            getSourceDirs('main').each {
               pathElement(location: it)
            }

            gwtDependenciesWithoutDev.each {
               pathElement(location: it)
            }

            // libraries like Gin need the classes too
            pathElement(location: project.sourceSets.main.classesDir)
         }

         standardJvmArgs.each {
            jvmarg(value: it)
         }

         jvmarg(value: '-Xdebug')
         jvmarg(value: '-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005')

         standardArgs.each {
            arg(value: it.key)
            arg(value: it.value)
         }

         arg(value: '-war')
         arg(value: "${developementModeDir}")

         arg(value: '-logLevel')
         arg(value: "${logLevel ?: ShellLogLevel.INFO}")

         arg(value: '-startupUrl')
         arg(value: startupUrl)
         
         targetModules.each {
            arg(value: it)
         }
      }

      deployWebAppFiles.cancel()
   }


   private String getDevModeClassName()
   {
      return project.isGwt2() ? 'com.google.gwt.dev.DevMode' : 'com.google.gwt.dev.HostedMode'
   }

   private def printUsageHints()
   {
      println """
The GwtDevelopmentShell task does't recompile any classes in either this or the dependent
projects.  If you've changed any non GWT classes you'll need to recompile.
"""
   }

}