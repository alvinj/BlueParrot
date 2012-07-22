package com.alvinalexander.blueparrot

import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

trait MacOSXApplicationInterface
{
  def doAboutAction
  def doPreferencesAction
  def doQuitAction
}

class MacOSXApplicationAdapter(handler: MacOSXApplicationInterface) extends ApplicationAdapter {

  override def handleQuit(e: ApplicationEvent) {
    handler.doQuitAction
  }

  override def handlePreferences(e: ApplicationEvent) {
    handler.doPreferencesAction
  }

  override def handleAbout(e: ApplicationEvent) {
    // tell the system we're handling this, so it won't display
    // the default system "about" dialog after ours is shown.
    e.setHandled(true)
    handler.doAboutAction
  }
}














