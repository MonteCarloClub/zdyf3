<script setup lang="ts">
import { useUserStore } from "@/store/user";

const userStore = useUserStore();

const login = () => {
  userStore.login()
}
</script>

<template>
  <div>
    hello: {{ userStore.getToken }}
  </div>
  
  <a-button type="primary" @click="login">
    登录
  </a-button>
</template>

<style scoped>
</style>
