<template>
  <transition-group
    class="grid-rows"
    tag="div"
    v-on:before-enter="beforeEnter"
    v-on:enter="animEnter"
    appear
  >
    <Mine   key="0" data-index="0.3" />
    <Upload key="1" data-index="0" />
  </transition-group>
</template>

<script>
// @ is an alias to /src
import Mine from "./_Mine.vue";
import Upload from "./_Upload.vue";

export default {
  name: "Files",
  components: {
    Mine,
    Upload,
  },
  methods: {
    beforeEnter: function (el) {
      if (el.dataset.index > -1) {
        el.style.opacity = 0.5;
        el.style.transform = "translateX(30px)";
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
</style>