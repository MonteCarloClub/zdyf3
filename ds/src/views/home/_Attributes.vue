<template>
  <section class="section">
    <div class="section-title">属性管理</div>
    <div class="flex-cols">
      <div v-for="(attr, index) in attrs" :key="index" class="flex-entry">
        <div class="entry-title">{{ attr.title }}</div>
        <div class="entry-desc">{{ attr.desc }}</div>
      </div>
    </div>
  </section>
</template>

<script>
import { DynamicImage } from "@/mixins/DynamicImage";

export default {
  name: "Attributes",
  mixins: [DynamicImage("home/")],
  methods: {},
  data() {
    return {
      attrs: [
        { title: "声明", desc: "声明用户属性" },
        { title: "申请", desc: "用户申请属性" },
        { title: "查询", desc: "查询属性" },
        { title: "审批", desc: "审批属性申请" },
        { title: "同步", desc: "同步用户属性" },
      ]
    };
  },
};
</script>

<style scoped>
.flex-cols {
  display: flex;
  padding-bottom: 48px
}

.flex-entry {
  flex: 1;
  padding: 16px;
  margin: 0 16px;
  border-radius: 4px;
  box-sizing: border-box;
  border: 2px solid #fff;
  background-image: linear-gradient(0deg, #fff, #f3f5f8);
  box-shadow: 8px 8px 20px 0 rgb(55 99 170 / 10%), -8px -8px 20px 0 #fff;
}
</style>