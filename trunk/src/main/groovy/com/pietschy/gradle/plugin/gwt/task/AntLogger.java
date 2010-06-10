package com.pietschy.gradle.plugin.gwt.task;

import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.BuildEvent;
import org.apache.ivy.util.Message;
import org.gradle.api.Project;

import java.io.PrintStream;

/**
 * Created by IntelliJ IDEA.
 * User: andrew
 * Date: Nov 2, 2009
 * Time: 3:22:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class AntLogger implements BuildLogger
{
   private Project project;

   public AntLogger(Project p)
   {
      project = p;
   }

   public void setMessageOutputLevel(int level)
   {
      // ignore
   }

   public void setOutputPrintStream(PrintStream output)
   {
      // ignore
   }

   public void setEmacsMode(boolean emacsMode)
   {
      // ignore
   }

   public void setErrorPrintStream(PrintStream err)
   {
      // ignore
   }

   public void buildStarted(BuildEvent event)
   {
      // ignore
   }

   public void buildFinished(BuildEvent event)
   {
      // ignore
   }

   public void targetStarted(BuildEvent event)
   {
      // ignore
   }

   public void targetFinished(BuildEvent event)
   {
      // ignore
   }

   public void taskStarted(BuildEvent event)
   {
      // ignore
   }

   public void taskFinished(BuildEvent event)
   {
      // ignore
   }

   public void messageLogged(BuildEvent event)
   {
      StringBuffer message = new StringBuffer();
      if (event.getTask() != null)
      {
         message.append(event.getTask().getTaskName()).append(" ");
      }

      message.append(event.getMessage()).append("\n");

      if (event.getPriority() <= Message.MSG_INFO)
      {
         if (event.getException() != null)
         {
            project.getLogger().progress(message.toString(), event.getException());
         }
         else
         {
            project.getLogger().progress(message.toString());
         }
      }

   }
}

