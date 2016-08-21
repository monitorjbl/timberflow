package com.monitorjbl.timbersaw.config;

import com.monitorjbl.timbersaw.domain.SingleStep;
import com.monitorjbl.timbersaw.filters.DropFilter;
import com.monitorjbl.timbersaw.filters.GrepFilter;
import com.monitorjbl.timbersaw.outputs.StdoutOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;

public class Configuration {
  private static List<SingleStep> steps = new ArrayList<>();

  //regexes courtesy of logstash (https://github.com/logstash-plugins/logstash-patterns-core)
  private static final Map<String, String> PATTERNS = new HashMap<String, String>() {{
    put("WORD", "\\b\\w+\\b");
    put("DATA", ".*?");
    put("GREEDYDATA", ".*");
    put("BASE10NUM", "(?<![0-9.+-])(?>[+-]?(?:(?:[0-9]+(?:\\.[0-9]+)?)|(?:\\.[0-9]+)))");
    put("NUMBER", "(?:" + get("BASE10NUM") + ")");
    put("IPV6", "((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?");
    put("IPV4", "(?<![0-9])(?:(?:[0-1]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])[.](?:[0-1]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])[.](?:[0-1]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])[.](?:[0-1]?[0-9]{1,2}|2[0-4][0-9]|25[0-5]))(?![0-9])");
    put("IP", "(?:" + get("IPV6") + "|" + get("IPV4") + ")");
    put("UNIXPATH", "(/([\\w_%!$@:.,+~-]+|\\\\.)*)+");
    put("WINPATH", "(?>[A-Za-z]+:|\\\\)(?:\\\\[^\\\\?*]*)+");
    put("PATH", "(?:" + get("UNIXPATH") + "|" + get("WINPATH") + ")");
  }};

  public static void applyConfig() {
    steps = newArrayList(
        new SingleStep<>(0, GrepFilter.class.getSimpleName(), generateGrepConfig("message", "%{DATA:timestamplocal}\\|%{NUMBER:duration}\\|%{WORD:requesttype}\\|%{IP:clientip}\\|%{DATA:username}\\|%{WORD:method}\\|%{PATH:resource}\\|%{DATA:protocol}\\|%{NUMBER:statuscode}\\|%{NUMBER:bytes}")),
        new SingleStep<>(1, DropFilter.class.getSimpleName(), newArrayList("message")),
        new SingleStep<>(2, StdoutOutput.class.getSimpleName(), "json"));
  }

  public static <E> SingleStep<E> step(Integer step) {
    return steps.get(step);
  }

  public static GrepConfig generateGrepConfig(String field, String pattern) {
    Pattern group = Pattern.compile("%\\{[^\\}]+\\}");
    Pattern var = Pattern.compile("%\\{\\s*(?<type>[^:]+):\\s*(?<field>[^\\|]+)\\s*\\}");

    String replaced = pattern;
    List<String> fields = new ArrayList<>();
    Matcher matcher = group.matcher(pattern);

    while(matcher.find()) {
      String extractor = matcher.group();
      Matcher m = var.matcher(extractor);
      if(m.matches()) {
        fields.add(m.group("field"));
        replaced = replaced.replace(extractor, String.format("(?<%s>%s)", m.group("field"), PATTERNS.get(m.group("type"))));
      }
    }

    return new GrepConfig(field, Pattern.compile(replaced), fields);
  }
}
