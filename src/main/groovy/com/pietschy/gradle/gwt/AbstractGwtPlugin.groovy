package com.pietschy.gradle.gwt

import org.gradle.api.Plugin
import org.gradle.api.artifacts.dsl.ConfigurationHandler
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.WarPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.ProjectPluginsContainer

import org.gradle.api.tasks.SourceSet

import com.pietschy.gradle.gwt.task.TestGwt
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

  public static final String GWT_TEST_COMPILE_CONFIGURATION_NAME = 'gwtTestCompile'
  public static final String GWT_TEST_RUNTIME_CONFIGURATION_NAME = 'gwtTestRuntime'

  public static final String TASK_COMPILE_GWT = "compileGwt"
  public static final String TASK_TEST_GWT = 'testGwt'

  public static final String SOURCE_GWT_TEST = 'gwtTest'


  public final void use(Project project, ProjectPluginsContainer projects)
  {
    initialisePluginsAndConfigurations(project)

    project.convention.plugins.gwt = new GwtPluginConvention(project)

    configurePlugin(project)
    installConfigurations(project)
    configureSourceSets(project)
    configureTestGwt(project)

    project.afterEvaluate {
      configureVersionDependencies(project)
    }

  }

  protected abstract void initialisePluginsAndConfigurations(Project project)

  protected abstract void configurePlugin(Project project)

  private def installConfigurations(Project project)
  {
    ConfigurationHandler configs = project.configurations
    Configuration gwtUser = configs.add(GWT_USER_CONFIGURATION_NAME).setVisible(false).setDescription("The gwt-dev jar for the current platform.");

    Configuration gwtDev = configs.add(GWT_DEV_CONFIGURATION_NAME).setVisible(false).setDescription("The gwt-dev jar for the current platform.");

    configs.add(GWT_DEV_MODE_NATIVE_LIBS_CONFIGURATION_NAME).setVisible(false).setDescription("The zip of GWT Dev library that includes the native libs for the current platform.");

    def gwtTestCompileConf = configs.add(GWT_TEST_COMPILE_CONFIGURATION_NAME).setVisible(false).setTransitive(false).setDescription("Dependencies for gwtTest compilation.").extendsFrom(gwtUser, gwtDev, configs.getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME));

    configs.add(GWT_TEST_RUNTIME_CONFIGURATION_NAME)
            .setVisible(false)
            .setTransitive(false)
            .setDescription("Dependencies for gwtTest runtime.")
            .extendsFrom(gwtTestCompileConf);

    configs.getByName(WarPlugin.PROVIDED_COMPILE_CONFIGURATION_NAME).extendsFrom(gwtUser, gwtDev)
  }

  private void configureSourceSets(Project project)
  {
    ConfigurationHandler configurations = project.getConfigurations()

    SourceSet main = project.sourceSets.findByName('main')
    SourceSet gwtTestSources = project.sourceSets.findByName(SOURCE_GWT_TEST)
    if (gwtTestSources == null)
    {
      gwtTestSources = project.sourceSets.add(SOURCE_GWT_TEST)
    }

    // Now we set up the default compile and runtime classpaths for the gwtTest source set.
    //
    // The compile tasks are automagically created by gradle based on the source set name and will
    // use the convention mapping of the source set to get their paths.
    //
    //
    ConventionMapping conventions = ((IConventionAware) gwtTestSources).getConventionMapping();

    conventions.map("compileClasspath", {Convention convention, IConventionAware conventionAwareObject ->
      return project.files(main.getClasses(),
                           configurations.getByName(GWT_TEST_COMPILE_CONFIGURATION_NAME));
    } as ConventionValue);

    conventions.map("runtimeClasspath", {Convention convention, IConventionAware conventionAwareObject ->
      return project.files(gwtTestSources.getClasses(),
                           main.getClasses(),
                           configurations.getByName(GWT_TEST_COMPILE_CONFIGURATION_NAME));
    } as ConventionValue)
  }

  void configureVersionDependencies(Project project)
  {
    DependencyHandler deps = project.getDependencies()

    String gwtVersion = project.gwtVersion;
    String gwtPlatform = project.gwtPlatform

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
    SourceSet gwtTestSources = project.sourceSets[SOURCE_GWT_TEST]

    TestGwt testGwt = project.tasks.add(TASK_TEST_GWT, TestGwt.class)
    testGwt.dependsOn(gwtTestSources.getClassesTaskName())

    project.tasks.getByName('check').dependsOn TASK_TEST_GWT
  }
}