package com.pietschy.gradle.gwt.task

import org.gradle.api.Project
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.OutputDirectory

import org.gradle.api.artifacts.Configuration

import com.pietschy.gradle.gwt.AbstractGwtPlugin
import org.gradle.api.artifacts.dsl.ConfigurationHandler

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Apr 28, 2009
 * Time: 10:55:05 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractGwtTask extends ConventionTask
{
   private String destinationRootName = 'gwt';
   private String genDirName = "gen"
   private String workDirName = "work"

   private Style style = null

   protected List<String> targetModules = []

   def AbstractGwtTask()
   {
   }


   public void modules(String[] modules)
   {
      modules.each {
         targetModules += it
      }
   }
   
   public Style getStyle()
   {
      return style;
   }
   
   public void setStyle(Style style)
   {
      this.style = style;
   }

   public File getDestinationRoot()
   {
      return new File(project.getBuildDir(), 'gwt');
   }

   @OutputDirectory
   public File getGenDir()
   {
      return new File(getDestinationRoot(), genDirName);
   }

   @OutputDirectory
   public File getWorkDir()
   {
      return new File(getDestinationRoot(), workDirName);
   }


   public List<String> getStandardJvmArgs()
   {
      List<String> args = ['-Xmx512M'];

      if (project.isGwt1() && project.isMacOs())
      {
         args += '-XstartOnFirstThread'

         if (project.is64Bit()) {
            args += '-d32'
         }
      }

      return args;
   }

   public Map<String,String> getStandardArgs()
   {
      Map<String, String> args = [:]
      args.put('-gen', genDir.getAbsolutePath())
      args.put('-workDir', workDir.getAbsolutePath())

      if (style != null)
      {
         args.put('-style', style.name);
      }

      return args;
   }

   public Set<Project> getDependsOnProjects()
   {
      return project.getDependsOnProjects()
   }

   public Set<File> getAllSourceDirs(String setName)
   {
      Set<File> dirs = getSourceDirs(setName)
      dirs += getDependsOnProjectsSourceDirs(setName)
      return dirs;
   }

   public Set<File> getSourceDirs(String setName)
   {
      Set<File> dirs = new HashSet<File>()
      dirs += project.sourceSets[setName].java.srcDirs
      dirs += project.sourceSets[setName].resources.srcDirs

      return dirs
   }

   public Set<File> getDependsOnProjectsSourceDirs(String setName)
   {
      Set<File> dirs = new HashSet<File>()

      dependsOnProjects.each { Project p ->
         dirs += p.sourceSets[setName].java.srcDirs
         dirs += p.sourceSets[setName].resources.srcDirs
      }

      return dirs
   }

   public Set<File> getGwtDependencies()
   {
      return getGwtDependencies('runtime', true, true)
   }

   public Set<File> getGwtDependenciesWithoutDev()
   {
      return getGwtDependencies('runtime', false, true)
   }

   public Set<File> getGwtDependenciesWithoutDevAndUser()
   {
      return getGwtDependencies('runtime', false, false)
   }

   public Set<File> getGwtTestDependenciesWithoutDev()
   {
      return getGwtDependencies(AbstractGwtPlugin.GWT_TEST_RUNTIME_CONFIGURATION_NAME, false, true)
   }

   private Set<File> getGwtDependencies(String conf, boolean includeDev, boolean includeUser)
   {
      Set<File> deps = new HashSet<File>()

      ConfigurationHandler configs = project.configurations

      deps += getDeepDependencies(configs.getByName(conf))

      if (!includeUser)
      {
         deps -= getDeepDependencies(configs.getByName(AbstractGwtPlugin.GWT_USER_CONFIGURATION_NAME))
      }

      if (!includeDev)
      {
         deps -= getDeepDependencies(configs.getByName(AbstractGwtPlugin.GWT_DEV_CONFIGURATION_NAME))
      }

      return deps
   }
  
   protected Set<File> getDeepDependencies(Configuration configuration)
   {
      HashSet<File> deps = new HashSet<File>()
      deps += configuration.resolve()
      for (Configuration c : configuration.extendsFrom) {
         deps += getDeepDependencies(c)
      }

      return deps
   }

   protected def installAntLogger()
   {
      project.ant.antProject.addBuildListener new AntLogger(project)
   }
   

}
 