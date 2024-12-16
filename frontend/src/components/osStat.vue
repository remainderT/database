<template>
  <div class="container">
    <h2 style="color: black" class="custom-font">OS Statistics</h2>
    <div class="os-container">
      <div class="os-list">
        <div v-for="(os, index) in osData" :key="index" class="os-item">
          <div class="os-name" style="color:black">{{ os.name }}</div>
          <div class="os-count" style="color:black">{{ os.count }}</div>
          <div class="os-percentage" style="color:black">{{ os.percentage*100 }}%</div>
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
import { inject } from 'vue';

export default {
  name: "OSStatistics",
  data() {
      return {
        osData:[]
    };
  },
  mounted() {
    this.createChart();
  },
  methods: {
    createChart() {
          const ctx = this.$refs.chartCanvas.getContext( '2d' );
          this.osData = [];
          const temp = inject("response")._value;
      console.log(temp);
      const response = JSON.parse(JSON.stringify(temp));
          console.log( response );
          if ( response.data.data != null ) {
        let sum = 0;
        for (let i = 0; i < response.data.data.osStats.length; i++) {
            sum += response.data.data.osStats[i].cnt;
        }
        for (let i = 0; i < response.data.data.osStats.length; i++) {
            this.osData .push( {
                name: response.data.data.osStats[ i ].os,
                percentage: response.data.data.osStats[ i ].cnt / sum,
                count: response.data.data.osStats[ i ].cnt
            } );
        }
      }
      new Chart(ctx, {
        type: 'pie',
        data: {
          labels: this.osData.map(OS => OS.name),
          datasets: [{
            data: this.osData.map(OS => OS.percentage),
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

.os-container {
  display: flex;
  justify-content: space-between;
  border: 1px solid #ddd;
  border-radius: 5px;
  padding: 20px;
}

.os-list {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.os-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  padding: 10px;
  border-bottom: 1px solid #ddd;
}

.os-name {
  flex: 1;
  text-align: left;
}

.os-count {
  flex: 1;
  text-align: center;
}

.os-percentage {
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