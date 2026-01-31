<template>
  <div class="knowledge-graph">
    <svg ref="svg" width="1000" height="700"></svg>
  </div>
</template>

<script>
import * as d3 from 'd3';

export default {
  name: 'KnowledgeGraph',
  props: {
    data: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      selectedNode: null,
      simulation: null,
      allNodes: [],
      allLinks: []
    };
  },
  watch: {
    data: {
      handler(newData) {
        this.updateGraph(newData);
      },
      deep: true
    }
  },
  mounted() {
    this.initGraph(this.data);
  },
  methods: {
    initGraph(dataArray) {
      if (!dataArray || !Array.isArray(dataArray)) {
        console.error('知识图谱数据未加载');
        return;
      }

      // 清空之前的数据
      this.allNodes = [];
      this.allLinks = [];

      const svg = d3.select(this.$refs.svg);
      const width = +svg.attr('width');
      const height = +svg.attr('height');

      const rootRadius = Math.min(width, height) / 4 - 50; // 根节点的分布半径
      const childRadius = Math.min(width, height) / 2 - 50; // 子节点的分布半径

      this.simulation = d3.forceSimulation()
        .force('charge', d3.forceManyBody().strength(-100))
        .force('x', d3.forceX(width / 2).strength(0.05))
        .force('y', d3.forceY(height / 2).strength(0.05))
        .force('collide', d3.forceCollide(30).strength(0.7).iterations(2));

      const roots = dataArray.map(data => d3.hierarchy(data, d => d.children));

      // 初始化根节点位置
      roots.forEach((root, i) => {
        const angle = (i / roots.length) * 2 * Math.PI;
        root.x = width / 2 + rootRadius * Math.cos(angle);
        root.y = height / 2 + rootRadius * Math.sin(angle);
      });

      // 处理每个根节点及其子节点
      roots.forEach(root => {
        const nodes = root.descendants();
        const links = root.links();

        // 初始化子节点位置
        nodes.slice(1).forEach((d, i) => {
          const angle = (i / (nodes.length - 1)) * 2 * Math.PI;
          d.x = root.x + childRadius * Math.cos(angle) + Math.random() * 50 - 25; 
          d.y = root.y + childRadius * Math.sin(angle) + Math.random() * 50 - 25; 
        });

        this.allNodes.push(...nodes);
        this.allLinks.push(...links);
      });

      this.renderGraph(svg);
    },
    updateGraph(dataArray) {
      if (!dataArray || !Array.isArray(dataArray)) {
        console.error('知识图谱数据未加载');
        return;
      }

      // 清空之前的数据和SVG内容
      this.allNodes = [];
      this.allLinks = [];

      const svg = d3.select(this.$refs.svg);
      svg.selectAll('*').remove(); // 清空SVG中的所有元素
      
      const width = +svg.attr('width');
      const height = +svg.attr('height');

      const rootRadius = Math.min(width, height) / 4 - 50; 
      const childRadius = Math.min(width, height) / 2 - 50; 

      const roots = dataArray.map(data => d3.hierarchy(data, d => d.children));

      roots.forEach((root, i) => {
        const angle = (i / roots.length) * 2 * Math.PI;
        root.x = width / 2 + rootRadius * Math.cos(angle);
        root.y = height / 2 + rootRadius * Math.sin(angle);
      });

      roots.forEach(root => {
        const nodes = root.descendants();
        const links = root.links();

        nodes.slice(1).forEach((d, i) => {
          const angle = (i / (nodes.length - 1)) * 2 * Math.PI;
          d.x = root.x + childRadius * Math.cos(angle) + Math.random() * 50 - 25; 
          d.y = root.y + childRadius * Math.sin(angle) + Math.random() * 50 - 25; 
        });

        this.allNodes.push(...nodes);
        this.allLinks.push(...links);
      });

      this.renderGraph(svg);
    },
    renderGraph(svg) {
      const width = +svg.attr('width');
      const height = +svg.attr('height');

      // 更新模拟器节点
      this.simulation.nodes(this.allNodes);

      // 绘制链接
      const link = svg.selectAll('.link')
        .data(this.allLinks)
        .join('line')
        .attr('class', 'link')
        .attr('stroke', '#999')
        .attr('stroke-opacity', 0.6)
        .attr('stroke-width', 1)
        .attr('marker-end', 'url(#end)');

      // 绘制节点
      const node = svg.selectAll('.node')
        .data(this.allNodes, d => d.id)
        .join(
          enter => enter.append('g')
            .attr('class', 'node')
            .call(this.drag(this.simulation)),
          update => update,
          exit => exit.remove()
        );

      node.append('circle')
        .attr('r', 30)
        .attr('fill', d => d.depth === 0 ? '#ffcc00' : '#c6e48b')
        .attr('stroke', 'black')
        .attr('stroke-width', 2);

      node.append('text')
        .attr('dy', '.35em')
        .attr('text-anchor', 'middle')
        .text(d => d.data.name)
        .attr('font-size', '12px')
        .attr('fill', 'black')
        .attr('transform', 'translate(0, 5)');

      node.on('click', (event, d) => {
        this.selectedNode = d.data;
        this.$emit('showInfo', d.data);
      });

      // 添加箭头标记
      const markerWidth = 10;
      const markerHeight = 10;
      const refX = markerWidth;
      const refY = markerHeight / 2;

      svg.append('defs').selectAll('marker')
        .data(['end'])
        .join('marker')
        .attr('id', String)
        .attr('viewBox', `0 -5 ${markerWidth} ${markerHeight}`)
        .attr('refX', refX)
        .attr('refY', refY)
        .attr('markerWidth', markerWidth)
        .attr('markerHeight', markerHeight)
        .attr('orient', 'auto')
        .append('path')
        .attr('d', 'M0,-5L10,0L0,5')
        .attr('fill', '#999');

      this.simulation.on('tick', () => {
        svg.selectAll('.link')
          .attr('x1', d => d.source.x)
          .attr('y1', d => d.source.y)
          .attr('x2', d => d.target.x)
          .attr('y2', d => d.target.y);

        svg.selectAll('.node')
          .attr('transform', d => `translate(${d.x}, ${d.y})`);
      });
    },
    drag(simulation) {
      function dragstarted(event, d) {
        if (!event.active) simulation.alphaTarget(0.3).restart();
        d.fx = d.x;
        d.fy = d.y;
      }

      function dragged(event, d) {
        d.fx = event.x;
        d.fy = event.y;
      }

      function dragended(event, d) {
        if (!event.active) simulation.alphaTarget(0);
        d.fx = null;
        d.fy = null;
      }

      return d3.drag()
        .on('start', dragstarted)
        .on('drag', dragged)
        .on('end', dragended);
    }
  }
};
</script>

<style scoped>
.knowledge-graph {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.knowledge-graph svg {
  background-color: white;
}

.link {
  stroke: #999;
  stroke-opacity: 0.6;
  stroke-width: 1px;
  fill: none;
}

.node circle {
  fill: #ffcc00; 
  stroke-width: 1.5px;
}

.node text {
  font-family: Arial, sans-serif;
  font-size: 12px;
  pointer-events: none;
}
</style>