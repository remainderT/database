<template>
  <div class="app">
    <nav v-show="!visible" class="navbar navbar-expand navbar-light fixed-top">
      <div class="container">
        <div class="collapse navbar-collapse">
          <ul class="navbar-nav ml-auto">
            <li class="nav-item" v-if="isVip&&!query">
              <button @click="image" class="nav-link">Images</button>
            </li>
            <li class="nav-item" v-else-if="!isVip">
              <button @click="vip" class="nav-link">
                <div style="color: gold">VIP</div>
              </button>
            </li>
            <li class="nav-item">
              <button v-if="!query" @click="openDialog" class="nav-link">
                Quick Start
              </button>
            </li>
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
    <!-- Sidebar -->
    <aside class="sidebar" :class="{ collapsed: isSidebarCollapsed }">
      <button @click="toggleSidebar" class="toggle-button">
        <span :class="{ rotated: isSidebarCollapsed }">▶</span>
      </button>
      <nav v-if="!isSidebarCollapsed">
        <ul>
          <br />
          <div class="input-container">
            <input
              v-model="newGroup"
              type="text"
              placeholder="Create A New Group"
            />
            <button class="gradient-button" @click="addNewGroup">+</button>
          </div>
          <br />
          <div v-for="(item, index) in paginatedData" :key="index">
            <button
              v-if="this.index != index"
              class="custom-button"
              @click="Query(item.gid, index, item.name)"
            >
              <div v-if="item.name">{{ item.name }}</div>
              <div v-else>group{{ index }}</div>
            </button>
            <button v-else class="custom-button0">
              <div v-if="item.name">{{ item.name }}</div>
              <div v-else>group{{ index }}</div>
            </button>
          </div>
        </ul>
      </nav>
    </aside>

    <!-- Main Content -->
    <div class="home-page">
      <div class="background-animation"></div>
      <div class="content">
        <div class="container mt-4" v-if="query">
          <h3 style="color: black">Hello</h3>
          <h3 style="color: black">Welcome to Shortlink</h3>
        </div>
        <div v-else>
          <br />
          <br />
          <button class="custom-button2 modify" @click="changeGroupName">
            Change Group Name:
          </button>
          <input
            type="text"
            class="left"
            v-model="newGroupName"
            placeholder="Enter Your New Group Name"
          />
          <button class="custom-button2 delete" @click="deleteItem">
            Delete This Group
          </button>
          <br />
          <br />
          <div class="scroll-container">
            <table class="table table-striped table-hover shadow-sm">
              <thead class="thead-dark">
                <tr>
                  <th scope="col" class="custom-font">Icon</th>
                  <th scope="col" class="custom-font">Info</th>
                  <th scope="col" class="custom-font">FullShortUrl</th>
                  <th scope="col" class="custom-font">Operation</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(item, index) in groupData" :key="index">
                    <td v-if="item.isPic==1">
                        <img :src="item.originUrl" class="icon" />
                    </td>
                  <td v-else>
                    <img :src="item.favicon" class="icon" />
                  </td>
                  <td class="custom-font">{{ item.describe }}</td>
                  <td>
                    <a
                      :href="`http://${item.fullShortUrl}`"
                      target="_blank"
                      class="custom-font"
                    >
                      {{ `http://${item.fullShortUrl}` }}
                    </a>
                  </td>
                  <td class="custom-font">
                    <button
                      class="custom-button1"
                      @click="deleteLink(index, item.fullShortUrl)"
                    >
                      Delete
                    </button>
                    &nbsp;
                    <button
                      class="custom-button1"
                      @click="openDrawer(item.fullShortUrl)"
                    >
                      Query
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
            <div class="custom-font" v-show="length == 0">
              <h5 style="color: black">=====NO LINK CREATED=====</h5>
            </div>
          </div>
          <br />
          <div>
            <GlobalDialog v-if="this.dialogVisible" />
          </div>
          <div v-if="visible" class="drawer-overlay" @click.self="closeDrawer">
            <div class="drawer" :class="{ 'drawer-open': visible }">
              <br />
              <h3 style="color: black" class="custom-font0">Statistics Report</h3>
              <br />
              <button class="drawer-close" @click="closeDrawer">×</button>

              <div class="content">
                <div class="scroll-container">
                  <Dashboard />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, inject, provide } from "vue";
import axios from "axios";
import GlobalDialog from "./GlobalDialog.vue";
import Dashboard from "./Dashboard.vue";

export default {
  name: "HomePage",
  components: {
    GlobalDialog,
    Dashboard,
  },
    setup() {
    const nowLink = ref("");
    const dialogVisible = ref(false);
    provide("dialogVisible", dialogVisible);
    const groupData = ref([]);
    const tableData = ref([]);
    const isSidebarCollapsed = ref(false);
    const currentPage = ref(1);
    const pageSize = ref(7);
    const totalItems = ref(1);
    const paginatedData = ref([]);
        const gid = inject("gid");
    const isVip = inject("isVip");
    computed(() => {
      const start = (currentPage.value - 1) * pageSize.value;
      const end = start + pageSize.value;
      return tableData.value.data.slice(start, end);
    });

    const toggleSidebar = () => {
      isSidebarCollapsed.value = !isSidebarCollapsed.value;
    };
    const headers = inject("headers");
    const initialize = async () => {
      try {
        const response = await axios.get("/api/short-link/group", { headers });
        tableData.value = response.data;
        paginatedData.value = tableData.value.data;
        console.log(paginatedData.value);
        const info = await axios.get(
          `/api/short-link/user/${headers.username}`,
          { headers }
        );
        isVip.value = info.data.data.isVip == 0 ? false : true;
      } catch (error) {
        console.error("Error:", error);
      }
    };

    const query = ref(true);
    const newGroup = ref("");
    const index = ref(-1);
    const newGroupName = ref("");
    initialize();
    const length = ref(0);
    const visible = ref(false);
    provide("query", query);
    // 打开抽屉
    const openDrawer = (link) => {
      nowLink.value = link;
      visible.value = true;
    };

    // 关闭抽屉
    const closeDrawer = () => {
      visible.value = false;
    };
    const fetchShortLinks = async (gid) => {
      try {
        const response = await axios.get(`/api/short-link/page`, {
          headers: headers, // 请求头信息
          params: { gid, current: 1, size: 10 }, // gid 作为查询参数
        });
        groupData.value = response.data.data.records;
        length.value = response.data.data.records.length;
        console.log("Item fetched:", groupData.value);
      } catch (error) {
        console.error("Error getting item:", error);
        alert("Failed to query");
      }
    };

    provide("link", nowLink);
        provide( "fetch", fetchShortLinks );
        provide( "headers", headers );
        provide( "gid", gid );
        const name = inject("name");
    return {
      isVip,
      nowLink,
      visible,
      openDrawer,
      closeDrawer,
      currentPage,
      pageSize,
      totalItems,
      paginatedData,
      initialize,
      isSidebarCollapsed,
      toggleSidebar,
      tableData,
      query,
      newGroup,
      headers,
      groupData,
      gid,
      index,
      dialogVisible,
      newGroupName,
      length,
        fetchShortLinks,
      name,
    };
  },
  methods: {
    async vip() {
      try {
        const response = await axios.post(
          "/api/short-link/order/create",
          {},
          {
            headers: this.headers,
          }
        );
          console.log(response);
        alert("Ordered Created");
        const id = ref([""]);
          id.value = response.data.data.id;
        const payResponse = await axios.get("/api/short-link/order/pay", {
          headers: this.headers,
          params: { id: id.value },
        });
        const formHtml = payResponse.data;
        if (formHtml) {
          // 创建一个临时容器
          const tempDiv = document.createElement("div");
          tempDiv.innerHTML = formHtml;
          document.body.appendChild(tempDiv);

          // 提交表单
          const form = tempDiv.querySelector('form[name="punchout_form"]');
            if ( form ) {
                form.submit();
          } else {
            console.error("未找到表单");
          }
          document.body.removeChild(tempDiv);
        
        } else {
          console.error("未获取到有效的表单 HTML");
        }
      } catch (error) {
        console.log(error);
      }
    },
    async changeGroupName() {
      if (this.newGroupName == "") {
        return;
      }
      try {
        const response = await axios.put(
          "/api/short-link/group",
          {
            name: this.newGroupName,
            gid: this.gid,
          },
          {
            headers: this.headers, // 使用 headers 代替请求头
          }
        );
        console.log(response);
        this.paginatedData[this.index].name = this.newGroupName;
        this.newGroupName = "";
    
        alert("Group Name Changed!");
      } catch (error) {
        alert("Error");
        console.log(error);
      }
    },
      openDialog() {
          this.$router.push("/quickStart");
    },
    start() {
      this.$router.push("/createShortLink");
    },
    bin() {
      this.$router.push("/recycleBin");
    },
    image() {
      this.$router.push("/photo");
    },
    home() {
      this.index = -1;
      this.gid = "";
      this.query = true;
      this.$router.push("/home");
    },
    async logout() {
      try {
        const response = await axios.delete(`/api/short-link/user/logout`, {
          headers: this.headers, // 请求头信息
          params: {
            username: this.headers.username,
            token: this.headers.token,
          },
        });
        console.log(response.data);
        this.$router.push("/");
      } catch (error) {
        alert(error);
        console.log(error);
      }
    },
    async addNewGroup() {
      if (this.paginatedData.length == 10) {
        alert("Group Number Limit Exceeded!");
        return;
      }

      try {
        const response = await axios.post(
          "/api/short-link/group",
          {
            name: this.newGroup, // 使用 newGroup 代替 ss1111
          },
          {
            headers: this.headers, // 使用 headers 代替请求头
          }
        );
        console.log(response);
        this.initialize();
      } catch (error) {
        console.error("Error:", error);
      }
    },
    Home() {
      this.query = false;
      },
    sleep(ms) {
      return new Promise((resolve) => setTimeout(resolve, ms));
    },
    async  Query( gid, index, name ) {
      await this.sleep(1000);
        this.fetchShortLinks( gid );
        this.gid = gid;
      if (this.query) {
        this.query = false;
      }
        this.index = index;
        this.name = name;
    },
    async deleteItem() {
      try {
        const response = await axios.delete(`/api/short-link/group`, {
          headers: this.headers, // 请求头信息
          params: { gid: this.gid }, // gid 作为查询参数
        });
        console.log("Item deleted:", response.data);
        alert("Group Deleted!");
      } catch (error) {
        console.error("Error deleting item:", error);
        alert("Failed to delete item.");
      }
      this.paginatedData.splice(this.index, 1);
      this.query = true;
    },

    async deleteLink(index, fullShortUrl) {
      try {
        const response = await axios.post(
          "/api/short-link/recycle-bin/save",
          {
            gid: this.gid,
            fullShortUrl: fullShortUrl,
          },
          {
            headers: this.headers, // 使用 headers 代替请求头
          }
        );
        console.log(response);
        this.fetchShortLinks(this.gid);
        //this.groupData.value.splice(index, 1);
      } catch (error) {
        console.error("Error:", error);
      }
    },
  },
};
</script>

<style scoped>
/* Make navbar background white and text black */
.navbar {
  color: black; /* Black text color */
  z-index: 1000;
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
.app {
  display: flex;
  height: 100vh;
}

/* Sidebar styles */
.sidebar {
  width: 250px;
  background-color: #333;
  color: #fff;
  padding: 20px;
  transition: width 0.3s;
}

.sidebar.collapsed {
  width: 40px;
}

.toggle-button {
  margin-bottom: 20px;
  background-color: #444;
  color: #fff;
  border: none;
  padding: 8px 12px;
  cursor: pointer;
}

.sidebar ul {
  list-style: none;
  padding: 0;
}

.sidebar ul li {
  margin: 15px 0;
}

.sidebar ul li a {
  color: #fff;
  text-decoration: none;
}

.sidebar ul li a:hover {
  text-decoration: underline;
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

.toggle-button {
  position: absolute;
  top: 50%;
  left: -15px; /* Position the button slightly outside the sidebar */
  transform: translateY(-50%);
  background-color: #333; /* 保持与侧边栏背景一致 */
  color: #fff;
  border: none;
  padding: 8px 12px;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  border-radius: 50%;
}

.toggle-button span {
  display: inline-block;
  transition: transform 0.3s;
}

.toggle-button span.rotated {
  transform: rotate(180deg);
}

.custom-button {
  width: 210px;
  padding: 12px 0;
  background-color: transparent;
  border: none;
  color: white;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 1px;
  text-transform: uppercase;
  cursor: pointer;
  transition: background-color 0.3s ease, color 0.3s ease;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3); /* 添加阴影 */
}

.custom-button:hover {
  background-color: white;
  color: #333;
  text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.5); /* 增强阴影效果 */
}

.table {
  font-family: "Arial", sans-serif;
  font-size: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); /* 阴影效果 */
  overflow: hidden;
  margin: 20px 30px;
  margin-left: 40px;
  width: 1200px;
}

.table thead {
  background-color: #1a1a2e; /* 深色背景 */
  color: #fff; /* 白色文字 */
  text-align: center;
  border-bottom: 3px solid #161616; /* 边框颜色 */
}

.table th,
.table td {
  text-align: center; /* 文字居中 */
  padding: 15px; /* 内边距 */
}

.table td {
  background-color: #f4f4f4; /* 单元格背景色 */
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

.pagination-button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 15px;
  cursor: pointer;
  margin: 0 10px;
  border-radius: 5px;
}

.pagination-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.pagination-container span {
  font-size: 16px;
}

.scroll-container {
  max-height: 600px; /* 限制容器的最大高度 */
  overflow-y: auto; /* 启用垂直滚动条 */
  overflow-x: hidden; /* 禁用水平滚动条（可选） */
}

.scroll-container::-webkit-scrollbar-thumb {
  background: #888; /* 滚动条滑块颜色 */
  border-radius: 4px; /* 滑块圆角 */
  height: 50px; /* 滑块最小长度 */
}

.custom-button1 {
  max-width: 400px;
  padding: 10px;
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
  background: transparent;
  text-decoration: underline; /* 添加下划线 */
}

.custom-button1:hover {
  background-color: rgb(219, 218, 218);
  color: #333;
}

.custom-button1 a {
  color: inherit; /* 继承父元素的颜色，这里是白色 */
  text-decoration: none; /* 去掉默认的下划线 */
}

.custom-button1 a:hover {
  color: inherit; /* 保持白色，不受链接默认颜色影响 */
  text-decoration: none; /* 去掉悬停时的下划线 */
}

.input-container {
  display: flex;
  align-items: center; /* 垂直居中 */
  gap: 10px; /* 设置两者的间距 */
}

.gradient-button {
  /* 基础样式 */
  display: inline-flex;
  justify-content: center;
  align-items: center;
  width: 50px;
  height: 30px;
  font-size: 24px;
  font-weight: bold;
  color: white; /* 字体颜色 */
  background: black; /* 黑白渐变背景 */
  border: none; /* 去掉边框 */
  cursor: pointer;

  transition: all 0.3s ease; /* 添加平滑过渡 */
}

.gradient-button:hover {
  /* 鼠标悬停效果 */
  transform: scale(1.05); /* 放大 */
}

.gradient-button:active {
  /* 点击效果 */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  transform: scale(0.95); /* 稍微缩小 */
}

.button-container {
  position: relative; /* 为子元素提供定位参考 */
  width: 100%; /* 占满容器宽度 */
  height: 50px; /* 设置容器高度，可根据需要调整 */
}

.custom-button2 {
  position: absolute; /* 允许按钮定位 */
  margin: 10px; /* 添加边距 */
  text-decoration: underline; /* 添加下划线 */
}

.custom-button2.modify {
  top: 40px; /* 顶部对齐 */
  left: 30px; /* 靠左对齐 */
}

.custom-button2.delete {
  top: 40px; /* 顶部对齐 */
  right: 35px; /* 靠右对齐 */
}

.custom-button2 {
  max-width: 400px;
  padding: 10px;
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
  background: transparent;
}

.custom-button2:hover {
  background-color: rgb(219, 218, 218);
  color: #333;
}

.custom-button2 a {
  color: inherit; /* 继承父元素的颜色，这里是白色 */
  text-decoration: none; /* 去掉默认的下划线 */
}

.custom-button2 a:hover {
  color: inherit; /* 保持白色，不受链接默认颜色影响 */
  text-decoration: none; /* 去掉悬停时的下划线 */
}

.left {
  top: 42px;
  left: 280px; /* 靠左对齐 */
  position: absolute; /* 允许按钮定位 */
  margin: 10px; /* 添加边距 */
  width: 230px;
  display: flex;
  align-items: center; /* 垂直居中 */
  gap: 10px; /* 设置两者的间距 */
}

.custom-button0 {
  width: 210px;
  padding: 12px 0;
  background-color: white;
  border: none;
  color: #333;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 1px;
  text-transform: uppercase;
  cursor: pointer;
  transition: background-color 0.3s ease, color 0.3s ease;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3); /* 添加阴影 */
}
.icon {
  width: 25px; /* 设置图标宽度 */
  height: 25px; /* 设置图标高度 */
}
.drawer-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(7, 7, 7, 0.5);
}

.drawer {
  position: fixed;
  top: 0;
  right: -300px; /* 初始状态在屏幕外 */
  width: 800px;
  height: 100%;
  background-color: rgb(255, 255, 255);
  transition: right 0.3s ease-in-out;
  box-shadow: -2px 0 5px rgba(0, 0, 0, 0.3);
}

.drawer-open {
  right: 0; /* 打开时位于屏幕内 */
}

.drawer-close {
  color: black;
  position: absolute;
  top: 10px;
  left: 10px;
  font-size: 24px;
  background: none;
  border: none;
  cursor: pointer;
}

.drawer-close:hover {
  color: rgb(183, 181, 181);
}

.custom-font {
  font-family: "STFangsong", "FangSong", serif; /* 华文仿宋 */
  font-size: 18px; /* 可根据需要调整字体大小 */
}
.custom-font0 {
  font-family: "STFangsong", "FangSong", serif; /* 华文仿宋 */
}
.black-font {
  font-family: "Dancing Script", "Lobster", cursive;
  font-size: 24px; /* 可根据需求调整字体大小 */
  font-style: italic; /* 让字体更具手写风格 */
}
</style>