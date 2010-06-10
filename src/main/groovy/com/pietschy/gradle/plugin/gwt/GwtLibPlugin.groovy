package com.pietschy.gradle.plugin.gwt

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ProjectPluginsContainer
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.WarPlugin
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.tasks.bundling.Jar


/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Apr 29, 2009
 * Time: 4:55:02 PM
 * To change this template use File | Settings | File Templates.
 */
class GwtLibPlugin extends AbstractGwtPlugin {

   protected void initialisePluginsAndConfigurations(Project project)
   {
      project.usePlugin(JavaPlugin.class)

      // this is a cut and paste from the WarPlugin.
      Configuration provideCompileConfiguration = project.configurations.add(WarPlugin.PROVIDED_COMPILE_CONFIGURATION_NAME).setVisible(false).
              setDescription("Additional compile classpath for libraries that should not be part of the WAR archive.");

      Configuration provideRuntimeConfiguration = project.configurations.add(WarPlugin.PROVIDED_RUNTIME_CONFIGURATION_NAME).setVisible(false).
              extendsFrom(provideCompileConfiguration).
              setDescription("Additional runtime classpath for libraries that should not be part of the WAR archive.");

      project.configurations.getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME).extendsFrom(provideCompileConfiguration);
      project.configurations.getByName(JavaPlugin.RUNTIME_CONFIGURATION_NAME).extendsFrom(provideRuntimeConfiguration);

   }

   protected void configurePlugin(Project project)
   {
      // the jar producted by the project needs to include all of the java source.
      Jar jarTask = (Jar) project.tasks.getByName(JavaPlugin.JAR_TASK_NAME)
      project.println "configuring ${jarTask}"
      project.sourceSets.main.java.srcDirs.each { File dir ->
         jarTask.configure {
            fileSet(dir: dir) {
               include('**/*')
            }
         }
      }
   }
}