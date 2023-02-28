<template>
  <div class="home">
    <Banner />
    <!-- 属性管理-->
    <Attributes />
    <!-- 小节内容 -->
    <Organizations />
    <!-- 小节内容 -->
    <Files />
    <!-- 底部 -->
    <Footer />
  </div>
</template>

<script>
// @ is an alias to /src
import Banner from "./_Banner.vue";
import Files from "./_Files.vue";
import Attributes from "./_Attributes.vue";
import Organizations from "./_Organizations.vue";
import Footer from "@/components/Footer.vue";
import { throttle } from "@/utils/helpers";

export default {
  name: "Home",
  components: { Banner, Footer, Attributes, Organizations, Files },
  data() {
    return {};
  },
  methods: {
    handleScroll: throttle(function () {}, 300),
  },
  created() {
    // 创建该组件时，监听滚动函数
    window.addEventListener("scroll", this.handleScroll);
  },
  destroyed() {
    // 切换到其它页面时，移除滚动监听
    window.removeEventListener("scroll", this.handleScroll);
  },
};
</script>

<style scoped>
section:nth-child(even) {
  background-color: var(--bg-color-0, #081326);
}

section:nth-child(odd) {
  background-color: var(--bg-color-1, #091629);
}
</style>

<style>
/* shared by child components */
.section-title {
  text-align: center;
  font-size: 1.8em;
  padding: 0.5em 0 1em 0;
}

.section {
  padding: 36px var(--body-margin, 32px);
}

.section > div{
  filter: invert(1);
}

.entry-title {
  margin-bottom: 8px;
  font-size: 18px;
  font-weight: 500;
  color: #000;
  line-height: 24px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.entry-desc {
  font-size: 14px;
  color: #98a3b7;
  line-height: 20px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>