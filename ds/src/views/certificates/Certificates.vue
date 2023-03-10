<template>
  <el-container>
    <!-- <el-aside width="200px" style="background-color: rgb(238, 241, 246)">
      <el-menu default-active="2">
        <el-menu-item index="2">
          <i class="el-icon-menu"></i>
          <span slot="title">证书列表</span>
        </el-menu-item>
        <el-menu-item index="3" disabled>
          <i class="el-icon-document"></i>
          <span slot="title">证书申请</span>
        </el-menu-item>
        <el-menu-item index="4">
          <i class="el-icon-setting"></i>
          <span slot="title">导航四</span>
        </el-menu-item>
      </el-menu>
    </el-aside> -->

    <el-main>
      <keep-alive>
        <router-view></router-view>
      </keep-alive>
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: "Certificates",
  components: {},
  data() {
    return {};
  },

  mounted() {},

  methods: {},
};
</script>

<style scoped>
.container {
  margin: var(--content-padding, 16px) 0;
  padding: 16px;
  background-color: white;
}
</style>


<style>
.el-table .cell {
  white-space: nowrap !important;
}

.el-descriptions-item__content {
  white-space: nowrap !important;
  overflow: hidden;
  text-overflow: ellipsis;
}

.el-descriptions-item__label {
  white-space: nowrap !important;
}
</style>