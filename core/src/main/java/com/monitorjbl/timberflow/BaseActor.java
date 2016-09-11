package com.monitorjbl.timberflow;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import com.monitorjbl.timberflow.api.Config;
import com.monitorjbl.timberflow.domain.ActorConfig;
import com.monitorjbl.timberflow.monitor.StatsMessage;
import com.monitorjbl.timberflow.reflection.ConstructorInstantiationException;
import com.monitorjbl.timberflow.reflection.ObjectCreator;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.monitorjbl.timberflow.utils.ThreadUtils.sleep;
import static java.util.Arrays.stream;

public abstract class BaseActor extends UntypedActor {
  private final ActorConfig config;
  private final Thread throughputMonitor;
  private long messages = 0;

  protected BaseActor(ActorConfig config) {
    this.config = config;
    this.throughputMonitor = new Thread(() -> {
      long lastCheck = messages;
      long lastCheckTime = System.currentTimeMillis();
      ActorRef monitor = context().actorFor("/user/monitor");

      while(true) {
        sleep(1000);
        long throughput = ((messages - lastCheck) / (System.currentTimeMillis() - lastCheckTime));
        monitor.tell(new StatsMessage("input", self().path().name(), throughput), self());
        lastCheckTime = System.currentTimeMillis();
        lastCheck = messages;
      }
    });
    this.throughputMonitor.start();
  }

  protected <E> E instiatiatePlugin(Class<E> cls, ActorConfig config) {
    //if constructor args are specified, use them
    if(config.getConfig().getConstructorArgs().size() > 0) {
      List<Object> props = config.getConfig().getConstructorArgs();
      return ObjectCreator.newInstance(cls, props.toArray(new Object[props.size()]));
    }

    //else, try to use default constructor or constructor with Config parameter
    Constructor[] constructors = cls.getConstructors();
    Optional<Constructor> withConfig = stream(constructors)
        .filter(c -> c.getParameters().length == 1)
        .filter(c -> Config.class.isAssignableFrom(c.getParameters()[0].getType()))
        .findFirst();
    Optional<Constructor> withDefault = stream(constructors)
        .filter(c -> c.getParameters().length == 0)
        .findFirst();

    if(withConfig.isPresent()) {
      return ObjectCreator.newInstance(cls, new Object[]{config.getConfig()});
    } else if(withDefault.isPresent()) {
      return ObjectCreator.newInstance(cls, new Object[]{});
    } else {
      throw new ConstructorInstantiationException("No valid constructor found for " + cls.getCanonicalName());
    }
  }

  protected void handleMessage(Map<String, String> fields) {
    messages++;
    config.getAddedFields().forEach(fields::put);
  }
}
