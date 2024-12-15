
<template>
  <nav class="navbar">
    <div class="logo">Upload Photo</div>
    <div class="nav-links">
      <router-link to="/home"> Home </router-link>
    </div>
  </nav>

  <div class="card">
    <div class="top">
      <p>Drag & drop images</p>
    </div>

    <div
      class="drag-area"
      @dragover.prevent="onDragOver"
      @dragleave.prevent="onDragLeave"
      @drop.prevent="onDrop"
    >
      <span v-if="!isDragging">
        Drag & drop image here or
        <span class="select" role="button" @click="selectFiles"> Choose </span>
      </span>
      <div v-else class="select">Drop Image Here</div>
      <input
        name="file"
        type="file"
        class="file"
        ref="fileInput"
        multiple
        @change="onFileSelect"
      />
    </div>
    <div class="container">
      <div class="image" v-for="(image, index) in images" :key="index">
        <span class="delete" @click="removeImage(index)">×</span>
        <img :src="image.url" />
      </div>
    </div>
    <button type="button" @click="uploadImages">Upload</button>
  </div>
</template>

<script>
import axios from "axios";
import { inject } from "vue";

export default {
  setup() {
    const headers = inject("headers");
    return { headers};
  },
  data() {
    return {
      images: [],
      files: [],
        isDragging: false,
        gid: inject("gid"),
    };
  },
  methods: {
    selectFiles() {
      this.$refs.fileInput.click();
    },
    onFileSelect(event) {
      const files = event.target.files;
      if (files.length === 0) return;
      for (let i = 0; i < files.length; i++) {
        if (files[i].type.split("/")[0] !== "image") continue;
        if (!this.images.some((e) => e.name === files[i].name)) {
          this.images.push({
            name: files[i].name,
            url: URL.createObjectURL(files[i]),
          });
          this.files.push(files[i]);
        }
      }
      console.log(this.images);
    },
    onDragOver() {
      this.isDragging = true;
    },
    onDragLeave() {
      this.isDragging = false;
    },
    onDrop(event) {
      this.isDragging = false;
      const files = event.dataTransfer.files;
      for (let i = 0; i < files.length; i++) {
        if (files[i].type.split("/")[0] !== "image") continue;
        if (!this.images.some((e) => e.name === files[i].name)) {
          this.images.push({
            name: files[i].name,
            url: URL.createObjectURL(files[i]),
          });
          this.files.push(files[i]);
        }
      }
    },
    removeImage(index) {
      this.images.splice(index, 1);
    },

    async uploadImages() {
        const formData = new FormData();
        console.log(this.gid);
      this.files.forEach((file) => {
        formData.append(`file`, file);
        formData.append(`gid`, this.gid);
        formData.append(`describe`, "");
      });
      console.log(formData.values);
      try {
        const tempHeaders = {
          ...this.headers,
          "Content-Type": "multipart/form-data",
        };
        const response = await axios.post(
          "/api/short-link/image/create",
            {
                file: this.files[0],
                gid: this.gid,
                describe:"IMAGE"
          },
          {
            headers: tempHeaders,
          }
        );
        console.log("Upload successful:", response.data);
        alert("UpLoadSuccess");
      } catch (error) {
        console.error("Upload failed:", error);
      }
    },
  },
};
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #333;
  color: white;
  padding: 10px 20px;
}

.card {
  width: 100%;
  padding: 10px;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
  border-radius: 5px;
  overflow: hidden;
}

.card .top {
  text-align: center;
}

.card p {
  font-weight: bold;
  color: black;
}

.card button {
  outline: 0;
  border: 0;
  color: white;
  border-radius: 4px;
  font-weight: 400;
  padding: 8px 13px;
  width: 100%;
  background: #007bff;
  cursor: pointer;
}

.card .drag-area {
  height: 150px;
  border-radius: 5px;
  border: 2px dashed darkred;
  background: white;
  color: #007bff;
  display: flex;
  justify-content: center;
  align-items: center;
  user-select: none;
  -webkit-user-select: none;
  margin-top: 10px;
}

.card .drag-area .visible {
  font-size: 18px;
}

.card .select {
  color: purple;
  margin-left: 5px;
  cursor: pointer;
  transition: 0.4s;
}

.card .select:hover {
  opacity: 0.6;
}

.card .container {
  width: 100%;
  height: auto;
  display: flex;
  justify-content: flex-start;
  flex-wrap: wrap;
  max-height: 200px;
  position: relative;
  margin-bottom: 8px;
}

.card .container .image {
  width: 75px;
  margin-right: 5px;
  height: 75px;
  position: relative;
  margin-bottom: 8px;
}

.card .container .image img {
  width: 100%;
  height: 100%;
  border-radius: 5px;
}

.card .container .image span {
  position: absolute;
  top: -2px;
  right: 9px;
  font-size: 20px;
  cursor: pointer;
}

.card input,
.card .drag-area .on-drop,
.card .drag-area.dragover .visible {
  display: none;
}

.delete {
  z-index: 999;
  color: red;
}
</style>

