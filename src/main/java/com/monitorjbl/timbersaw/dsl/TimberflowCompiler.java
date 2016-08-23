package com.monitorjbl.timbersaw.dsl;

import com.monitorjbl.timbersaw.config.Config;
import com.monitorjbl.timbersaw.domain.SingleStep;
import com.monitorjbl.timbersaw.dsl.TimberflowParser.CompilationUnitContext;
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

  private List<SingleStep> generateFlow(DSL dsl) {
    List<SingleStep> steps = new ArrayList<>();

    AtomicInteger inputCounter = new AtomicInteger();
    dsl.getInputs().getPlugins().forEach(p -> handleInputPlugin(inputCounter.incrementAndGet(), p));

    AtomicInteger programCounter = new AtomicInteger();
    dsl.getFilters().getPlugins().forEach(p -> handlePlugin(steps, programCounter.getAndIncrement(), p));
    dsl.getOutputs().getPlugins().forEach(p -> handlePlugin(steps, programCounter.getAndIncrement(), p));
    return steps;
  }

  private void handleInputPlugin(Integer counter, DSLPlugin plugin) {
    plugin.setConfig(ctx.generatePluginConfig(plugin.getName(), plugin));
    plugin.setName(plugin.getName() + "-" + counter);
  }

  @SuppressWarnings("unchecked")
  private void handlePlugin(List<SingleStep> steps, Integer pc, DSLPlugin plugin) {
    Config config = ctx.generatePluginConfig(plugin.getName(), plugin);
    steps.add(new SingleStep(pc, plugin.getName() + "-" + pc, config));
    plugin.setConfig(config);
    plugin.setName(plugin.getName() + "-" + pc);
  }

}
