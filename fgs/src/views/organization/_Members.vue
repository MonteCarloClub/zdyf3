<template>
  <Card title="组织成员">
    <el-table :data="members(info)" style="width: 100%">
      <el-table-column show-overflow-tooltip prop="name" label="名称"> </el-table-column>
      <el-table-column prop="value" label="状态">
        <template slot-scope="scope">
          <el-tag :type="statusTypes[scope.row.value]">
            {{ scope.row.value ? "已接受" : "未响应" }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
  </Card>
</template>

<script>
// @ is an alias to /src
import Card from "@/components/Card.vue";

export default {
  name: "Members",
  components: {
    Card,
  },
  props: {
    info: {
      uidMap: {},
    },
  },

  data() {
    return {
      statusTypes: {
        true: "success",
        false: "danger",
      },
    };
  },
  methods: {
    members(info) {
      if (info == undefined) return [];
      const { uidMap } = info;

      if (uidMap == undefined) return [];
      let members = [];
      for (let key of Object.keys(uidMap)) {
        members.push({
          name: key,
          value: uidMap[key],
        });
      }
      return members;
    },
  },
};
</script>