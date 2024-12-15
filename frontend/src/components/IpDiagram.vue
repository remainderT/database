<template>
  <div class="container">
    <h2>High Frequency Access IP</h2>
    <div class="ip-list">
      <div class="ip-header">
        <div class="ip-address-header">IP Address</div>
        <div class="ip-count-header">IP Access Frequency</div>
      </div>
      <div v-if="sortedIPs.length > 0" class="ip-items">
        <div v-for="(ip, index) in sortedIPs" :key="index" class="ip-item">
          <div class="ip-address">{{ ip.address }}</div>
          <div class="ip-count">{{ ip.count }}</div>
        </div>
      </div>
      <div v-else class="no-data">No Access Data for the Selected Date</div>
    </div>
  </div>
</template>

<script>
export default {
  name: "IpDiagram",
  data() {
    return {
      ipData: [
        { address: '222.195.81.88', count: 4 },
        { address: '119.39.133.67', count: 3 },
        { address: '120.236.191.53', count: 3 },
        { address: '219.136.16.23', count: 2 },
        { address: '222.20.193.114', count: 2 },
      ],
    };
  },
  computed: {
    sortedIPs() {
      return this.ipData;
    },
  },
  created() {
    this.ipData.sort((a, b) => b.count - a.count);
  },
};
</script>

<style scoped>
.container {
  width: 80%;
  margin: 0 auto;
}

.ip-list {
  display: flex;
  flex-direction: column;
}

.ip-header {
  display: flex;
  justify-content: space-between;
  font-weight: bold;
  margin-bottom: 10px;
}

.ip-address-header {
  flex: 1;
  text-align: left;
}

.ip-count-header {
  flex: 1;
  text-align: right;
}

.ip-items {
  display: flex;
  flex-direction: column;
}

.ip-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.ip-address {
  flex: 1;
  text-align: left;
}

.ip-count {
  flex: 1;
  text-align: right;
}

.no-data {
  text-align: center;
  font-size: 1.2em;
  color: #999;
}
</style>
