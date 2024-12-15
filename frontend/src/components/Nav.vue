<script>
import { inject } from "vue";
import axios from "axios";
export default {
  name: "NavBar",
  data() {
    return {
      headers: [],
    };
  },
  methods: {
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
      this.headers = inject("headers");
      alert(this.headers);
      try {
        const response = await axios.delete(`/api/short-link/user/logout`, {
          headers: this.headers, // 请求头信息
          params: {
            username: this.headers.username,
            token: this.headers.token,
          }, // gid 作为查询参数
        });
        console.log(response.data);
      } catch (error) {
        alert(this.headers.username);
        alert("Wrong Operation");
        console.log(error);
      }
    },
  },
};
</script>

<template>
  <nav class="navbar navbar-expand navbar-light fixed-top">
    <div class="container">
      <div class="collapse navbar-collapse">
        <ul class="navbar-nav ml-auto">
          <li class="nav-item">
            <button @click="start" class="nav-link">Quick Start</button>
          </li>
          <li class="nav-item">
            <button @click="bin" class="nav-link">Bin</button>
          </li>
          <li class="nav-item">
            <button @click="home" class="nav-link">Home</button>
          </li>
          <li class="nav-item">
            <a href="/" class="nav-link">Login</a>
          </li>
          <li class="nav-item">
            <button @click="logout" class="nav-link">Logout</button>
          </li>
        </ul>
      </div>
    </div>
  </nav>
</template>


<style scoped>
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
</style>
