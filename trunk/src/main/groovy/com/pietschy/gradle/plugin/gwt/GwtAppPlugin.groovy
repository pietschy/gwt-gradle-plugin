package com.pietschy.gradle.plugin.gwt

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ProjectPluginsContainer
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.WarPlugin
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.ConfigurationHandler
import com.pietschy.gradle.plugin.gwt.task.CompileGwt
import org.gradle.api.Task
import org.gradle.api.tasks.bundling.War


/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Apr 29, 2009
 * Time: 4:55:02 PM
 * To change this template use File | Settings | File Templates.
 */
class GwtAppPlugin extends AbstractGwtPlugin {


   protected void initialisePluginsAndConfigurations(Project project)
   {
      project.usePlugin(WarPlugin.class)
   }

   protected void configurePlugin(Project project)
   {
      CompileGwt compileGwt = project.tasks.add(TASK_COMPILE_GWT, CompileGwt.class)

      War warTask = (War) project.tasks.getByName(WarPlugin.WAR_TASK_NAME)
      warTask.dependsOn(compileGwt)

      warTask.configure {
         fileSet(dir: compileGwt.destinationDir) {
            include('**/*')
         }
      }
   }

}