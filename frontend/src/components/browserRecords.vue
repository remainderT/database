<template>
  <div class="container">
    <h2 style="color: black;" class="custom-font">Browser Records</h2>
    <canvas ref="chartCanvas"></canvas>
  </div>
</template>

<script>
import Chart from "chart.js/auto";
import { inject } from "vue";

export default {
  name: "Browser_Records",
  mounted() {
    this.createChart();
  },
    methods: {
      async createChart() {
          const link = inject( "link" );
          const temp = inject( "response" )._value;
          console.log(temp);
      const response = JSON.parse(JSON.stringify(temp));
      console.log(response);
      const browsers = [];
      const shortLinkData = [
        {
          name: link.value,
          distribution: [],
        },
      ];
      if (response.data.data != null) {
        for (let i = 0; i < response.data.data.browserStats.length; i++) {
          browsers.push(response.data.data.browserStats[i].browser);
          shortLinkData[0].distribution.push(
            response.data.data.browserStats[i].cnt
          );
        }
      }

      const ctx = this.$refs.chartCanvas.getContext("2d");
      new Chart(ctx, {
        type: "bar",
        data: {
          labels: browsers,
          datasets: shortLinkData.map((link, index) => ({
            label: link.name,
            data: link.distribution,
            backgroundColor: `rgba(54, 162, 235, ${0.2 + index * 0.2})`,
            borderColor: `rgba(54, 162, 235, ${1 - index * 0.2})`,
            borderWidth: 1,
          })),
        },
        options: {
          scales: {
            y: {
              beginAtZero: true,
              ticks: {
                stepSize: 1, // 步长设置为 1，确保每个刻度间隔为1
                callback: function (value) {
                  return Number.isInteger(value) ? value : ""; // 只显示整数值，非整数不显示
                },
              },
            },
          },
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

.custom-font {
  font-family: "STFangsong", "FangSong", serif; /* 华文仿宋 */
}
</style>