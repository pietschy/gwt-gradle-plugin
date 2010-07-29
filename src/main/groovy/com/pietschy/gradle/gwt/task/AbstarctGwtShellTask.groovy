package com.pietschy.gradle.gwt.task

import org.gradle.util.GFileUtils

import com.pietschy.gradle.gwt.GwtAppPlugin
import org.gradle.api.artifacts.Configuration

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Apr 28, 2009
 * Time: 10:55:05 AM
 * To change this template use File | Settings | File Templates.
 */
class AbstractGwtShellTask extends AbstractGwtTask
{
   private String developmentModeDirName = 'shell'
   private String developmentModeLibsDirName = 'shellLibs'
   def ShellLogLevel logLevel


   protected def prepareWebAppLibs()
   {
      // now copy the libs over.. this sadly includes servlet-api.jar and other
      // attrocities so I should probably fix that at some stage.
      File webinfLibs = new File(developementModeDir, "WEB-INF/lib")
      webinfLibs.mkdirs()
      gwtDependenciesWithoutDevAndUser.each {File file ->
         GFileUtils.copyFileToDirectory(file, webinfLibs)
      }
   }

   public File getDevelopementModeDir()
   {
      return new File(getDestinationRoot(), developmentModeDirName);
   }

   public Set<File> getShellLibs()
   {
      // this ensures the gwt-dev.jar is on the classpath
      // with it's native libs in the same directory
      HashSet<File> jars = new HashSet<File>()
      shellLibsDir.eachFile { File f ->
         if (f.name ==~ /.*\.jar/)
         {
            jars.add(f);
         }
      }

      return jars;
   }

   public File getShellLibsDir()
   {
      return new File(getDestinationRoot(), developmentModeLibsDirName);
   }

   protected void prepareShellLibsDir()
   {
      // need to prepare the libs from the gwt configuration.
      // The config contains a jar and a zip file containing dlls.
      // the all need to be in the same directory to find each other so
      // we put them in the hostedModeLibsDir and add this to the classpath
      // of our java instance.
      if (!shellLibsDir.exists())
      {
         project.println "Extracting dev mode libs to: ${shellLibsDir}"
         shellLibsDir.mkdirs()
         project.configurations.getByName(GwtAppPlugin.GWT_DEV_CONFIGURATION_NAME).resolve().each { File f ->
            GFileUtils.copyFileToDirectory(f, shellLibsDir)
         }

         if (!project.isGwt2())
         {
            Configuration conf = project.configurations.getByName(GwtAppPlugin.GWT_DEV_MODE_NATIVE_LIBS_CONFIGURATION_NAME);
            getDeepDependencies(conf).each { File f ->
               project.ant.unzip(src: f, dest: shellLibsDir)
            }
         }
      }
   }

}