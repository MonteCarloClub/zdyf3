<template>
  <Card title="属性搜索">
    <template v-slot:op>
      <el-input size="medium" placeholder="请输入要搜索的属性" v-model="newAttr">
        <el-button slot="append" @click="searchAttr">搜索</el-button>
      </el-input>
    </template>
    <div v-if="attr">
      <el-table :data="[attr]">
        <el-table-column show-overflow-tooltip label="属性名" prop="attrName" />
        <el-table-column label="申请时间" prop="createTime" width="160">
          <template slot-scope="scope">
              {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column show-overflow-tooltip label="申请人" prop="fromUserName" />
        <el-table-column label="状态" prop="status" width="150">
          <template slot-scope="scope">
            <el-tag size="small" effect="plain">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="300" align="right">
          <template slot-scope="scope">
            <el-button size="mini" @click="submitPartPK(scope.row)"> 提交属性秘密 </el-button>
            <el-button size="mini" @click="agreeAttr(scope.row)" type="primary"> 同意 </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-table :data="members(attr)" style="width: 100%">
        <el-table-column show-overflow-tooltip prop="name" label="小组成员"> </el-table-column>
        <el-table-column prop="value" label="是否同意该属性" align="right">
          <template slot-scope="scope">
            <el-tag :type="statusTypes[scope.row.value]">
              {{ scope.row.value ? "已同意" : "未响应" }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-empty v-else></el-empty>
  </Card>
</template>

<script>
// @ is an alias to /src
import Card from "@/components/Card.vue";
import { getters } from "@/store/store";
import { orgApi } from "@/api/organizations";
import { TimeFormat } from "@/mixins/TimeFormat";

export default {
  name: "Search",
  mixins: [TimeFormat],
  components: {
    Card,
  },

  props: {
    info: {},
  },

  data() {
    return {
      newAttr: "",
      attr: false,
      statusTypes: {
        true: "success",
        false: "danger",
      },
    };
  },

  methods: {
    searchAttr() {
      this.attr = false;
      const orgName = this.info.orgId;
      const attrName = `${orgName}:${this.newAttr}`;

      orgApi
        .tempOrgAttrInfo({ orgName, attrName })
        .then((attrInfo) => {
          if (attrInfo) {
            // attrInfo.createTime = this.formatTime(attrInfo.createTime);
            this.attr = attrInfo;
          } else {
            this.$message({
              message: "没有该属性",
              type: "info",
            });
          }
        })
        .catch((e) => {
          this.$message({
            message: e.message,
            type: "error",
          });
        });
    },

    submitPartPK(attr) {
      const userName = getters.userName();
      const orgName = this.info.orgId;
      const { attrName } = attr;

      orgApi
        .submitOrgAttppk({ userName, attrName, orgName })
        .then(() => {
          this.$message({
            message: "提交成功",
            duration: 2 * 1000,
            type: "success",
          });
          // 尝试最终确认
          this.trycompleteOrgPK(userName, orgName, attrName);
        })
        .catch((e) => {
          this.$message({
            message: e.message,
            type: "error",
          });
        });
    },

    trycompleteOrgPK(userName, orgName, attrName) {
      orgApi
        .completeOrgAttr({ userName, orgName, attrName })
        .then(() => {
          this.$message({
            message: "组织属性已确认创建",
            duration: 2 * 1000,
            type: "success",
          });
        })
        .catch(console.log);
    },

    agreeAttr(attr) {
      const userName = getters.userName();
      const orgName = this.info.orgId;
      const { attrName } = attr;

      orgApi
        .approvalAttr({ userName, orgName, attrName })
        .then(() => {
          this.$message({
            message: "提交成功",
            duration: 2 * 1000,
            type: "success",
          });
          // 尝试最终确认
          // this.trycompleteOrgPK(userName, org.name);
        })
        .catch((e) => {
          this.$message({
            message: e.message,
            type: "error",
          });
        });
    },

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
