package com.github.agmcc.swordfish.graph;

import com.google.common.graph.ValueGraph;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class GraphUtils {

  // TODO: Use Tarjan's algorithm
  public <N, V> boolean isCyclic(final ValueGraph<N, V> graph) {
    final Map<N, SearchState> state = new HashMap<>();
    graph.nodes().forEach(n -> state.put(n, SearchState.NOT_PROCESSED));

    for (final N node : graph.nodes()) {
      if (state.get(node) == SearchState.NOT_PROCESSED) {
        if (dfs(graph, node, state)) {
          return true;
        }
      }
    }
    return false;
  }

  private <N, V> boolean dfs(
      final ValueGraph<N, V> dependencyGraph, final N node, final Map<N, SearchState> state) {
    state.put(node, SearchState.PROCESSING);

    for (final N successor : dependencyGraph.successors(node)) {
      if (state.get(successor) == SearchState.PROCESSING) {
        return true;
      }

      if (state.get(successor) == SearchState.NOT_PROCESSED
          && dfs(dependencyGraph, successor, state)) {
        return true;
      }
    }

    state.put(node, SearchState.PROCESSED);
    return false;
  }

  private enum SearchState {
    NOT_PROCESSED,
    PROCESSING,
    PROCESSED
  }
}
