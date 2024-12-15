<template>
  <div class="container">
    <h2 style="color:black" class="custom-font">24 Hours Record</h2>
    <canvas ref="chartCanvas"></canvas>
  </div>
</template>

<script>
import Chart from "chart.js/auto";
import { inject } from "vue";

export default {
  name: "VisitRecordsInADay.vue",
  mounted() {
    this.createChart();
  },
  methods: {
    async createChart() {
      const link = inject("link");
          const temp = inject( "response" )._value;
          console.log(temp);
      const response = JSON.parse(JSON.stringify(temp));
      console.log(response);
          const days = [
        "0",
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "10",
        "11",
        "12",
        "13",
        "14",
        "15",
        "16",
        "17",
        "18",
        "19",
        "20",
        "21",
        "22",
        "23",
      ];
      const shortLinkData = [
        {
          name: link.value,
          distribution:
            response.data.data == null ? [] : response.data.data.hourStats,
        },
      ];
      const ctx = this.$refs.chartCanvas.getContext("2d");
      new Chart(ctx, {
        type: "line",
        data: {
          labels: days,
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