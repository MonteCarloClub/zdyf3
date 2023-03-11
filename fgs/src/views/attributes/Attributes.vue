<template>
  <transition-group
    class="grid-rows"
    tag="div"
    v-on:before-enter="beforeEnter"
    v-on:enter="animEnter"
    appear
  >
    <Mine key="0" data-index="0"/>
    <Applications key="2" data-index="0.3"/>
    <Approvals key="1" data-index="0.5"/>
    <History key="3" data-index="0.6"/>
  </transition-group>
</template>

<script>
// @ is an alias to /src
import Mine from "./_Mine.vue";
import History from "./_History.vue";
import Approvals from "./_Approvals.vue";
import Applications from "./_Applications.vue";

export default {
  name: "Attributes",
  components: {
    Mine,
    History,
    Approvals,
    Applications,
  },
  data() {
    return {
      attributes: [],
    };
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
        // 清空初始样式
        el.style = "";
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