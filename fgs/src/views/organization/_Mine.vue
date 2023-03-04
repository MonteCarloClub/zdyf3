<template>
  <Card title="组织信息">
    <el-descriptions v-if="info" :column="3">
      <el-descriptions-item label="组织名称">{{ info.orgId }}</el-descriptions-item>
      <el-descriptions-item label="成员总数">{{ info.n }}</el-descriptions-item>
      <el-descriptions-item label="阈值">{{ info.t }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ formatTime(info.createTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="创建人">{{ info.fromUserName }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ info.status }}</el-descriptions-item>
      <el-descriptions-item v-if="info.opk" label="组织公钥">{{ info.opk }}</el-descriptions-item>
    </el-descriptions>
  </Card>
</template>

<script>
// @ is an alias to /src
import Card from "@/components/Card.vue";
import { TimeFormat } from "@/mixins/TimeFormat";

export default {
  name: "Mine",
  mixins: [TimeFormat],
  components: {
    Card,
  },
  props: {
    info: {
      orgId: "",
      t: "",
      n: "",
      createTime: "",
      fromUserName: "",
      status: "",
    },
  },
};
</script>