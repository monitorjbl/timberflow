package com.monitorjbl.timberflow.plugins.filter.grep;

import com.monitorjbl.timberflow.api.ConfigParser;
import com.monitorjbl.timberflow.api.PluginContent;
import com.monitorjbl.timberflow.api.PluginContent.KeyValue;
import com.monitorjbl.timberflow.plugins.filter.grep.GrepConfig.Match;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GrepConfigParser implements ConfigParser<GrepConfig> {
  private final static Pattern EXTRACTOR = Pattern.compile("%\\{[^\\}]+\\}");
  private final static Pattern EXTRACTOR_DETAIL = Pattern.compile("%\\{\\s*(?<type>[^:]+):\\s*(?<field>[^\\|]+)\\s*\\}");
  private final static Pattern NAMED_GROUP = Pattern.compile("\\(\\?<([a-zA-Z_]+)>(.*)\\)");
  //The Java spec doesn't allow regex named groups to contain regexes.
  //This value should be substituted in its place and substituted back
  //out when saving the field name
  public static final String UNDERSCORE = "TFLOWUSCORE";
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
  public GrepConfig generateConfig(PluginContent dslPlugin) {
    List<Match> matches = new ArrayList<>();
    List<KeyValue> kvList = dslPlugin.getMultiProperties().get("match");
    if(kvList != null) {
      kvList.forEach(m -> matches.add(generateMatch(m.getKey(), m.getValue())));
    }

    Integer instances = (Integer) dslPlugin.getSingleProperties().get("instances");
    return new GrepConfig(matches, instances == null ? 1 : instances);
  }

  public Match generateMatch(String field, String pattern) {
    String replaced = pattern;

    //replace all underscored named groups
    Matcher namedGroupMatch = NAMED_GROUP.matcher(replaced);
    while(namedGroupMatch.find()) {
      String namedGroup = namedGroupMatch.group();
      replaced = replaced.replace(namedGroup, namedGroup.replaceAll("_", UNDERSCORE));
    }

    //replace all extractors
    List<String> fields = new ArrayList<>();
    Matcher extractorMatch = EXTRACTOR.matcher(pattern);
    while(extractorMatch.find()) {
      String extractor = extractorMatch.group();
      Matcher extractorDetail = EXTRACTOR_DETAIL.matcher(extractor);
      if(extractorDetail.matches()) {
        String f = extractorDetail.group("field").replaceAll("_", UNDERSCORE);
        fields.add(f);
        replaced = replaced.replace(extractor, String.format("(?<%s>%s)", f, PATTERNS.get(extractorDetail.group("type"))));
      }
    }

    return new Match(field, Pattern.compile(replaced), fields);
  }
}
