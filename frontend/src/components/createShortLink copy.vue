<template>
    <button @click="openDrawer"> open</button>
  <div v-if="visible" class="drawer-overlay" @click.self="closeDrawer">
    <div class="drawer" :class="{ 'drawer-open': visible }">
      <button class="drawer-close" @click="closeDrawer">×</button>
      <slot></slot>
    </div>
  </div>
</template>

<script>
import { ref } from "vue";
export default {
  setup() {
    const visible = ref(false);

    // 打开抽屉
    const openDrawer = () => {
      visible.value = true;
    };

    // 关闭抽屉
    const closeDrawer = () => {
      visible.value = false;
    };

    // 返回要暴露的方法
        return {
        visible,
      openDrawer,
      closeDrawer,
    };
  },
};
</script>

<style scoped>
.drawer-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1000;
}

.drawer {
  position: fixed;
  top: 0;
  right: -300px; /* 初始状态在屏幕外 */
  width: 600px;
  height: 100%;
  background-color: #fff;
  transition: right 0.3s ease-in-out;
  box-shadow: -2px 0 5px rgba(0, 0, 0, 0.3);
}

.drawer-open {
  right: 0; /* 打开时位于屏幕内 */
}

.drawer-close {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 24px;
  background: none;
  border: none;
  cursor: pointer;
}

.drawer-close:hover {
  color: red;
}
</style>
