<template>
  <div class="container">
    <h2>Visitor Network</h2>
    <div class="network-container">
      <div class="network-list">
        <div v-for="(network, index) in networkData" :key="index" class="network-item">
          <div class="network-name">{{ network.name }}</div>
          <div class="network-count">{{ network.count }}</div>
          <div class="network-percentage">{{ network.percentage }}%</div>
        </div>
      </div>
      <div class="chart-container">
        <canvas ref="chartCanvas"></canvas>
      </div>
    </div>
  </div>
</template>

<script>
import Chart from 'chart.js/auto';

export default {
  name: "VisitorNetworkStatistics",
  data() {
    return {
      networkData: [
        { name: 'WiFi', count: 1, percentage: 33.3 },
        { name: ' Mobile Data', count: 2, percentage: 66.6 },
      ],
    };
  },
  mounted() {
    this.createChart();
  },
  methods: {
    createChart() {
      const ctx = this.$refs.chartCanvas.getContext('2d');
      new Chart(ctx, {
        type: 'pie',
        data: {
          labels: this.networkData.map(network => network.name),
          datasets: [{
            data: this.networkData.map(network => network.percentage),
            backgroundColor: ['#36A2EB', '#FF6384'],
            borderWidth: 1,
          }],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
        },
      });
    },
  },
};
</script>

<style scoped>
.container {
  width: 80%;
  margin: 0 auto;
}

.network-container {
  display: flex;
  justify-content: space-between;
  border: 1px solid #ddd;
  border-radius: 5px;
  padding: 20px;
}

.network-list {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.network-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  padding: 10px;
  border-bottom: 1px solid #ddd;
}

.network-name {
  flex: 1;
  text-align: left;
}

.network-count {
  flex: 1;
  text-align: center;
}

.network-percentage {
  flex: 1;
  text-align: right;
}

.total {
  text-align: center;
  font-size: 1.2em;
  font-weight: bold;
  margin-top: 20px;
}

.chart-container {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}
</style>