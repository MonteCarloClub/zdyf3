<template>
  <div id="app">
    <el-container style="border: 1px solid #eee">
      <el-aside width="230px" :style="'height:' + windowHeight + 'px'" class="aside">
        <el-menu :default-openeds="['attrs', 'files']">
          <el-menu-item index="user">
            <router-link slot="title" to="/user">
              <i class="el-icon-user"></i>
              <span>我的</span>
            </router-link>
          </el-menu-item>
          <el-submenu index="attrs">
            <template slot="title"> <i class="el-icon-message"></i>属性 </template>
            <el-menu-item index="1-1">
              <router-link slot="title" to="/attr"> 我的属性 </router-link>
            </el-menu-item>
            <el-menu-item index="1-2">
              <router-link slot="title" to="/attr/application"> 属性授权 </router-link>
            </el-menu-item>
            <el-menu-item index="1-3">
              <router-link slot="title" to="/attr/approve"> 属性审批 </router-link>
            </el-menu-item>
            <el-menu-item index="1-4">
              <router-link slot="title" to="/attr/history"> 历史记录 </router-link>
            </el-menu-item>
          </el-submenu>
          <el-submenu index="files">
            <template slot="title"> <i class="el-icon-document"></i>文件 </template>
            <el-menu-item index="1-1">
              <router-link slot="title" to="/files"> 我的文件 </router-link>
            </el-menu-item>
            <el-menu-item index="1-2">
              <router-link slot="title" to="/files/upload"> 上传文件 </router-link>
            </el-menu-item>
          </el-submenu>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header style="text-align: right; font-size: 12px">
          <Navigator />
        </el-header>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import Navigator from "@/components/Navigator.vue";
export default {
  name: "App",
  components: {
    Navigator,
  },
  data() {
    return {
      windowHeight: 500,
    };
  },
  computed: {},
  methods: {},

  mounted() {
    // console.log(window.innerHeight);
    this.windowHeight = window.innerHeight;
  },
};
</script>

<style scoped>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  min-width: 900px;
  margin: auto;
}

.el-header {
  top: 0;
  z-index: 100;
  position: sticky;
  height: var(--header-height, 56px) !important;
  padding: 0 !important;
  background-color: var(--bg-color-0, white);
  transition: ease 0.6s all;
}

.aside {
  background-color: white;
  position: sticky;
  top: 0;
}

.aside a {
  width: 100%;
  display: inline-block;
  color: black;
  text-decoration: none !important;
}
</style>
