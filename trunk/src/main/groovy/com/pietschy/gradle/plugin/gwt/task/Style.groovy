package com.pietschy.gradle.plugin.gwt.task
/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Nov 1, 2009
 * Time: 1:14:52 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Style {
   OBFUSCATED("OBF"), PRETTY("PRETTY"), DETAILED("DETAILED")

   private String name;

   def Style(String name)
   {
      this.name = name;
   }

   public String getName()
   {
      return name;
   }
}