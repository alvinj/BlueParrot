package com.alvinalexander

import java.util.Calendar
import java.text.SimpleDateFormat
import scala.util.Random

package object blueparrot {
  
  def getRandomWaitTimeInMinutes(maxWaitTime: Int):Int = {
    val r = new Random(System.currentTimeMillis)
    r.nextInt(maxWaitTime)
  }
  
  // give me a max of 3, i'll return 0, 1, or 2
  def getRandomIntFromZeroUpToMaxExclusive(maxInt: Int):Int = {
    val r = new Random(System.currentTimeMillis)
    r.nextInt(maxInt)
  }
  

  // TODO move these date/time utilities to a GitHub project

  // returns true if minutes = 0, i.e., the current time is "on the hour"
  def onTheHour :Boolean = {
    val today = Calendar.getInstance().getTime()
    val minuteFormat = new SimpleDateFormat("mm")
    val currentMinuteAsString = minuteFormat.format(today)
    try {
      val currentMinute = Integer.parseInt(currentMinuteAsString)
      if (currentMinute % 60 == 0) return true
      else return false
    } catch {
      case _ => return false
    }
  }

  // returns the current hour as a string
  def getCurrentHour: String = {
    val today = Calendar.getInstance().getTime()
    val hourFormat = new SimpleDateFormat("hh")
    try {
      // returns something like "01" if i just return at this point, so cast it to
      // an int, then back to a string (or return the leading '0' if you prefer)
      val currentHour = Integer.parseInt(hourFormat.format(today))
      return "" + currentHour
    } catch {
      // TODO return Some/None/Whatever
      case _ => return "0"
    }
    return hourFormat.format(today)
  }
  
  // returns the current minute as a string
  def getCurrentMinute: String = {
    val today = Calendar.getInstance().getTime()
    val minuteFormat = new SimpleDateFormat("mm")
    // in this case, returning "01" is okay
    return minuteFormat.format(today)
  }
  
  // returns "AM" or "PM"
  def getAmOrPm: String = {
    val today = Calendar.getInstance().getTime()
    val amPmFormat = new SimpleDateFormat("a")
    return amPmFormat.format(today)
  }


}