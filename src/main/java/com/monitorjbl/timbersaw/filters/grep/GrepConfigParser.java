package com.monitorjbl.timbersaw.filters.grep;

import com.monitorjbl.timbersaw.config.ConfigParser;
import com.monitorjbl.timbersaw.dsl.DSLPlugin;
import com.monitorjbl.timbersaw.dsl.KeyValue;
import com.monitorjbl.timbersaw.filters.grep.GrepConfig.Match;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrepConfigParser implements ConfigParser<GrepConfig> {
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

  @Override
  public GrepConfig generateConfig(DSLPlugin dslPlugin) {
    List<Match> matches = new ArrayList<>();
    List<KeyValue> kvList = dslPlugin.getMultiProperties().get("match");
    if(kvList != null) {
      kvList.forEach(m -> matches.add(generateMatch(m.getKey(), m.getValue())));
    }
    return new GrepConfig(matches);
  }

  public Match generateMatch(String field, String pattern) {
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

    return new Match(field, Pattern.compile(replaced), fields);
  }
}
