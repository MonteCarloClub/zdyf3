<template>
  <Card >
    <div>
      <router-link v-for="(nav, index) in navs" :key="index" :to="nav.to">
        <div class="tab" v-bind:class="{ current: $route.name === nav.name }">{{ nav.title }}</div>
      </router-link>
    </div>
  </Card>
</template>

<script>
// @ is an alias to /src
import Card from "@/components/Card.vue";

export default {
  name: "Nav",
  components: {
    Card,
  },
  data() {
    return {
      navs: [
        {
          to: "/user",
          name: "attributes",
          title: "属性",
        },
        {
          to: "/user/files",
          name: "files",
          title: "文件",
        },
        {
          to: "/user/organizations",
          name: "organizations",
          title: "组织",
        },
      ],
    };
  },

  methods: {},

  mounted() {
    // console.log(this.$route.name);
  },
};
</script>

<style scoped>
a {
  text-decoration: none;
}

.tab {
  color: var(--ft-color-0, #d4d4d4);
  padding: 12px 0;
  border-radius: 4px;
  transition: all 0.2s ease;
  text-decoration: none !important;
}

.current {
  padding-left: 12px;
  color: var(--ft-color-1, #d4d4d4);
}

.tab:hover {
  padding-left: 12px;
  color: var(--ft-color-1, #d4d4d4);
  cursor: pointer;
}

</style>