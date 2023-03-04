<template>
  <div class="logo">
    <div>数据共享交换平台</div>
  </div>
</template>

<script>
export default {
  name: "Logo",
};
</script>

<style scoped>
.logo {
  display: flex;
  height: 100%;
  align-items: center;
}

.logo > img {
  height: 80%;
  width: auto;
}

@font-face {
  font-family: "MoonDance";
  src: local("MoonDance"), url(../assets/fonts/MoonDance-Regular.ttf) format("truetype");
}

.logo > div {
  font-family: MoonDance;
  font-size: 24px;
  font-weight: bold;
  margin-left: 12px;
}
</style>