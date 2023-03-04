<template>
  <Card title="我的信息" key="-1" data-index="-1">
    <template v-slot:op>
      <el-button size="small" @click="logoutClicked">退出登录</el-button>
    </template>
    <el-descriptions :column="1">
      <el-descriptions-item label="用户名">{{ name }} </el-descriptions-item>
      <el-descriptions-item label="我的角色">
        {{ roleTitles[role]}}
      </el-descriptions-item>
      <el-descriptions-item label="所在通道">{{ channel }}</el-descriptions-item>
    </el-descriptions>
  </Card>
</template>

<script>
// @ is an alias to /src
import Card from "@/components/Card.vue";
import { getters } from "@/store/store";
import { actions } from "@/store/actions";

export default {
  name: "Info",

  components: {
    Card,
  },
  data() {
    return {
      roleTitles: {
        user: "普通用户",
        org: "机构用户",
      },
    };
  },

  computed: {
    ...getters.mapUser(["name", "channel", "role"]),
  },

  methods: {
    logoutClicked() {
      actions.logout().then(() => {
        this.$message({
          message: "退出登录",
          type: "success",
        });
        location.reload();
      });
    },
  },
};
</script>