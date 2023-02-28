<template>
  <Card>
    <div v-if="orgs.length">
      <router-link v-for="(org, index) in orgs" :key="index" :to="`/user/organization/${org}`">
        <div class="tab" v-bind:class="{ current: $route.params.org === org }">{{ org }}</div>
      </router-link>
    </div>
    <div v-else>暂未加入组织</div>
  </Card>
</template>

<script>
// @ is an alias to /src
import Card from "@/components/Card.vue";
import { getters } from "@/store/store";

export default {
  name: "Orgs",
  components: {
    Card,
  },
  data() {
    return {
      orgs: [],
    };
  },

  methods: {},

  mounted() {
    const a = getters.properties(["OPKMap"]);
    this.orgs = Object.keys(a.OPKMap);
  },
};
</script>

<style scoped>
a {
  text-decoration: none;
}

.tab {
  color: gray;
  padding: 12px 0;
  border-radius: 4px;
  transition: all 0.2s ease;
  text-decoration: none !important;
}

.current {
  padding-left: 12px;
  color: black;
}

.tab:hover {
  padding-left: 12px;
  color: black;
  cursor: pointer;
}
</style>