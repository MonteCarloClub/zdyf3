<template>
  <section class="section">
    <div class="section-title">组织管理</div>
    <div class="grid-cols-3">
      <div v-for="(attr, index) in organizations" :key="index" class="grid-entry">
        <img :src="imgHome(attr.icon)" :alt="attr.title" />
        <div class="entry-title">{{ attr.title }}</div>
        <div class="entry-desc">{{ attr.desc }}</div>
      </div>
    </div>
  </section>
</template>

<script>
import { DynamicImage } from "@/mixins/DynamicImage";

export default {
  name: "Organizations",
  mixins: [DynamicImage("home/")],
  methods: {},
  data() {
    return {
      organizations: [
        { title: "创建", icon: "create.svg",   desc: "申请成立新组织" },
        { title: "加入", icon: "join.svg",     desc: "同意加入新组织" },
        { title: "属性", icon: "attrs.svg",    desc: "提交part-pk（生成组织 / 组织属性）" },
        { title: "查询", icon: "search.svg",   desc: "查询新增组织 / 组织属性申请" },
        { title: "声明", icon: "declare.svg",  desc: "声明组织新属性" },
        { title: "审批", icon: "approval.svg", desc: "审批组织新属性" },
      ],
    };
  },
};
</script>

<style scoped>

.grid-cols-3 {
  display: grid;
  grid-template-columns: repeat(3, 33.33%);
}

.grid-entry {
  padding: 32px 0;
  margin: 0 16px;
  text-align: center;
}

.grid-entry img {
  height: 81px;
  margin: 16px;
}

</style>