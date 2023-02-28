<template>
  <div class="main-content">
    <div class="">
      <transition-group
        class="grid-rows nav-col"
        tag="div"
        v-on:before-enter="beforeEnter"
        v-on:enter="animEnter"
        appear
      >
        <Info key="-1" data-index="0" />
        <Nav key="2" data-index="1" />
        <Orgs key="3" data-index="1.5" />
      </transition-group>
    </div>
    <keep-alive>
      <router-view></router-view>
    </keep-alive>
  </div>
</template>

<script>
import Nav from "./_Nav.vue";
import Info from "./_Info.vue";
import Orgs from "./_Orgs.vue";

export default {
  name: "User",
  components: {
    Nav,
    Info,
    Orgs,
  },
  data() {
    return {};
  },
  methods: {
    beforeEnter: function (el) {
      if (el.dataset.index > -1) {
        el.style.opacity = 0;
        el.style.transform = "translateY(30px)";
      }
    },
    animEnter: function (el, done) {
      var delay = el.dataset.index * 250;
      setTimeout(function () {
        el.style = ""; // 清空初始的偏移样式
        done();
      }, delay);
    },
  },
};
</script>

<style scoped>
.grid-rows {
  display: grid;
  grid-template-columns: auto;
  grid-gap: var(--row-distance, 10px);
}

.grid-rows > div {
  transition: all 0.5s;
}

.nav-col {
  width: 300px;
  float: left;
  margin-right: var(--row-distance, 10px);
}
</style>


<style>
/* shared by child components */
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