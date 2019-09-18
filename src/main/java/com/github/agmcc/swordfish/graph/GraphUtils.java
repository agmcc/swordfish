package com.github.agmcc.swordfish.graph;

import com.google.common.graph.Graph;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class GraphUtils {

  // TODO: Use Tarjan's algorithm
  public <N> boolean isCyclic(final Graph<N> graph) {
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

  public <N> Iterator<N> dfsIterator(final N root, final Graph<N> graph) {
    return new Iterator<N>() {

      private final Deque<N> stack = new ArrayDeque<>();
      private final Set<N> discovered = new HashSet<>();

      {
        stack.push(root);
      }

      @Override
      public boolean hasNext() {
        return !stack.isEmpty();
      }

      @Override
      public N next() {
        final N node = stack.pop();
        if (!discovered.contains(node)) {
          discovered.add(node);
          graph.successors(node).forEach(stack::push);
        }
        return node;
      }
    };
  }

  private <N> boolean dfs(final Graph<N> graph, final N node, final Map<N, SearchState> state) {
    state.put(node, SearchState.PROCESSING);

    for (final N successor : graph.successors(node)) {
      if (state.get(successor) == SearchState.PROCESSING) {
        return true;
      }

      if (state.get(successor) == SearchState.NOT_PROCESSED && dfs(graph, successor, state)) {
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
