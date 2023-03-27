<template>
  <div>
    <router-link to="/">home</router-link>|
    <router-link to="/sign">sign</router-link>
  </div>
  <img alt="Vue logo" src="./assets/logo.png" />
  <div>{{ userName }}</div>
  <router-view></router-view>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useUserStore } from "@/store/user";

const store = useUserStore();
const userName = computed(() => store.getUserInfo?.username)
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
