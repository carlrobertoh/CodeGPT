package ee.carlrobert.codegpt.toolwindow.chat;

import static org.apache.commons.text.StringEscapeUtils.escapeEcmaScript;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.data.DataHolder;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class CodeBlockNodeRenderer implements NodeRenderer {

  @Override
  public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
    return Set.of(new NodeRenderingHandler<>(FencedCodeBlock.class, this::render));
  }

  private void render(FencedCodeBlock node, NodeRendererContext context, HtmlWriter html) {
    var code = String.join("", node.getContentLines());

    html.attr("class", "code-header")
        .withAttr().tag("div")
        .attr("class", "lang")
        .withAttr().tag("span")
        .text(node.getInfo())
        .tag("/span")
        .attr("class", "actions")
        .withAttr().tag("div")
        .attr("class", "copy-button")
        .attr("onclick", String.format("window.JavaPanelBridge.copyCode('%s')", escapeEcmaScript(code)))
        .withAttr().tag("button")
        .text("Copy")
        .tag("/button");
    html.attr("class", "replace-button")
        .attr("disabled", "")
        .attr("title", "Please wait until the response has been generated")
        .attr("onclick", String.format("window.JavaPanelBridge.replaceCode('%s')", escapeEcmaScript(code)))
        .withAttr().tag("button")
        .text("Replace Selection")
        .tag("/button");
    html.tag("/div")
        .tag("/div")
        .attr("class", "code-wrapper")
        .withAttr().tag("div");
    context.delegateRender();
    html.tag("/div");
  }

  public static class Factory implements NodeRendererFactory {

    @NotNull
    @Override
    public NodeRenderer apply(@NotNull DataHolder options) {
      return new CodeBlockNodeRenderer();
    }
  }
}
