package com.monitorjbl.timberflow.dsl;

import com.monitorjbl.timberflow.domain.ActorConfig;
import com.monitorjbl.timberflow.domain.ConditionalStep;
import com.monitorjbl.timberflow.domain.SingleStep;
import com.monitorjbl.timberflow.domain.Step;
import com.monitorjbl.timberflow.dsl.TimberflowParser.CompilationUnitContext;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TimberflowCompiler {
  private final CompilationContext ctx;

  public TimberflowCompiler(CompilationContext ctx) {
    this.ctx = ctx;
  }

  public DSL compile(String src) {
    TimberflowLexer lexer = new TimberflowLexer(new ANTLRInputStream(src));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    TimberflowParser parser = new TimberflowParser(tokens);
    CompilationUnitContext context = parser.compilationUnit();

    ParseTreeWalker treeWalker = new ParseTreeWalker();
    ASTWalker astWalker = new ASTWalker(ctx);
    treeWalker.walk(astWalker, context);

    DSL dsl = astWalker.getDsl();
    dsl.setSteps(generateFlow(dsl));
    return dsl;
  }

  private List<Step> generateFlow(DSL dsl) {
    List<Step> steps = new ArrayList<>();

    AtomicInteger inputCounter = new AtomicInteger();
    dsl.inputPlugins().forEach(p -> handleInputPlugin(inputCounter.incrementAndGet(), p));

    AtomicInteger programCounter = new AtomicInteger();
    dsl.getFilters().forEach(s -> handleStatement("filter", steps, programCounter, s));
    dsl.getOutputs().forEach(s -> handleStatement("output", steps, programCounter, s));
    return steps;
  }

  private void handleInputPlugin(Integer counter, DSLPlugin plugin) {
    plugin.setActorConfig(ctx.generateActorConfig("input", plugin.getName(), plugin));
    plugin.setName("input-" + plugin.getName() + "-" + counter);
  }

  private void handleStatement(String block, List<Step> steps, AtomicInteger programCounter, DSLBlockStatement statement) {
    Integer pc = programCounter.getAndIncrement();
    if(statement instanceof DSLPlugin) {
      handlePlugin(block, steps, pc, (DSLPlugin) statement);
    } else if(statement instanceof DSLBranch) {
      DSLBranch branch = ((DSLBranch) statement);
      steps.add(new ConditionalStep(pc, branch.getComparison(), pc + branch.getPlugins().size()));
      branch.getPlugins().forEach(p -> handlePlugin(block, steps, programCounter.getAndIncrement(), p));
    }
  }

  @SuppressWarnings("unchecked")
  private void handlePlugin(String block, List<Step> steps, Integer pc, DSLPlugin plugin) {
    ActorConfig config = ctx.generateActorConfig(block, plugin.getName(), plugin);
    steps.add(new SingleStep(pc, plugin.getCls(), block + "-" + plugin.getName() + "-" + pc, config.getConfig()));
    plugin.setActorConfig(config);
    plugin.setName(block + "-" + plugin.getName() + "-" + pc);
  }

}
