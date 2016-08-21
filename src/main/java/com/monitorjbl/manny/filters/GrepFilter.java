package com.monitorjbl.manny.filters;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import com.monitorjbl.manny.config.Configuration;
import com.monitorjbl.manny.config.GrepConfig;
import com.monitorjbl.manny.domain.LogLine;
import com.monitorjbl.manny.domain.SingleStep;

import java.util.regex.Matcher;

public class GrepFilter extends UntypedActor {

  @Override
  public void onReceive(Object message) throws Throwable {
    if(message instanceof LogLine) {
      LogLine logLine = (LogLine) message;
      extractFields((GrepConfig) Configuration.step(logLine.getCurrentStep()).getConfig(), logLine);

      SingleStep next = Configuration.step(logLine.getCurrentStep() + 1);
      ActorSelection nextActor = context().actorSelection("../" + next.getName());
      nextActor.tell(new LogLine(next.getNumber(), logLine.getFields()), context().system().deadLetters());
    } else {
      unhandled(message);
    }
  }

  private static void extractFields(GrepConfig config, LogLine logLine) {
    Matcher matcher = config.getRegex().matcher(logLine.getField(config.getField()));
    if(matcher.matches()) {
      config.getFields().forEach(field -> logLine.getFields().put(field, matcher.group(field)));
    }
  }


  public static void main(String[] args) {
    GrepConfig config = Configuration.generateGrepConfig("message", "%{DATA:timestamplocal}\\|%{NUMBER:duration}\\|%{WORD:requesttype}\\|%{IP:clientip}\\|%{DATA:username}\\|%{WORD:method}\\|%{PATH:resource}\\|%{DATA:protocol}\\|%{NUMBER:statuscode}\\|%{NUMBER:bytes}");
    Matcher m = config.getRegex().matcher("20160820030010|1|REQUEST|171.70.184.30|admin|DELETE|/blrbms1-npm-local/:properties|HTTP/1.1|204|0");

    if(m.matches()) {
      System.out.println(m.group("timestamplocal"));
    }
  }

}
