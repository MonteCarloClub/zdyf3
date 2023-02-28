<template>
  <div>
    <Card title="属性审批">
      <div v-if="attributes.length">
        <el-table :data="attributes" style="width: 100%">
          <el-table-column prop="isPublic" label="" width="50">
            <template slot-scope="scope">
              {{ scope.row.isPublic ? "公开" : "私有" }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="类型" width="100">
            <template slot-scope="scope">
              {{ scope.row.applyType }}
            </template>
          </el-table-column>
          <el-table-column show-overflow-tooltip prop="attrName" label="属性名"> </el-table-column>
          <el-table-column show-overflow-tooltip prop="fromUid" label="申请人"> </el-table-column>
          <el-table-column show-overflow-tooltip prop="remark" label="申请备注"> </el-table-column>

          <el-table-column label="操作" width="250">
            <template slot-scope="scope">
              <el-button size="mini" type="warning" @click="revoke(scope.row)"> 撤销 </el-button>
              <el-button size="mini" type="success" @click="agree(scope.row, true)">
                同意
              </el-button>
              <el-button size="mini" type="danger" @click="agree(scope.row, false)">
                拒绝
              </el-button>
            </template>
          </el-table-column>

          <el-table-column prop="status" label="状态" width="100" align="right">
            <template slot-scope="scope">
              <el-tag :type="statusTypes[scope.row.status]" size="small">
                {{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-empty v-else :image-size="100" description="暂无待审批的属性"></el-empty>
    </Card>
    <el-dialog title="属性审批" :visible.sync="dialogFormVisible">
      <el-form :model="form" label-width="80px">
        <el-form-item label="备注">
          <el-input type="textarea" v-model="form.remark"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="approval">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// @ is an alias to /src
import Card from "@/components/Card.vue";
import { attrApi } from "@/api/attributes";
import { getters } from "@/store/store";

export default {
  name: "Approvals",
  components: {
    Card,
  },

  mounted() {
    const { OPKMap, name } = getters.properties(["OPKMap", "name"]);

    // 0 是用户
    const query = [
      {
        to: name,
        role: 0,
      },
    ];
    Object.keys(OPKMap).map((org) => {
      query.push({
        to: org,
        role: 1,
      });
    });

    const status = "ALL";
    let res = [];

    Promise.all(query.map(({ to, role }) => attrApi.applications({ to, role, status })))
      .then((responses) => {
        responses.forEach((response) => {
          res = res.concat(response);
        });
        this.attributes = res;
      })
      .catch(console.log);
  },

  data() {
    return {
      attributes: [],
      form: {
        remark: "",
      },
      params: {},
      dialogFormVisible: false,

      statusTypes: {
        SUCCESS: "success",
        FAIL: "danger",
        PENDING: "info",
      },
    };
  },

  methods: {
    agree(application, agree) {
      const to = getters.userName();
      const { attrName, fromUid } = application;
      this.params = { to: fromUid, agree, attr: attrName, user: to };
      this.dialogFormVisible = true;
    },
    approval() {
      attrApi
        .approval({ ...this.params, ...this.form })
        .then(() => {
          this.$message({
            message: "审批成功",
            duration: 2 * 1000,
            type: "success",
          });
        })
        .catch((e) => {
          this.$message({
            message: e.message,
            duration: 2 * 1000,
            type: "error",
          });
        })
        .finally(() => {
          this.dialogFormVisible = false;
        });
    },
    revoke(application) {
      const userName = getters.userName();
      const { attrName, fromUid } = application;
      attrApi
        .revoke({
          userName,
          toUserName: fromUid,
          attrName
        })
        .then(() => {
          this.$message({
            message: "撤销成功",
            duration: 2 * 1000,
            type: "success",
          });
        })
        .catch((e) => {
          this.$message({
            message: e.message,
            duration: 2 * 1000,
            type: "error",
          });
        });
    },
  },
};
</script>