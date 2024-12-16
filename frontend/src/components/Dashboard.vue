<template>
  <div class="dashboard" v-if="show" >
    <div class="print-icon" @click="exportToPDF">
      <svg
        class="h-6 w-6 text-blue-600"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <polyline points="6 9 6 2 18 2 18 9" />
        <path
          d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"
        />
        <rect x="6" y="14" width="12" height="8" />
      </svg>
    </div>

    <div v-if="exporting" class="progress-bar">
      <div class="progress" :style="{ width: progress + '%' }"></div>
    </div>
    <VisitedRecordsInADay class="dashboard-item" />
    <WeekDistribution class="dashboard-item" />
    <BrowserRecords class="dashboard-item" />
    <DeviceStatistics class="dashboard-item" />
    <NetworkStatistics class="dashboard-item" />
    <OsStat class="dashboard-item" />
    <ShowMap class="dashboard-item" />
  </div>
  <div v-else>
    <div class="form-container">
      <form action="">
        <div class="form-group">
          <label for="validDate" style="color: black" class="custom-font">StartDate</label>
          <input v-model="sevenDaysAgo" type="date" id="validDate" 
          class="custom-font"/>
        </div>
        <div class="form-group">
          <label for="validDate" style="color: black" class="custom-font">EndDate</label>
          <input v-model="today" type="date" id="validDate" class="custom-font" />
        </div>
      </form>
    </div>
    <button
      @click="initialize"
      class="custom-button"
      style="color:black"
    >
      show
    </button>
  </div>
</template>

<script>
import { defineComponent } from "vue";
import jsPDF from "jspdf";
import html2canvas from "html2canvas";
import WeekDistribution from "@/components/DaysStatistics.vue";
import DeviceStatistics from "@/components/DevicesStatistics.vue";
import ShowMap from "@/components/Map.vue";
import { inject, provide, ref } from "vue";
import VisitedRecordsInADay from "./visitedRecordsInADay.vue";
import BrowserRecords from "./browserRecords.vue";
import axios from "axios";
import NetworkStatistics from "./NetworkStatistics.vue";
import OsStat from "./osStat.vue";

export default defineComponent({
  name: "ShowDashboard",
  components: {
    VisitedRecordsInADay,
    WeekDistribution,
    BrowserRecords,
      DeviceStatistics,
      NetworkStatistics,
    OsStat,
    ShowMap,
  },
  data() {
    return {
      exporting: false,
      progress: 0,
    };
  },
  setup() {
    const show = ref(false);
    const headers = inject("headers");
    // 获取今天的日期
    const link = inject("link");
    const todayDate = new Date();
    const today = ref(
      `${todayDate.getFullYear()}-${String(todayDate.getMonth() + 1).padStart(
        2,
        "0"
      )}-${String(todayDate.getDate()).padStart(2, "0")}`
    );
    // 获取7天前的日期
    const sevenDaysAgoDate = new Date();
    sevenDaysAgoDate.setDate(sevenDaysAgoDate.getDate() - 6);
    const sevenDaysAgo = ref(
      `${sevenDaysAgoDate.getFullYear()}-${String(
        sevenDaysAgoDate.getMonth() + 1
      ).padStart(2, "0")}-${String(sevenDaysAgoDate.getDate()).padStart(
        2,
        "0"
      )}`
    );
    provide("today", today);
    provide("sevenDaysAgo", sevenDaysAgo);
    provide("link", link);
    provide("headers", headers);
    const response = ref(null);
    const initialize = async () => {
      response.value = await axios.get(`/api/short-link/stats`, {
        headers: headers, // 请求头信息
        params: {
          fullShortUrl: link.value,
          startDate: sevenDaysAgo.value,
          endDate: today.value,
        },
      });
      show.value = true;
    };
    console.log(response);
    provide("response", response);
    alert("Report Fetched");
    return { today, sevenDaysAgo, link, response, show, initialize };
  },
  methods: {
    async exportToPDF() {
      this.exporting = true;
      this.progress = 0;

      const pdf = new jsPDF({
        orientation: 'portrait',
        unit: 'mm',
        format: 'a4',
      });

      const dashboardItems = document.querySelectorAll('.dashboard-item');
      const totalItems = dashboardItems.length;

      const margin = 10;
      let yOffset = margin;

      for (let i = 0; i < totalItems; i++) {
        const item = dashboardItems[i];
        const canvas = await html2canvas(item, { scale: 2 });
        const imgData = canvas.toDataURL('image/jpeg');

        const imgWidth = 210 - 2 * margin;
        const imgHeight = (canvas.height * imgWidth) / canvas.width;

        // next page
        if (yOffset + imgHeight > 297 - margin) {
          pdf.addPage();
          yOffset = margin; // new page
        }

        pdf.addImage(imgData, 'JPEG', margin, yOffset, imgWidth, imgHeight);
        yOffset += imgHeight;

        // Update progress
        this.progress = ((i + 1) / totalItems) * 100;
      }

      const pdfBlob = pdf.output('blob');

      try {
        const handle = await window.showSaveFilePicker({
          suggestedName: 'Data Export.pdf', // 默认文件名
          types: [
            {
              description: 'PDF doc',
              accept: { 'application/pdf': ['.pdf'] },
            },
          ],
        });

        const writable = await handle.createWritable();
        await writable.write(pdfBlob);
        await writable.close();

        alert('PDF has been successfully exported.');

      } catch (error) {
        console.error('uhm error when exporting :', error);
      } finally {
        this.exporting = false;
        this.progress = 0;
      }
    },
  },
});
</script>

<style scoped>
.dashboard {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.dashboard-item {
  margin-bottom: 30px;
}


.print-icon {
  position: absolute;
  top: 10px;
  right: 10px;
  cursor: pointer;
  background-color: cornflowerblue;
  border-radius: 50%;
  padding: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.print-icon:hover {
  background-color: rgb(1, 1, 1);
}

svg {
  width: 24px;
  height: 24px;
}

.progress-bar {
  width: 100%;
  height: 10px;
  background-color: #e0e0e0;
  border-radius: 5px;
  margin-bottom: 20px;
  overflow: hidden;
}

.progress {
  height: 100%;
  background-color: cornflowerblue;
  transition: width 0.3s ease;
}


.gradient-button:hover {
  /* 鼠标悬停效果 */
  transform: scale(1.05); /* 放大 */
}

.gradient-button:active {
  /* 点击效果 */
  transform: scale(0.95); /* 稍微缩小 */
}
.form-container {
  max-height: 600px;
  max-width: 450px;
  margin: 0 auto;
  padding: 10px;
  border: 1px solid transparent;
  border-radius: 8px;
  background-color: transparent;
  font-family: Arial, sans-serif;
}

form {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.full-width {
  grid-column: span 2;
}

label {
  margin-bottom: 0.5rem;
  color: #555;
}

.custom-font {
  font-family: "STFangsong", "FangSong", serif; /* 华文仿宋 */
  font-size: 18px; /* 可根据需要调整字体大小 */
} 
.custom-button {
  width: 120px;
  background-color: transparent;
  color: white;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 1px;
  text-transform: uppercase;
  cursor: pointer;
  transition: background-color 0.3s ease, color 0.3s ease
}

.custom-button:hover {
  background-color: rgb(167, 167, 167);
  color: #333;
}
</style>