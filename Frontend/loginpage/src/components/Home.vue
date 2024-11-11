<script>
export default {
  name: 'HomePage',
};
</script>


<template>
  <div class="home-page">
    <div class="background-animation"></div>
    <div class="content">

      <h3>Hello</h3>
      <h3>Welcome to Shortlink</h3>

      <div class="shortlink-section">
        <h4>Create a Shortlink</h4>
        <div class="form-group">
          <input type="text" class="form-control" placeholder="Enter URL" v-model="url"/>
        </div>
        <button class="btn btn-primary" @click="createShortlink">Create Shortlink</button>
        <div v-if="shortlink" class="shortlink-result">
          <p>Your Shortlink: <a :href="shortlink" target="_blank">{{ shortlink }}</a></p>
        </div>
      </div>

    </div>
  </div>
</template>

<style scoped>
.home-page {
  position: relative;
  height: 100vh;
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
  background: linear-gradient(45deg, #4682b4, #6495ed, #add8e6, #87cefa);
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
</style>