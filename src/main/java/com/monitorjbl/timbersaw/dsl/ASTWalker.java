package com.monitorjbl.timbersaw.dsl;

import com.monitorjbl.timbersaw.domain.Comparison;
import com.monitorjbl.timbersaw.domain.Comparison.CompareOperation;
import com.monitorjbl.timbersaw.dsl.TimberflowParser.ConditionContext;
import com.monitorjbl.timbersaw.dsl.TimberflowParser.MapContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ASTWalker extends TimberflowBaseListener {
  private static Logger log = LoggerFactory.getLogger(ASTWalker.class);

  private final CompilationContext compilationCtx;

  private DSL dsl = new DSL();
  private List<DSLBlockStatement> currentBlock;
  private DSLPlugin currentPlugin;
  private DSLBranch currentBranch;

  public ASTWalker(CompilationContext compilationCtx) {
    this.compilationCtx = compilationCtx;
  }

  @Override
  public void enterInputBlock(TimberflowParser.InputBlockContext ctx) {
    setCurrentBlock("inputs");
  }

  @Override
  public void exitInputBlock(TimberflowParser.InputBlockContext ctx) {
    dsl.setInputs(currentBlock.stream().map(s -> (DSLPlugin) s).collect(toList()));
  }

  @Override
  public void enterFilterBlock(TimberflowParser.FilterBlockContext ctx) {
    setCurrentBlock("filters");
  }

  @Override
  public void exitFilterBlock(TimberflowParser.FilterBlockContext ctx) {
    dsl.setFilters(currentBlock);
  }

  @Override
  public void enterOutputBlock(TimberflowParser.OutputBlockContext ctx) {
    setCurrentBlock("outputs");
  }

  @Override
  public void exitOutputBlock(TimberflowParser.OutputBlockContext ctx) {
    dsl.setOutputs(currentBlock);
  }

  @Override
  public void enterBranch(TimberflowParser.BranchContext ctx) {
    Comparison comparison = parseComparison(ctx.condition());
    currentBranch = new DSLBranch(comparison);
  }

  @Override
  public void exitBranch(TimberflowParser.BranchContext ctx) {
    currentBlock.add(currentBranch);
    currentBranch = null;
  }

  @Override
  public void enterPlugin(TimberflowParser.PluginContext ctx) {
    setCurrentPlugin(ctx);
  }

  @Override
  public void exitPlugin(TimberflowParser.PluginContext ctx) {
    if(currentBranch != null){
      currentBranch.getPlugins().add(currentPlugin);
    } else {
      currentBlock.add(currentPlugin);
    }
  }

  @Override
  public void enterConfiguration(TimberflowParser.ConfigurationContext ctx) {
    String key = ctx.Identifier().getText();
    if(ctx.Equals() != null) {
      if(ctx.StringLiteral() != null) {
        currentPlugin.getSingleProperties().put(key, stripStringLiteral(ctx.StringLiteral()));
      } else if(ctx.BooleanLiteral() != null) {
        currentPlugin.getSingleProperties().put(key, Boolean.valueOf(ctx.BooleanLiteral().getText()));
      } else if(ctx.IntegerLiteral() != null) {
        currentPlugin.getSingleProperties().put(key, Integer.valueOf(ctx.BooleanLiteral().getText()));
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
    currentBlock = new ArrayList<>();
  }

  private void setCurrentPlugin(TimberflowParser.PluginContext ctx) {
    String name = ctx.Identifier().getText();
    log.trace("Starting parse of plugin {}", name);
    currentPlugin = new DSLPlugin(name, compilationCtx.getPluginClass(name));
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

  private Comparison parseComparison(ConditionContext ctx) {
    List<TerminalNode> identifiers = ctx.Identifier();
    List<TerminalNode> literals = ctx.StringLiteral();
    List<TerminalNode> comparisons = ctx.Comparison();
    List<TerminalNode> booleans = ctx.BooleanOperator();

    Comparison first = null;
    for(int i = 0; i < identifiers.size(); i++) {
      System.out.println(identifiers.get(i).getText() + " " + literals.get(i));
      CompareOperation compareOperation = CompareOperation.fromString(comparisons.get(i).getText());
      if(first == null) {
        first = new Comparison(identifiers.get(i).getText(), compareOperation, stripStringLiteral(literals.get(i)));
      }
    }

    return first;
  }

  DSL getDsl() {
    return dsl;
  }
}
