<!-- components/GlobalDialog.vue -->
<template>
    <div class="background-animation"></div>
  <div class="dialog-backdrop">
    <div class="dialog">
      <div class="dialog-body">
        <div class="app">
          <!-- Main Content -->
          <div class="home-page">
            <div class="content">
              <div class="form-container">
                <div class="form-container">
                  <button @click="closeDialog" class="custom-button">
                    close
                  </button>
                  <h3>CREATE THE LINK</h3>
                  <form @submit.prevent="">
                    <div class="form-group">
                      <label for="domain">Domain</label>
                      <input
                        v-model="form.domain"
                        type="text"
                        id="domain"
                        placeholder="Enter your domain"
                      />
                    </div>

                    <div class="form-group">
                      <label for="originUrl">originUrl</label>
                      <input
                        v-model="form.originUrl"
                        type="url"
                        id="originUrl"
                        placeholder="Enter your originUrl"
                      />
                    </div>

                    <div class="form-group">
                      <label for="createdType">CreatedType</label>
                      <select v-model="form.createdType" id="createdType">
                        <option value="0">Interface</option>
                        <option value="1">Console</option>
                      </select>
                    </div>
                    <div class="form-group">
                      <label for="originUrl">Group Id</label>
                      <h5 class="infinite-text">{{ gid }}</h5>
                    </div>
                    <div class="form-group">
                      <label for="validDateType">ValidDateType</label>
                      <select id="validDateType" v-model="form.validDateType">
                        <option value="0">Unlimited</option>
                        <option value="1">Custom</option>
                      </select>
                    </div>

                    <div class="form-group" v-if="form.validDateType === '1'">
                      <label for="validDate">validDate</label>
                      <input
                        v-model="form.validDate"
                        type="datetime-local"
                        id="validDate"
                      />
                    </div>

                    <div class="form-group" v-else>
                      <label>validDate</label>
                      <h5 class="infinite-text">Permanent</h5>
                    </div>
                    <div class="form-group full-width">
                      <label for="describe">Description</label>
                      <textarea
                        v-model="form.describe"
                        id="describe"
                        placeholder="Enter your description"
                      ></textarea>
                      <button @click="fetch">FETCH</button>
                    </div>
                    <button class="submit-button" @click="submitForm">SUBMIT</button>
                  </form>
                  
                  <div v-if="responseData && responseData.code == 0">
                    <a
                      class="underlined-link"
                      :href="`http://${responseData.data.fullShortUrl}`"
                      :style="{ color: 'black' }"
                      target="_blank"
                    >
                      Your ShortLink: {{ responseData.data.fullShortUrl }}
                    </a>
                  </div>
                  <div v-else-if="responseData">
                    <h4 :style="{ color: 'black' }">!ERROR!</h4>
                  </div>
                  <div v-else>
                    <br>
                    <div v-show="responseData && responseData.code == 0">
                        hahaha
                    </div>
                  </div>
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
import { inject, ref } from "vue";
import axios from "axios";

export default {
  setup() {
    const form = ref({
      domain: "",
      originUrl: "",
      gid: "",
      createdType: 1,
      validDateType: 0,
      validDate: "2024-12-31 23:59:00",
      describe: "111",
    });
    const responseData = ref(null);
    const url = ref("");
    const shortlink = ref("");
    // 设置 validDate 默认为当前时间的后一天

    const isSidebarCollapsed = ref(false);
    const toggleSidebar = () => {
      isSidebarCollapsed.value = !isSidebarCollapsed.value;
    };
    const gid = inject("gid");
    const submitForm = async () => {
      try {
        form.value.gid = gid;
        // 发起POST请求
        // 发起 POST 请求并附加请求头
        const response = await axios.post(
          "/api/short-link/create", // 目标 URL
          form.value, // 请求体
          { headers } // 请求头
        );
        // 将响应数据保存到响应数据对象
        responseData.value = response.data;
          console.log( "成功:", response.data );
      } catch (error) {
        console.error("Error:", error);
        alert("Creation Error\n Please try again");
      }
    };
    const dialog = [];
    const headers = inject("headers");
        const dialogVisible = inject( "dialogVisible" );
        const query = inject( "query" );
    return {
      dialog,
      headers,
      dialogVisible,
      url,
      shortlink,
      isSidebarCollapsed,
      toggleSidebar,
      form,
      submitForm,
      responseData,
        gid,
        query
    };
  },
    methods: {
    sleep(ms) {
      return new Promise((resolve) => setTimeout(resolve, ms));
    },
    closeDialog() {
        console.log( this.query );
        this.$router.push("/home");
      }, async fetch() {
          try {
              const response = await axios.get( `/api/short-link/title`, {
                  headers: this.headers, // 请求头信息
                  params: { url: this.form.originUrl }, // gid 作为查询参数
              } );
              this.form.describe = response.data.data;
              console.log(this.form.describe);
          } catch (error) {
              console.log(error);
          }
    }
  },
};
</script>

<style scoped>
.dialog-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}
.dialog {
  background: white;
  padding: 20px;
  border-radius: 8px;
  width: 600px;
}
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
  width: 90%;
  height: 95%;
  background: white;
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

.form-container {
  margin: 0 auto;
  padding: 10px;
  border: 1px solid transparent;
  border-radius: 8px;
  background-color: transparent;
  font-family: Arial, sans-serif;
}

h3 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #333;
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

input,
select,
textarea {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-sizing: border-box;
  font-size: 16px;
}

textarea {
  resize: vertical;
}

.submit-button {
  grid-column: span 2;
  padding: 10px 0;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 18px;
  transition: background-color 0.3s ease;
}

.submit-button:hover {
  background-color: #0056b3;
}

.custom-button {
  position: absolute;
  top: 0px;
  right: 10px;
  background: transparent;
  border: none;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  color: #191818;
}

.custom-button:hover {
  background-color: rgb(252, 249, 249);
  color: #333;
  text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.5); /* 增强阴影效果 */
}

.custom-button a {
  color: inherit; /* 继承父元素的颜色，这里是白色 */
  text-decoration: none; /* 去掉默认的下划线 */
}

.custom-button a:hover {
  color: inherit; /* 保持白色，不受链接默认颜色影响 */
  text-decoration: none; /* 去掉悬停时的下划线 */
}
@import url("https://fonts.googleapis.com/css2?family=Roboto+Slab:wght@700&display=swap");
.infinite-text {
  color: black;
  font-family: "Roboto Slab", serif; /* 替换成所选字体 */
  font-weight: 700;
  font-size: 24px; /* 根据需要调整大小 */
  letter-spacing: 1px; /* 增加字母间距 */
}
.underlined-link {
  text-decoration: underline;
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
</style>
 