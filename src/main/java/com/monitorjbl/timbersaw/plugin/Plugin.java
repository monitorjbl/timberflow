package com.monitorjbl.timbersaw.plugin;

import com.monitorjbl.timbersaw.config.ConfigParser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Plugin {
  String dslName();
  Class<? extends ConfigParser> configParser();
}
