<template>
  <div class="app">
    <nav class="navbar navbar-expand navbar-light fixed-top">
      <div class="container">
        <div class="collapse navbar-collapse">
          <ul class="navbar-nav ml-auto">
            <li class="nav-item">
              <button @click="bin" class="nav-link">Bin</button>
            </li>
            <li class="nav-item">
              <button @click="home" class="nav-link">Home</button>
            </li>
            <li class="nav-item">
              <button @click="logout" class="nav-link">Logout</button>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <!-- Main Content -->
    <div class="home-page">
      <div class="background-animation"></div>
      <div class="content">
        <div class="scroll-container">
          <table class="table table-striped table-hover shadow-sm">
            <thead class="thead-dark">
              <tr>
                <th scope="col" class="custom-font">Gid</th>
                <th scope="col" class="custom-font">Info</th>
                <th scope="col" class="custom-font">FullShortUrl</th>
                <th scope="col" class="custom-font">Edit</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in binData" :key="index">
                <td class="custom-font">{{item.gid}}</td>
                <td class="custom-font">{{ item.describe }}</td>
                <td class="custom-font">
                  <a href="{item.fullShortUrl}" target="_blank">{{
                    item.fullShortUrl
                  }}</a>
                </td>
                <td class="custom-font">
                  <button
                    class="custom-button"
                    @click="restore(item.gid, item.fullShortUrl, index)"
                  >
                    Restore
                  </button>
                  <br />
                  <button
                    class="custom-button"
                    @click="eliminate(item.gid, item.fullShortUrl, index)"
                  >
                    Eliminate
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";
import { inject, ref } from "vue";

export default {
  name: "HomePage",
  setup() {
    const isSidebarCollapsed = ref(false);
    const toggleSidebar = () => {
      isSidebarCollapsed.value = !isSidebarCollapsed.value;
    };
    const binData = ref([]);
    const headers = inject("headers");
    const initialize = async () => {
      try {
        const response = await axios.get("/api/short-link/recycle-bin/page", {
          headers: headers,
          params: { current: "1", size: "1000" },
        });
        binData.value = response.data.data.records;
      } catch (error) {
        console.error("Error:", error);
      }
    };
    initialize();
    return {
      isSidebarCollapsed,
      toggleSidebar,
      status: 1,
      binData,
        headers,
      initialize
    };
  },
  methods: {
    navigateToCreateShortlink() {
      this.$router.push("/createShortLink");
    },
    navigateToRecycleBin() {
      this.$router.push("/recycleBin");
    },
    navigateToMonitor() {
      this.$router.push("/monitor");
    },
    navigateToMyGroup() {
      this.$router.push("/myGroup");
    },
    async restore(gid, fullShortUrl) {
      try {
        await axios.post(
          "/api/short-link/recycle-bin/recover",
          {
            gid: gid,
            fullShortUrl: fullShortUrl,
          },
          {
            headers: this.headers,
          }
        );
        this.initialize();
      } catch (error) {
        alert(error);
        console.log("Error:", error);
      }
    },
    async eliminate(gid, fullShortUrl) {
      try {
        await axios.post(
          "/api/short-link/recycle-bin/remove",
          {
            gid: gid,
            fullShortUrl: fullShortUrl,
          },
          {
            headers: this.headers,
          }
        );
          this.initialize();
      } catch (error) {
        alert(error);
        console.log("Error:", error);
      }
    },
    start() {
      this.$router.push("/createShortLink");
    },
    bin() {
      this.$router.push("/recycleBin");
    },
    home() {
      this.$router.push("/home");
    },
    async logout() {
      try {
        const response = await axios.delete(`/api/short-link/user/logout`, {
          headers: this.headers, // 请求头信息
          params: {
            username: this.headers.username,
            token: this.headers.token,
          }, // gid 作为查询参数
        });
        console.log(response.data);
        this.$router.push("/");
      } catch (error) {
        alert(error);
        console.log(error);
      }
    },
  },
};
</script>

<style scoped>
.app {
  display: flex;
  height: 100vh;
}

/* Main content styles */
.home-page {
  flex: 1;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow: hidden;
}

.background-animation {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg, #030303, #1183a6, #167168, #87cefa);
  background-size: 400% 400%;
  animation: gradientAnimation 15s ease infinite;
}

@keyframes gradientAnimation {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

.content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: white;
}

.shortlink-section {
  margin-top: 20px;
}

.form-group {
  margin-bottom: 1rem;
}

.btn-primary {
  width: 100%;
}

.shortlink-result {
  margin-top: 20px;
  font-size: 1.2rem;
}

.shortlink-result a {
  color: #ff7e5f;
  text-decoration: none;
}

.shortlink-result a:hover {
  text-decoration: underline;
}

.toggle-button span {
  display: inline-block;
  transition: transform 0.3s;
}

.toggle-button span.rotated {
  transform: rotate(180deg);
}

.custom-button {
  width: 150px;
  padding: 3px 0;
  background-color: transparent;
  border: none;
  color: rgb(5, 5, 5);
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 1px;
  text-transform: uppercase;
  cursor: pointer;
  transition: background-color 0.3s ease, color 0.3s ease;
}

.custom-button:hover {
  background-color: rgb(219, 218, 218);
  color: #333;
}

.custom-button a {
  color: inherit; /* 继承父元素的颜色，这里是白色 */
  text-decoration: none; /* 去掉默认的下划线 */
}

.custom-button a:hover {
  color: inherit; /* 保持白色，不受链接默认颜色影响 */
  text-decoration: none; /* 去掉悬停时的下划线 */
}

.table {
  font-family: "Arial", sans-serif;
  font-size: 16px;
  overflow: hidden;
  margin: 20px 30px;
  margin-left: 10px;
  width: 1200px;
}

.table thead {
  background-color: #f4f4f5; /* 深色背景 */
  color: #fff; /* 白色文字 */
  text-align: center;
  border-bottom: 3px solid #f6f3f3; /* 边框颜色 */
}

.table th,
.table td {
  text-align: center; /* 文字居中 */
  padding: 15px; /* 内边距 */
}

.table td {
  background-color: #fbfcfe; /* 单元格背景色 */
  border-top: 1px solid #ddd; /* 表格行的边框 */
}

.table-striped tbody tr:nth-of-type(odd) {
  background-color: #f9f9f9; /* 隔行变色效果 */
}

.button-hover tbody tr:hover {
  background-color: #e6e6e6; /* 鼠标悬停时的背景色 */
  transform: scale(1.02); /* 悬停时放大效果 */
  transition: all 0s ease; /* 平滑过渡效果 */
}

.badge {
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  text-transform: uppercase;
}

.bg-success {
  background-color: #28a745 !important; /* 绿色 */
}

.bg-warning {
  background-color: #ffc107 !important; /* 黄色 */
}

.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.pagination-container span {
  font-size: 16px;
}

.scroll-container {
  max-height: 600px; /* 限制容器的最大高度 */
  overflow-y: auto; /* 启用垂直滚动条 */
  overflow-x: hidden; /* 禁用水平滚动条（可选） */
}

/* Make navbar background white and text black */
.navbar {
  color: black; /* Black text color */
}

.navbar .nav-link {
  color: black !important; /* Ensure the links are black */
}

.navbar .navbar-brand {
  color: black !important; /* Make brand text black */
}

.navbar .nav-item:hover .nav-link {
  color: #007bff !important; /* Optional: change link color on hover */
}

.black-font {
  font-family: "Dancing Script", "Lobster", cursive;
  font-size: 24px; /* 可根据需求调整字体大小 */
  font-style: italic; /* 让字体更具手写风格 */
}

.custom-font {
  font-family: "STFangsong", "FangSong", serif; /* 华文仿宋 */
  font-size: 18px; /* 可根据需要调整字体大小 */
}
</style>
