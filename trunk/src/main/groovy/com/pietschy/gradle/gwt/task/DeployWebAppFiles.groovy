package com.pietschy.gradle.gwt.task

import org.gradle.api.Project

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Nov 2, 2009
 * Time: 3:43:13 PM
 * To change this template use File | Settings | File Templates.
 */
class DeployWebAppFiles extends TimerTask {

   private Project project
   private File webappRoot
   private boolean firstRun = true;

   def DeployWebAppFiles(Project project, File webappRoot)
   {
      this.project = project
      this.webappRoot = webappRoot;
   }

   public void run()
   {
      project.print 'Refreshing Web app files'
      // copy all files under src/main/webapp to the hosted mode directory
      project.ant.copy(todir: webappRoot) {
         fileset(dir: project.webAppDir, includes: '**/*')
      }
   }

   public boolean cancel()
   {
      project.println('Done')
      return super.cancel();
   }


}