<template>
  <div class="container">
    <h2 style="color:black" class="custom-font">Access Device </h2>
    <div class="device-container">
      <div class="device-list">
        <div v-for="(device, index) in deviceData" :key="index" class="device-item">
          <div class="device-name" style="color:black">{{ device.name }}</div>
          <div class="device-count" style="color:black">{{ device.count }}</div>
          <div class="device-percentage" style="color:black">{{ device.percentage }}%</div>
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
  name: "DeviceStat",
  data() {
    return {
      deviceData: [
        { name: 'Computer', count: 6, percentage: 60 },
        { name: 'Mobile Device', count: 4, percentage: 40 },
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
      labels: this.deviceData.map(device => device.name),
      datasets: [{
        data: this.deviceData.map(device => device.percentage),
      }],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          labels: {
            color: 'black', // 设置图例文字颜色为黑色
              },
          },
      },
    },
  });
}

  },
};
</script>


<style scoped>
.container {
  width: 80%;
  margin: 0 auto;
}

.device-container {
  display: flex;
  justify-content: space-between;
  border: 1px solid #ddd;
  border-radius: 5px;
  padding: 20px;
}

.device-list {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.device-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  padding: 10px;
  border-bottom: 1px solid #ddd;
}

.device-name {
  flex: 1;
  text-align: left;
}

.device-count {
  flex: 1;
  text-align: center;
}

.device-percentage {
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

.custom-font {
  font-family: "STFangsong", "FangSong", serif; /* 华文仿宋 */
}
</style>
