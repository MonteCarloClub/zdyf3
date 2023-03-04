<template>
  <transition-group
    class="grid-rows"
    tag="div"
    v-on:before-enter="beforeEnter"
    v-on:enter="animEnter"
    v-on:leave="animLeave"
    appear
  >
    <Mine key="0" data-index="0" :info="info" />
    <Members key="3" data-index="0.3" :info="info" />
    <template v-if="success">
      <Attributes key="5" data-index="0.5" :info="info" />
      <Search key="4" data-index="0.5" :info="info" />
    </template>
  </transition-group>
</template>

<script>
// @ is an alias to /src
import Mine from "./_Mine.vue";
import Search from "./_Search.vue";
import Members from "./_Members.vue";
import Attributes from "./_Attributes.vue";
import { orgApi } from "@/api/organizations";

export default {
  name: "Organization",
  components: {
    Mine,
    Search,
    Members,
    Attributes,
  },
  watch: {
    $route(to) {
      this.initOrg(to.params.org);
    },
  },
  data() {
    return {
      info: {},
      success: false,
    };
  },
  mounted() {
    this.initOrg(this.$route.params.org);
  },

  methods: {
    initOrg(id) {
      this.success = false;
      if (id == undefined) return;
      orgApi
        .tempOrgInfo({
          orgName: id,
        })
        .then((org) => {
          this.info = org;
          if (org && org.status === "SUCCESS") {
            this.success = true;
            // 如果是成功创建的组织，试图获取详细信息
            this.getOrgDetail(id);
          }
        })
        .catch((e) => {
          this.$message({
            message: e.message,
            type: "error",
          });
        });
    },

    getOrgDetail(orgName) {
      orgApi
        .detailInfo({ orgName })
        .then((detail) => {
          this.info = {
            ...this.info,
            opk: detail.opk,
            attributes: detail.attrSet,
          };
        })
        .catch(console.log);
    },

    beforeEnter: function (el) {
      if (el.dataset.index > -1) {
        el.style.opacity = 0.5;
        el.style.transform = "translateY(-30px)";
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

    animLeave: function (el, done) {
      if (el.dataset.index > -1) {
        el.style.opacity = 0.5;
        done();
      }
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