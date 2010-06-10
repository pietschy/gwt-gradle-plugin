package com.pietschy.gradle.plugin.gwt

import org.gradle.api.Project

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Oct 31, 2009
 * Time: 2:59:35 PM
 * To change this template use File | Settings | File Templates.
 */
class GwtPluginConvention {

   private Project project

   boolean includeGwtServlet = true;
   String gwtTestSourceName = 'gwtTest'

   def GwtPluginConvention(Project project)
   {
      this.project = project;
   }

   def boolean isGwt1()
   {
      return project.gwtVersion.startsWith('1')
   }

   def boolean isGwt2()
   {
      return project.gwtVersion.startsWith('2')
   }

   def String getGwtPlatform()
   {
      def String name = (System.properties["os.name"] as String).toLowerCase()
      if (name.startsWith("windows"))
      {
         return "win"
      }
      else if (name.startsWith("mac os x"))
      {
         return "mac"
      }
      else // linux at this stage.
      {
         return "linux"
      }
   }

   public boolean isMacOs()
   {
      return getGwtPlatform().equals("mac")
   }

   public boolean is64Bit()
   {
      return System.getProperty('sun.arch.data.model').equals('64')
   }


}