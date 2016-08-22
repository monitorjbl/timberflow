package com.monitorjbl.timbersaw.dsl;

import com.monitorjbl.timbersaw.dsl.TimberflowParser.MapContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ASTWalker extends TimberflowBaseListener {
  private static Logger log = LoggerFactory.getLogger(ASTWalker.class);
  private DSL dsl = new DSL();
  private DSLBlock currentBlock;
  private DSLPlugin currentPlugin;

  @Override
  public void enterInputBlock(TimberflowParser.InputBlockContext ctx) { setCurrentBlock("inputs"); }

  @Override
  public void exitInputBlock(TimberflowParser.InputBlockContext ctx) { dsl.setInputs(currentBlock); }

  @Override
  public void enterFilterBlock(TimberflowParser.FilterBlockContext ctx) { setCurrentBlock("filters"); }

  @Override
  public void exitFilterBlock(TimberflowParser.FilterBlockContext ctx) { dsl.setFilters(currentBlock); }

  @Override
  public void enterOutputBlock(TimberflowParser.OutputBlockContext ctx) { setCurrentBlock("outputs"); }

  @Override
  public void exitOutputBlock(TimberflowParser.OutputBlockContext ctx) { dsl.setOutputs(currentBlock); }

  @Override
  public void enterPlugin(TimberflowParser.PluginContext ctx) { setCurrentPlugin(ctx); }

  @Override
  public void exitPlugin(TimberflowParser.PluginContext ctx) {
    currentBlock.getPlugins().add(currentPlugin);
  }

  @Override
  public void enterConfiguration(TimberflowParser.ConfigurationContext ctx) {
    String key = ctx.Identifier().getText();
    if(ctx.Equals() != null) {
      if(ctx.StringLiteral() != null) {
        currentPlugin.getSingleProperties().put(key, stripStringLiteral(ctx.StringLiteral()));
      } else if(ctx.map() != null) {
        currentPlugin.getSingleProperties().put(key, ctx.map());
      }
    } else if(ctx.map() != null) {
      if(!currentPlugin.getMultiProperties().containsKey(key)) {
        currentPlugin.getMultiProperties().put(key, new ArrayList<>());
      }
      List<KeyValue> currentList = currentPlugin.getMultiProperties().get(key);
      parseMap(ctx.map()).forEach(currentList::add);
    }
  }

  private void setCurrentBlock(String name) {
    log.trace("Starting parse of block {}", name);
    currentBlock = new DSLBlock(name);
  }

  private void setCurrentPlugin(TimberflowParser.PluginContext ctx) {
    log.trace("Starting parse of plugin {}", ctx.Identifier().getText());
    currentPlugin = new DSLPlugin(ctx.Identifier().getText());
  }

  private String stripStringLiteral(TerminalNode node) {
    return node.getText().substring(1, node.getText().length() - 1);
  }

  private List<KeyValue> parseMap(MapContext ctx) {
    List<TerminalNode> nodes = ctx.StringLiteral();
    List<KeyValue> pairs = new ArrayList<>();
    for(int i = 0; i < nodes.size(); i = i + 2) {
      pairs.add(new KeyValue(stripStringLiteral(nodes.get(i)), stripStringLiteral(nodes.get(i + 1))));
    }
    return pairs;
  }

  DSL getDsl() {
    return dsl;
  }
}
