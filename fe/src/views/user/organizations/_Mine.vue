<template>
  <div>
    <Card title="我的组织">
      <template v-slot:op>
        <el-button size="small" @click="joinDialogFormVisible = true"> 加入组织 </el-button>
        <el-button size="small" type="primary" @click="applyDialogFormVisible = true">
          申请创建组织
        </el-button>
      </template>
      <div v-if="OPKMap">
        <el-table :data="formatOrganization(OPKMap)" style="width: 100%">
          <el-table-column show-overflow-tooltip prop="name" label="组织名称" width="180"> </el-table-column>
          <el-table-column show-overflow-tooltip prop="value" label="组织公钥">
            <template slot-scope="scope">
              {{ scope.row.value || "暂无" }}
            </template>
          </el-table-column>

          <el-table-column label="操作" width="160" align="right">
            <template slot-scope="scope">
              <el-button size="mini" @click="submitPartPK(scope.row)"> 提交组织秘密 </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-empty v-else :image-size="100" description="没有正在申请的属性"></el-empty>
    </Card>

    <el-dialog title="申请成立新组织" :visible.sync="applyDialogFormVisible">
      <el-form ref="applyForm" :rules="applyRules" :model="form" label-width="80px">
        <el-form-item prop="orgName" label="组织名称">
          <el-input v-model="form.orgName" placeholder="请输入组织名称"></el-input>
        </el-form-item>

        <el-form-item prop="threshold" label="阈值">
          <el-input v-model="form.threshold" placeholder="请输入阈值"></el-input>
        </el-form-item>

        <el-form-item prop="userStr" label="其它成员">
          <el-input v-model="form.userStr" placeholder="请输入除您之外包含的其它成员"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="applyDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="applyForOrg">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="加入组织" :visible.sync="joinDialogFormVisible">
      <el-form ref="joinForm" :rules="joinRules" :model="joinForm" label-width="80px">
        <el-form-item prop="orgName" label="组织名称">
          <el-input v-model="joinForm.orgName" placeholder="请输入组织名称"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="joinDialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="joinOrg">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
// @ is an alias to /src
import Card from "@/components/Card.vue";
import { orgApi } from "@/api/organizations";
import { getters } from "@/store/store";

export default {
  name: "Mine",
  components: {
    Card,
  },

  computed: {
    ...getters.mapUser(["OPKMap"]),
  },

  data() {
    return {
      applyDialogFormVisible: false,
      form: {
        threshold: "",
        userStr: "",
        orgName: "",
      },
      applyRules: {
        orgName: [{ required: true, trigger: "blur", message: "组织名不能为空" }],
        threshold: [{ required: true, trigger: "blur", message: "阈值不能为空" }],
        userStr: [{ required: true, trigger: "blur", message: "成员不能为空" }],
      },
      joinDialogFormVisible: false,
      joinForm: {
        orgName: "",
      },
      joinRules: {
        orgName: [{ required: true, trigger: "blur", message: "组织名不能为空" }],
      },
    };
  },

  methods: {
    formatOrganization(OPKMap) {
      let attributes = [];
      for (let key of Object.keys(OPKMap)) {
        attributes.push({
          name: key,
          value: OPKMap[key].OPK,
        });
      }
      return attributes;
    },

    applyForOrg() {
      const userName = getters.userName();
      if (!userName) return;

      this.$refs.applyForm.validate((valid) => {
        if (!valid) return;

        const { threshold, userStr, orgName } = this.form;
        const users = userStr.split(" ");
        users.push(userName);

        orgApi
          .create({
            userName,
            threshold,
            users,
            orgName,
            usersNum: users.length,
          })
          .then(() => {
            this.$message({
              message: "申请成功",
              duration: 2 * 1000,
              type: "success",
            });
          })
          .catch((e) => {
            this.$message({
              message: e.message,
              type: "error",
            });
          })
          .finally(() => {
            this.applyDialogFormVisible = false;
          });
      });
    },

    joinOrg() {
      const userName = getters.userName();
      if (!userName) return;

      this.$refs.joinForm.validate((valid) => {
        if (!valid) return;

        const { orgName } = this.joinForm;

        orgApi
          .join({ userName, orgName })
          .then(() => {
            this.$message({
              message: "加入成功",
              duration: 2 * 1000,
              type: "success",
            });
          })
          .catch((e) => {
            this.$message({
              message: e.message,
              type: "error",
            });
          })
          .finally(() => {
            this.applyDialogFormVisible = false;
          });
      });
    },

    submitPartPK(org) {
      const userName = getters.userName();

      orgApi
        .submitppk({ userName, orgName: org.name })
        .then(() => {
          this.$message({
            message: "提交成功",
            duration: 2 * 1000,
            type: "success",
          });
          // 尝试最终确认
          this.trycompleteOrgPK(userName, org.name);
        })
        .catch((e) => {
          this.$message({
            message: e.message,
            type: "error",
          });
        });
    },

    trycompleteOrgPK(userName, orgName) {
      orgApi
        .completeOrg({ userName, orgName })
        .then(() => {
          this.$message({
            message: "组织已确认创建",
            duration: 2 * 1000,
            type: "success",
          });
        })
        .catch(console.log);
    },
  },
};
</script>