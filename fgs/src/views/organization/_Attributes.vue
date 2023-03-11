<template>
  <Card title="组织属性">
    <template v-slot:op>
      <el-input size="medium" placeholder="请输入要声明的属性" v-model="newAttr">
        <el-button slot="append" @click="generateAttr">声明新属性</el-button>
      </el-input>
    </template>
    <el-table :data="getAttributes(info)" style="width: 100%">
      <el-table-column show-overflow-tooltip prop="name" label="属性名"> </el-table-column>
    </el-table>
  </Card>
</template>

<script>
// @ is an alias to /src
import Card from "@/components/Card.vue";
import { getters } from "@/store/store";
import { orgApi } from "@/api/organizations";

export default {
  name: "Attributes",
  components: {
    Card,
  },

  props: {
    info: {},
  },

  data() {
    return {
      newAttr: "",
    };
  },

  methods: {
    getAttributes(info) {
      if (info == undefined) return [];
      const attrMap = info.attributes;

      if (attrMap == undefined) return;

      let attributes = [];
      for (let key of Object.keys(attrMap)) {
        attributes.push({
          name: attrMap[key],
        });
      }
      return attributes;
    },

    generateAttr() {
      const userName = getters.userName();
      const orgName = this.info.orgId;
      const attrName = `${orgName}:${this.newAttr}`;

      orgApi
        .declareAttr({ userName, orgName, attrName })
        .then(() => {
          this.$message({
            message: "创建成功",
            duration: 2 * 1000,
            type: "success",
          });
        })
        .catch((e) => {
          this.$message({
            message: e.message,
            type: "error",
          });
        });
    },
  },
};
</script>
