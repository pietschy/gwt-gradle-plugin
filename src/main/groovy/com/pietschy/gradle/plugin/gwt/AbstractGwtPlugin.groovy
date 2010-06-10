package com.pietschy.gradle.plugin.gwt

import org.gradle.api.Plugin
import org.gradle.api.artifacts.dsl.ConfigurationHandler
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.WarPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.ProjectPluginsContainer
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.Compile
import com.pietschy.gradle.plugin.gwt.task.TestGwt
import org.gradle.api.internal.IConventionAware
import org.gradle.api.tasks.ConventionValue
import org.gradle.api.internal.ConventionMapping
import org.gradle.api.plugins.Convention

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Nov 1, 2009
 * Time: 12:28:49 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractGwtPlugin implements Plugin
{

   public static final String GWT_DEV_MODE_NATIVE_LIBS_CONFIGURATION_NAME = 'gwtDevelopmentModeLibs'
   public static final String GWT_USER_CONFIGURATION_NAME = 'gwtUser'
   public static final String GWT_DEV_CONFIGURATION_NAME = 'gwtDev'

   public static final String TASK_COMPILE_GWT = "compileGwt"
   public static final String TASK_TEST_GWT = 'testGwt'


   public final void use(Project project, ProjectPluginsContainer projects)
   {
      initialisePluginsAndConfigurations(project)

      project.convention.plugins.gwt = new GwtPluginConvention(project)

      configurePlugin(project)

      project.afterEvaluate {
         configureVersionDependencies(project)
         configureTestGwt(project)
      }

   }

   protected abstract void initialisePluginsAndConfigurations(Project project)

   protected abstract void configurePlugin(Project project)

   void configureVersionDependencies(Project project)
   {
      ConfigurationHandler configs = project.configurations
      DependencyHandler deps = project.getDependencies()

      String gwtVersion = project.gwtVersion;
      String gwtPlatform = project.gwtPlatform

      Configuration gwtUser = configs.add(GWT_USER_CONFIGURATION_NAME).setVisible(false).setDescription("The gwt-dev jar for the current platform.");

      Configuration gwtDev = configs.add(GWT_DEV_CONFIGURATION_NAME).setVisible(false).setDescription("The gwt-dev jar for the current platform.");

      configs.add(GWT_DEV_MODE_NATIVE_LIBS_CONFIGURATION_NAME).setVisible(false).setDescription("The zip of GWT Dev library that includes the native libs for the current platform.");


      configs.getByName(WarPlugin.PROVIDED_COMPILE_CONFIGURATION_NAME).extendsFrom(gwtUser, gwtDev)

      // the gwt user library.
      deps.add(GWT_USER_CONFIGURATION_NAME, "com.google.gwt:gwt-user:${gwtVersion}")

      if (project.isGwt2())
      {
         // the dev library is required to compile any deferred binding code
         deps.add(GWT_DEV_CONFIGURATION_NAME, "com.google.gwt:gwt-dev:${gwtVersion}")
      }
      else
      {
         // the dev library is required to compile any deferred binding code
         deps.add(GWT_DEV_CONFIGURATION_NAME, "com.google.gwt:gwt-dev:${gwtVersion}:${gwtPlatform}@jar")
         deps.add(GWT_DEV_MODE_NATIVE_LIBS_CONFIGURATION_NAME, "com.google.gwt:gwt-dev:${gwtVersion}:${gwtPlatform}-libs@zip")
      }


      if (project.includeGwtServlet)
      {
         deps.add(JavaPlugin.COMPILE_CONFIGURATION_NAME, "com.google.gwt:gwt-servlet:${gwtVersion}")
      }

   }

   protected void configureTestGwt(Project project)
   {
      SourceSet main = project.sourceSets.main
      SourceSet gwtTestSources = project.sourceSets.add(project.gwtTestSourceName)

      // hook up this source set with the configurations it needs to be build.  The compile
      // tasks are automagically created by gradle based on the source set name.  In this case
      // we're just running of the standard testCompile and testRuntime configs.
      ConventionMapping conventions = ((IConventionAware) gwtTestSources).getConventionMapping();

      conventions.map("compileClasspath",
                      {Convention convention, IConventionAware conventionAwareObject ->
                      return project.files(main.getClasses(), project.getConfigurations().getByName(JavaPlugin.TEST_COMPILE_CONFIGURATION_NAME));
                      } as ConventionValue);

      conventions.map("runtimeClasspath",
                      {Convention convention, IConventionAware conventionAwareObject ->
                      return project.files(gwtTestSources.getClasses(), main.getClasses(), project.getConfigurations().getByName(JavaPlugin.TEST_RUNTIME_CONFIGURATION_NAME));
                      } as ConventionValue);

      TestGwt testGwt = project.tasks.add(TASK_TEST_GWT, TestGwt.class)
      testGwt.dependsOn(gwtTestSources.getClassesTaskName())

      project.tasks.getByName('check').dependsOn TASK_TEST_GWT
   }
}