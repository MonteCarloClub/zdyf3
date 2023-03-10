<template>
  <Card title="历史记录">
    <div v-if="records.length">
      <el-table :data="records" style="width: 100%">
        <el-table-column show-overflow-tooltip prop="uid" label="当前用户"> </el-table-column>
        <el-table-column show-overflow-tooltip prop="fromUid" label="操作用户"> </el-table-column>
        <el-table-column show-overflow-tooltip prop="attrName" label="属性名称"> </el-table-column>
        <el-table-column show-overflow-tooltip prop="operation" label="操作"> </el-table-column>
        <el-table-column sortable prop="timeStamp" label="操作时间"></el-table-column>
      </el-table>
    </div>
    <el-empty v-else :image-size="100" description="还没有记录"></el-empty>
  </Card>
</template>

<script>
// @ is an alias to /src
import Card from "@/components/Card.vue";
import { TimeFormat } from "@/mixins/TimeFormat";
import { getters } from "@/store/store";
import { attrApi } from "@/api/attributes";

export default {
  name: "History",
  components: {
    Card,
  },
  mixins: [TimeFormat],

  data() {
    return {
      records: [],
    };
  },

  methods: {},

  mounted() {
    const userName = getters.userName();
    attrApi.history(userName).then((res) => {
      this.records = res;
    });
  },
};
</script>
