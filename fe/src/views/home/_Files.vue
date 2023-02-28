<template>
  <section class="section">
    <div class="section-title">文件管理</div>
    <div class="grid-cols-6_4">
      <div class="image-box">
        <img :src="imgHome('files.jpg')" alt="文件" />
      </div>
      <div style="background-color: black"></div>
      <div class="list-rows">
        <div v-for="(file, index) in files" :key="index" class="list-entry">
          <img :src="imgHome(file.icon)" :alt="file.title" />
          <div class="entry-title">{{ file.title }}</div>
          <div class="entry-desc">{{ file.desc }}</div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import { DynamicImage } from "@/mixins/DynamicImage";

export default {
  name: "Files",
  mixins: [DynamicImage("home/")],
  methods: {},
  data() {
    return {
      files: [
        { title: "加密", icon: "encrypt.svg", desc: "加密共享文件" },
        { title: "解密", icon: "decrypt.svg", desc: "解密共享文件" },
        { title: "查询", icon: "query.svg",   desc: "查询共享文件" },
      ],
    };
  },
};
</script>

<style scoped>

.grid-cols-6_4 {
  display: grid;
  grid-template-columns: 45% 3px auto;
  gap: 5%;
}

.image-box {
  height: 360px;
  width: 480px;
  justify-self: end;
  position: relative;
  filter: invert(1);
}

.image-box::before {
  content: "";
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  background-color: white;
  transform: rotateZ(3deg);
  transform-origin: right;
  box-shadow: 8px 8px 20px 0 rgb(85 85 85 / 10%), -5px -5px 20px 0px rgb(85 85 85 / 10%);
}

.image-box img {
  position: absolute;
  left: 5px;
  top: 5px;
  max-width: calc(100% - 10px);
  max-height: calc(100% - 10px);
}

.list-rows {
  max-width: 240px;
}

.list-entry {
  display: grid;
  margin: 32px 0;
  grid-template-columns: 100px auto;
  grid-template-areas:
    "l t"
    "l b";
}

.list-entry > div {
  margin: 0 !important;
  align-self: center;
}

.list-entry > img {
  grid-area: l;
  padding: 0 10px;
  height: 70px;
  /* https://stackoverflow.com/questions/14387690/how-can-i-show-only-corner-borders */
  background: linear-gradient(to right, black 2px, transparent 2px) 0 0,
    linear-gradient(to right, black 2px, transparent 2px) 0 100%,
    linear-gradient(to left, black 2px, transparent 2px) 100% 0,
    linear-gradient(to left, black 2px, transparent 2px) 100% 100%,
    linear-gradient(to bottom, black 2px, transparent 2px) 0 0,
    linear-gradient(to bottom, black 2px, transparent 2px) 100% 0,
    linear-gradient(to top, black 2px, transparent 2px) 0 100%,
    linear-gradient(to top, black 2px, transparent 2px) 100% 100%;

  background-repeat: no-repeat;
  background-size: 20px 20px;
}

.list-entry > .entry-title {
  grid-area: t;
}

.list-entry > .entry-desc {
  grid-area: b;
}

</style>