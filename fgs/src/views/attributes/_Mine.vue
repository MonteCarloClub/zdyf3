<template>
  <Card title="我的属性">
    <template v-slot:op>
      <el-input size="medium" placeholder="请输入要声明的属性" v-model="newAttr">
        <el-button slot="append" @click="generateAttr">声明新属性</el-button>
      </el-input>
    </template>
    <div v-if="APKMap">
      <el-table :data="getAttributes(APKMap)" style="width: 100%">
        <el-table-column show-overflow-tooltip prop="name" label="属性名"> </el-table-column>
        <el-table-column show-overflow-tooltip prop="value" label="属性公钥"> </el-table-column>
      </el-table>
    </div>
    <el-empty v-else :image-size="100" description="还没有属性"></el-empty>
  </Card>
</template>

<script>
// @ is an alias to /src
import Card from "@/components/Card.vue";
import { getters } from "@/store/store";
import { actions } from "@/store/actions";

export default {
  name: "Mine",
  components: {
    Card,
  },

  computed: {
    ...getters.mapUser(["APKMap"]),
  },

  data() {
    return {
      newAttr: "",
    };
  },

  methods: {
    getAttributes(attrMap) {
      let attributes = [];
      for (let key of Object.keys(attrMap)) {
        attributes.push({
          name: key,
          value: attrMap[key].Gy,
        });
      }
      return attributes;
    },
    generateAttr() {
      const { name } = getters.properties(["name", "password"]);
      const attr = `${name}:${this.newAttr}`;
      actions
        .generate({ name, attr })
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
