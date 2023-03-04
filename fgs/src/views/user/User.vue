<template>
  <div class="cards">
    <Info />
    <Orgs />
  </div>
</template>

<script>
import Info from "./_Info.vue";
import Orgs from "./_Orgs.vue";

export default {
  name: "User",
  components: {
    Info,
    Orgs,
  },
  data() {
    return {};
  },
  methods: {},
};
</script>

<style scoped>
.cards > div {
  margin-bottom: var(--row-distance, 10px);
}
</style>