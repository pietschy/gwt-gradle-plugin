package com.pietschy.gradle.plugin.gwt.task

import org.apache.tools.ant.types.FileSet
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.SourceSet


/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Apr 28, 2009
 * Time: 10:55:05 AM
 * To change this template use File | Settings | File Templates.
 */
class TestGwt extends AbstractGwtShellTask
{

   public String testOutputRootDirName = 'test'
   
   private String includes = '**/*TestSuite.class'
   private String excludes = '**/Abstract*.class'
   private String gwtArgs = "-web -out ${testOutputDir} -gen ${genDir}";

   def TestGwt()
   {
   }

   @TaskAction
   def void runTests()
   {
      installAntLogger()

      HashSet<String> testClasses = getTestClassNames()

      if (testClasses.isEmpty())
      {
         println "TestGwt: no tests to run"
      }
      else
      {

         println "Running TestGwt task"
         println "Using -Dgwt.args=\"${gwtArgs}\""

         prepareShellLibsDir()

         // todo: fix this to use test suites....
         testClasses.each { String className ->

            project.ant.java(failonerror: true, fork: true, classname: 'junit.textui.TestRunner') {

               classpath {

                  shellLibs.each {
                     pathElement(location: it)
                  }

                  getAllSourceDirs('main').each {
                     pathElement(location: it)
                  }

                  getAllSourceDirs(project.gwtTestSourceName).each {
                     pathElement(location: it)
                  }

                  pathElement(location: project.sourceSets.main.classesDir)
                  pathElement(location: project.sourceSets[project.gwtTestSourceName].classesDir)

                  gwtTestDependenciesWithoutDev.each {
                     pathElement(location: it)
                  }
               }

               standardJvmArgs.each {
                  jvmArg(value: it)
               }

               if (gwtArgs != null)
               {
                  jvmarg(line: "-Dgwt.args=\"${gwtArgs}\"")
               }

               arg(value: className)
            }
         }
      }
   }

   private HashSet<String> getTestClassNames()
   {
      HashSet<String> testClasses = new HashSet<String>()

      File testClassesDir = project.sourceSets[project.gwtTestSourceName].classesDir
      int prefixLength = testClassesDir.absolutePath.length() + 1
      int dotClassLength = '.class'.length();

      FileTree tree = project.fileTree(dir: testClassesDir)
      tree.include(includes)
      tree.exclude(excludes)

      tree.each {File file ->
         String path = file.absolutePath
         String temp = path.substring(prefixLength, path.length() - dotClassLength)
         testClasses.add(temp.replace(File.separatorChar, (char) '.'))
      }
      return testClasses
   }

   @OutputDirectory
   public File getTestOutputDir()
   {
      return new File(getDestinationRoot(), testOutputRootDirName)
   }

   public String getIncludes()
   {
      return includes;
   }

   public void setIncludes(String includes)
   {
      this.includes = includes;
   }

   public String getExcludes()
   {
      return excludes;
   }

   public void setExcludes(String excludes)
   {
      this.excludes = excludes;
   }

   public void setGwtArgs(String gwtArgs)
   {
      this.gwtArgs = gwtArgs;
   }

   public String getGwtArgs()
   {
      return gwtArgs;
   }

}