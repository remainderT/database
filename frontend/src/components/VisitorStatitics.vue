<template>
  <div class="container">
    <h2>Visitor Type</h2>
    <div class="visitor-container">
      <div class="visitor-list">
        <div v-for="(visitor, index) in visitorData" :key="index" class="visitor-item">
          <div class="visitor-name">{{ visitor.name }}</div>
          <div class="visitor-count">{{ visitor.count }}</div>
          <div class="visitor-percentage">{{ visitor.percentage }}%</div>
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
  name: "VisitorTypeStatistics",
  data() {
    return {
      visitorData: [
        { name: 'New Visitor', count: 5, percentage: 62.5 },
        { name: 'Old Visitor', count: 3, percentage: 37.5 },
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
          labels: this.visitorData.map(visitor => visitor.name),
          datasets: [{
            data: this.visitorData.map(visitor => visitor.count),
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

.visitor-container {
  display: flex;
  justify-content: space-between;
  border: 1px solid #ddd;
  border-radius: 5px;
  padding: 20px;
}

.visitor-list {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.visitor-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  padding: 10px;
  border-bottom: 1px solid #ddd;
}

.visitor-name {
  flex: 1;
  text-align: left;
}

.visitor-count {
  flex: 1;
  text-align: center;
}

.visitor-percentage {
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