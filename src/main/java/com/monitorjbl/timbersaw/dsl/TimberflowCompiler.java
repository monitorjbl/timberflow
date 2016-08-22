package com.monitorjbl.timbersaw.dsl;

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

  public List<SingleStep> compile(String src) {
    TimberflowLexer lexer = new TimberflowLexer(new ANTLRInputStream(src));
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    TimberflowParser parser = new TimberflowParser(tokens);
    CompilationUnitContext context = parser.compilationUnit();

    ParseTreeWalker treeWalker = new ParseTreeWalker();
    ASTWalker astWalker = new ASTWalker();
    treeWalker.walk(astWalker, context);

    return generateFlow(astWalker.getDsl());
  }

  private List<SingleStep> generateFlow(DSL dsl) {
    List<SingleStep> steps = new ArrayList<>();
    AtomicInteger pc = new AtomicInteger();

    dsl.getFilters().getPlugins().forEach(p -> steps.add(generateStep(pc.getAndIncrement(), p)));
    dsl.getOutputs().getPlugins().forEach(p -> steps.add(generateStep(pc.getAndIncrement(), p)));
    return steps;
  }

  @SuppressWarnings("unchecked")
  private SingleStep generateStep(int index, DSLPlugin plugin) {
    return new SingleStep(index, ctx.getPluginClass(plugin.getName()).getSimpleName(), ctx.generatePluginConfig(plugin.getName(), plugin));
  }
}
